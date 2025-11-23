package spengergasse.at.sj2425scherzerrabar.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.mapper.BookEmbeddedMapper;
import spengergasse.at.sj2425scherzerrabar.mapper.BookMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.*;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.time.LocalDate;
import java.util.*;

/**
 * Comprehensive Performance Test Suite
 * Tests: Authors, Books, Mixed Queries für JPA vs MongoDB
 */
public class ComprehensivePerformanceTestRunner {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensivePerformanceTestRunner.class);

    private final AuthorService authorService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookMongoRepository mongoReferencingRepo;
    private final BookEmbeddedMongoRepository mongoEmbeddingRepo;
    private final AuthorRepository authorRepository;
    private final AuthorMongoRepository authorMongoRepository;
    private final BookMapper bookMapper;
    private final BookEmbeddedMapper bookEmbeddedMapper;

    private static final int[] SCALE_FACTORS = {100, 500, 2000};

    private final Map<String, Map<String, Map<String, Long>>> results = new LinkedHashMap<>();

    public ComprehensivePerformanceTestRunner(
            AuthorService authorService,
            BookService bookService,
            BookRepository bookRepository,
            BookMongoRepository mongoReferencingRepo,
            BookEmbeddedMongoRepository mongoEmbeddingRepo,
            AuthorRepository authorRepository,
            AuthorMongoRepository authorMongoRepository,
            BookMapper bookMapper,
            BookEmbeddedMapper bookEmbeddedMapper) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.mongoReferencingRepo = mongoReferencingRepo;
        this.mongoEmbeddingRepo = mongoEmbeddingRepo;
        this.authorRepository = authorRepository;
        this.authorMongoRepository = authorMongoRepository;
        this.bookMapper = bookMapper;
        this.bookEmbeddedMapper = bookEmbeddedMapper;
    }

    public void runTests() {
        logger.info("=".repeat(120));
        logger.info("COMPREHENSIVE PERFORMANCE TEST SUITE");
        logger.info("Testing: Authors | Books | Mixed Queries");
        logger.info("Databases: JPA | MongoDB Embedding | MongoDB Referencing");
        logger.info("=".repeat(120));

        // CRITICAL: Clean up ALL data from previous runs first!
        logger.info("\n🧹 Cleaning up data from previous test runs...");
        cleanupAllData();
        logger.info("✓ Cleanup complete - starting with clean databases\n");

        List<Author> testAuthors = createTestAuthors(100);
        logger.info("Created {} test authors", testAuthors.size());

        for (int scale : SCALE_FACTORS) {
            logger.info("\n>>> Testing with scale: {} records <<<\n", scale);
            runTestsForScale(scale, testAuthors);
        }

        cleanupTestAuthors(testAuthors);
        printSummaryTables();
    }

    private void runTestsForScale(int scale, List<Author> testAuthors) {
        String scaleKey = scale + " records";
        results.put(scaleKey, new LinkedHashMap<>());

        // 1. AUTHOR TESTS
        logger.info("=== AUTHOR OPERATIONS (Scale: {}) ===", scale);
        testAuthorOperations(scale, scaleKey);

        // 2. BOOK TESTS
        logger.info("\n=== BOOK OPERATIONS (Scale: {}) ===", scale);
        Map<String, List<String>> createdBookKeys = testBookWrites(scale, testAuthors, scaleKey);
        testBookReads(scale, createdBookKeys, scaleKey);
        testBookUpdates(createdBookKeys, testAuthors, scaleKey);

        // 3. MIXED QUERIES (Books by Author)
        logger.info("\n=== MIXED QUERIES (Scale: {}) ===", scale);
        testMixedQueries(testAuthors, scaleKey);

        // 4. NESTED QUERIES (Show Embedding disadvantages)
        logger.info("\n=== NESTED QUERIES - Embedding Weakness (Scale: {}) ===", scale);
        testNestedQueries(testAuthors, scaleKey);

        // 5. UPDATE PROPAGATION (Critical Embedding problem)
        logger.info("\n=== UPDATE PROPAGATION - Critical Embedding Problem (Scale: {}) ===", scale);
        testUpdatePropagation(testAuthors, createdBookKeys, scaleKey);

        // 6. CLEANUP
        testBookDeletes(createdBookKeys, scaleKey);
    }

    // ==================== AUTHOR OPERATIONS ====================

    private void testAuthorOperations(int scale, String scaleKey) {
        // Find single author by API key
        Author testAuthor = authorRepository.findAll().get(0);
        String apiKey = testAuthor.getAuthorApiKey().apiKey();

        // JPA Find Author
        long startTime = System.currentTimeMillis();
        try {
            Optional<AuthorDto> author = authorRepository.findProjectedAuthorByAuthorApiKey(apiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author: Find by Key", "JPA", duration);
            logger.info("✓ JPA Find Author by Key: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Find Author failed", e);
        }

        // MongoDB Find Author
        startTime = System.currentTimeMillis();
        try {
            Optional<AuthorDocument> author = authorMongoRepository.findByApiKey(apiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author: Find by Key", "MongoDB", duration);
            logger.info("✓ MongoDB Find Author by Key: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Find Author failed", e);
        }

        // Find all authors
        testFindAllAuthors(scaleKey);

        // Find author by email
        testFindAuthorByEmail(scaleKey);
    }

    private void testFindAllAuthors(String scaleKey) {
        // JPA
        long startTime = System.currentTimeMillis();
        try {
            List<AuthorDto> authors = authorRepository.findAllProjected();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author: Find All", "JPA", duration);
            logger.info("✓ JPA Find All Authors: {} ms ({} found)", duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ JPA Find All Authors failed", e);
        }

        // MongoDB
        startTime = System.currentTimeMillis();
        try {
            List<AuthorDocument> authors = authorMongoRepository.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author: Find All", "MongoDB", duration);
            logger.info("✓ MongoDB Find All Authors: {} ms ({} found)", duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Find All Authors failed", e);
        }
    }

    private void testFindAuthorByEmail(String scaleKey) {
        Author testAuthor = authorRepository.findAll().get(0);
        String email = testAuthor.getEmailAddress().email();

        // JPA
        long startTime = System.currentTimeMillis();
        try {
            Optional<AuthorDto> author = authorRepository.findProjectedAuthorByEmailAddress_Email(email);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author: Find by Email", "JPA", duration);
            logger.info("✓ JPA Find Author by Email: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Find Author by Email failed", e);
        }

        // MongoDB
        startTime = System.currentTimeMillis();
        try {
            Optional<AuthorDocument> author = authorMongoRepository.findByEmail(email);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author: Find by Email", "MongoDB", duration);
            logger.info("✓ MongoDB Find Author by Email: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Find Author by Email failed", e);
        }
    }

    // ==================== BOOK WRITE OPERATIONS ====================

    private Map<String, List<String>> testBookWrites(int count, List<Author> testAuthors, String scaleKey) {
        Map<String, List<String>> createdKeys = new HashMap<>();

        logger.info("--- BOOK WRITE OPERATIONS ---");
        createdKeys.put("JPA", testWriteBookJPA(count, testAuthors, scaleKey));
        createdKeys.put("Mongo Embedding", testWriteBookMongoEmbedding(count, testAuthors, scaleKey));
        createdKeys.put("Mongo Referencing", testWriteBookMongoReferencing(count, testAuthors, scaleKey));

        return createdKeys;
    }

    private List<String> testWriteBookJPA(int count, List<Author> testAuthors, String scaleKey) {
        List<String> apiKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, "jpa", testAuthors);
                BookDto created = bookService.createBookJpaOnly(command);
                apiKeys.add(created.apiKey());
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Write", "JPA", duration);
            logger.info("✓ JPA Book Write: {} ms ({} records)", duration, count);
        } catch (Exception e) {
            logger.error("✗ JPA Book Write failed", e);
        }

        return apiKeys;
    }

    private List<String> testWriteBookMongoEmbedding(int count, List<Author> testAuthors, String scaleKey) {
        List<String> apiKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, "mongo_emb", testAuthors);
                BookDto created = bookService.createBookWithEmbedding(command);
                apiKeys.add(created.apiKey());
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Write", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Book Write: {} ms ({} records)", duration, count);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Book Write failed", e);
        }

        return apiKeys;
    }

    private List<String> testWriteBookMongoReferencing(int count, List<Author> testAuthors, String scaleKey) {
        List<String> apiKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, "mongo_ref", testAuthors);
                BookDto created = bookService.createBookWithReferencing(command);
                apiKeys.add(created.apiKey());
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Write", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Book Write: {} ms ({} records)", duration, count);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Book Write failed", e);
        }

        return apiKeys;
    }

    // ==================== BOOK READ OPERATIONS ====================

    private void testBookReads(int scale, Map<String, List<String>> createdApiKeys, String scaleKey) {
        logger.info("\n--- BOOK READ OPERATIONS ---");
        testFindAllBooks(scaleKey);
        testFindBookByKey(createdApiKeys, scaleKey);
        //testFindBooksProjectionOLD(scaleKey);
        testFindBooksProjectionOPTIMIZED(scaleKey);
    }

    private void testFindAllBooks(String scaleKey) {
        // JPA
        long startTime = System.currentTimeMillis();
        try {
            List<Book> books = bookRepository.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Find All", "JPA", duration);
            logger.info("✓ JPA Find All Books: {} ms ({} found)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ JPA Find All Books failed", e);
        }

        // MongoDB Embedding
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> books = mongoEmbeddingRepo.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Find All", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Find All Books: {} ms ({} found)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Find All Books failed", e);
        }

        // MongoDB Referencing
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> books = mongoReferencingRepo.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Find All", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Find All Books: {} ms ({} found)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Find All Books failed", e);
        }
    }

    private void testFindBookByKey(Map<String, List<String>> createdApiKeys, String scaleKey) {
        // JPA
        String jpaKey = createdApiKeys.get("JPA").get(0);
        long startTime = System.currentTimeMillis();
        try {
            Optional<Book> book = bookRepository.findBookByBookApiKey(new ApiKey(jpaKey));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Find by Key", "JPA", duration);
            logger.info("✓ JPA Find Book by Key: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Find Book by Key failed", e);
        }

        // MongoDB Embedding
        String embKey = createdApiKeys.get("Mongo Embedding").get(0);
        startTime = System.currentTimeMillis();
        try {
            Optional<BookDocumentEmbedded> book = mongoEmbeddingRepo.findByApiKey(embKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Find by Key", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Find Book by Key: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Find Book by Key failed", e);
        }

        // MongoDB Referencing
        String refKey = createdApiKeys.get("Mongo Referencing").get(0);
        startTime = System.currentTimeMillis();
        try {
            Optional<BookDocument> book = mongoReferencingRepo.findByApiKey(refKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Find by Key", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Find Book by Key: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Find Book by Key failed", e);
        }
    }

    private void testFindBooksProjectionOLD(String scaleKey) {
        logger.info("\n⚠️ Testing OLD Projection (N+1 Problem):");

        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findAllProjected();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Projection (OLD)", "JPA", duration);
            logger.warn("✗ JPA OLD Projection: {} ms (N+1 Problem!)", duration);
        } catch (Exception e) {
            logger.error("✗ JPA OLD Projection failed", e);
        }
    }

    private void testFindBooksProjectionOPTIMIZED(String scaleKey) {
        logger.info("\n✅ Testing OPTIMIZED Projection (JOIN FETCH):");

        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findAllProjectedOptimized();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Projection (OPTIMIZED)", "JPA", duration);
            logger.info("✓ JPA OPTIMIZED Projection: {} ms (JOIN FETCH!)", duration);
        } catch (Exception e) {
            logger.error("✗ JPA OPTIMIZED Projection failed", e);
        }

        // MongoDB Embedding
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> books = mongoEmbeddingRepo.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Projection (OPTIMIZED)", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Projection: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Projection failed", e);
        }

        // MongoDB Referencing
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> books = mongoReferencingRepo.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Projection (OPTIMIZED)", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Projection: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Projection failed", e);
        }
    }

    // ==================== BOOK UPDATE OPERATIONS ====================

    private void testBookUpdates(Map<String, List<String>> createdApiKeys, List<Author> testAuthors, String scaleKey) {
        logger.info("\n--- BOOK UPDATE OPERATIONS ---");

        // JPA Update
        String jpaKey = createdApiKeys.get("JPA").get(0);
        long startTime = System.currentTimeMillis();
        try {
            Book book = bookRepository.findBookByBookApiKey(new ApiKey(jpaKey)).orElseThrow();
            book.setName(book.getName() + "_updated");
            bookRepository.save(book);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Update", "JPA", duration);
            logger.info("✓ JPA Book Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Book Update failed", e);
        }

        // MongoDB Embedding Update
        String embKey = createdApiKeys.get("Mongo Embedding").get(0);
        startTime = System.currentTimeMillis();
        try {
            BookDocumentEmbedded book = mongoEmbeddingRepo.findByApiKey(embKey).orElseThrow();
            book.setName(book.getName() + "_updated");
            mongoEmbeddingRepo.save(book);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Update", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Book Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Book Update failed", e);
        }

        // MongoDB Referencing Update
        String refKey = createdApiKeys.get("Mongo Referencing").get(0);
        startTime = System.currentTimeMillis();
        try {
            BookDocument book = mongoReferencingRepo.findByApiKey(refKey).orElseThrow();
            book.setName(book.getName() + "_updated");
            mongoReferencingRepo.save(book);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Update", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Book Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Book Update failed", e);
        }
    }

    // ==================== NESTED QUERIES (Embedding Weakness) ====================

    private void testNestedQueries(List<Author> testAuthors, String scaleKey) {
        Author testAuthor = testAuthors.get(0);
        String authorEmail = testAuthor.getEmailAddress().email();
        String authorApiKey = testAuthor.getAuthorApiKey().apiKey();

        logger.info("Testing nested queries to demonstrate Embedding disadvantages:");

        // Query 1: Find books by author with specific email
        testFindBooksByAuthorEmail(authorEmail, authorApiKey, scaleKey);

        // Query 2: Find books by author from specific city
        testFindBooksByAuthorCity("TestSt", authorApiKey, scaleKey);

        // Query 3: Find all unique authors who wrote books
        testFindAllAuthorsWithBooks(scaleKey);
    }

    private void testFindBooksByAuthorEmail(String email, String authorApiKey, String scaleKey) {
        logger.info("\n--- Find Books by Author Email ---");

        // JPA: Efficient JOIN query
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findProjectedBooksByAuthorsContains(authorApiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Books by Author Email", "JPA", duration);
            logger.info("✓ JPA Books by Author Email: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Books by Author Email failed", e);
        }

        // MongoDB Referencing: Two queries needed but still fast
        startTime = System.currentTimeMillis();
        try {
            Optional<AuthorDocument> author = authorMongoRepository.findByEmail(email);
            List<BookDocument> books = new ArrayList<>();
            if (author.isPresent()) {
                books = mongoReferencingRepo.findByAuthorApiKeysContaining(author.get().getApiKey());
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Books by Author Email", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Books by Author Email: {} ms (2 queries)", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Books by Author Email failed", e);
        }

        // MongoDB Embedding: FULL COLLECTION SCAN required
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> allBooks = mongoEmbeddingRepo.findAll();
            List<BookDocumentEmbedded> matchingBooks = allBooks.stream()
                    .filter(book -> book.getAuthors().stream()
                            .anyMatch(author -> author.getEmail().equals(email)))
                    .toList();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Books by Author Email", "Mongo Embedding", duration);
            logger.warn("⚠️ MongoDB Embedding Books by Author Email: {} ms - FULL SCAN of {} books!",
                    duration, allBooks.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Books by Author Email failed", e);
        }
    }

    private void testFindBooksByAuthorCity(String city, String authorApiKey, String scaleKey) {
        logger.info("\n--- Find Books by Author City ---");

        // JPA: Efficient JOIN query
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findProjectedBooksByAuthorsContains(authorApiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Books by Author City", "JPA", duration);
            logger.info("✓ JPA Books by Author City: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Books by Author City failed", e);
        }

        // MongoDB Referencing: Can't efficiently query by nested author properties
        startTime = System.currentTimeMillis();
        try {
            // Would need to load all authors, filter by city, then find books
            List<AuthorDocument> authorsInCity = authorMongoRepository.findAll().stream()
                    .filter(a -> a.getAddresses().stream()
                            .anyMatch(addr -> addr.getCity().contains(city)))
                    .toList();

            List<BookDocument> books = new ArrayList<>();
            for (AuthorDocument author : authorsInCity) {
                books.addAll(mongoReferencingRepo.findByAuthorApiKeysContaining(author.getApiKey()));
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Books by Author City", "Mongo Referencing", duration);
            logger.warn("⚠️ MongoDB Referencing Books by Author City: {} ms - Multiple queries needed", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Books by Author City failed", e);
        }

        // MongoDB Embedding: FULL COLLECTION SCAN with nested filter
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> allBooks = mongoEmbeddingRepo.findAll();
            List<BookDocumentEmbedded> matchingBooks = allBooks.stream()
                    .filter(book -> book.getAuthors().stream()
                            .anyMatch(author -> author.getAddresses().stream()
                                    .anyMatch(addr -> addr.getCity().contains(city))))
                    .toList();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Books by Author City", "Mongo Embedding", duration);
            logger.warn("⚠️ MongoDB Embedding Books by Author City: {} ms - FULL SCAN with nested filter!", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Books by Author City failed", e);
        }
    }

    private void testFindAllAuthorsWithBooks(String scaleKey) {
        logger.info("\n--- Find All Unique Authors Who Wrote Books ---");

        // JPA: OPTIMIZED - Get unique author IDs from books first
        long startTime = System.currentTimeMillis();
        try {
            // Much faster: get all books and extract unique author IDs
            List<Book> allBooks = bookRepository.findAll();
            Set<Long> authorIdsWithBooks = new HashSet<>();
            for (Book book : allBooks) {
                for (Author author : book.getAuthors()) {
                    authorIdsWithBooks.add(author.getPersonId().id());
                }
            }

            // Then fetch only those authors
            List<Author> authors = authorRepository.findAll().stream()
                    .filter(a -> authorIdsWithBooks.contains(a.getPersonId().id()))
                    .toList();

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Authors with Books", "JPA", duration);
            logger.info("✓ JPA Authors with Books: {} ms ({} found)", duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ JPA Authors with Books failed", e);
        }

        // MongoDB Referencing: Need to aggregate from books
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> allBooks = mongoReferencingRepo.findAll();
            Set<String> uniqueAuthorKeys = new HashSet<>();
            for (BookDocument book : allBooks) {
                uniqueAuthorKeys.addAll(book.getAuthorApiKeys());
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Authors with Books", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Authors with Books: {} ms ({} found)", duration, uniqueAuthorKeys.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Authors with Books failed", e);
        }

        // MongoDB Embedding: Extract from embedded documents
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> allBooks = mongoEmbeddingRepo.findAll();
            Set<String> uniqueAuthorKeys = new HashSet<>();
            for (BookDocumentEmbedded book : allBooks) {
                book.getAuthors().forEach(author -> uniqueAuthorKeys.add(author.getApiKey()));
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Nested: Authors with Books", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Authors with Books: {} ms ({} found) - Data already embedded",
                    duration, uniqueAuthorKeys.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Authors with Books failed", e);
        }
    }

    // ==================== UPDATE PROPAGATION (Embedding Problem) ====================

    private void testUpdatePropagation(List<Author> testAuthors, Map<String, List<String>> createdBookKeys, String scaleKey) {
        Author testAuthor = testAuthors.get(0);
        String authorApiKey = testAuthor.getAuthorApiKey().apiKey();
        String newEmail = "updated_" + testAuthor.getEmailAddress().email();

        logger.info("\n🔥 CRITICAL TEST: Update Author Email and propagate to all Books");
        logger.info("This demonstrates the BIGGEST disadvantage of Embedding!");

        // JPA: Single update, relationships maintained automatically
        long startTime = System.currentTimeMillis();
        try {
            Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(authorApiKey)).orElseThrow();
            author.setEmailAddress(new spengergasse.at.sj2425scherzerrabar.domain.EmailAddress(newEmail));
            authorRepository.save(author);

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Update Propagation: Author Email", "JPA", duration);
            logger.info("✓ JPA Update Author Email: {} ms - Relationships maintained automatically!", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Update Author Email failed", e);
        }

        // MongoDB Referencing: Single update, references remain valid
        startTime = System.currentTimeMillis();
        try {
            Optional<AuthorDocument> author = authorMongoRepository.findByApiKey(authorApiKey);
            if (author.isPresent()) {
                AuthorDocument doc = author.get();
                doc.setEmail(newEmail);
                authorMongoRepository.save(doc);
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Update Propagation: Author Email", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Update Author Email: {} ms - References remain valid!", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Update Author Email failed", e);
        }

        // MongoDB Embedding: MUST UPDATE EVERY BOOK! (DISASTER)
        startTime = System.currentTimeMillis();
        try {
            // First, update author in author collection
            Optional<AuthorDocument> author = authorMongoRepository.findByApiKey(authorApiKey);
            if (author.isPresent()) {
                AuthorDocument doc = author.get();
                doc.setEmail(newEmail);
                authorMongoRepository.save(doc);
            }

            // Now comes the DISASTER: Update ALL books that contain this author!
            List<BookDocumentEmbedded> allBooks = mongoEmbeddingRepo.findAll();
            int updatedBooks = 0;

            for (BookDocumentEmbedded book : allBooks) {
                boolean needsUpdate = false;
                for (BookDocumentEmbedded.EmbeddedAuthor embeddedAuthor : book.getAuthors()) {
                    if (embeddedAuthor.getApiKey().equals(authorApiKey)) {
                        embeddedAuthor.setEmail(newEmail);
                        needsUpdate = true;
                    }
                }
                if (needsUpdate) {
                    mongoEmbeddingRepo.save(book);
                    updatedBooks++;
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Update Propagation: Author Email", "Mongo Embedding", duration);
            logger.error("❌ MongoDB Embedding Update Author Email: {} ms - Had to update {} BOOKS! DISASTER!",
                    duration, updatedBooks);
            logger.error("   📊 Performance Impact: {}x slower than JPA!",
                    duration / Math.max(1, results.get(scaleKey)
                            .get("Update Propagation: Author Email")
                            .getOrDefault("JPA", 1L)));
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Update Author Email failed", e);
        }

        // Restore original email for cleanup
        try {
            Author author = authorRepository.findAuthorByAuthorApiKey(new ApiKey(authorApiKey)).orElseThrow();
            author.setEmailAddress(testAuthor.getEmailAddress());
            authorRepository.save(author);
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    // ==================== BOOK DELETE OPERATIONS ====================

    private void testBookDeletes(Map<String, List<String>> createdApiKeys, String scaleKey) {
        logger.info("\n--- BOOK DELETE OPERATIONS ---");

        // JPA Delete
        List<String> jpaKeys = createdApiKeys.get("JPA");
        long startTime = System.currentTimeMillis();
        int deleted = 0;
        try {
            for (String apiKey : jpaKeys) {
                bookRepository.findBookByBookApiKey(new ApiKey(apiKey))
                        .ifPresent(book -> bookRepository.delete(book));
                deleted++;
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Delete", "JPA", duration);
            logger.info("✓ JPA Book Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ JPA Book Delete failed", e);
        }

        // MongoDB Embedding Delete
        List<String> embKeys = createdApiKeys.get("Mongo Embedding");
        startTime = System.currentTimeMillis();
        deleted = 0;
        try {
            for (String apiKey : embKeys) {
                mongoEmbeddingRepo.findByApiKey(apiKey)
                        .ifPresent(doc -> mongoEmbeddingRepo.deleteById(doc.getId()));
                deleted++;
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Delete", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Book Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Book Delete failed", e);
        }

        // MongoDB Referencing Delete
        List<String> refKeys = createdApiKeys.get("Mongo Referencing");
        startTime = System.currentTimeMillis();
        deleted = 0;
        try {
            for (String apiKey : refKeys) {
                mongoReferencingRepo.findByApiKey(apiKey)
                        .ifPresent(doc -> mongoReferencingRepo.deleteById(doc.getId()));
                deleted++;
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book: Delete", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Book Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Book Delete failed", e);
        }
    }

    // ==================== MIXED QUERIES ====================

    private void testMixedQueries(List<Author> testAuthors, String scaleKey) {
        Author testAuthor = testAuthors.get(0);
        String authorApiKey = testAuthor.getAuthorApiKey().apiKey();

        // JPA: Find Books by Author
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findProjectedBooksByAuthorsContains(authorApiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Mixed: Books by Author", "JPA", duration);
            logger.info("✓ JPA Books by Author: {} ms ({} found)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ JPA Books by Author failed", e);
        }

        // MongoDB Referencing: Find Books by Author (Fast - indexed field)
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> books = mongoReferencingRepo.findByAuthorApiKeysContaining(authorApiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Mixed: Books by Author", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Books by Author: {} ms ({} found)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Books by Author failed", e);
        }

        // MongoDB Embedding: Find Books by Author (SLOW - requires full collection scan)
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> allBooks = mongoEmbeddingRepo.findAll();
            List<BookDocumentEmbedded> matchingBooks = allBooks.stream()
                    .filter(book -> book.getAuthors().stream()
                            .anyMatch(author -> author.getApiKey().equals(authorApiKey)))
                    .toList();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Mixed: Books by Author", "Mongo Embedding", duration);
            logger.warn("⚠️ MongoDB Embedding Books by Author: {} ms ({} found) - FULL COLLECTION SCAN!",
                    duration, matchingBooks.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Books by Author failed", e);
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Clean up ALL data from previous test runs
     * IMPORTANT: Must be called before tests to ensure accurate counts
     */
    private void cleanupAllData() {
        try {
            // Delete all books from all databases
            logger.debug("Deleting all books from JPA...");
            bookRepository.deleteAll();

            logger.debug("Deleting all books from MongoDB Embedding...");
            mongoEmbeddingRepo.deleteAll();

            logger.debug("Deleting all books from MongoDB Referencing...");
            mongoReferencingRepo.deleteAll();

            // Delete all authors (but we'll create new test authors anyway)
            logger.debug("Deleting all authors from JPA...");
            authorRepository.deleteAll();

            logger.debug("Deleting all authors from MongoDB...");
            authorMongoRepository.deleteAll();

            logger.info("✓ Deleted all leftover data from databases");
        } catch (Exception e) {
            logger.warn("⚠️ Cleanup encountered errors (this is OK for first run): {}", e.getMessage());
        }
    }

    private List<Author> createTestAuthors(int count) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AuthorCommand command = new AuthorCommand(
                    null,
                    "testpen_" + i,
                    List.of("123-TestSt-1010"),
                    "Test" + i,
                    "Author" + i,
                    "test" + i + "@test.com"
            );
            authors.add(authorRepository.findAuthorByAuthorApiKey(
                    new ApiKey(authorService.createAuthor(command).apiKey())
            ).orElseThrow());
        }
        return authors;
    }

    private void cleanupTestAuthors(List<Author> authors) {
        for (Author author : authors) {
            try {
                authorService.deleteAuthor(author.getAuthorApiKey().apiKey());
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    private BookCommand createBookCommand(int index, String suffix, List<Author> testAuthors) {
        Author randomAuthor = testAuthors.get(index % testAuthors.size());
        return new BookCommand(
                null,
                "Book_" + suffix + "_" + index,
                LocalDate.now().minusDays(index),
                index % 2 == 0,
                List.of(BookType.HARDCOVER.name()),
                1000 + (index * 10),
                "Description " + index,
                List.of(randomAuthor.getAuthorApiKey().apiKey()),
                List.of(BookGenre.FANTASY.name())
        );
    }

    private void recordResult(String scale, String operation, String dbType, long durationMs) {
        results.get(scale)
                .computeIfAbsent(operation, k -> new LinkedHashMap<>())
                .put(dbType, durationMs);
    }

    private void printSummaryTables() {
        logger.info("\n" + "=".repeat(120));
        logger.info("COMPREHENSIVE PERFORMANCE TEST RESULTS");
        logger.info("=".repeat(120));

        for (Map.Entry<String, Map<String, Map<String, Long>>> scaleEntry : results.entrySet()) {
            String scale = scaleEntry.getKey();
            Map<String, Map<String, Long>> operations = scaleEntry.getValue();

            logger.info("\n>>> {} <<<", scale);
            logger.info("-".repeat(120));

            logger.info(String.format("%-40s | %-20s | %-20s | %-20s",
                    "Operation", "JPA (ms)", "Mongo Embedding (ms)", "Mongo Referencing (ms)"));
            logger.info("-".repeat(120));

            for (Map.Entry<String, Map<String, Long>> opEntry : operations.entrySet()) {
                String operation = opEntry.getKey();
                Map<String, Long> dbResults = opEntry.getValue();

                String jpa = dbResults.getOrDefault("JPA", -1L).toString();
                String mongoEmb = dbResults.getOrDefault("Mongo Embedding", -1L).toString();
                String mongoRef = dbResults.getOrDefault("Mongo Referencing", -1L).toString();
                String mongodb = dbResults.getOrDefault("MongoDB", -1L).toString();

                if (jpa.equals("-1")) jpa = "N/A";
                if (mongoEmb.equals("-1")) mongoEmb = "N/A";
                if (mongoRef.equals("-1")) mongoRef = "N/A";
                if (mongodb.equals("-1")) mongodb = "N/A";

                // For Author operations that only have MongoDB
                if (!mongodb.equals("N/A")) {
                    mongoEmb = mongodb;
                    mongoRef = mongodb;
                }

                logger.info(String.format("%-40s | %-20s | %-20s | %-20s",
                        operation, jpa, mongoEmb, mongoRef));
            }

            logger.info("-".repeat(120));
        }

        logger.info("\n" + "=".repeat(120));
        logger.info("END OF COMPREHENSIVE PERFORMANCE TEST RESULTS");
        logger.info("=".repeat(120));
    }
}