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
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;
import spengergasse.at.sj2425scherzerrabar.mapper.BookEmbeddedMapper;
import spengergasse.at.sj2425scherzerrabar.mapper.BookMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookEmbeddedMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMongoRepository mongoRepository;
    private final BookEmbeddedMongoRepository embeddedMongoRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper mapper;
    private final BookEmbeddedMapper embeddedMapper;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BookService(BookRepository bookRepository,
                       BookMongoRepository mongoRepository,
                       BookEmbeddedMongoRepository embeddedMongoRepository,
                       AuthorRepository authorRepository,
                       BookMapper mapper,
                       BookEmbeddedMapper embeddedMapper) {
        this.bookRepository = bookRepository;
        this.mongoRepository = mongoRepository;
        this.embeddedMongoRepository = embeddedMongoRepository;
        this.authorRepository = authorRepository;
        this.mapper = mapper;
        this.embeddedMapper = embeddedMapper;
    }

    // ==================== BATCH CREATE METHODS (PERFORMANCE FIX) ====================

    @Transactional
    public List<String> createBooksBatchJpaOnly(List<BookCommand> commands) {
        logger.debug("entered createBooksBatchJpaOnly with {} books", commands.size());
        List<Book> books = new ArrayList<>();

        for (BookCommand command : commands) {
            List<Author> authors = command.authorIds().stream()
                    .map(ApiKey::new)
                    .map(authorRepository::findAuthorByAuthorApiKey)
                    .flatMap(Optional::stream)
                    .toList();

            books.add(new Book(
                    command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                    command.genre().stream().map(BookGenre::valueOf).toList(),
                    authors, command.types().stream().map(BookType::valueOf).toList(),
                    command.description()
            ));
        }

        // JPA Batch Insert (saveAll utilizes Hibernate JDBC batching)
        List<Book> savedBooks = bookRepository.saveAll(books);
        return savedBooks.stream().map(b -> b.getBookApiKey().apiKey()).toList();
    }

    @Transactional
    public List<String> createBooksBatchReferencing(List<BookCommand> commands) {
        logger.debug("entered createBooksBatchReferencing with {} books", commands.size());
        List<Book> books = new ArrayList<>();

        // 1. Prepare Entities
        for (BookCommand command : commands) {
            List<Author> authors = command.authorIds().stream()
                    .map(ApiKey::new)
                    .map(authorRepository::findAuthorByAuthorApiKey)
                    .flatMap(Optional::stream)
                    .toList();
            books.add(new Book(
                    command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                    command.genre().stream().map(BookGenre::valueOf).toList(),
                    authors, command.types().stream().map(BookType::valueOf).toList(),
                    command.description()
            ));
        }

        // 2. JPA Batch Save
        List<Book> savedBooks = bookRepository.saveAll(books);

        // 3. Mongo Batch Save
        try {
            List<BookDocument> mongoDocs = savedBooks.stream()
                    .map(mapper::toMongoDocument)
                    .toList();
            mongoRepository.saveAll(mongoDocs);
        } catch (Exception e) {
            logger.error("Failed to batch save books to MongoDB (referencing)", e);
        }

        return savedBooks.stream().map(b -> b.getBookApiKey().apiKey()).toList();
    }

    @Transactional
    public List<String> createBooksBatchEmbedding(List<BookCommand> commands) {
        logger.debug("entered createBooksBatchEmbedding with {} books", commands.size());
        List<Book> books = new ArrayList<>();

        // 1. Prepare Entities
        for (BookCommand command : commands) {
            List<Author> authors = command.authorIds().stream()
                    .map(ApiKey::new)
                    .map(authorRepository::findAuthorByAuthorApiKey)
                    .flatMap(Optional::stream)
                    .toList();
            books.add(new Book(
                    command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                    command.genre().stream().map(BookGenre::valueOf).toList(),
                    authors, command.types().stream().map(BookType::valueOf).toList(),
                    command.description()
            ));
        }

        // 2. JPA Batch Save
        List<Book> savedBooks = bookRepository.saveAll(books);

        // 3. Mongo Batch Save
        try {
            List<BookDocumentEmbedded> mongoDocs = savedBooks.stream()
                    .map(embeddedMapper::toMongoDocument)
                    .toList();
            embeddedMongoRepository.saveAll(mongoDocs);
        } catch (Exception e) {
            logger.error("Failed to batch save books to MongoDB (embedding)", e);
        }

        return savedBooks.stream().map(b -> b.getBookApiKey().apiKey()).toList();
    }

    // ==================== CREATE METHODS ====================

    /**
     * Erstellt Book NUR in JPA/PostgreSQL
     */
    @Transactional
    public BookDto createBookJpaOnly(BookCommand command) {
        logger.debug("entered createBookJpaOnly");
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
        logger.debug("Book saved to PostgreSQL only: {}", savedBook.getBookApiKey().apiKey());
        return BookDto.bookDtoFromBook(savedBook);
    }

    /**
     * Erstellt Book in JPA + MongoDB (Referencing)
     */
    @Transactional
    public BookDto createBookWithReferencing(BookCommand command) {
        logger.debug("entered createBookWithReferencing");
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
            BookDocument docRef = mapper.toMongoDocument(savedBook);
            mongoRepository.save(docRef);
            logger.debug("Book saved to MongoDB (referencing): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (referencing)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    /**
     * Erstellt Book in JPA + MongoDB (Embedding)
     */
    @Transactional
    public BookDto createBookWithEmbedding(BookCommand command) {
        logger.debug("entered createBookWithEmbedding");
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
            BookDocumentEmbedded docEmb = embeddedMapper.toMongoDocument(savedBook);
            embeddedMongoRepository.save(docEmb);
            logger.debug("Book saved to MongoDB (embedding): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (embedding)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    /**
     * Erstellt Book ÜBERALL (JPA + MongoDB Referencing + MongoDB Embedding)
     * STANDARD-METHODE für Controller
     */
    @Transactional
    public BookDto createBook(BookCommand command) {
        logger.debug("entered createBook (ALL)");
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

        // Save to MongoDB (Referencing)
        try {
            BookDocument docRef = mapper.toMongoDocument(savedBook);
            mongoRepository.save(docRef);
            logger.debug("Book saved to MongoDB (referencing): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (referencing)", e);
        }

        // Save to MongoDB (Embedding)
        try {
            BookDocumentEmbedded docEmb = embeddedMapper.toMongoDocument(savedBook);
            embeddedMongoRepository.save(docEmb);
            logger.debug("Book saved to MongoDB (embedding): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (embedding)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    // ==================== CREATE2 METHODS (mit Pennames) ====================

    /**
     * Erstellt Book NUR in JPA/PostgreSQL (mit Pennames)
     */
    @Transactional
    public BookDto createBook2JpaOnly(BookCommand2 command) {
        logger.debug("entered createBook2JpaOnly");
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
        logger.debug("Book saved to PostgreSQL only: {}", savedBook.getBookApiKey().apiKey());
        return BookDto.bookDtoFromBook(savedBook);
    }

    /**
     * Erstellt Book in JPA + MongoDB (Referencing) mit Pennames
     */
    @Transactional
    public BookDto createBook2WithReferencing(BookCommand2 command) {
        logger.debug("entered createBook2WithReferencing");
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
            BookDocument docRef = mapper.toMongoDocument(savedBook);
            mongoRepository.save(docRef);
            logger.debug("Book saved to MongoDB (referencing): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (referencing)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    /**
     * Erstellt Book in JPA + MongoDB (Embedding) mit Pennames
     */
    @Transactional
    public BookDto createBook2WithEmbedding(BookCommand2 command) {
        logger.debug("entered createBook2WithEmbedding");
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
            BookDocumentEmbedded docEmb = embeddedMapper.toMongoDocument(savedBook);
            embeddedMongoRepository.save(docEmb);
            logger.debug("Book saved to MongoDB (embedding): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (embedding)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    /**
     * Erstellt Book ÜBERALL (JPA + MongoDB Referencing + MongoDB Embedding) mit Pennames
     * STANDARD-METHODE für Controller
     */
    @Transactional
    public BookDto createBook2(BookCommand2 command) {
        logger.debug("entered createBook2 (ALL)");
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

        // Save to MongoDB (Referencing)
        try {
            BookDocument docRef = mapper.toMongoDocument(savedBook);
            mongoRepository.save(docRef);
            logger.debug("Book saved to MongoDB (referencing): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (referencing)", e);
        }

        // Save to MongoDB (Embedding)
        try {
            BookDocumentEmbedded docEmb = embeddedMapper.toMongoDocument(savedBook);
            embeddedMongoRepository.save(docEmb);
            logger.debug("Book saved to MongoDB (embedding): {}", savedBook.getBookApiKey().apiKey());
        } catch (Exception e) {
            logger.error("Failed to save book to MongoDB (embedding)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    // ==================== DELETE METHODS ====================

    /**
     * Löscht Book ÜBERALL (JPA + beide MongoDB Collections)
     * STANDARD-METHODE für Controller
     */
    @Transactional
    public void deleteBook(String apiKey) {
        logger.debug("entered deleteBook (ALL)");
        Book book = bookRepository.findBookByBookApiKey(new ApiKey(apiKey))
                .orElseThrow(() -> BookServiceException.noBookForApiKey(apiKey));
        bookRepository.delete(book);

        // Delete from MongoDB (Referencing)
        try {
            mongoRepository.findByApiKey(apiKey)
                    .ifPresent(doc -> mongoRepository.deleteById(doc.getId()));
            logger.debug("Book deleted from MongoDB (referencing): {}", apiKey);
        } catch (Exception e) {
            logger.error("Failed to delete book from MongoDB (referencing)", e);
        }

        // Delete from MongoDB (Embedding)
        try {
            embeddedMongoRepository.findByApiKey(apiKey)
                    .ifPresent(doc -> embeddedMongoRepository.deleteById(doc.getId()));
            logger.debug("Book deleted from MongoDB (embedding): {}", apiKey);
        } catch (Exception e) {
            logger.error("Failed to delete book from MongoDB (embedding)", e);
        }
    }

    // ==================== UPDATE METHODS ====================

    @Transactional
    public BookDto updateBook(BookCommand command) {
        logger.debug("entered updateBook (ALL)");

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

        // FIX: Erstelle NEUE mutable Lists statt toList() zu verwenden
        book.setGenres(new ArrayList<>(command.genre().stream()
                .map(BookGenre::valueOf)
                .toList()));
        book.setBookTypes(new ArrayList<>(command.types().stream()
                .map(BookType::valueOf)
                .toList()));
        book.setAuthors(new ArrayList<>(authors));

        Book savedBook = bookRepository.save(book);

        // Update MongoDB (Referencing)
        try {
            mongoRepository.findByApiKey(command.apiKey()).ifPresent(doc -> {
                doc.setName(command.name());
                doc.setAvailableOnline(command.availableOnline());
                doc.setDescription(command.description());
                doc.setReleaseDate(command.releaseDate());
                doc.setGenres(new ArrayList<>(command.genre()));
                doc.setBookTypes(new ArrayList<>(command.types()));
                doc.setAuthorApiKeys(authors.stream()
                        .map(a -> a.getAuthorApiKey().apiKey())
                        .toList());
                mongoRepository.save(doc);
            });
            logger.debug("Book updated in MongoDB (referencing): {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update book in MongoDB (referencing)", e);
        }

        // Update MongoDB (Embedding)
        try {
            embeddedMongoRepository.findByApiKey(command.apiKey()).ifPresent(doc -> {
                BookDocumentEmbedded updated = embeddedMapper.toMongoDocument(savedBook);
                doc.setName(updated.getName());
                doc.setAvailableOnline(updated.getAvailableOnline());
                doc.setDescription(updated.getDescription());
                doc.setReleaseDate(updated.getReleaseDate());
                doc.setGenres(new ArrayList<>(updated.getGenres()));
                doc.setBookTypes(new ArrayList<>(updated.getBookTypes()));
                doc.setAuthors(new ArrayList<>(updated.getAuthors()));
                embeddedMongoRepository.save(doc);
            });
            logger.debug("Book updated in MongoDB (embedding): {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update book in MongoDB (embedding)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }

    @Transactional
    public BookDto updateBook2(BookCommand2 command) {
        logger.debug("entered updateBook2 (ALL)");

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

        // FIX: Erstelle NEUE mutable Lists statt toList() zu verwenden
        book.setGenres(new ArrayList<>(command.genre().stream()
                .map(BookGenre::valueOf)
                .toList()));
        book.setBookTypes(new ArrayList<>(command.types().stream()
                .map(BookType::valueOf)
                .toList()));
        book.setAuthors(new ArrayList<>(authors));

        Book savedBook = bookRepository.save(book);

        // Update MongoDB (Referencing)
        try {
            mongoRepository.findByApiKey(command.apiKey()).ifPresent(doc -> {
                doc.setName(command.name());
                doc.setAvailableOnline(command.availableOnline());
                doc.setDescription(command.description());
                doc.setReleaseDate(command.releaseDate());
                doc.setGenres(new ArrayList<>(command.genre()));
                doc.setBookTypes(new ArrayList<>(command.types()));
                doc.setAuthorApiKeys(authors.stream()
                        .map(a -> a.getAuthorApiKey().apiKey())
                        .toList());
                mongoRepository.save(doc);
            });
            logger.debug("Book updated in MongoDB (referencing): {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update book in MongoDB (referencing)", e);
        }

        // Update MongoDB (Embedding)
        try {
            embeddedMongoRepository.findByApiKey(command.apiKey()).ifPresent(doc -> {
                BookDocumentEmbedded updated = embeddedMapper.toMongoDocument(savedBook);
                doc.setName(updated.getName());
                doc.setAvailableOnline(updated.getAvailableOnline());
                doc.setDescription(updated.getDescription());
                doc.setReleaseDate(updated.getReleaseDate());
                doc.setGenres(new ArrayList<>(updated.getGenres()));
                doc.setBookTypes(new ArrayList<>(updated.getBookTypes()));
                doc.setAuthors(new ArrayList<>(updated.getAuthors()));
                embeddedMongoRepository.save(doc);
            });
            logger.debug("Book updated in MongoDB (embedding): {}", command.apiKey());
        } catch (Exception e) {
            logger.error("Failed to update book in MongoDB (embedding)", e);
        }

        return BookDto.bookDtoFromBook(savedBook);
    }


    // ==================== READ METHODS ====================

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

    // ==================== SYNC METHODS ====================

    @Transactional
    public void syncAllToMongo() {
        logger.info("Starting sync from PostgreSQL to MongoDB (both collections)");
        List<Book> allBooks = bookRepository.findAll();
        int synced = 0;
        for (Book book : allBooks) {
            try {
                // Sync to Referencing
                BookDocument docRef = mapper.toMongoDocument(book);
                mongoRepository.save(docRef);

                // Sync to Embedding
                BookDocumentEmbedded docEmb = embeddedMapper.toMongoDocument(book);
                embeddedMongoRepository.save(docEmb);

                synced++;
            } catch (Exception e) {
                logger.error("Failed to sync book: {}", book.getBookApiKey().apiKey(), e);
            }
        }
        logger.info("Synced {} of {} books to MongoDB (both collections)", synced, allBooks.size());
    }

    // ==================== EXCEPTION CLASS ====================

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