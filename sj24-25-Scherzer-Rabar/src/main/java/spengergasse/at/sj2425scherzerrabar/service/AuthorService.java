package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.mapper.AuthorMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly=true)
public class AuthorService {

    private final AuthorRepository authorRepository;  // JPA Repository
    private final AuthorMongoRepository mongoRepository;  // MongoDB Repository
    private final AuthorMapper mapper;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public AuthorService(AuthorRepository authorRepository,
                         AuthorMongoRepository mongoRepository,
                         AuthorMapper mapper) {
        this.authorRepository = authorRepository;
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    // ==================== CREATE METHODS ====================

    /**
     * Erstellt Author NUR in JPA/PostgreSQL
     */
    @Transactional
    public AuthorDto createAuthorJpaOnly(AuthorCommand command) {
        logger.debug("entered createAuthorJpaOnly");

        Author author = new Author(
                command.firstname(), command.lastname(),
                command.address().stream().map(Address::addressFromString).toList(),
                new EmailAddress(command.emailAddress()), command.penname()
        );

        Author savedAuthor = authorRepository.save(author);
        logger.debug("Author saved to PostgreSQL only: {}", savedAuthor.getAuthorApiKey().apiKey());

        return AuthorDto.authorDtoFromAuthor(savedAuthor);
    }

    /**
     * Erstellt Author in JPA + MongoDB
     */
    @Transactional
    public AuthorDto createAuthorWithMongo(AuthorCommand command) {
        logger.debug("entered createAuthorWithMongo");

        Author author = new Author(
                command.firstname(), command.lastname(),
                command.address().stream().map(Address::addressFromString).toList(),
                new EmailAddress(command.emailAddress()), command.penname()
        );

        Author savedAuthor = authorRepository.save(author);

        try {
            AuthorDocument mongoDoc = mapper.toMongoDocument(savedAuthor);
            mongoRepository.save(mongoDoc);
            logger.debug("Author saved to JPA + MongoDB: {}", savedAuthor.getAuthorApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save author to MongoDB", e);
        }

        return AuthorDto.authorDtoFromAuthor(savedAuthor);
    }

    /**
     * Erstellt Author ÜBERALL (JPA + MongoDB)
     * STANDARD-METHODE für Controller
     */
    @Transactional
    public AuthorDto createAuthor(AuthorCommand command) {
        logger.debug("entered createAuthor (ALL)");

        // 1. Domain-Objekt erstellen
        Author author = new Author(
                command.firstname(), command.lastname(),
                command.address().stream().map(Address::addressFromString).toList(),
                new EmailAddress(command.emailAddress()), command.penname()
        );

        // 2. In PostgreSQL speichern (Primary Database)
        Author savedAuthor = authorRepository.save(author);

        // 3. In MongoDB speichern (Secondary Database)
        try {
            AuthorDocument mongoDoc = mapper.toMongoDocument(savedAuthor);
            mongoRepository.save(mongoDoc);
            logger.debug("Author also saved to MongoDB: {}", savedAuthor.getAuthorApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save author to MongoDB", e);
            // PostgreSQL bleibt als Primary - MongoDB-Fehler werden geloggt aber nicht geworfen
        }

        return AuthorDto.authorDtoFromAuthor(savedAuthor);
    }

    // ==================== DELETE METHODS ====================

    /**
     * Löscht Author ÜBERALL (JPA + MongoDB)
     * STANDARD-METHODE für Controller
     */
    @Transactional
    public void deleteAuthor(String apiKey) {
        logger.debug("entered deleteAuthor (ALL)");

        // 1. Aus PostgreSQL löschen
        Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(apiKey))
                .orElseThrow(() -> AuthorServiceException.noAuthorForApiKey(apiKey));
        authorRepository.delete(author);

        // 2. Aus MongoDB löschen
        try {
            mongoRepository.findByApiKey(apiKey)
                    .ifPresent(doc -> mongoRepository.deleteById(doc.getId()));
            logger.debug("Author also deleted from MongoDB: {}", apiKey);
        } catch (Exception e) {
            logger.error("Failed to delete author from MongoDB", e);
        }
    }

    // ==================== UPDATE METHODS ====================

    /**
     * Update Author NUR in JPA/PostgreSQL
     */
    @Transactional
    public AuthorDto updateAuthorJpaOnly(AuthorCommand command) {
        logger.debug("entered updateAuthorJpaOnly");

        Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> AuthorServiceException.noAuthorForApiKey(command.apiKey()));

        author.setPenname(command.penname());
        author.setFirstName(command.firstname());
        author.setLastName(command.lastname());
        author.setEmailAddress(new EmailAddress(command.emailAddress()));
        author.setAddress(new ArrayList<>(command.address().stream().map(Address::addressFromString).toList()));

        Author savedAuthor = authorRepository.save(author);
        logger.debug("Author updated in PostgreSQL only: {}", savedAuthor.getAuthorApiKey().apiKey());

        return AuthorDto.authorDtoFromAuthor(savedAuthor);
    }

    /**
     * Update Author in JPA + MongoDB
     */
    @Transactional
    public AuthorDto updateAuthorWithMongo(AuthorCommand command) {
        logger.debug("entered updateAuthorWithMongo");

        Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> AuthorServiceException.noAuthorForApiKey(command.apiKey()));

        author.setPenname(command.penname());
        author.setFirstName(command.firstname());
        author.setLastName(command.lastname());
        author.setEmailAddress(new EmailAddress(command.emailAddress()));
        author.setAddress(new ArrayList<>(command.address().stream().map(Address::addressFromString).toList()));

        Author savedAuthor = authorRepository.save(author);

        try {
            mongoRepository.findByApiKey(command.apiKey())
                    .ifPresent(doc -> {
                        doc.setFirstName(command.firstname());
                        doc.setLastName(command.lastname());
                        doc.setPenname(command.penname());
                        doc.setEmail(command.emailAddress());
                        doc.setAddresses(
                                command.address().stream()
                                        .map(Address::addressFromString)
                                        .map(AuthorDocument.AddressMongo::fromAddress)
                                        .toList()
                        );
                        mongoRepository.save(doc);
                    });
            logger.debug("Author updated in JPA + MongoDB: {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update author in MongoDB", e);
        }

        return AuthorDto.authorDtoFromAuthor(savedAuthor);
    }

