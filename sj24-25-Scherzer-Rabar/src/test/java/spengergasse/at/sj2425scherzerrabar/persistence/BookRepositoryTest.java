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
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookRepositoryTest  {
    @Autowired
    private BookRepository bookRepository;
    private static Book book;
    private boolean isInitialized = false;
    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
     void setUp() {
        if (!isInitialized) {
            book = FixturesFactory.book(FixturesFactory.author());
            bookRepository.saveAndFlush(book);
            isInitialized = true;
        }
    }


    @Test
    public void can_save() {
        assertNotNull(book);
    }
    @Test
    void default_constr(){
        Book defaultconstructed = new Book();
        assertNotNull(defaultconstructed);
    }

    @Test
    void can_find_all_projected() {
        var found = bookRepository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(book.getBookApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_book_api_key() {
        var found = bookRepository.findProjectedBookByBookApiKey(book.getBookApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().name()).isEqualTo(book.getName());
        softly.assertAll();
    }

    @Test
    void can_not_get_projection_with_not_existing_api_key(){
        assertThat(bookRepository.findProjectedBookByBookApiKey("ApiKey not existing")).isNotPresent();
    }

    @Test
    void can_find_projected_by_contains_author() {
        var found = bookRepository.findProjectedBooksByAuthorsContains(book.getAuthors().getFirst().getAuthorApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(book.getBookApiKey().apiKey()));
        softly.assertThat(found).anyMatch(dto -> dto.authorIds().getFirst().equals(book.getAuthors().getFirst().getAuthorApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_not_find_projected_by_contains_not_existing_author() {
        var found = bookRepository.findProjectedBooksByAuthorsContains("ApiKey not existing");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isEmpty();
        softly.assertAll();
    }

}