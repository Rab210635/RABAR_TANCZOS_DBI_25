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
import spengergasse.at.sj2425scherzerrabar.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BorrowingRepositoryTest {
    @Autowired
    private BorrowingRepository repository;

    private static Borrowing borrowing;
    private boolean isInitialized = false;


    @BeforeEach
    void setUp() {
        if(!isInitialized) {
            var adresse = new Address("spengergasse 20","Vienna",1010);
            var email = FixturesFactory.emailAddress();
            var author = FixturesFactory.author();
            author.setPenname("Aron");
            author.setEmailAddress(new EmailAddress("aron@gmail.com"));
            var book = FixturesFactory.book(author);
            var publisher = FixturesFactory.publisher(FixturesFactory.address2());
            var customer = new Customer("Max","Mustermann", email,List.of(adresse));
            var branch = FixturesFactory.filiale();
            var copy = new Copy(publisher,BookType.PAPERBACK,244,book,branch);
            borrowing = new Borrowing(customer,List.of(copy),LocalDate.of(2024,5,5),25);

            repository.saveAndFlush(borrowing);
            isInitialized = true;
        }
    }


    @Test
    void can_save(){
        assertNotNull(borrowing);
    }

    @Test
    void default_constr(){
        Borrowing defaultconstructed = new Borrowing();
        assertNotNull(defaultconstructed);
    }

    @Test
    void can_find_all_projected() {
        var found = repository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(borrowing.getBorrowingApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_borrowing_api_key() {
        var found = repository.findProjectedBorrowingByBorrowingApiKey(borrowing.getBorrowingApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().fromDate()).isEqualTo(borrowing.getFromDate());
        softly.assertAll();
    }

    @Test
    void can_not_get_projection_with_not_existing_api_key(){
        assertThat(repository.findProjectedBorrowingByBorrowingApiKey("ApiKey not existing")).isNotPresent();
    }

    @Test
    void can_find_projected_borrowing_by_customer() {
        var found = repository.findProjectedBorrowingsByCustomerByCustomer(borrowing.getCustomer().getCustomerApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(borrowing.getBorrowingApiKey().apiKey()));
        softly.assertThat(found).anyMatch(dto -> dto.customerApiKey().equals(borrowing.getCustomer().getCustomerApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_not_find_projected_by_contains_not_existing_customer() {
        var found = repository.findProjectedBorrowingsByCustomerByCustomer("Customer not existing");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isEmpty();
        softly.assertAll();
    }

    @Test
    void can_find_projected_borrowing_by_contains_copy() {
        var found = repository.findProjectedBorrowingsByCopiesContains(borrowing.getCopies().getFirst().getCopyApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(borrowing.getBorrowingApiKey().apiKey()));
        softly.assertThat(found).anyMatch(dto -> dto.copyApiKeys().getFirst().equals(borrowing.getCopies().getFirst().getCopyApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_not_find_projected_by_contains_not_existing_copy() {
        var found = repository.findProjectedBorrowingsByCopiesContains("Copy not existing");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isEmpty();
        softly.assertAll();
    }
}