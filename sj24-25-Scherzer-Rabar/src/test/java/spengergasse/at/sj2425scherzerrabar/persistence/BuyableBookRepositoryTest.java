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
import spengergasse.at.sj2425scherzerrabar.domain.BuyableBook;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BuyableBookRepositoryTest {

    @Autowired
    private BuyableBookRepository repository;

    private BuyableBook buyableBook;
    private boolean isInitialized = false;

    @BeforeEach
    void setUp() {
        if (!isInitialized) {
            buyableBook = FixturesFactory.buyableBook();
            repository.saveAndFlush(buyableBook);
            isInitialized = true;
        }
    }

    @Test
    void can_save() {
        assertNotNull(buyableBook.getBuyableBookApiKey());
    }

    @Test
    void default_constr() {
        BuyableBook defaultConstructed = new BuyableBook();
        assertNotNull(defaultConstructed);
    }

    @Test
    void can_find_projected_by_buyableBookApiKey() {
        var found = repository.findProjectedBuyableBookByBuyableBookApiKey(buyableBook.getBuyableBookApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().buyableBookApiKey()).isEqualTo(buyableBook.getBuyableBookApiKey().apiKey());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        List<BuyableBookDto> found = repository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.buyableBookApiKey().equals(buyableBook.getBuyableBookApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_publisher() {
        var found = repository.findProjectedByPublisher(buyableBook.getPublisher().getPublisherApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.publisherApiKey().equals(buyableBook.getPublisher().getPublisherApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_bookType() {
        var found = repository.findProjectedByBookType(buyableBook.getBookType());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.bookType().equals(buyableBook.getBookType().name()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_price() {
        var found = repository.findProjectedByPrice(buyableBook.getPrice());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.price().equals(buyableBook.getPrice()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_book() {
        var found = repository.findProjectedByBook(buyableBook.getBook().getBookApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.bookApiKey().equals(buyableBook.getBook().getBookApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void cannot_find_projected_by_invalid_buyableBookApiKey() {
        var found = repository.findProjectedBuyableBookByBuyableBookApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_projected_by_invalid_publisher() {
        var found = repository.findProjectedByPublisher("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_projected_by_invalid_bookType() {
        var found = repository.findProjectedByBookType(BookType.valueOf("PAPERBACK"));

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_projected_by_invalid_price() {
        var found = repository.findProjectedByPrice(-1F);

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_projected_by_invalid_book() {
        var found = repository.findProjectedByBook("invalid-api-key");

        assertThat(found).isEmpty();
    }
}
