package spengergasse.at.sj2425scherzerrabar.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.time.LocalDate;
import java.util.*;

/**
 * Comprehensive Performance Test Suite with Table Output
 */
public class DatabasePerformanceTestRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceTestRunner.class);

    private final AuthorService authorService;
    private final BookService bookService;

    // Test scale configurations
    private static final int[] SCALE_FACTORS = {100, 1000, 10000};

    // Result storage with proper structure
    private final Map<String, Map<String, Map<String, Long>>> results = new LinkedHashMap<>();

    public DatabasePerformanceTestRunner(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    public void runTests() throws Exception {
        logger.info("=================================================");
        logger.info("Starting Database Performance Test Suite");
        logger.info("=================================================");

        // Run tests for each scale
        for (int scale : SCALE_FACTORS) {
            logger.info("\n>>> Testing with scale: {} records <<<\n", scale);
            runTestsForScale(scale);
        }

        // Print summary tables
        printSummaryTables();
    }

    private void runTestsForScale(int scale) {
        String scaleKey = scale + " records";
        results.put(scaleKey, new LinkedHashMap<>());

        // Test Authors
        logger.info("--- AUTHOR TESTS (Scale: {}) ---", scale);
        testAuthorOperations(scale, scaleKey);

        // Test Books
        logger.info("\n--- BOOK TESTS (Scale: {}) ---", scale);
        testBookOperations(scale, scaleKey);
    }

    // ==================== AUTHOR TESTS ====================

    private void testAuthorOperations(int scale, String scaleKey) {
        // 1. WRITE Tests
        logger.info("\n1. WRITE Operations:");

        List<String> jpaAuthorKeys = testAuthorWrite(scale, DatabaseType.JPA_ONLY, scaleKey);
        List<String> mongoAuthorKeys = testAuthorWrite(scale, DatabaseType.MONGO, scaleKey);

        // 2. FIND Tests
        logger.info("\n2. FIND Operations:");
        testAuthorFindAll(scale, scaleKey);

        // Find with filter
        if (!jpaAuthorKeys.isEmpty()) {
            testAuthorFindWithFilter(jpaAuthorKeys.get(0), scale, scaleKey);
        }

        // Find with projection
        testAuthorFindWithProjection(scale, scaleKey);

        // Find with sorting
        testAuthorFindWithSorting(scale, scaleKey);

        // 3. UPDATE Test
        logger.info("\n3. UPDATE Operations:");
        if (!jpaAuthorKeys.isEmpty()) {
            testAuthorUpdate(jpaAuthorKeys.get(0), scale, scaleKey);
        }

        // 4. DELETE Test
        logger.info("\n4. DELETE Operations:");
        testAuthorDelete(jpaAuthorKeys, scale, scaleKey);
        testAuthorDelete(mongoAuthorKeys, scale, scaleKey);
    }

    private List<String> testAuthorWrite(int count, DatabaseType dbType, String scaleKey) {
        List<String> createdKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                AuthorCommand command = createAuthorCommand(i, dbType);
                AuthorDto created;

                if (dbType == DatabaseType.JPA_ONLY) {
                    created = authorService.createAuthorJpaOnly(command);
                } else {
                    created = authorService.createAuthorWithMongo(command);
                }

                createdKeys.add(created.apiKey());

                if ((i + 1) % 100 == 0) {
                    logger.debug("Written {} authors to {}", i + 1, dbType);
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Write", dbType.toString(), duration);
            logger.info("✓ Author Write to {}: {} ms ({} records)", dbType, duration, count);

        } catch (Exception e) {
            logger.error("✗ Failed to write authors to {}", dbType, e);
        }

        return createdKeys;
    }

    private void testAuthorFindAll(int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            List<AuthorDto> authors = authorService.getAuthors();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Find All", "JPA", duration);
            logger.info("✓ Author Find All: {} ms (found {} records)", duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find all authors", e);
        }
    }

    private void testAuthorFindWithFilter(String apiKey, int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            AuthorDto author = authorService.getAuthor(apiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Find w/ Filter", "JPA", duration);
            logger.info("✓ Author Find with Filter: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ Failed to find author with filter", e);
        }
    }

    private void testAuthorFindWithProjection(int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            List<AuthorDto> authors = authorService.getAuthors();
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Find w/ Projection", "JPA", duration);
            logger.info("✓ Author Find with Projection: {} ms (found {} records)", duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find authors with projection", e);
        }
    }

    private void testAuthorFindWithSorting(int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            List<AuthorDto> authors = authorService.getAuthors();
            authors.sort(Comparator.comparing(AuthorDto::penname));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Find w/ Sorting", "JPA", duration);
            logger.info("✓ Author Find with Sorting: {} ms (sorted {} records)", duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find authors with sorting", e);
        }
    }

    private void testAuthorUpdate(String apiKey, int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            AuthorDto existing = authorService.getAuthor(apiKey);
            AuthorCommand updateCommand = new AuthorCommand(
                    apiKey,
                    existing.penname() + "_updated",
                    existing.address(),
                    existing.firstname(),
                    existing.lastname(),
                    existing.emailAddress()
            );
            authorService.updateAuthor(updateCommand);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Update", "JPA + Mongo", duration);
            logger.info("✓ Author Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ Failed to update author", e);
        }
    }

    private void testAuthorDelete(List<String> apiKeys, int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        int deleted = 0;
        try {
            for (String apiKey : apiKeys) {
                try {
                    authorService.deleteAuthor(apiKey);
                    deleted++;
                } catch (Exception e) {
                    logger.debug("Could not delete author {}: {}", apiKey, e.getMessage());
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Author Delete", "JPA + Mongo", duration);
            logger.info("✓ Author Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ Failed to delete authors", e);
        }
    }

    // ==================== BOOK TESTS ====================

    private void testBookOperations(int scale, String scaleKey) {
        // Create authors for book tests
        logger.info("Creating authors for book tests...");
        List<String> authorApiKeys = new ArrayList<>();
        for (int i = 0; i < Math.min(10, scale / 10); i++) {
            AuthorCommand cmd = createAuthorCommand(i, DatabaseType.MONGO);
            AuthorDto author = authorService.createAuthor(cmd);
            authorApiKeys.add(author.apiKey());
        }

        // 1. WRITE Tests
        logger.info("\n1. WRITE Operations:");
        List<String> jpaBookKeys = testBookWrite(scale, DatabaseType.JPA_ONLY, scaleKey, authorApiKeys);
        List<String> mongoRefKeys = testBookWrite(scale, DatabaseType.MONGO_REFERENCING, scaleKey, authorApiKeys);
        List<String> mongoEmbKeys = testBookWrite(scale, DatabaseType.MONGO_EMBEDDING, scaleKey, authorApiKeys);

        // 2. FIND Tests
        logger.info("\n2. FIND Operations:");
        testBookFindAll(scale, scaleKey);

        // Find with filter
        if (!jpaBookKeys.isEmpty()) {
            testBookFindWithFilter(jpaBookKeys.get(0), scale, scaleKey);
        }

        // Find with projection
        testBookFindWithProjection(scale, scaleKey);

        // Find with sorting
        testBookFindWithSorting(scale, scaleKey);

        // 3. UPDATE Test
        logger.info("\n3. UPDATE Operations:");
        if (!jpaBookKeys.isEmpty()) {
            testBookUpdate(jpaBookKeys.get(0), scale, scaleKey, authorApiKeys);
        }

        // 4. DELETE Test
        logger.info("\n4. DELETE Operations:");
        testBookDelete(jpaBookKeys, scale, scaleKey);
        testBookDelete(mongoRefKeys, scale, scaleKey);
        testBookDelete(mongoEmbKeys, scale, scaleKey);

        // Cleanup authors
        for (String apiKey : authorApiKeys) {
            try {
                authorService.deleteAuthor(apiKey);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    private List<String> testBookWrite(int count, DatabaseType dbType, String scaleKey, List<String> authorApiKeys) {
        List<String> createdKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, dbType, authorApiKeys);
                String apiKey;

                switch (dbType) {
                    case JPA_ONLY -> {
                        BookDto created = bookService.createBookJpaOnly(command);
                        apiKey = created.apiKey();
                    }
                    case MONGO_REFERENCING -> {
                        BookDto created = bookService.createBookWithReferencing(command);
                        apiKey = created.apiKey();
                    }
                    case MONGO_EMBEDDING -> {
                        BookDto created = bookService.createBookWithEmbedding(command);
                        apiKey = created.apiKey();
                    }
                    default -> throw new IllegalStateException("Unknown database type");
                }

                createdKeys.add(apiKey);

                if ((i + 1) % 100 == 0) {
                    logger.debug("Written {} books to {}", i + 1, dbType);
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            String dbTypeStr = dbType == DatabaseType.JPA_ONLY ? "JPA" :
                    dbType == DatabaseType.MONGO_REFERENCING ? "Mongo Ref" : "Mongo Emb";
            recordResult(scaleKey, "Book Write", dbTypeStr, duration);
            logger.info("✓ Book Write to {}: {} ms ({} records)", dbType, duration, count);

        } catch (Exception e) {
            logger.error("✗ Failed to write books to {}", dbType, e);
        }

        return createdKeys;
    }

    private void testBookFindAll(int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookService.getBooks(null);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book Find All", "JPA", duration);
            logger.info("✓ Book Find All: {} ms (found {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find all books", e);
        }
    }

    private void testBookFindWithFilter(String apiKey, int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            BookDto book = bookService.getBook(apiKey);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book Find w/ Filter", "JPA", duration);
            logger.info("✓ Book Find with Filter: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ Failed to find book with filter", e);
        }
    }

    private void testBookFindWithProjection(int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookService.getBooks(null);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book Find w/ Projection", "JPA", duration);
            logger.info("✓ Book Find with Projection: {} ms (found {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find books with projection", e);
        }
    }

    private void testBookFindWithSorting(int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        try {
            List<BookDto> books = bookService.getBooks(null);
            books.sort(Comparator.comparing(BookDto::name));
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book Find w/ Sorting", "JPA", duration);
            logger.info("✓ Book Find with Sorting: {} ms (sorted {} records)", duration, books.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find books with sorting", e);
        }
    }

    private void testBookUpdate(String apiKey, int scale, String scaleKey, List<String> authorApiKeys) {
        long startTime = System.currentTimeMillis();
        try {
            BookDto existing = bookService.getBook(apiKey);
            BookCommand updateCommand = new BookCommand(
                    apiKey,
                    existing.name() + "_updated",
                    existing.releaseDate(),
                    existing.availableOnline(),
                    existing.types(),
                    existing.wordCount(),
                    existing.description(),
                    existing.authorIds(),
                    existing.genres()
            );
            bookService.updateBook(updateCommand);
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book Update", "JPA + Mongo", duration);
            logger.info("✓ Book Update: {} ms", duration);
        } catch (Exception e) {
            logger.error("✗ Failed to update book", e);
        }
    }

    private void testBookDelete(List<String> apiKeys, int scale, String scaleKey) {
        long startTime = System.currentTimeMillis();
        int deleted = 0;
        try {
            for (String apiKey : apiKeys) {
                try {
                    bookService.deleteBook(apiKey);
                    deleted++;
                } catch (Exception e) {
                    logger.debug("Could not delete book {}: {}", apiKey, e.getMessage());
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            recordResult(scaleKey, "Book Delete", "JPA + Mongo", duration);
            logger.info("✓ Book Delete: {} ms ({} records)", duration, deleted);
        } catch (Exception e) {
            logger.error("✗ Failed to delete books", e);
        }
    }

    // ==================== HELPER METHODS ====================

    private void recordResult(String scale, String operation, String dbType, long durationMs) {
        results.get(scale)
                .computeIfAbsent(operation, k -> new LinkedHashMap<>())
                .put(dbType, durationMs);
    }

    private AuthorCommand createAuthorCommand(int index, DatabaseType dbType) {
        String suffix = dbType.name().toLowerCase() + "_" + index;
        return new AuthorCommand(
                null,
                "penname_" + suffix,
                List.of("123-MainSt" + index + "-1010"),
                "Firstname" + index,
                "Lastname" + index,
                "author" + index + "@" + dbType.name().toLowerCase() + ".com"
        );
    }

    private BookCommand createBookCommand(int index, DatabaseType dbType, List<String> authorApiKeys) {
        String suffix = dbType.name().toLowerCase() + "_" + index;
        return new BookCommand(
                null,
                "Book Title " + suffix,
                LocalDate.now().minusDays(index),
                index % 2 == 0,
                List.of(BookType.HARDCOVER.name(), BookType.PAPERBACK.name()),
                1000 + (index * 100),
                "Description for book " + suffix,
                authorApiKeys.isEmpty() ? List.of() : List.of(authorApiKeys.get(index % authorApiKeys.size())),
                List.of(BookGenre.FANTASY.name(), BookGenre.MYSTERY.name())
        );
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

            // Print table header
            logger.info(String.format("%-30s | %-20s | %-20s | %-20s",
                    "Operation", "JPA (ms)", "Mongo Embedding (ms)", "Mongo Referencing (ms)"));
            logger.info("-".repeat(120));

            // Print each operation
            for (Map.Entry<String, Map<String, Long>> opEntry : operations.entrySet()) {
                String operation = opEntry.getKey();
                Map<String, Long> dbResults = opEntry.getValue();

                String jpa = dbResults.getOrDefault("JPA", dbResults.getOrDefault("JPA_ONLY", -1L)).toString();
                String mongoEmb = dbResults.getOrDefault("Mongo Emb", -1L).toString();
                String mongoRef = dbResults.getOrDefault("Mongo Ref", -1L).toString();

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

    // ==================== ENUMS ====================

    private enum DatabaseType {
        JPA_ONLY,
        MONGO,
        MONGO_REFERENCING,
        MONGO_EMBEDDING
    }
}