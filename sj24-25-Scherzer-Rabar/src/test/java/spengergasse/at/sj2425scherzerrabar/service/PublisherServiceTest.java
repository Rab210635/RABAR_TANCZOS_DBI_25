package spengergasse.at.sj2425scherzerrabar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.commands.PublisherCommand;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;
import spengergasse.at.sj2425scherzerrabar.persistence.PublisherRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PublisherServiceTest {
    private @Mock PublisherRepository publisherRepository;

    private PublisherService publisherService;

    @BeforeEach
    void setUp() {
        publisherService = new PublisherService(publisherRepository);
    }


    @Test
    void can_create_publisher() {
        // Arrange
        Address address = FixturesFactory.address2();
        PublisherCommand command = new PublisherCommand(new ApiKey("apiKey").apiKey(), "New Publisher", address.toString());
        when(publisherRepository.save(any(Publisher.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        PublisherDto publisherDto = publisherService.createPublisher(command);

        // Assert
        assertThat(publisherDto).isNotNull();
        assertThat(publisherDto.name()).isEqualTo("New Publisher");
    }

    @Test
    void cant_delete_non_existing_publisher() {
        // Arrange
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> publisherService.deletePublisherByApiKey("invalidApiKey"))
                .isInstanceOf(PublisherService.PublisherServiceException.class)
                .hasMessageContaining("Publisher with api key (invalidApiKey) not existent");
    }

    @Test
    void can_delete_existing_publisher() {
        // Arrange
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));

        // Act
        publisherService.deletePublisherByApiKey("validApiKey");

        // Assert
        verify(publisherRepository, times(1)).delete(publisher);
    }

    @Test
    void cant_update_non_existing_publisher() {
        // Arrange
        Address address = FixturesFactory.address2();
        PublisherCommand command = new PublisherCommand("apiKey", "Updated Publisher", address.toString());
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> publisherService.updatePublisher(command))
                .isInstanceOf(PublisherService.PublisherServiceException.class)
                .hasMessageContaining("Publisher with api key (apiKey) not existent");
    }

    @Test
    void can_update_existing_publisher() {
        // Arrange
        Address address = FixturesFactory.address2();
        Publisher publisher = FixturesFactory.publisher(address);
        Address newAddress = new Address("New Street", "New City", 5432);
        PublisherCommand command = new PublisherCommand(new ApiKey("apiKey").apiKey(), "Updated Publisher", new Address("New Street", "New City", 5432).toString());
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(publisherRepository.save(any(Publisher.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        PublisherDto updatedPublisher = publisherService.updatePublisher(command);

        // Assert
        assertThat(updatedPublisher).isNotNull();
        assertThat(updatedPublisher.name()).isEqualTo("Updated Publisher");
        assertThat(updatedPublisher.address()).isEqualTo(newAddress.toString());
    }

    @Test
    void can_get_publisher_by_api_key() {
        // Arrange
        PublisherDto publisher = PublisherDto.publisherDtoFromPublisher(FixturesFactory.publisher(FixturesFactory.address2())) ;
        when(publisherRepository.findProjectedByPublisherApiKey(any())).thenReturn(Optional.of(publisher));

        // Act
        PublisherDto publisherDto = publisherService.getPublisherByApiKey("validApiKey");

        // Assert
        assertThat(publisherDto).isNotNull();
        assertThat(publisherDto.name()).isEqualTo("Dornbund");
    }

    @Test
    void cant_get_non_existing_publisher() {
        // Arrange
        when(publisherRepository.findProjectedByPublisherApiKey(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> publisherService.getPublisherByApiKey("invalidApiKey"))
                .isInstanceOf(PublisherService.PublisherServiceException.class)
                .hasMessageContaining("Publisher with api key (invalidApiKey) not existent");
    }

    @Test
    void can_get_all_publishers() {
        // Arrange
        PublisherDto publisher1 = PublisherDto.publisherDtoFromPublisher(FixturesFactory.publisher(FixturesFactory.address2()));
        when(publisherRepository.findAllProjected()).thenReturn(List.of(publisher1, publisher1));

        // Act
        List<PublisherDto> publishers = publisherService.getAllPublishers();

        // Assert
        assertThat(publishers).hasSize(2);
    }

    @Test
    void can_get_publisher_by_name(){
        PublisherDto publisher = PublisherDto.publisherDtoFromPublisher(FixturesFactory.publisher(FixturesFactory.address2()));
        when(publisherRepository.findProjectedByName(any())).thenReturn(Optional.of(publisher));
        PublisherDto publisherDto = publisherService.getPublisherByName("Dornbund");
        assertThat(publisherDto).isNotNull();
        assertThat(publisherDto.name()).isEqualTo("Dornbund");
    }
}