    /**
     * Update Author ÜBERALL (JPA + MongoDB)
     * STANDARD-METHODE für Controller
     */
    @Transactional
    public AuthorDto updateAuthor(AuthorCommand command) {
        logger.debug("entered updateAuthor (ALL)");

        // 1. In PostgreSQL aktualisieren
        Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> AuthorServiceException.noAuthorForApiKey(command.apiKey()));

        author.setPenname(command.penname());
        author.setFirstName(command.firstname());
        author.setLastName(command.lastname());
        author.setEmailAddress(new EmailAddress(command.emailAddress()));
        author.setAddress(new ArrayList<>(command.address().stream().map(Address::addressFromString).toList()));

        Author savedAuthor = authorRepository.save(author);

        // 2. In MongoDB aktualisieren
        try {
            mongoRepository.findByApiKey(command.apiKey())
                    .ifPresent(doc -> {
                        doc.setFirstName(command.firstname());
                        doc.setLastName(command.lastname());
                        doc.setPenname(command.penname());
                        doc.setEmail(command.emailAddress());
                        doc.setAddresses(
                                command.address().stream()
                                        .map(Address::addressFromString)
                                        .map(AuthorDocument.AddressMongo::fromAddress)
                                        .toList()
                        );
                        mongoRepository.save(doc);
                    });
            logger.debug("Author also updated in MongoDB: {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update author in MongoDB", e);
        }

        logger.debug("updated author: {}", author.getAuthorApiKey().apiKey());
        return AuthorDto.authorDtoFromAuthor(savedAuthor);
    }

    // ==================== READ METHODS ====================

    public AuthorDto getAuthor(String apiKey) {
        logger.debug("entered getAuthor");
        // Lesen aus PostgreSQL (Primary Database)
        return authorRepository.findProjectedAuthorByAuthorApiKey(apiKey)
                .orElseThrow(() -> AuthorServiceException.noAuthorForApiKey(apiKey));
    }

    public List<AuthorDto> getAuthors() {
        logger.debug("entered getAuthors");
        // Lesen aus PostgreSQL (Primary Database)
        return authorRepository.findAllProjected();
    }

    public AuthorDto getAuthorByPenname(String penname) {
        logger.debug("entered getAuthorByPenname");
        return authorRepository.findProjectedAuthorByPenname(penname)
                .orElseThrow(() -> AuthorServiceException.noAuthorForPenname(penname));
    }

    public AuthorDto getAuthorByEmailAddress(String emailAddress) {
        logger.debug("entered getAuthorByEmailAddress");
        return authorRepository.findProjectedAuthorByEmailAddress_Email(emailAddress)
                .orElseThrow(() -> AuthorServiceException.noAuthorForEmail(emailAddress));
    }

    // ==================== SYNC METHODS ====================

    /**
     * Synchronisiert alle Autoren von PostgreSQL zu MongoDB
     * Nützlich bei Inkonsistenzen oder Initial-Setup
     */
    @Transactional
    public void syncAllToMongo() {
        logger.info("Starting sync from PostgreSQL to MongoDB");
        List<Author> allAuthors = authorRepository.findAll();

        int synced = 0;
        for (Author author : allAuthors) {
            try {
                AuthorDocument doc = mapper.toMongoDocument(author);
                mongoRepository.save(doc);
                synced++;
            } catch (Exception e) {
                logger.error("Failed to sync author: {}", author.getAuthorApiKey().apiKey(), e);
            }
        }

        logger.info("Synced {} of {} authors to MongoDB", synced, allAuthors.size());
    }

    // ==================== EXCEPTION CLASS ====================

    public static class AuthorServiceException extends RuntimeException {
        public AuthorServiceException(String message) {
            super(message);
        }

        public static AuthorServiceException noAuthorForApiKey(String apiKey) {
            return new AuthorServiceException("Author with api key (%s) not existent".formatted(apiKey));
        }

        public static AuthorServiceException noAuthorForPenname(String penname) {
            return new AuthorServiceException("Author with penname (%s) not existent".formatted(penname));
        }

        public static AuthorServiceException noAuthorForEmail(String email) {
            return new AuthorServiceException("Author with email (%s) not existent".formatted(email));
        }
    }
}