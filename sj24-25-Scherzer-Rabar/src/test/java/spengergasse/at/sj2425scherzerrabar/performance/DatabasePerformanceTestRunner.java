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
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.mapper.BookEmbeddedMapper;
import spengergasse.at.sj2425scherzerrabar.mapper.BookMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookEmbeddedMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.time.LocalDate;
import java.util.*;

/**
 * Optimized Performance Test Suite
 * Tests: JPA vs MongoDB Embedding vs MongoDB Referencing
 */
public class DatabasePerformanceTestRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceTestRunner.class);

    private final AuthorService authorService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookMongoRepository mongoReferencingRepo;
    private final BookEmbeddedMongoRepository mongoEmbeddingRepo;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    private final BookEmbeddedMapper bookEmbeddedMapper;

    // Test scale configurations
    private static final int[] SCALE_FACTORS = {100, 1000, 10000};

    // Result storage: Scale -> Operation -> DbType -> Duration
    private final Map<String, Map<String, Map<String, Long>>> results = new LinkedHashMap<>();

    public DatabasePerformanceTestRunner(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.bookRepository = null;
        this.mongoReferencingRepo = null;
        this.mongoEmbeddingRepo = null;
        this.authorRepository = null;
        this.bookMapper = null;
        this.bookEmbeddedMapper = null;
    }

    public DatabasePerformanceTestRunner(AuthorService authorService,
                                         BookService bookService,
                                         BookRepository bookRepository,
                                         BookMongoRepository mongoReferencingRepo,
                                         BookEmbeddedMongoRepository mongoEmbeddingRepo,
                                         AuthorRepository authorRepository,
                                         BookMapper bookMapper,
                                         BookEmbeddedMapper bookEmbeddedMapper) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.mongoReferencingRepo = mongoReferencingRepo;
        this.mongoEmbeddingRepo = mongoEmbeddingRepo;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
        this.bookEmbeddedMapper = bookEmbeddedMapper;
    }

    public void runTests() {
        logger.info("=================================================");
        logger.info("Starting Optimized Performance Test Suite");
        logger.info("Testing: JPA | MongoDB Embedding | MongoDB Referencing");
        logger.info("=================================================");

        // Create test authors once (shared across all tests)
        List<Author> testAuthors = createTestAuthors(100);
        logger.info("Created {} test authors", testAuthors.size());

        // Run tests for each scale
        for (int scale : SCALE_FACTORS) {
            logger.info("\n>>> Testing with scale: {} records <<<\n", scale);
            runTestsForScale(scale, testAuthors);
        }

        // Cleanup
        cleanupTestAuthors(testAuthors);

        // Print summary tables
        printSummaryTables();
    }

    private void runTestsForScale(int scale, List<Author> testAuthors) {
        String scaleKey = scale + " records";
        results.put(scaleKey, new LinkedHashMap<>());

        // 1. WRITE Operations
        logger.info("--- WRITE OPERATIONS (Scale: {}) ---", scale);
        Map<String, List<String>> createdApiKeys = testWrites(scale, testAuthors, scaleKey);

        // 2. FIND Operations
        logger.info("\n--- FIND OPERATIONS (Scale: {}) ---", scale);
        testFinds(scale, createdApiKeys, scaleKey);

        // 3. UPDATE Operations
        logger.info("\n--- UPDATE OPERATIONS (Scale: {}) ---", scale);
        testUpdates(createdApiKeys, testAuthors, scaleKey);

        // 4. DELETE Operations
        logger.info("\n--- DELETE OPERATIONS (Scale: {}) ---", scale);
        testDeletes(createdApiKeys, scaleKey);
    }

    // ==================== WRITE OPERATIONS ====================

    private Map<String, List<String>> testWrites(int count, List<Author> testAuthors, String scaleKey) {
        Map<String, List<String>> createdKeys = new HashMap<>();

        // Test JPA Write
        createdKeys.put("JPA", testWriteJPA(count, testAuthors, scaleKey));

        // Test MongoDB Embedding Write
        createdKeys.put("Mongo Embedding", testWriteMongoEmbedding(count, testAuthors, scaleKey));

        // Test MongoDB Referencing Write
        createdKeys.put("Mongo Referencing", testWriteMongoReferencing(count, testAuthors, scaleKey));

        return createdKeys;
    }

    private List<String> testWriteJPA(int count, List<Author> testAuthors, String scaleKey) {
        List<String> apiKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, "jpa", testAuthors);
                BookDto created = bookService.createBookJpaOnly(command);
                apiKeys.add(created.apiKey());
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Write", "JPA", duration);
            logger.info("✓ JPA Write: {} ms ({} records)", duration, count);
        } catch (Exception e) {
            logger.error("✗ JPA Write failed", e);
        }

        return apiKeys;
    }

    private List<String> testWriteMongoEmbedding(int count, List<Author> testAuthors, String scaleKey) {
        List<String> apiKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, "mongo_emb", testAuthors);
                BookDto created = bookService.createBookWithEmbedding(command);
                apiKeys.add(created.apiKey());
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Write", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Write: {} ms ({} records)", duration, count);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Write failed", e);
        }

        return apiKeys;
    }

    private List<String> testWriteMongoReferencing(int count, List<Author> testAuthors, String scaleKey) {
        List<String> apiKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, "mongo_ref", testAuthors);
                BookDto created = bookService.createBookWithReferencing(command);
                apiKeys.add(created.apiKey());
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Write", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Write: {} ms ({} records)", duration, count);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Write failed", e);
        }

        return apiKeys;
    }

    // ==================== FIND OPERATIONS ====================

    private void testFinds(int scale, Map<String, List<String>> createdApiKeys, String scaleKey) {
        // 1. Find All (no filter)
        testFindAll(createdApiKeys, scaleKey);

        // 2. Find with Filter
        testFindWithFilter(createdApiKeys, scaleKey);

        // 3. Find with Filter + Projection
        testFindWithProjection(createdApiKeys, scaleKey);

        // 4. Find with Filter + Projection + Sorting
        testFindWithSorting(createdApiKeys, scaleKey);
    }

    private void testFindAll(Map<String, List<String>> createdApiKeys, String scaleKey) {
        // JPA Find All
        long startTime = System.currentTimeMillis();
        try {
            List<Book> books = bookRepository.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find All", "JPA", duration);
            logger.info("✓ JPA Find All: {} ms (found {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ JPA Find All failed", e);
        }

        // MongoDB Embedding Find All
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> books = mongoEmbeddingRepo.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find All", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Find All: {} ms (found {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Find All failed", e);
        }

        // MongoDB Referencing Find All
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> books = mongoReferencingRepo.findAll();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find All", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Find All: {} ms (found {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Find All failed", e);
        }
    }

    private void testFindWithFilter(Map<String, List<String>> createdApiKeys, String scaleKey) {
        // JPA Find with Filter
        String jpaKey = createdApiKeys.get("JPA").get(0);
        long startTime = System.currentTimeMillis();
        try {
            Optional<Book> book = bookRepository.findBookByBookApiKey(new ApiKey(jpaKey));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Filter", "JPA", duration);
            logger.info("✓ JPA Find with Filter: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Find with Filter failed", e);
        }

        // MongoDB Embedding Find with Filter
        String embKey = createdApiKeys.get("Mongo Embedding").get(0);
        startTime = System.currentTimeMillis();
        try {
            Optional<BookDocumentEmbedded> book = mongoEmbeddingRepo.findByApiKey(embKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Filter", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Find with Filter: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Find with Filter failed", e);
        }

        // MongoDB Referencing Find with Filter
        String refKey = createdApiKeys.get("Mongo Referencing").get(0);
        startTime = System.currentTimeMillis();
        try {
            Optional<BookDocument> book = mongoReferencingRepo.findByApiKey(refKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Filter", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Find with Filter: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Find with Filter failed", e);
        }
    }

    private void testFindWithProjection(Map<String, List<String>> createdApiKeys, String scaleKey) {
        // JPA Find with Projection
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findAllProjected();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Projection", "JPA", duration);
            logger.info("✓ JPA Find with Projection: {} ms (found {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ JPA Find with Projection failed", e);
        }

        // MongoDB Embedding - projection simulation
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> books = mongoEmbeddingRepo.findAll();
            // Simulate projection by only accessing specific fields
            books.forEach(b -> {
                b.getApiKey();
                b.getName();
                b.getReleaseDate();
            });
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Projection", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Find with Projection: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Find with Projection failed", e);
        }

        // MongoDB Referencing - projection simulation
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> books = mongoReferencingRepo.findAll();
            books.forEach(b -> {
                b.getApiKey();
                b.getName();
                b.getReleaseDate();
            });
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Projection", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Find with Projection: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Find with Projection failed", e);
        }
    }

    private void testFindWithSorting(Map<String, List<String>> createdApiKeys, String scaleKey) {
        // JPA Find with Sorting
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookRepository.findAllProjected();
            books.sort(Comparator.comparing(BookDto::name));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Sorting", "JPA", duration);
            logger.info("✓ JPA Find with Sorting: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Find with Sorting failed", e);
        }

        // MongoDB Embedding Find with Sorting
        startTime = System.currentTimeMillis();
        try {
            List<BookDocumentEmbedded> books = mongoEmbeddingRepo.findAll();
            books.sort(Comparator.comparing(BookDocumentEmbedded::getName));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Sorting", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Find with Sorting: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Find with Sorting failed", e);
        }

        // MongoDB Referencing Find with Sorting
        startTime = System.currentTimeMillis();
        try {
            List<BookDocument> books = mongoReferencingRepo.findAll();
            books.sort(Comparator.comparing(BookDocument::getName));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Find w/ Sorting", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Find with Sorting: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Find with Sorting failed", e);
        }
    }

    // ==================== UPDATE OPERATIONS ====================

    private void testUpdates(Map<String, List<String>> createdApiKeys, List<Author> testAuthors, String scaleKey) {
        // JPA Update
        String jpaKey = createdApiKeys.get("JPA").get(0);
        long startTime = System.currentTimeMillis();
        try {
            Book book = bookRepository.findBookByBookApiKey(new ApiKey(jpaKey)).orElseThrow();
            book.setName(book.getName() + "_updated");
            bookRepository.save(book);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Update", "JPA", duration);
            logger.info("✓ JPA Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ JPA Update failed", e);
        }

        // MongoDB Embedding Update
        String embKey = createdApiKeys.get("Mongo Embedding").get(0);
        startTime = System.currentTimeMillis();
        try {
            BookDocumentEmbedded book = mongoEmbeddingRepo.findByApiKey(embKey).orElseThrow();
            book.setName(book.getName() + "_updated");
            mongoEmbeddingRepo.save(book);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Update", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Update failed", e);
        }

        // MongoDB Referencing Update
        String refKey = createdApiKeys.get("Mongo Referencing").get(0);
        startTime = System.currentTimeMillis();
        try {
            BookDocument book = mongoReferencingRepo.findByApiKey(refKey).orElseThrow();
            book.setName(book.getName() + "_updated");
            mongoReferencingRepo.save(book);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Update", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Update failed", e);
        }
    }

    // ==================== DELETE OPERATIONS ====================

    private void testDeletes(Map<String, List<String>> createdApiKeys, String scaleKey) {
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
            recordResult(scaleKey, "Delete", "JPA", duration);
            logger.info("✓ JPA Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ JPA Delete failed", e);
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
            recordResult(scaleKey, "Delete", "Mongo Embedding", duration);
            logger.info("✓ MongoDB Embedding Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ MongoDB Embedding Delete failed", e);
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
            recordResult(scaleKey, "Delete", "Mongo Referencing", duration);
            logger.info("✓ MongoDB Referencing Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ MongoDB Referencing Delete failed", e);
        }
    }

    // ==================== HELPER METHODS ====================

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
        logger.info("\n");
        logger.info("=".repeat(120));
        logger.info("PERFORMANCE TEST RESULTS - SUMMARY TABLES");
        logger.info("=".repeat(120));

        for (Map.Entry<String, Map<String, Map<String, Long>>> scaleEntry : results.entrySet()) {
            String scale = scaleEntry.getKey();
            Map<String, Map<String, Long>> operations = scaleEntry.getValue();

            logger.info("\n>>> {} <<<", scale);
            logger.info("-".repeat(120));

            logger.info(String.format("%-30s | %-20s | %-20s | %-20s",
                    "Operation", "JPA (ms)", "Mongo Embedding (ms)", "Mongo Referencing (ms)"));
            logger.info("-".repeat(120));

            for (Map.Entry<String, Map<String, Long>> opEntry : operations.entrySet()) {
                String operation = opEntry.getKey();
                Map<String, Long> dbResults = opEntry.getValue();

                String jpa = dbResults.getOrDefault("JPA", -1L).toString();
                String mongoEmb = dbResults.getOrDefault("Mongo Embedding", -1L).toString();
                String mongoRef = dbResults.getOrDefault("Mongo Referencing", -1L).toString();

                if (jpa.equals("-1")) jpa = "N/A";
                if (mongoEmb.equals("-1")) mongoEmb = "N/A";
                if (mongoRef.equals("-1")) mongoRef = "N/A";

                logger.info(String.format("%-30s | %-20s | %-20s | %-20s",
                        operation, jpa, mongoEmb, mongoRef));
            }

            logger.info("-".repeat(120));
        }

        logger.info("\n" + "=".repeat(120));
        logger.info("END OF PERFORMANCE TEST RESULTS");
        logger.info("=".repeat(120));
    }
}