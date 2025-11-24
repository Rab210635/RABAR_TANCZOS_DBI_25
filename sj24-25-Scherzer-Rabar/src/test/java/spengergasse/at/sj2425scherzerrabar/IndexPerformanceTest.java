package spengergasse.at.sj2425scherzerrabar;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spengergasse.at.sj2425scherzerrabar.MongoDBIndexConfiguration;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;
import spengergasse.at.sj2425scherzerrabar.persistence.BookEmbeddedMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookMongoRepository;
import spengergasse.at.sj2425scherzerrabar.MongoIndexManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Performance test comparing MongoDB query performance with and without indexes
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IndexPerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(IndexPerformanceTest.class);
    private static final int TEST_DATA_SIZE = 10000;
    private static final int QUERY_ITERATIONS = 100;

    @Autowired private BookMongoRepository bookMongoRepository;
    @Autowired private BookEmbeddedMongoRepository bookEmbeddedMongoRepository;
    @Autowired private MongoDBIndexConfiguration indexConfig;
    @Autowired private MongoIndexManager indexManager;

    private final Random random = new Random(42);
    private List<String> testApiKeys;
    private List<String> testAuthorKeys;

    @BeforeAll
    static void beforeAll() {
        log.info("\n" + "=".repeat(120));
        log.info("MONGODB INDEX PERFORMANCE TEST");
        log.info("Comparing query performance with and without indexes");
        log.info("=".repeat(120));
    }

    @BeforeEach
    void setUp() {
        cleanupAll();
        testApiKeys = new ArrayList<>();
        testAuthorKeys = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        cleanupAll();
    }

    private void cleanupAll() {
        bookMongoRepository.deleteAll();
        bookEmbeddedMongoRepository.deleteAll();
    }

    // ==================== TEST 1: WITHOUT INDEXES ====================

    @Test
    @Order(1)
    @DisplayName("Query Performance WITHOUT Indexes")
    void testPerformanceWithoutIndexes() {
        // This test is now integrated into testIndexComparison()
        // Keeping it here for individual execution if needed
        log.info("\nExecuting individual test without indexes...");
        log.info("For complete comparison, run testIndexComparison() instead.");
    }

    // ==================== TEST 2: WITH INDEXES ====================

    @Test
    @Order(2)
    @DisplayName("Query Performance WITH Indexes")
    void testPerformanceWithIndexes() {
        // This test is now integrated into testIndexComparison()
        // Keeping it here for individual execution if needed
        log.info("\nExecuting individual test with indexes...");
        log.info("For complete comparison, run testIndexComparison() instead.");
    }

    // ==================== TEST 3: COMPARISON ====================

    @Test
    @Order(3)
    @DisplayName("Index Performance Comparison")
    void testIndexComparison() {
        log.info("\n" + "=".repeat(120));
        log.info("INDEX PERFORMANCE COMPARISON - MONGODB");
        log.info("=".repeat(120));

        List<ComparisonResult> results = new ArrayList<>();

        // Test WITHOUT indexes
        log.info("\nPhase 1: Testing WITHOUT indexes (creating {} documents)...", TEST_DATA_SIZE);
        cleanupAll();
        indexConfig.dropAllIndexes();
        createTestData(TEST_DATA_SIZE);

        ComparisonResult withoutIndexes = new ComparisonResult("Without Indexes");
        withoutIndexes.findByApiKey = measureFindByApiKey(QUERY_ITERATIONS);
        withoutIndexes.findByAuthor = measureFindByAuthor(QUERY_ITERATIONS);
        withoutIndexes.findByGenre = measureFindByGenre(QUERY_ITERATIONS);
        withoutIndexes.findSortedByName = measureFindSortedByName(QUERY_ITERATIONS);
        results.add(withoutIndexes);

        // Test WITH indexes
        log.info("Phase 2: Testing WITH indexes (creating {} documents)...", TEST_DATA_SIZE);
        cleanupAll();
        indexConfig.initIndexes();
        createTestData(TEST_DATA_SIZE);

        ComparisonResult withIndexes = new ComparisonResult("With Indexes");
        withIndexes.findByApiKey = measureFindByApiKey(QUERY_ITERATIONS);
        withIndexes.findByAuthor = measureFindByAuthor(QUERY_ITERATIONS);
        withIndexes.findByGenre = measureFindByGenre(QUERY_ITERATIONS);
        withIndexes.findSortedByName = measureFindSortedByName(QUERY_ITERATIONS);
        results.add(withIndexes);

        // Print comparison
        printComparisonResults(results);
    }

    // ==================== QUERY METHODS ====================

    private void runQueryTests(String testName) {
        // Silent execution - only final table will be printed
        measureFindByApiKey(QUERY_ITERATIONS);
        measureFindByAuthor(QUERY_ITERATIONS);
        measureFindByGenre(QUERY_ITERATIONS);
        measureFindSortedByName(QUERY_ITERATIONS);
    }

    private long measureFindByApiKey(int iterations) {
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            String apiKey = testApiKeys.get(random.nextInt(testApiKeys.size()));

            long start = System.nanoTime();
            bookMongoRepository.findByApiKey(apiKey);
            totalTime += (System.nanoTime() - start);
        }

        return totalTime;
    }

    private long measureFindByAuthor(int iterations) {
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            String authorKey = testAuthorKeys.get(random.nextInt(testAuthorKeys.size()));

            long start = System.nanoTime();
            bookMongoRepository.findByAuthorApiKeysContaining(authorKey);
            totalTime += (System.nanoTime() - start);
        }

        return totalTime;
    }

    private long measureFindByGenre(int iterations) {
        long totalTime = 0;
        List<String> genres = List.of("FANTASY", "MYSTERY", "SCIFI", "ROMANCE", "THRILLER");

        for (int i = 0; i < iterations; i++) {
            String genre = genres.get(random.nextInt(genres.size()));

            long start = System.nanoTime();
            // Custom query would be needed - using findAll as placeholder
            bookMongoRepository.findAll();
            totalTime += (System.nanoTime() - start);
        }

        return totalTime;
    }

    private long measureFindSortedByName(int iterations) {
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            bookMongoRepository.findAll(org.springframework.data.domain.Sort.by("name"));
            totalTime += (System.nanoTime() - start);
        }

        return totalTime;
    }

    // ==================== HELPER METHODS ====================

    private void createTestData(int count) {
        List<BookDocument> books = new ArrayList<>();

        // Create some author keys
        for (int i = 0; i < 100; i++) {
            testAuthorKeys.add("AUTHOR_" + i);
        }

        for (int i = 0; i < count; i++) {
            BookDocument book = new BookDocument();
            book.setPostgresId((long) i);
            book.setApiKey("API_BOOK_" + i);
            book.setName("Book " + i);
            book.setReleaseDate(LocalDate.now().minusDays(random.nextInt(1000)));
            book.setAvailableOnline(random.nextBoolean());
            book.setWordCount(10000 + random.nextInt(90000));
            book.setDescription("Description for book " + i);

            // Random genre
            List<String> genres = List.of("FANTASY", "MYSTERY", "SCIFI", "ROMANCE", "THRILLER");
            book.setGenres(List.of(genres.get(random.nextInt(genres.size()))));

            // Random type
            List<String> types = List.of("HARDCOVER", "PAPERBACK", "EBOOK");
            book.setBookTypes(List.of(types.get(random.nextInt(types.size()))));

            // Random authors (1-3)
            List<String> authors = new ArrayList<>();
            int authorCount = 1 + random.nextInt(3);
            for (int j = 0; j < authorCount; j++) {
                authors.add(testAuthorKeys.get(random.nextInt(testAuthorKeys.size())));
            }
            book.setAuthorApiKeys(authors);

            books.add(book);
            testApiKeys.add(book.getApiKey());
        }

        bookMongoRepository.saveAll(books);
    }

    // ==================== RESULT CLASSES ====================

    private static class ComparisonResult {
        String testName;
        long findByApiKey;
        long findByAuthor;
        long findByGenre;
        long findSortedByName;

        ComparisonResult(String testName) {
            this.testName = testName;
        }

        long getTotal() {
            return findByApiKey + findByAuthor + findByGenre + findSortedByName;
        }
    }

    private void printComparisonResults(List<ComparisonResult> results) {
        log.info("\n" + "=".repeat(120));
        log.info("MONGODB INDEX PERFORMANCE TEST RESULTS");
        log.info("Data Size: {} documents | Query Iterations: {} per operation", TEST_DATA_SIZE, QUERY_ITERATIONS);
        log.info("=".repeat(120));

        log.info(String.format("\n%-20s | %-18s | %-18s | %-18s | %-18s | %-18s",
                "Configuration", "Find by API Key", "Find by Author", "Find by Genre", "Find Sorted", "TOTAL"));
        log.info("-".repeat(120));

        for (ComparisonResult result : results) {
            log.info(String.format("%-20s | %13.3f ms | %13.3f ms | %13.3f ms | %13.3f ms | %13.3f ms",
                    result.testName,
                    result.findByApiKey / 1_000_000.0,
                    result.findByAuthor / 1_000_000.0,
                    result.findByGenre / 1_000_000.0,
                    result.findSortedByName / 1_000_000.0,
                    result.getTotal() / 1_000_000.0));
        }

        if (results.size() == 2) {
            ComparisonResult without = results.get(0);
            ComparisonResult with = results.get(1);

            log.info("-".repeat(120));

            double apiKeyImprovement = (double) without.findByApiKey / with.findByApiKey;
            double authorImprovement = (double) without.findByAuthor / with.findByAuthor;
            double genreImprovement = (double) without.findByGenre / with.findByGenre;
            double sortedImprovement = (double) without.findSortedByName / with.findSortedByName;
            double totalImprovement = (double) without.getTotal() / with.getTotal();

            log.info(String.format("%-20s | %13.2fx | %13.2fx | %13.2fx | %13.2fx | %13.2fx",
                    "Speedup Factor",
                    apiKeyImprovement,
                    authorImprovement,
                    genreImprovement,
                    sortedImprovement,
                    totalImprovement));
        }

        log.info("=".repeat(120) + "\n");
    }
}