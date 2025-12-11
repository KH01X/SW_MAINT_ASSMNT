package ModernizeSystem.Repository;

import ModernizeSystem.Model.CustomerModel;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    List<CustomerModel> findAll();

    Optional<CustomerModel> findById(String id);

    Optional<CustomerModel> findByEmail(String email);

    CustomerModel save(CustomerModel customer);

    String nextCustomerId();
}
