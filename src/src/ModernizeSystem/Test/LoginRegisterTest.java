package ModernizeSystem.Test;

import ModernizeSystem.Model.CustomerModel;
import ModernizeSystem.Model.UserModel;
import ModernizeSystem.Repository.CustomerRepository;
import ModernizeSystem.Repository.StaffRepository;
import ModernizeSystem.Service.AuthenticationService;
import ModernizeSystem.Service.RegistrationRequest;
import ModernizeSystem.Service.RegistrationService;
import ModernizeSystem.Util.ValidationException;

import org.junit.jupiter.api.*;

import java.util.*;

public class LoginRegisterTest {

    // ============================================================
    // AUTHENTICATION TESTS
    // ============================================================

    @Test
    void testValidCustomerLogin() {
        CustomerRepository customerRepo = new FakeCustomerRepo(
                new CustomerModel("C1000", "pw123", "mail@mail.com")
        );
        StaffRepository staffRepo = new FakeStaffRepo();

        AuthenticationService auth = new AuthenticationService(customerRepo, staffRepo);

        UserModel user = auth.login("C1000", "pw123");

        Assertions.assertEquals("C1000", user.getId());
    }

    @Test
    void testInvalidPasswordThrowsException() {
        CustomerRepository customerRepo = new FakeCustomerRepo(
                new CustomerModel("C1000", "correctPw", "mail@mail.com")
        );
        StaffRepository staffRepo = new FakeStaffRepo();

        AuthenticationService auth = new AuthenticationService(customerRepo, staffRepo);

        Assertions.assertThrows(
                SecurityException.class,
                () -> auth.login("C1000", "wrongPw")
        );
    }

    @Test
    void testUnknownCustomerIdThrowsException() {
        CustomerRepository customerRepo = new FakeCustomerRepo();
        StaffRepository staffRepo = new FakeStaffRepo();

        AuthenticationService auth = new AuthenticationService(customerRepo, staffRepo);

        Assertions.assertThrows(
                SecurityException.class,
                () -> auth.login("C9999", "anypw")
        );
    }

    // ============================================================
    // REGISTRATION TESTS
    // ============================================================

    @Test
    void testDuplicateEmailRejectsRegistration() {
        CustomerRepository repo = new FakeCustomerRepo(
                new CustomerModel("C1000", "pw", "x@mail.com")
        );

        RegistrationService reg = new RegistrationService(repo);

        Assertions.assertThrows(
                ValidationException.class,
                () -> reg.register(new RegistrationRequest("a", "a", "x@mail.com"))
        );
    }

    @Test
    void testSuccessfulRegistration() {
        FakeCustomerRepo repo = new FakeCustomerRepo();
        repo.setNextId("C2000");

        RegistrationService reg = new RegistrationService(repo);

        var result = reg.register(
                new RegistrationRequest("pass", "pass", "new@mail.com")
        );

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("C2000", result.customer().getId());
    }

    @Test
    void testPasswordMismatchThrowsException() {
        CustomerRepository repo = new FakeCustomerRepo();

        RegistrationService reg = new RegistrationService(repo);

        Assertions.assertThrows(
                ValidationException.class,
                () -> reg.register(new RegistrationRequest("abc", "xyz", "user@mail.com"))
        );
    }

    // ============================================================
    // FAKE REPOSITORIES (NO MOCKITO)
    // ============================================================

    static class FakeCustomerRepo implements CustomerRepository {

        private final List<CustomerModel> customers = new ArrayList<>();
        private String nextId = "C1001";

        FakeCustomerRepo(CustomerModel... initial) {
            customers.addAll(Arrays.asList(initial));
        }

        void setNextId(String id) {
            this.nextId = id;
        }

        @Override
        public List<CustomerModel> findAll() {
            return customers;
        }

        @Override
        public Optional<CustomerModel> findById(String id) {
            return customers.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<CustomerModel> findByEmail(String email) {
            return customers.stream()
                    .filter(c -> c.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }

        @Override
        public CustomerModel save(CustomerModel customer) {
            customers.add(customer);
            return customer;
        }

        @Override
        public String nextCustomerId() {
            return nextId;
        }
    }

    static class FakeStaffRepo implements StaffRepository {
        @Override
        public List findAll() {
            return List.of();
        }

        @Override
        public Optional findById(String id) {
            return Optional.empty();
        }
    }
}
