package ModernizeSystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-based implementation of {@link CustomerRepository} that reads and writes
 * legacy pipe-delimited rows inside {@code cusData.txt}.
 */
public class FileCustomerRepository implements CustomerRepository {

    private static final Logger LOGGER = Logger.getLogger(FileCustomerRepository.class.getName());
    private static final String DEFAULT_FILE = "cusData.txt";
    private static final String COLUMN_SEPARATOR = "|";

    private final Path customerFile;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public FileCustomerRepository() {
        this(Path.of(DEFAULT_FILE));
    }

    public FileCustomerRepository(Path customerFile) {
        this.customerFile = customerFile;
        ensureFileExists();
    }

    @Override
    public List<Customer> findAll() {
        lock.readLock().lock();
        try {
            if (!Files.exists(customerFile)) {
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(customerFile, StandardCharsets.UTF_8);
            List<Customer> customers = new ArrayList<>();

            for (String line : lines) {
                parseCustomer(line).ifPresent(customers::add);
            }
            return customers;

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read customer data", ex);
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Customer> findById(String id) {
        return findAll().stream()
                .filter(customer -> customer.getuserID().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return findAll().stream()
                .filter(customer -> customer.getuserEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Customer save(Customer customer) {
        lock.writeLock().lock();
        try {
            String serialized = serialize(customer);
            Files.writeString(customerFile, serialized, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return customer;

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to persist customer", ex);
            throw new IllegalStateException(ErrorMessage.IO_ERROR, ex);

        } finally {
            lock.writeLock().unlock();
        }
    }

    // ============================================================================
    // FIXED VERSION — NEXT CUSTOMER ID GENERATOR
    // ============================================================================
    @Override
    public String nextCustomerId() {
        OptionalInt maxId = findAll().stream()
                .map(Customer::getuserID)
                .filter(id -> id != null && id.startsWith("C"))
                .map(id -> id.substring(1))
                .filter(FileCustomerRepository::isNumeric)
                .mapToInt(Integer::parseInt)
                .max();

        int next = maxId.orElse(1000) + 1;
        return "C" + next;
    }

    // ============================================================================
    // PARSE CUSTOMER RECORD
    // ============================================================================
    private static Optional<Customer> parseCustomer(String row) {
        if (row == null || row.isBlank()) return Optional.empty();

        String[] parts = row.split("\\|", -1);

        if (parts.length < 3) {
            LOGGER.log(Level.WARNING, "Skipping corrupted customer record: {0}", row);
            return Optional.empty();
        }

        Customer customer = new Customer();
        customer.setuserID(parts[0].trim());
        customer.setuserPw(parts[1].trim());
        customer.setuserEmail(parts[2].trim());

        return Optional.of(customer);
    }

    private static String serialize(Customer customer) {
        return String.format("%s|%s|%s%n",
                customer.getuserID(),
                customer.getuserPw(),
                customer.getuserEmail());
    }

    // ============================================================================
    // ENSURE FILE EXISTS
    // ============================================================================
    private void ensureFileExists() {
        if (Files.exists(customerFile)) return;

        lock.writeLock().lock();
        try {
            Files.createFile(customerFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Unable to create customer data file", ex);
            throw new IllegalStateException("Unable to create customer data file", ex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ============================================================================
    // NUMERIC CHECK
    // ============================================================================
    private static boolean isNumeric(String value) {
        if (value == null || value.isBlank()) return false;

        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
