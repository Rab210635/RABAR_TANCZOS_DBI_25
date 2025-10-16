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
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;
import spengergasse.at.sj2425scherzerrabar.domain.Order;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class
OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    private Order order;
    private boolean isInitialized = false;

    @BeforeEach
    void setUp() {
        if (!isInitialized) {
            var author = FixturesFactory.author();
            author.setPenname("Heidi");
            author.setEmailAddress(new EmailAddress("Heidi@gmail.com"));
            var buyableBook = FixturesFactory.buyableBook(author);
            order = FixturesFactory.order(buyableBook);
            repository.saveAndFlush(order);
            isInitialized = true;
        }
    }

    @Test
    void can_save() {
        assertNotNull(order.getOrderApiKey());
    }

    @Test
    void default_constr() {
        Order defaultConstructed = new Order();
        assertNotNull(defaultConstructed);
    }

    @Test
    void can_find_projected_by_orderApiKey() {
        var found = repository.findProjectedByOrderApiKey(order.getOrderApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().apiKey()).isEqualTo(order.getOrderApiKey().apiKey());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        var found = repository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(order.getOrderApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_all_projected_by_customerApiKey() {
        var found = repository.findAllProjectedByCustomerApiKey(order.getCustomer().getCustomerApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.customerApiKey().equals(order.getCustomer().getCustomerApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_all_projected_by_date() {
        var found = repository.findAllProjectedByDate(order.getDate());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.date().equals(order.getDate()));
        softly.assertAll();
    }

    @Test
    void cannot_find_projected_by_invalid_orderApiKey() {
        var found = repository.findProjectedByOrderApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_all_projected_by_invalid_customerApiKey() {
        var found = repository.findAllProjectedByCustomerApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_all_projected_by_invalid_date() {
        var found = repository.findAllProjectedByDate(LocalDate.of(1990, 1, 1));

        assertThat(found).isEmpty();
    }
}
