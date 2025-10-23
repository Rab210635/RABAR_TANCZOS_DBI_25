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
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    private static Author author;
    private boolean isInitialized = false;


    @BeforeEach
    void setUp() {
        if (!isInitialized) {
            author = FixturesFactory.author();
            repository.saveAndFlush(author);
            isInitialized = true;
        }
    }

    @Test
    void can_save(){
        assertNotNull(author);
    }

    @Test
    void default_constr(){

        Author defaultconstructed = new Author();
        assertNotNull(defaultconstructed);
    }

    @Test
    void can_find_projected_by_authorApiKey() {
        var found = repository.findProjectedAuthorByAuthorApiKey(author.getAuthorApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().address()).isEqualTo(author.getAddress().stream().map(Address::toString).toList());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        var found = repository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(author.getAuthorApiKey().apiKey()));
        softly.assertAll();
    }


    @Test
    void can_not_get_projection_with_not_existing_api_key(){
        assertThat(repository.findProjectedAuthorByAuthorApiKey("ApiKey not existing")).isNotPresent();
    }

    @Test
    void can_find_projected_by_author_penname() {
        var found = repository.findProjectedAuthorByPenname(author.getPenname());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().address()).isEqualTo(author.getAddress().stream().map(Address::toString).toList());
        softly.assertAll();
    }
    @Test
    void can_not_get_projection_with_not_existing_penname(){
        assertThat(repository.findProjectedAuthorByPenname("Penname not existing")).isNotPresent();
    }

    @Test
    void can_find_projected_by_author_email() {
        var found = repository.findProjectedAuthorByEmailAddress_Email(author.getEmailAddress().email());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().address()).isEqualTo(author.getAddress().stream().map(Address::toString).toList());
        softly.assertAll();
    }
    @Test
    void can_not_get_projection_with_not_existing_email(){
        assertThat(repository.findProjectedAuthorByEmailAddress_Email("Email not existing")).isNotPresent();
    }
}