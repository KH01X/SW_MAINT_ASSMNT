package ModernizeSystem.Repository;

import ModernizeSystem.Model.CustomerModel;
import java.util.List;
import java.util.Optional;

/**
 * Abstraction for persisting and retrieving CustomerModel records.
 * Follows Dependency Inversion Principle.
 */
public interface CustomerRepository {

    /**
     * @return all customers available in the data store.
     */
    List<CustomerModel> findAll();

    /**
     * Looks up a customer via unique ID.
     */
    Optional<CustomerModel> findById(String id);

    /**
     * Looks up a customer via email (used to prevent duplicate accounts).
     */
    Optional<CustomerModel> findByEmail(String email);

    /**
     * Persists the provided customer and returns the stored entity.
     */
    CustomerModel save(CustomerModel customer);

    /**
     * Calculates the next sequential customer ID (e.g., C1003).
     */
    String nextCustomerId();
}
