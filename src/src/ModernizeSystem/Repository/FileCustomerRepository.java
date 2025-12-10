package ModernizeSystem.Repository;

import ModernizeSystem.Model.CustomerModel;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

public class FileCustomerRepository implements CustomerRepository {

    private static final Logger LOGGER = Logger.getLogger(FileCustomerRepository.class.getName());
    private static final String FILE_NAME = "cusData.txt";

    private final Path path;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public FileCustomerRepository() {
        this.path = Path.of(FILE_NAME);
        ensureFileExists();
    }

    @Override
    public List<CustomerModel> findAll() {
        lock.readLock().lock();
        try {
            if (!Files.exists(path)) return new ArrayList<>();

            List<String> lines = Files.readAllLines(path);
            List<CustomerModel> customers = new ArrayList<>();

            for (String line : lines) {
                CustomerModel c = parse(line);
                if (c != null) customers.add(c);
            }

            return customers;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file", e);
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<CustomerModel> findById(String id) {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<CustomerModel> findByEmail(String email) {
        return findAll().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public CustomerModel save(CustomerModel customer) {
        lock.writeLock().lock();
        try {
            String line = String.format("%s|%s|%s%n",
                    customer.getId(), customer.getPassword(), customer.getEmail());

            Files.writeString(path, line, StandardOpenOption.APPEND);

            return customer;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to file", e);
            throw new IllegalStateException("Failed to save user", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String nextCustomerId() {
        lock.readLock().lock();
        try {
            int lastNumber = findAll().stream()
                    .map(CustomerModel::getId)
                    .filter(id -> id.startsWith("C"))
                    .map(id -> id.substring(1))
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(1000);

            return "C" + (lastNumber + 1);

        } finally {
            lock.readLock().unlock();
        }
    }

    private CustomerModel parse(String line) {
        if (line == null || line.isBlank()) return null;

        String[] p = line.split("\\|");
        if (p.length != 3) return null;

        return new CustomerModel(p[0], p[1], p[2]);
    }

    private void ensureFileExists() {
        try {
            if (!Files.exists(path)) Files.createFile(path);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not create file", e);
        }
    }
}
