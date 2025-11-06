package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand2;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;
import spengergasse.at.sj2425scherzerrabar.mapper.BookMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMongoRepository mongoRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper mapper;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BookService(BookRepository bookRepository,
                       BookMongoRepository mongoRepository,
                       AuthorRepository authorRepository,
                       BookMapper mapper) {
        this.bookRepository = bookRepository;
        this.mongoRepository = mongoRepository;
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    @Transactional
    public BookDto createBook(BookCommand command) {
        logger.debug("entered createBook");
        List<Author> authors = command.authorIds().stream()
                .map(ApiKey::new)
                .map(authorRepository::findAuthorByAuthorApiKey)
                .flatMap(Optional::stream)
                .toList();
        if (authors.isEmpty()) throw BookServiceException.noAuthors();

        Book book = new Book(
                command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                command.genre().stream().map(BookGenre::valueOf).toList(),
                authors, command.types().stream().map(BookType::valueOf).toList(),
                command.description()
        );

        Book savedBook = bookRepository.save(book);

        try {
            BookDocument doc = mapper.toMongoDocument(savedBook);
            mongoRepository.save(doc);
            logger.debug("Book also saved to MongoDB: {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    @Transactional
    public BookDto createBook2(BookCommand2 command) {
        logger.debug("entered createBook2");
        List<Author> authors = command.authorPennames().stream()
                .map(authorRepository::findAuthorsByPenname)
                .flatMap(Optional::stream)
                .toList();
        if (authors.isEmpty()) throw BookServiceException.noAuthors();

        Book book = new Book(
                command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                command.genre().stream().map(BookGenre::valueOf).toList(),
                authors, command.types().stream().map(BookType::valueOf).toList(),
                command.description()
        );

        Book savedBook = bookRepository.save(book);

        try {
            BookDocument doc = mapper.toMongoDocument(savedBook);
            mongoRepository.save(doc);
            logger.debug("Book also saved to MongoDB: {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    @Transactional
    public void deleteBook(String apiKey) {
        logger.debug("entered deleteBook");
        Book book = bookRepository.findBookByBookApiKey(new ApiKey(apiKey))
                .orElseThrow(() -> BookServiceException.noBookForApiKey(apiKey));
        bookRepository.delete(book);

        try {
            mongoRepository.findByApiKey(apiKey)
                    .ifPresent(doc -> mongoRepository.deleteById(doc.getId()));
            logger.debug("Book also deleted from MongoDB: {}", apiKey);
        } catch (Exception e) {
            logger.error("Failed to delete book from MongoDB", e);
        }
    }

    @Transactional
    public BookDto updateBook(BookCommand command) {
        logger.debug("entered updateBook");

        Book book = bookRepository.findBookByBookApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> BookServiceException.noBookForApiKey(command.apiKey()));

        List<Author> authors = command.authorIds().stream()
                .map(ApiKey::new)
                .map(authorRepository::findAuthorByAuthorApiKey)
                .flatMap(Optional::stream)
                .toList();
        if (authors.isEmpty()) throw BookServiceException.noAuthors();

        book.setName(command.name());
        book.setAvailableOnline(command.availableOnline());
        book.setDescription(command.description());
        book.setReleaseDate(command.releaseDate());
        book.setGenres(command.genre().stream().map(BookGenre::valueOf).toList());
        book.setBookTypes(command.types().stream().map(BookType::valueOf).toList());
        book.setAuthors(authors);

        Book savedBook = bookRepository.save(book);

        try {
            mongoRepository.findByApiKey(command.apiKey()).ifPresent(doc -> {
                doc.setName(command.name());
                doc.setAvailableOnline(command.availableOnline());
                doc.setDescription(command.description());
                doc.setReleaseDate(command.releaseDate());
                doc.setGenres(command.genre());
                doc.setBookTypes(command.types());
                doc.setAuthorApiKeys(authors.stream()
                        .map(a -> a.getAuthorApiKey().apiKey())
                        .toList());
                mongoRepository.save(doc);
            });
            logger.debug("Book also updated in MongoDB: {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update book in MongoDB", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    @Transactional
    public BookDto updateBook2(BookCommand2 command) {
        logger.debug("entered updateBook2");

        Book book = bookRepository.findBookByBookApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> BookServiceException.noBookForApiKey(command.apiKey()));

        List<Author> authors = command.authorPennames().stream()
                .map(authorRepository::findAuthorsByPenname)
                .flatMap(Optional::stream)
                .toList();
        if (authors.isEmpty()) throw BookServiceException.noAuthors();

        book.setName(command.name());
        book.setAvailableOnline(command.availableOnline());
        book.setDescription(command.description());
        book.setReleaseDate(command.releaseDate());
        book.setGenres(command.genre().stream().map(BookGenre::valueOf).toList());
        book.setBookTypes(command.types().stream().map(BookType::valueOf).toList());
        book.setAuthors(authors);

        Book savedBook = bookRepository.save(book);

        try {
            mongoRepository.findByApiKey(command.apiKey()).ifPresent(doc -> {
                doc.setName(command.name());
                doc.setAvailableOnline(command.availableOnline());
                doc.setDescription(command.description());
                doc.setReleaseDate(command.releaseDate());
                doc.setGenres(command.genre());
                doc.setBookTypes(command.types());
                doc.setAuthorApiKeys(authors.stream()
                        .map(a -> a.getAuthorApiKey().apiKey())
                        .toList());
                mongoRepository.save(doc);
            });
            logger.debug("Book also updated in MongoDB: {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update book in MongoDB", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    public BookDto getBook(String apiKey) {
        logger.debug("entered getBook");
        return bookRepository.findProjectedBookByBookApiKey(apiKey)
                .orElseThrow(() -> BookServiceException.noBookForApiKey(apiKey));
    }

    public BookDto2 getBook2(String apiKey) {
        logger.debug("entered getBook2");
        return bookRepository.findProjectedBookByBookApiKey2(apiKey)
                .orElseThrow(() -> BookServiceException.noBookForApiKey(apiKey));
    }

    public List<BookDto> getBooks(String authorApiKey) {
        logger.debug("entered getBooks");
        if (authorApiKey == null) return bookRepository.findAllProjected();
        var author = authorRepository.findProjectedAuthorByAuthorApiKey(authorApiKey)
                .orElseThrow(() -> BookServiceException.noAuthorForApikey(authorApiKey));
        return bookRepository.findProjectedBooksByAuthorsContains(author.apiKey());
    }

    public List<BookDto2> getBooks2() {
        logger.debug("entered getBooks2");
        return bookRepository.findAllProjected2();
    }

    @Transactional
    public void syncAllToMongo() {
        logger.info("Starting sync from PostgreSQL to MongoDB");
        List<Book> allBooks = bookRepository.findAll();
        int synced = 0;
        for (Book book : allBooks) {
            try {
                BookDocument doc = mapper.toMongoDocument(book);
                mongoRepository.save(doc);
                synced++;
            } catch (Exception e) {
                logger.error("Failed to sync book: {}", book.getBookApiKey().apiKey(), e);
            }
        }
        logger.info("Synced {} of {} books to MongoDB", synced, allBooks.size());
    }

    public static class BookServiceException extends RuntimeException {
        public BookServiceException(String message) {
            super(message);
        }

        public static BookServiceException noBookForApiKey(String apiKey) {
            return new BookServiceException("Book with api key (%s) not existent".formatted(apiKey));
        }

        public static BookServiceException noAuthors() {
            return new BookServiceException("No Authors for Book");
        }

        public static BookServiceException noAuthorForApikey(String apiKey) {
            return new BookServiceException("Author with api key (%s) not existent".formatted(apiKey));
        }
    }
}
