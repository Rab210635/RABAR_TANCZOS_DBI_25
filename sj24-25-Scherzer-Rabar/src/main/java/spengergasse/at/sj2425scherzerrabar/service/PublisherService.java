package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.PublisherCommand;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;
import spengergasse.at.sj2425scherzerrabar.persistence.PublisherRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublisherService {
    private final PublisherRepository publisherRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional
    public PublisherDto createPublisher(PublisherCommand command) {
        logger.debug("entered createPublisher");
        Publisher publisher = new Publisher(command.name(), Address.addressFromString(command.address()));
        publisher = publisherRepository.save(publisher);
        return PublisherDto.publisherDtoFromPublisher(publisher);
    }

    @Transactional
    public void deletePublisherByApiKey(String apiKey) {
        logger.debug("entered deletePublisherByApiKey");
        Publisher publisher = publisherRepository.findPublisherByPublisherApiKey(new ApiKey(apiKey))
                .orElseThrow(() -> PublisherServiceException.noPublisherForApiKey(apiKey));
        publisherRepository.delete(publisher);
    }

    @Transactional
    public PublisherDto updatePublisher(PublisherCommand command) {
        logger.debug("entered updatePublisher");
        Publisher publisher = publisherRepository.findPublisherByPublisherApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> PublisherServiceException.noPublisherForApiKey(command.apiKey()));

        publisher.setName(command.name());
        publisher.setAddress(Address.addressFromString(command.address()));
        publisher = publisherRepository.save(publisher);

        return PublisherDto.publisherDtoFromPublisher(publisher);
    }

    public List<PublisherDto> getAllPublishers() {
       logger.debug("entered getAllPublishers");
        return publisherRepository.findAllProjected();
    }

    public PublisherDto getPublisherByApiKey(String apiKey) {
        logger.debug("entered getPublisherByApiKey");
        return publisherRepository.findProjectedByPublisherApiKey(apiKey)
                .orElseThrow(() -> PublisherServiceException.noPublisherForApiKey(apiKey));
    }

    public PublisherDto getPublisherByName(String name) {
        logger.debug("entered getPublisherByName");
        return publisherRepository.findProjectedByName(name)
                .orElseThrow(() -> PublisherServiceException.noPublisherForName(name));
    }

    public static class PublisherServiceException extends RuntimeException
    {
        private PublisherServiceException(String message)
        {
            super(message);
        }

       public static PublisherServiceException noPublisherForApiKey(String apiKey)
        {
            return new PublisherServiceException("Publisher with api key (%s) not existent".formatted(apiKey));
        }

        static PublisherServiceException noPublisherForName(String name)
        {
            return new PublisherServiceException("Publisher with name (%s) not existent".formatted(name));
        }

    }
}
