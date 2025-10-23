package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class AuthorService {

    private final AuthorRepository authorRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public AuthorDto createAuthor(AuthorCommand command) {
        logger.debug("entered createAuthor");
        Author author = new Author(
                command.firstname(), command.lastname(),
                command.address().stream().map(Address::addressFromString).toList(),
                new EmailAddress(command.emailAddress()),command.penname()
        );
         authorRepository.save(author);
        return AuthorDto.authorDtoFromAuthor(author);
    }

    @Transactional
    public void deleteAuthor(String apiKey) {
        logger.debug("entered deleteAuthor");

        Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(apiKey))
               .orElseThrow(()->AuthorServiceException.noAuthorForApiKey(apiKey));
       authorRepository.delete(author);
    }

    @Transactional
    public AuthorDto updateAuthor(AuthorCommand command) {
        logger.debug("entered updateAuthor");

        Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> AuthorServiceException.noAuthorForApiKey(command.apiKey()));

        author.setPenname(command.penname());
        author.setFirstName(command.firstname());
        author.setLastName(command.lastname());
        author.setEmailAddress(new EmailAddress(command.emailAddress()));
        author.setAddress(command.address().stream().map(Address::addressFromString).toList());

        logger.debug("updated author: {}", author.getAuthorApiKey().apiKey());
        return AuthorDto.authorDtoFromAuthor(author);
    }


    public AuthorDto getAuthor(String apiKey) {
        logger.debug("entered getAuthor");
        return authorRepository.findProjectedAuthorByAuthorApiKey(apiKey)
                .orElseThrow(()->AuthorServiceException.noAuthorForApiKey(apiKey));
    }

    public List<AuthorDto> getAuthors() {
        logger.debug("entered getAuthors");
        return authorRepository.findAllProjected();
    }

    public AuthorDto getAuthorByPenname(String penname) {
        logger.debug("entered getAuthorByPenname");
        return authorRepository.findProjectedAuthorByPenname(penname)
                .orElseThrow(()-> AuthorServiceException.noAuthorForPenname(penname));
    }

    public AuthorDto getAuthorByEmailAddress(String emailAddress) {
        logger.debug("entered getAuthorByEmailAddress");
        return authorRepository.findProjectedAuthorByEmailAddress_Email(emailAddress)
                .orElseThrow(()-> AuthorServiceException.noAuthorForEmail(emailAddress));
    }


    public static class AuthorServiceException extends RuntimeException
    {
        public AuthorServiceException(String message)
        {
            super(message);
        }

        public static AuthorServiceException noAuthorForApiKey(String apiKey)
        {
            return new AuthorServiceException("Author with api key (%s) not existent".formatted(apiKey));
        }

        public static AuthorServiceException noAuthorForPenname(String penname)
        {
            return new AuthorServiceException("Author with penname (%s) not existent".formatted(penname));
        }

        public static AuthorServiceException noAuthorForEmail(String email)
        {
            return new AuthorServiceException("Author with email (%s) not existent".formatted(email));
        }
    }
}




