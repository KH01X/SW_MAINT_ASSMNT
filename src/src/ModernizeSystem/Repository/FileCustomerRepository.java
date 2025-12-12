package ModernizeSystem.Repository;

import ModernizeSystem.Model.CustomerModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-based implementation of CustomerRepository.
 * Stores customer data in pipe-delimited format inside cusData.txt.
 */
public class FileCustomerRepository implements CustomerRepository {

    private static final Logger LOGGER =
            Logger.getLogger(FileCustomerRepository.class.getName());

    private static final String DEFAULT_FILE = "cusData.txt";
    private static final String SEPARATOR = "|";

    private final Path path;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public FileCustomerRepository() {
        this(Path.of(DEFAULT_FILE));
    }

    public FileCustomerRepository(Path path) {
        this.path = path;
        ensureFileExists();
    }

    // =========================================================================
    // READ ALL
    // =========================================================================
    @Override
    public List<CustomerModel> findAll() {
        lock.readLock().lock();
        try {
            if (!Files.exists(path)) return new ArrayList<>();

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<CustomerModel> customers = new ArrayList<>();

            for (String line : lines) {
                parse(line).ifPresent(customers::add);
            }
            return customers;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read customer data", e);
            return new ArrayList<>();

        } finally {
            lock.readLock().unlock();
        }
    }

    // =========================================================================
    // FIND BY ID
    // =========================================================================
    @Override
    public Optional<CustomerModel> findById(String id) {
        return findAll().stream()
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    // =========================================================================
    // FIND BY EMAIL
    // =========================================================================
    @Override
    public Optional<CustomerModel> findByEmail(String email) {
        return findAll().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // =========================================================================
    // SAVE
    // =========================================================================
    @Override
    public CustomerModel save(CustomerModel customer) {
        lock.writeLock().lock();
        try {
            String line = serialize(customer);
            Files.writeString(
                    path,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            return customer;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save customer", e);
            throw new IllegalStateException("Failed to save customer", e);

        } finally {
            lock.writeLock().unlock();
        }
    }

    // =========================================================================
    // NEXT CUSTOMER ID (C1001, C1002, ...)
    // =========================================================================
    @Override
    public String nextCustomerId() {
        lock.readLock().lock();
        try {
            int last = findAll().stream()
                    .map(CustomerModel::getId)
                    .filter(id -> id != null && id.startsWith("C"))
                    .map(id -> id.substring(1))
                    .filter(FileCustomerRepository::isNumeric)
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(1000);

            return "C" + (last + 1);

        } finally {
            lock.readLock().unlock();
        }
    }

    // =========================================================================
    // PARSE LINE → CUSTOMER
    // =========================================================================
    private Optional<CustomerModel> parse(String line) {
        if (line == null || line.isBlank()) return Optional.empty();

        String[] parts = line.split("\\|", -1);
        if (parts.length != 3) {
            LOGGER.log(Level.WARNING, "Skipping corrupted record: {0}", line);
            return Optional.empty();
        }

        return Optional.of(
                new CustomerModel(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim()
                )
        );
    }

    private String serialize(CustomerModel customer) {
        return String.format("%s|%s|%s%n",
                customer.getId(),
                customer.getPassword(),
                customer.getEmail());
    }

    // =========================================================================
    // FILE SETUP
    // =========================================================================
    private void ensureFileExists() {
        if (Files.exists(path)) return;

        lock.writeLock().lock();
        try {
            Files.createFile(path);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to create customer data file", e);
            throw new IllegalStateException("Unable to create data file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static boolean isNumeric(String value) {
        if (value == null || value.isBlank()) return false;
        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
