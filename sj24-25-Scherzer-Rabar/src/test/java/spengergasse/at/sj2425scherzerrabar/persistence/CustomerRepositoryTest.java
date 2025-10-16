package spengergasse.at.sj2425scherzerrabar.persistence;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.TestcontainersConfiguration;
import spengergasse.at.sj2425scherzerrabar.domain.Customer;
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository repository;

    private Customer customer;
    private boolean isInitialized = false;

    @BeforeEach
    void setUp() {
        if (!isInitialized) {
            customer = FixturesFactory.customer();
            customer.setEmailAddress(new EmailAddress("testcustomer@gmail.com"));
            repository.saveAndFlush(customer);
            isInitialized = true;
        }
    }

    @Test
    void can_save() {

        assertNotNull(customer.getCustomerApiKey());
    }

    @Test
    void default_constr() {
        Customer defaultConstructed = new Customer();
        assertNotNull(defaultConstructed);
    }

    @Test
    void can_find_projected_by_customerApiKey() {
        var found = repository.findProjectedCustomerByCustomerApiKey(customer.getCustomerApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().apiKey()).isEqualTo(customer.getCustomerApiKey().apiKey());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        var found = repository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(customer.getCustomerApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_emailAddress() {
        var found = repository.findProjectedCustomerByEmailAddress_Email(customer.getEmailAddress().email());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().emailAddress()).isEqualTo(customer.getEmailAddress().email());
        softly.assertAll();
    }

    @Test
    void cannot_find_projected_by_invalid_customerApiKey() {
        var found = repository.findProjectedCustomerByCustomerApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_projected_by_invalid_emailAddress() {
        var found = repository.findProjectedCustomerByEmailAddress_Email("invalid-email@gmail.com");

        assertThat(found).isEmpty();
    }
}
