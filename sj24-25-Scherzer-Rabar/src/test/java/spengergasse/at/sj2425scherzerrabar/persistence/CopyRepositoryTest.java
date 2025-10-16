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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CopyRepositoryTest {
    @Autowired
    private CopyRepository repository;

    private Copy copy;
    private boolean isInitialized = false;

    @BeforeEach
    void setUp() {
        if (!isInitialized) {
            var author = FixturesFactory.author();
            author.setPenname("aronns");
            author.setEmailAddress(new EmailAddress("aarronsn@gmail.com"));
            copy = FixturesFactory.copy(author);
            repository.saveAndFlush(copy);
            isInitialized = true;
        }
    }

    @Test
    void can_save(){
        assertNotNull(copy.getCopyApiKey());
    }

    @Test
    void default_constr(){
        Copy defaultconstructed = new Copy();
        assertNotNull(defaultconstructed);
    }

    @Test
    void can_find_projected_by_copyApiKey() {
        var found = repository.findProjectedByCopyApiKey(copy.getCopyApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().bookApiKey()).isEqualTo(copy.getBook().getBookApiKey().apiKey());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        var found = repository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(copy.getCopyApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_all_projected_by_bookApiKey() {
        var found = repository.findAllProjectedByBook_BookApiKey(copy.getBook().getBookApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.bookApiKey().equals(copy.getBook().getBookApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_all_projected_by_publisherApiKey() {
        var found = repository.findAllProjectedByPublisher_PublisherApiKey(copy.getPublisher().getPublisherApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.publisherApiKey().equals(copy.getPublisher().getPublisherApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_all_projected_by_bookType() {
        var found = repository.findAllProjectedByBookType(copy.getBookType());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.bookType().equals(copy.getBookType().name()));
        softly.assertAll();
    }

    @Test
    void can_find_all_projected_by_branchApiKey() {
        var found = repository.findAllProjectedByInBranch_BranchApiKey(copy.getInBranch().getBranchApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.branchApiKey().equals(copy.getInBranch().getBranchApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void cannot_find_projected_by_invalid_copyApiKey() {
        var found = repository.findProjectedByCopyApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_all_projected_by_invalid_bookApiKey() {
        var found = repository.findAllProjectedByBook_BookApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_all_projected_by_invalid_publisherApiKey() {
        var found = repository.findAllProjectedByPublisher_PublisherApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_all_projected_by_invalid_branchApiKey() {
        var found = repository.findAllProjectedByInBranch_BranchApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }
}