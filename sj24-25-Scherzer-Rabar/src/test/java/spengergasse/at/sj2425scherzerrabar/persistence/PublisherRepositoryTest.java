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
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;
    private Publisher publisher;
    private boolean isInitialized = false;

    @BeforeEach
     void setUp() {
        if (!isInitialized) {
            var address = FixturesFactory.address2();
            publisher = FixturesFactory.publisher(address);
            publisherRepository.saveAndFlush(publisher);
            isInitialized = true;
        }

    }


    @Test
    void can_save() {
        assertNotNull(publisher);
    }
    @Test
    void default_constr(){
        Publisher defaultconstructed = new Publisher();
        assertNotNull(defaultconstructed);
    }

    @Test
    void can_find_projected_by_publisherApiKey() {
        var found = publisherRepository.findProjectedByPublisherApiKey(publisher.getPublisherApiKey().apiKey());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().address()).isEqualTo(publisher.getAddress().toString());
        softly.assertAll();
    }

    @Test
    void can_find_all_projected() {
        var found = publisherRepository.findAllProjected();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isNotEmpty();
        softly.assertThat(found).anyMatch(dto -> dto.apiKey().equals(publisher.getPublisherApiKey().apiKey()));
        softly.assertAll();
    }

    @Test
    void can_find_projected_by_publisherName() {
        var found = publisherRepository.findProjectedByName(publisher.getName());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(found).isPresent();
        softly.assertThat(found.get().address()).isEqualTo(publisher.getAddress().toString());
        softly.assertAll();
    }


    @Test
    void can_not_get_projection_with_not_existing_api_key() {
        assertThat(publisherRepository.findProjectedByPublisherApiKey("ApiKey not existent")).isNotPresent();
    }

    @Test
    void can_not_get_projection_with_not_existing_name(){
        assertThat(publisherRepository.findProjectedByName("Name not existent")).isNotPresent();
    }

}