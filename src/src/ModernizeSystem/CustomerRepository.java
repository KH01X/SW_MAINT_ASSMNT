package ModernizeSystem;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction for persisting and retrieving {@link Customer} records.
 * Enables the login/register module to follow the Dependency Inversion Principle.
 */
public interface CustomerRepository {

    /**
     * @return all customers available in the data store.
     */
    List<Customer> findAll();

    /**
     * Looks up a customer via unique ID.
     */
    Optional<Customer> findById(String id);

    /**
     * Looks up a customer via email (used to prevent duplicate accounts).
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Persists the provided customer and returns the stored entity.
     */
    Customer save(Customer customer);

    /**
     * Calculates the next sequential customer ID (e.g., C1003).
     */
    String nextCustomerId();
}
