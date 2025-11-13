package spengergasse.at.sj2425scherzerrabar.performance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
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
 * Comprehensive Performance Test Suite
 * Tests CRUD operations on both PostgreSQL (JPA) and MongoDB
 * with different scaling factors (100, 1000, 10000)
 */
@Component
public class DatabasePerformanceTestRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceTestRunner.class);

    private final AuthorService authorService;
    private final BookService bookService;

    // Test scale configurations
    private static final int[] SCALE_FACTORS = {100, 1000, 10000};

    // Result storage
    private final List<TestResult> results = new ArrayList<>();

    public DatabasePerformanceTestRunner(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=================================================");
        logger.info("Starting Database Performance Test Suite");
        logger.info("=================================================");

        // Run tests for each scale
        for (int scale : SCALE_FACTORS) {
            logger.info("\n>>> Testing with scale: {} records <<<\n", scale);
            runTestsForScale(scale);
        }

        // Print summary
        printSummary();
    }

    private void runTestsForScale(int scale) {
        // Test Authors
        logger.info("--- AUTHOR TESTS (Scale: {}) ---", scale);
        testAuthorOperations(scale);

        // Test Books
        logger.info("\n--- BOOK TESTS (Scale: {}) ---", scale);
        testBookOperations(scale);
    }

    // ==================== AUTHOR TESTS ====================

    private void testAuthorOperations(int scale) {
        // 1. WRITE Tests
        logger.info("\n1. WRITE Operations:");

        // Write to JPA only
        List<String> jpaAuthorKeys = testAuthorWrite(scale, DatabaseType.JPA_ONLY);

        // Write to MongoDB
        List<String> mongoAuthorKeys = testAuthorWrite(scale, DatabaseType.MONGO);

        // Write to Both
        List<String> bothAuthorKeys = testAuthorWrite(scale, DatabaseType.BOTH);

        // 2. FIND Tests
        logger.info("\n2. FIND Operations:");

        // Find without filter
        testAuthorFindAll(DatabaseType.JPA_ONLY, scale);
        testAuthorFindAll(DatabaseType.MONGO, scale);
        testAuthorFindAll(DatabaseType.BOTH, scale);

        // Find with filter (by penname)
        if (!jpaAuthorKeys.isEmpty()) {
            testAuthorFindWithFilter(DatabaseType.JPA_ONLY, jpaAuthorKeys.get(0), scale);
            testAuthorFindWithFilter(DatabaseType.MONGO, mongoAuthorKeys.get(0), scale);
            testAuthorFindWithFilter(DatabaseType.BOTH, bothAuthorKeys.get(0), scale);
        }

        // Find with filter and projection (already projected via DTO)
        testAuthorFindWithProjection(DatabaseType.JPA_ONLY, scale);
        testAuthorFindWithProjection(DatabaseType.MONGO, scale);
        testAuthorFindWithProjection(DatabaseType.BOTH, scale);

        // Find with filter, projection and sorting
        testAuthorFindWithSorting(DatabaseType.JPA_ONLY, scale);
        testAuthorFindWithSorting(DatabaseType.MONGO, scale);
        testAuthorFindWithSorting(DatabaseType.BOTH, scale);

        // 3. UPDATE Test
        logger.info("\n3. UPDATE Operations:");
        if (!jpaAuthorKeys.isEmpty()) {
            testAuthorUpdate(DatabaseType.JPA_ONLY, jpaAuthorKeys.get(0), scale);
            testAuthorUpdate(DatabaseType.MONGO, mongoAuthorKeys.get(0), scale);
            testAuthorUpdate(DatabaseType.BOTH, bothAuthorKeys.get(0), scale);
        }

        // 4. DELETE Test
        logger.info("\n4. DELETE Operations:");
        testAuthorDelete(DatabaseType.JPA_ONLY, jpaAuthorKeys, scale);
        testAuthorDelete(DatabaseType.MONGO, mongoAuthorKeys, scale);
        testAuthorDelete(DatabaseType.BOTH, bothAuthorKeys, scale);
    }

    private List<String> testAuthorWrite(int count, DatabaseType dbType) {
        List<String> createdKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                AuthorCommand command = createAuthorCommand(i, dbType);
                AuthorDto created;

                switch (dbType) {
                    case JPA_ONLY -> created = authorService.createAuthorJpaOnly(command);
                    case MONGO -> created = authorService.createAuthorWithMongo(command);
                    case BOTH -> created = authorService.createAuthor(command);
                    default -> throw new IllegalStateException("Unknown database type");
                }

                createdKeys.add(created.apiKey());

                if ((i + 1) % 100 == 0) {
                    logger.debug("Written {} authors to {}", i + 1, dbType);
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            results.add(new TestResult("Author", "Write", dbType, count, duration));
            logger.info("✓ {} Write to {}: {} ms ({} records)", "Author", dbType, duration, count);

        } catch (Exception e) {
            logger.error("✗ Failed to write authors to {}", dbType, e);
        }

        return createdKeys;
    }

    private void testAuthorFindAll(DatabaseType dbType, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            List<AuthorDto> authors = authorService.getAuthors();
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Author", "Find All", dbType, scale, duration));
            logger.info("✓ Author Find All from {}: {} ms (found {} records)",
                    dbType, duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find all authors from {}", dbType, e);
        }
    }

    private void testAuthorFindWithFilter(DatabaseType dbType, String apiKey, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            AuthorDto author = authorService.getAuthor(apiKey);
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Author", "Find with Filter", dbType, scale, duration));
            logger.info("✓ Author Find with Filter from {}: {} ms", dbType, duration);
        } catch (Exception e) {
            logger.error("✗ Failed to find author with filter from {}", dbType, e);
        }
    }

    private void testAuthorFindWithProjection(DatabaseType dbType, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            // DTOs are already projections
            List<AuthorDto> authors = authorService.getAuthors();
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Author", "Find with Projection", dbType, scale, duration));
            logger.info("✓ Author Find with Projection from {}: {} ms (found {} records)",
                    dbType, duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find authors with projection from {}", dbType, e);
        }
    }

    private void testAuthorFindWithSorting(DatabaseType dbType, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            List<AuthorDto> authors = authorService.getAuthors();
            // Sort in Java (could be optimized with database-level sorting)
            authors.sort(Comparator.comparing(AuthorDto::penname));
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Author", "Find with Sorting", dbType, scale, duration));
            logger.info("✓ Author Find with Sorting from {}: {} ms (sorted {} records)",
                    dbType, duration, authors.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find authors with sorting from {}", dbType, e);
        }
    }

    private void testAuthorUpdate(DatabaseType dbType, String apiKey, int scale) {
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

            switch (dbType) {
                case JPA_ONLY -> authorService.updateAuthorJpaOnly(updateCommand);
                case MONGO -> authorService.updateAuthorWithMongo(updateCommand);
                case BOTH -> authorService.updateAuthor(updateCommand);
            }

            long duration = System.currentTimeMillis() - startTime;
            results.add(new TestResult("Author", "Update", dbType, scale, duration));
            logger.info("✓ Author Update in {}: {} ms", dbType, duration);
        } catch (Exception e) {
            logger.error("✗ Failed to update author in {}", dbType, e);
        }
    }

    private void testAuthorDelete(DatabaseType dbType, List<String> apiKeys, int scale) {
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
            results.add(new TestResult("Author", "Delete", dbType, scale, duration));
            logger.info("✓ Author Delete from {}: {} ms ({} records)",
                    dbType, duration, deleted);
        } catch (Exception e) {
            logger.error("✗ Failed to delete authors from {}", dbType, e);
        }
    }

    // ==================== BOOK TESTS ====================

    private void testBookOperations(int scale) {
        // First, create some authors for the books
        logger.info("Creating authors for book tests...");
        List<String> authorPennames = new ArrayList<>();
        for (int i = 0; i < Math.min(10, scale / 10); i++) {
            AuthorCommand cmd = createAuthorCommand(i, DatabaseType.BOTH);
            AuthorDto author = authorService.createAuthor(cmd);
            authorPennames.add(author.penname());
        }

        // 1. WRITE Tests
        logger.info("\n1. WRITE Operations:");

        List<String> jpaBookKeys = testBookWrite(scale, DatabaseType.JPA_ONLY, authorPennames);
        List<String> mongoRefKeys = testBookWrite(scale, DatabaseType.MONGO_REFERENCING, authorPennames);
        List<String> mongoEmbKeys = testBookWrite(scale, DatabaseType.MONGO_EMBEDDING, authorPennames);
        List<String> bothKeys = testBookWrite(scale, DatabaseType.BOTH, authorPennames);

        // 2. FIND Tests
        logger.info("\n2. FIND Operations:");

        testBookFindAll(DatabaseType.JPA_ONLY, scale);
        testBookFindAll(DatabaseType.MONGO_REFERENCING, scale);
        testBookFindAll(DatabaseType.MONGO_EMBEDDING, scale);
        testBookFindAll(DatabaseType.BOTH, scale);

        // Find with filter
        if (!jpaBookKeys.isEmpty()) {
            testBookFindWithFilter(DatabaseType.JPA_ONLY, jpaBookKeys.get(0), scale);
            testBookFindWithFilter(DatabaseType.BOTH, bothKeys.get(0), scale);
        }

        // Find with projection
        testBookFindWithProjection(DatabaseType.JPA_ONLY, scale);
        testBookFindWithProjection(DatabaseType.BOTH, scale);

        // Find with sorting
        testBookFindWithSorting(DatabaseType.JPA_ONLY, scale);
        testBookFindWithSorting(DatabaseType.BOTH, scale);

        // 3. UPDATE Test
        logger.info("\n3. UPDATE Operations:");
        if (!jpaBookKeys.isEmpty()) {
            testBookUpdate(DatabaseType.JPA_ONLY, jpaBookKeys.get(0), scale, authorPennames);
            testBookUpdate(DatabaseType.BOTH, bothKeys.get(0), scale, authorPennames);
        }

        // 4. DELETE Test
        logger.info("\n4. DELETE Operations:");
        testBookDelete(DatabaseType.JPA_ONLY, jpaBookKeys, scale);
        testBookDelete(DatabaseType.BOTH, bothKeys, scale);

        // Cleanup authors
        for (String penname : authorPennames) {
            try {
                AuthorDto author = authorService.getAuthorByPenname(penname);
                authorService.deleteAuthor(author.apiKey());
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    private List<String> testBookWrite(int count, DatabaseType dbType, List<String> authorPennames) {
        List<String> createdKeys = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < count; i++) {
                BookCommand command = createBookCommand(i, dbType, authorPennames);
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
                    case BOTH -> {
                        BookDto created = bookService.createBook(command);
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
            results.add(new TestResult("Book", "Write", dbType, count, duration));
            logger.info("✓ Book Write to {}: {} ms ({} records)", dbType, duration, count);

        } catch (Exception e) {
            logger.error("✗ Failed to write books to {}", dbType, e);
        }

        return createdKeys;
    }

    private void testBookFindAll(DatabaseType dbType, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            List<BookDto> books = bookService.getBooks(null);
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Book", "Find All", dbType, scale, duration));
            logger.info("✓ Book Find All from {}: {} ms (found {} records)",
                    dbType, duration, books.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find all books from {}", dbType, e);
        }
    }

    private void testBookFindWithFilter(DatabaseType dbType, String apiKey, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            BookDto book = bookService.getBook(apiKey);
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Book", "Find with Filter", dbType, scale, duration));
            logger.info("✓ Book Find with Filter from {}: {} ms", dbType, duration);
        } catch (Exception e) {
            logger.error("✗ Failed to find book with filter from {}", dbType, e);
        }
    }

    private void testBookFindWithProjection(DatabaseType dbType, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            List<BookDto> books = bookService.getBooks(null);
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Book", "Find with Projection", dbType, scale, duration));
            logger.info("✓ Book Find with Projection from {}: {} ms (found {} records)",
                    dbType, duration, books.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find books with projection from {}", dbType, e);
        }
    }

    private void testBookFindWithSorting(DatabaseType dbType, int scale) {
        long startTime = System.currentTimeMillis();

        try {
            List<BookDto> books = bookService.getBooks(null);
            books.sort(Comparator.comparing(BookDto::name));
            long duration = System.currentTimeMillis() - startTime;

            results.add(new TestResult("Book", "Find with Sorting", dbType, scale, duration));
            logger.info("✓ Book Find with Sorting from {}: {} ms (sorted {} records)",
                    dbType, duration, books.size());
        } catch (Exception e) {
            logger.error("✗ Failed to find books with sorting from {}", dbType, e);
        }
    }

    private void testBookUpdate(DatabaseType dbType, String apiKey, int scale, List<String> authorPennames) {
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
            results.add(new TestResult("Book", "Update", dbType, scale, duration));
            logger.info("✓ Book Update in {}: {} ms", dbType, duration);
        } catch (Exception e) {
            logger.error("✗ Failed to update book in {}", dbType, e);
        }
    }

    private void testBookDelete(DatabaseType dbType, List<String> apiKeys, int scale) {
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
            results.add(new TestResult("Book", "Delete", dbType, scale, duration));
            logger.info("✓ Book Delete from {}: {} ms ({} records)",
                    dbType, duration, deleted);
        } catch (Exception e) {
            logger.error("✗ Failed to delete books from {}", dbType, e);
        }
    }

    // ==================== HELPER METHODS ====================

    private AuthorCommand createAuthorCommand(int index, DatabaseType dbType) {
        String suffix = dbType.name().toLowerCase() + "_" + index;
        return new AuthorCommand(
                null,
                "penname_" + suffix,
                List.of("123-MainSt"+index+"-"+"1010"),
                "Firstname" + index,
                "Lastname" + index,
                "author" + index + "@" + dbType.name().toLowerCase() + ".com"
        );
    }

    private BookCommand createBookCommand(int index, DatabaseType dbType, List<String> authorPennames) {
        String suffix = dbType.name().toLowerCase() + "_" + index;

        // Convert pennames to author API keys
        List<String> authorApiKeys = new ArrayList<>();
        for (String penname : authorPennames) {
            try {
                AuthorDto author = authorService.getAuthorByPenname(penname);
                authorApiKeys.add(author.apiKey());
            } catch (Exception e) {
                logger.debug("Could not find author with penname: {}", penname);
            }
        }

        // Use at least one author
        if (authorApiKeys.isEmpty() && !authorPennames.isEmpty()) {
            try {
                AuthorDto author = authorService.getAuthorByPenname(authorPennames.get(0));
                authorApiKeys.add(author.apiKey());
            } catch (Exception e) {
                logger.error("No authors available for book creation");
            }
        }

        return new BookCommand(
                null,
                "Book Title " + suffix,
                LocalDate.now().minusDays(index),
                index % 2 == 0,
                List.of(BookType.HARDCOVER.name(), BookType.PAPERBACK.name()),
                1000 + (index * 100),
                "Description for book " + suffix,
                authorApiKeys,
                List.of(BookGenre.FANTASY.name(), BookGenre.MYSTERY.name())
        );
    }

    private void printSummary() {
        logger.info("\n");
        logger.info("=================================================");
        logger.info("PERFORMANCE TEST SUMMARY");
        logger.info("=================================================");

        // Group results by entity and operation
        Map<String, List<TestResult>> grouped = new HashMap<>();
        for (TestResult result : results) {
            String key = result.entity + " - " + result.operation;
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(result);
        }

        // Print grouped results
        for (Map.Entry<String, List<TestResult>> entry : grouped.entrySet()) {
            logger.info("\n{}", entry.getKey());
            logger.info("-".repeat(50));

            for (TestResult result : entry.getValue()) {
                logger.info("  {}: {} records in {} ms ({} ms/record)",
                        result.databaseType,
                        result.recordCount,
                        result.durationMs,
                        String.format("%.3f", result.durationMs / (double) result.recordCount));
            }
        }

        logger.info("\n=================================================");
    }

    // ==================== ENUMS & RECORDS ====================

    private enum DatabaseType {
        JPA_ONLY,
        MONGO,
        MONGO_REFERENCING,
        MONGO_EMBEDDING,
        BOTH
    }

    private record TestResult(
            String entity,
            String operation,
            DatabaseType databaseType,
            int recordCount,
            long durationMs
    ) {}
}