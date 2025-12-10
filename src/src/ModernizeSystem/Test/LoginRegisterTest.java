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
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LoginRegisterTest {

    // ============================================================
    // AUTHENTICATION TESTS
    // ============================================================
    @Nested
    class AuthenticationTests {

        @Test
        void testValidCustomerLogin() {
            // Mock repos
            CustomerRepository customerRepo = mock(CustomerRepository.class);
            StaffRepository staffRepo = mock(StaffRepository.class);

            // Mock customer data
            when(customerRepo.findById("C1000"))
                    .thenReturn(Optional.of(new CustomerModel("C1000", "pw123", "mail@mail.com")));

            AuthenticationService authService = new AuthenticationService(customerRepo, staffRepo);

            // Test
            UserModel user = authService.login("C1000", "pw123");

            Assertions.assertEquals("C1000", user.getId());
        }

        @Test
        void testInvalidPasswordThrowsException() {
            CustomerRepository customerRepo = mock(CustomerRepository.class);
            StaffRepository staffRepo = mock(StaffRepository.class);

            when(customerRepo.findById("C1000"))
                    .thenReturn(Optional.of(new CustomerModel("C1000", "correctPw", "mail@mail.com")));

            AuthenticationService authService = new AuthenticationService(customerRepo, staffRepo);

            Assertions.assertThrows(
                    SecurityException.class,
                    () -> authService.login("C1000", "wrongPw")
            );
        }

        @Test
        void testUnknownCustomerIdThrowsException() {
            CustomerRepository customerRepo = mock(CustomerRepository.class);
            StaffRepository staffRepo = mock(StaffRepository.class);

            when(customerRepo.findById("C9999"))
                    .thenReturn(Optional.empty());

            AuthenticationService authService = new AuthenticationService(customerRepo, staffRepo);

            Assertions.assertThrows(
                    SecurityException.class,
                    () -> authService.login("C9999", "anypw")
            );
        }
    }

    // ============================================================
    // REGISTRATION TESTS
    // ============================================================
    @Nested
    class RegistrationTests {

        @Test
        void testDuplicateEmailRejectsRegistration() {
            CustomerRepository repo = mock(CustomerRepository.class);

            when(repo.findByEmail("x@mail.com"))
                    .thenReturn(Optional.of(new CustomerModel("C1000", "pw", "x@mail.com")));

            RegistrationService reg = new RegistrationService(repo);

            Assertions.assertThrows(
                    ValidationException.class,
                    () -> reg.register(new RegistrationRequest("a", "a", "x@mail.com"))
            );
        }

        @Test
        void testSuccessfulRegistration() {
            CustomerRepository repo = mock(CustomerRepository.class);

            when(repo.findByEmail("new@mail.com"))
                    .thenReturn(Optional.empty());

            when(repo.nextCustomerId())
                    .thenReturn("C2000");

            when(repo.save(any(CustomerModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            RegistrationService reg = new RegistrationService(repo);

            var result = reg.register(
                    new RegistrationRequest("pass", "pass", "new@mail.com")
            );

            Assertions.assertTrue(result.success());
            Assertions.assertEquals("C2000", result.customer().getId());
        }

        @Test
        void testPasswordMismatchThrowsException() {
            CustomerRepository repo = mock(CustomerRepository.class);

            RegistrationService reg = new RegistrationService(repo);

            Assertions.assertThrows(
                    ValidationException.class,
                    () -> reg.register(new RegistrationRequest("abc", "xyz", "user@mail.com"))
            );
        }
    }
}
