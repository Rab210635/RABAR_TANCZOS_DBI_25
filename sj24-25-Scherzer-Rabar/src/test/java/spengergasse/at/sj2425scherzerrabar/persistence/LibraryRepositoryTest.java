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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibraryRepositoryTest {

    @Autowired
    private LibraryRepository libraryRepository;

    private Library library;
    private boolean isInitialized = false;

    @BeforeEach
    void setUp() {
        if (!isInitialized) {
            var address = FixturesFactory.libraryAddress();
            var books = List.of(FixturesFactory.libBook(FixturesFactory.book(new Author("Thomas", "Fresh",List.of(FixturesFactory.address2()),new EmailAddress("dasd@email.com"),"coolerstiftname"))));
            library = FixturesFactory.thalia(address, books);
            libraryRepository.saveAndFlush(library);
            isInitialized = true;
        }
    }

    @Test
    void can_save() {
        assertNotNull(library.getLibraryApiKey());
    }

    @Test
    void default_constr() {
        Library defaultConstructed = new Library();
        assertNotNull(defaultConstructed);
    }

    @Test
    void can_find_projected_by_libraryApiKey() {
        var found = libraryRepository.findProjectedByLibraryApiKey(library.getLibraryApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().apiKey()).isEqualTo(library.getLibraryApiKey().apiKey());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        var found = libraryRepository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(library.getLibraryApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_name() {
        var found = libraryRepository.findProjectedByName(library.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().name()).isEqualTo(library.getName());
        softly.assertAll();
    }

    @Test
    void cannot_find_projected_by_invalid_libraryApiKey() {
        var found = libraryRepository.findProjectedByLibraryApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_projected_by_invalid_name() {
        var found = libraryRepository.findProjectedByName("Invalid Library Name");

        assertThat(found).isEmpty();
    }
}
