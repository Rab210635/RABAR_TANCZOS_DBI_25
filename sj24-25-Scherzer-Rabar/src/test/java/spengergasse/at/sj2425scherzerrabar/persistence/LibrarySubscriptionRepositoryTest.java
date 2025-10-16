package spengergasse.at.sj2425scherzerrabar.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.TestcontainersConfiguration;
import spengergasse.at.sj2425scherzerrabar.domain.BookInLibraries;
import spengergasse.at.sj2425scherzerrabar.domain.LibrarySubscription;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibrarySubscriptionRepositoryTest {

    @Autowired
    private LibrarySubscriptionRepository librarySubscriptionRepository;
    private static LibrarySubscription librarySubscription;

    @BeforeAll
    static void setUp() {
        var libraryAddress = FixturesFactory.libraryAddress();
        var books = List.of(new BookInLibraries());
        var library = FixturesFactory.thalia(libraryAddress,books);
         librarySubscription = FixturesFactory.thaliaAll(library);    }

    @Test
    void can_save() {
        var savedLibrarySubscription = librarySubscriptionRepository.saveAndFlush(librarySubscription);

        assertNotNull(savedLibrarySubscription);
    }

    @Test
    void default_constr(){
        LibrarySubscription defaultconstructed = new LibrarySubscription();
        assertNotNull(defaultconstructed);
    }
}