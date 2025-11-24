package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.persistence.*;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceTestService {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMongoRepository bookMongoRepository;
    private final BookEmbeddedMongoRepository bookEmbeddedMongoRepository;
    private final AuthorMongoRepository authorMongoRepository;
    private final MongoTemplate mongoTemplate;
    private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestService.class);

    public PerformanceTestService(BookService bookService,
                                  AuthorService authorService,
                                  BookRepository bookRepository,
                                  AuthorRepository authorRepository,
                                  BookMongoRepository bookMongoRepository,
                                  BookEmbeddedMongoRepository bookEmbeddedMongoRepository,
                                  AuthorMongoRepository authorMongoRepository,
                                  MongoTemplate mongoTemplate,
                                  EntityManager entityManager) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMongoRepository = bookMongoRepository;
        this.bookEmbeddedMongoRepository = bookEmbeddedMongoRepository;
        this.authorMongoRepository = authorMongoRepository;
        this.mongoTemplate = mongoTemplate;
        this.entityManager = entityManager;
    }

    // ==================== INDEX MANAGEMENT ====================

    public void createMongoIndexes() {
        logger.info("Creating MongoDB indexes...");

        // Indexes for BookDocument (Referencing)
        mongoTemplate.indexOps(BookDocument.class)
                .ensureIndex(new Index().on("api_key", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(BookDocument.class)
                .ensureIndex(new Index().on("name", Sort.Direction.ASC));
        mongoTemplate.indexOps(BookDocument.class)
                .ensureIndex(new Index().on("genres", Sort.Direction.ASC));
        mongoTemplate.indexOps(BookDocument.class)
                .ensureIndex(new Index().on("author_api_keys", Sort.Direction.ASC));

        // Indexes for BookDocumentEmbedded
        mongoTemplate.indexOps(BookDocumentEmbedded.class)
                .ensureIndex(new Index().on("api_key", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(BookDocumentEmbedded.class)
                .ensureIndex(new Index().on("name", Sort.Direction.ASC));
        mongoTemplate.indexOps(BookDocumentEmbedded.class)
                .ensureIndex(new Index().on("genres", Sort.Direction.ASC));
        mongoTemplate.indexOps(BookDocumentEmbedded.class)
                .ensureIndex(new Index().on("authors.api_key", Sort.Direction.ASC));
        mongoTemplate.indexOps(BookDocumentEmbedded.class)
                .ensureIndex(new Index().on("authors.penname", Sort.Direction.ASC));

        // Indexes for AuthorDocument
        mongoTemplate.indexOps(AuthorDocument.class)
                .ensureIndex(new Index().on("api_key", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(AuthorDocument.class)
                .ensureIndex(new Index().on("penname", Sort.Direction.ASC));
        mongoTemplate.indexOps(AuthorDocument.class)
                .ensureIndex(new Index().on("email", Sort.Direction.ASC));

        logger.info("MongoDB indexes created successfully");
    }

    public void dropMongoIndexes() {
        logger.info("Dropping MongoDB indexes...");

        mongoTemplate.indexOps(BookDocument.class).dropAllIndexes();
        mongoTemplate.indexOps(BookDocumentEmbedded.class).dropAllIndexes();
        mongoTemplate.indexOps(AuthorDocument.class).dropAllIndexes();

        logger.info("MongoDB indexes dropped successfully");
    }

    // ==================== TEST EXECUTION ====================

    public PerformanceTestResults runAllTests(int scale) {
        logger.info("Starting performance tests with scale: {}", scale);

        PerformanceTestResults results = new PerformanceTestResults();

        // Clean databases before tests
        cleanDatabases();

        // Test WITHOUT indexes
        logger.info("=== Testing WITHOUT indexes ===");
        results.withoutIndexes = runTestSuite(scale, false);

        // Clean and recreate indexes
        cleanDatabases();
        createMongoIndexes();
        createPostgresIndexes();

        // Test WITH indexes
        logger.info("=== Testing WITH indexes ===");
        results.withIndexes = runTestSuite(scale, true);

        logger.info("Performance tests completed");
        return results;
    }

    private TestSuiteResults runTestSuite(int scale, boolean withIndexes) {
        TestSuiteResults results = new TestSuiteResults();

        // 1. Write Tests
        results.writeTestSmall = runWriteTest(100, withIndexes);
        results.writeTestMedium = runWriteTest(1000, withIndexes);
        if (scale >= 10000) {
            results.writeTestLarge = runWriteTest(10000, withIndexes);
        }

        // 2. Read Tests
        results.findAllTest = runFindAllTest();
        results.findWithFilterTest = runFindWithFilterTest();
        results.findWithProjectionTest = runFindWithProjectionTest();
        results.findWithSortTest = runFindWithSortTest();

        // 3. Update Test
        results.updateTest = runUpdateTest();

        // 4. Delete Test
        results.deleteTest = runDeleteTest();

        return results;
    }

    // ==================== WRITE TESTS ====================

    @Transactional
    public TestResults runWriteTest(int count, boolean withIndexes) {
        logger.info("Running write test with {} records (indexes: {})", count, withIndexes);
        TestResults results = new TestResults("Write " + count + " records");

        // Create test authors first
        List<String> authorApiKeys = createTestAuthors(10);

        // Test PostgreSQL (JPA only)
        long startJpa = System.nanoTime();
        for (int i = 0; i < count; i++) {
            BookCommand command = createTestBookCommand(i, authorApiKeys);
            bookService.createBookJpaOnly(command);
        }
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000; // Convert to ms

        // Test MongoDB Referencing
        long startMongoRef = System.nanoTime();
        for (int i = 0; i < count; i++) {
            BookCommand command = createTestBookCommand(i + count, authorApiKeys);
            bookService.createBookWithReferencing(command);
        }
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;

        // Test MongoDB Embedding
        long startMongoEmb = System.nanoTime();
        for (int i = 0; i < count; i++) {
            BookCommand command = createTestBookCommand(i + (count * 2), authorApiKeys);
            bookService.createBookWithEmbedding(command);
        }
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;

        results.calculateImprovement();
        logger.info("Write test completed: JPA={}ms, MongoRef={}ms, MongoEmb={}ms",
                results.jpaTime, results.mongoReferencingTime, results.mongoEmbeddingTime);

        return results;
    }

    // ==================== READ TESTS ====================

    public TestResults runFindAllTest() {
        logger.info("Running find all test");
        TestResults results = new TestResults("Find All");

        // JPA
        long startJpa = System.nanoTime();
        List<Book> jpaBooks = bookRepository.findAll();
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000;
        results.jpaResultCount = jpaBooks.size();

        // MongoDB Referencing
        long startMongoRef = System.nanoTime();
        List<BookDocument> mongoRefBooks = bookMongoRepository.findAll();
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;
        results.mongoReferencingResultCount = mongoRefBooks.size();

        // MongoDB Embedding
        long startMongoEmb = System.nanoTime();
        List<BookDocumentEmbedded> mongoEmbBooks = bookEmbeddedMongoRepository.findAll();
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;
        results.mongoEmbeddingResultCount = mongoEmbBooks.size();

        results.calculateImprovement();
        logger.info("Find all completed: JPA={}ms ({}), MongoRef={}ms ({}), MongoEmb={}ms ({})",
                results.jpaTime, results.jpaResultCount,
                results.mongoReferencingTime, results.mongoReferencingResultCount,
                results.mongoEmbeddingTime, results.mongoEmbeddingResultCount);

        return results;
    }

    public TestResults runFindWithFilterTest() {
        logger.info("Running find with filter test");
        TestResults results = new TestResults("Find with Filter (Genre=FANTASY)");

        // JPA - Find books with FANTASY genre
        long startJpa = System.nanoTime();
        List<Book> jpaBooks = entityManager.createQuery(
                        "SELECT b FROM Book b WHERE :genre MEMBER OF b.genres", Book.class)
                .setParameter("genre", BookGenre.FANTASY)
                .getResultList();
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000;
        results.jpaResultCount = jpaBooks.size();

        // MongoDB Referencing
        long startMongoRef = System.nanoTime();
        Query query = new Query(Criteria.where("genres").is("FANTASY"));
        List<BookDocument> mongoRefBooks = mongoTemplate.find(query, BookDocument.class);
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;
        results.mongoReferencingResultCount = mongoRefBooks.size();

        // MongoDB Embedding
        long startMongoEmb = System.nanoTime();
        List<BookDocumentEmbedded> mongoEmbBooks = mongoTemplate.find(query, BookDocumentEmbedded.class);
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;
        results.mongoEmbeddingResultCount = mongoEmbBooks.size();

        results.calculateImprovement();
        logger.info("Find with filter completed: JPA={}ms ({}), MongoRef={}ms ({}), MongoEmb={}ms ({})",
                results.jpaTime, results.jpaResultCount,
                results.mongoReferencingTime, results.mongoReferencingResultCount,
                results.mongoEmbeddingTime, results.mongoEmbeddingResultCount);

        return results;
    }

    public TestResults runFindWithProjectionTest() {
        logger.info("Running find with projection test");
        TestResults results = new TestResults("Find with Filter + Projection");

        // JPA - Find books with projection
        long startJpa = System.nanoTime();
        List<BookDto> jpaBooks = bookRepository.findAllProjected();
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000;
        results.jpaResultCount = jpaBooks.size();

        // MongoDB Referencing - Only selected fields
        long startMongoRef = System.nanoTime();
        Query query = new Query();
        query.fields().include("name", "api_key", "genres", "author_api_keys");
        List<BookDocument> mongoRefBooks = mongoTemplate.find(query, BookDocument.class);
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;
        results.mongoReferencingResultCount = mongoRefBooks.size();

        // MongoDB Embedding
        long startMongoEmb = System.nanoTime();
        query = new Query();
        query.fields().include("name", "api_key", "genres", "authors");
        List<BookDocumentEmbedded> mongoEmbBooks = mongoTemplate.find(query, BookDocumentEmbedded.class);
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;
        results.mongoEmbeddingResultCount = mongoEmbBooks.size();

        results.calculateImprovement();
        logger.info("Find with projection completed: JPA={}ms ({}), MongoRef={}ms ({}), MongoEmb={}ms ({})",
                results.jpaTime, results.jpaResultCount,
                results.mongoReferencingTime, results.mongoReferencingResultCount,
                results.mongoEmbeddingTime, results.mongoEmbeddingResultCount);

        return results;
    }

    public TestResults runFindWithSortTest() {
        logger.info("Running find with sort test");
        TestResults results = new TestResults("Find with Filter + Projection + Sort");

        // JPA
        long startJpa = System.nanoTime();
        List<Book> jpaBooks = entityManager.createQuery(
                        "SELECT b FROM Book b WHERE :genre MEMBER OF b.genres ORDER BY b.name ASC", Book.class)
                .setParameter("genre", BookGenre.SCIENCE_FICTION)
                .getResultList();
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000;
        results.jpaResultCount = jpaBooks.size();

        // MongoDB Referencing
        long startMongoRef = System.nanoTime();
        Query query = new Query(Criteria.where("genres").is("SCIENCE_FICTION"));
        query.with(Sort.by(Sort.Direction.ASC, "name"));
        query.fields().include("name", "api_key", "genres");
        List<BookDocument> mongoRefBooks = mongoTemplate.find(query, BookDocument.class);
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;
        results.mongoReferencingResultCount = mongoRefBooks.size();

        // MongoDB Embedding
        long startMongoEmb = System.nanoTime();
        List<BookDocumentEmbedded> mongoEmbBooks = mongoTemplate.find(query, BookDocumentEmbedded.class);
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;
        results.mongoEmbeddingResultCount = mongoEmbBooks.size();

        results.calculateImprovement();
        logger.info("Find with sort completed: JPA={}ms ({}), MongoRef={}ms ({}), MongoEmb={}ms ({})",
                results.jpaTime, results.jpaResultCount,
                results.mongoReferencingTime, results.mongoReferencingResultCount,
                results.mongoEmbeddingTime, results.mongoEmbeddingResultCount);

        return results;
    }

    // ==================== UPDATE TEST ====================

    @Transactional
    public TestResults runUpdateTest() {
        logger.info("Running update test");
        TestResults results = new TestResults("Update 10 Random Books");

        // Get 10 random books from each database
        List<Book> jpaBooks = bookRepository.findAll().stream().limit(10).collect(Collectors.toList());
        List<BookDocument> mongoRefBooks = bookMongoRepository.findAll().stream().limit(10).collect(Collectors.toList());
        List<BookDocumentEmbedded> mongoEmbBooks = bookEmbeddedMongoRepository.findAll().stream().limit(10).collect(Collectors.toList());

        // JPA Update
        long startJpa = System.nanoTime();
        for (Book book : jpaBooks) {
            book.setDescription("Updated description at " + System.currentTimeMillis());
            book.setAvailableOnline(!book.getAvailableOnline());
            bookRepository.save(book);
        }
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000;
        results.jpaResultCount = jpaBooks.size();

        // MongoDB Referencing Update
        long startMongoRef = System.nanoTime();
        for (BookDocument doc : mongoRefBooks) {
            doc.setDescription("Updated description at " + System.currentTimeMillis());
            doc.setAvailableOnline(!doc.getAvailableOnline());
            bookMongoRepository.save(doc);
        }
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;
        results.mongoReferencingResultCount = mongoRefBooks.size();

        // MongoDB Embedding Update
        long startMongoEmb = System.nanoTime();
        for (BookDocumentEmbedded doc : mongoEmbBooks) {
            doc.setDescription("Updated description at " + System.currentTimeMillis());
            doc.setAvailableOnline(!doc.getAvailableOnline());
            bookEmbeddedMongoRepository.save(doc);
        }
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;
        results.mongoEmbeddingResultCount = mongoEmbBooks.size();

        results.calculateImprovement();
        logger.info("Update test completed: JPA={}ms, MongoRef={}ms, MongoEmb={}ms",
                results.jpaTime, results.mongoReferencingTime, results.mongoEmbeddingTime);

        return results;
    }

    // ==================== DELETE TEST ====================

    @Transactional
    public TestResults runDeleteTest() {
        logger.info("Running delete test");
        TestResults results = new TestResults("Delete 10 Random Books");

        // Get 10 random books from each database
        List<Book> jpaBooks = bookRepository.findAll().stream().limit(10).collect(Collectors.toList());
        List<BookDocument> mongoRefBooks = bookMongoRepository.findAll().stream().limit(10).collect(Collectors.toList());
        List<BookDocumentEmbedded> mongoEmbBooks = bookEmbeddedMongoRepository.findAll().stream().limit(10).collect(Collectors.toList());

        // JPA Delete
        long startJpa = System.nanoTime();
        for (Book book : jpaBooks) {
            bookRepository.delete(book);
        }
        long endJpa = System.nanoTime();
        results.jpaTime = (endJpa - startJpa) / 1_000_000;
        results.jpaResultCount = jpaBooks.size();

        // MongoDB Referencing Delete
        long startMongoRef = System.nanoTime();
        for (BookDocument doc : mongoRefBooks) {
            bookMongoRepository.delete(doc);
        }
        long endMongoRef = System.nanoTime();
        results.mongoReferencingTime = (endMongoRef - startMongoRef) / 1_000_000;
        results.mongoReferencingResultCount = mongoRefBooks.size();

        // MongoDB Embedding Delete
        long startMongoEmb = System.nanoTime();
        for (BookDocumentEmbedded doc : mongoEmbBooks) {
            bookEmbeddedMongoRepository.delete(doc);
        }
        long endMongoEmb = System.nanoTime();
        results.mongoEmbeddingTime = (endMongoEmb - startMongoEmb) / 1_000_000;
        results.mongoEmbeddingResultCount = mongoEmbBooks.size();

        results.calculateImprovement();
        logger.info("Delete test completed: JPA={}ms, MongoRef={}ms, MongoEmb={}ms",
                results.jpaTime, results.mongoReferencingTime, results.mongoEmbeddingTime);

        return results;
    }

    // ==================== HELPER METHODS ====================

    private List<String> createTestAuthors(int count) {
        List<String> apiKeys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AuthorCommand command = new AuthorCommand(
                    null,
                    "TestAuthor" + i,
                    Arrays.asList("Street " + i + "-Vienna-1010"),
                    "FirstName" + i,
                    "LastName" + i,
                    "author" + i + "@test.com"
            );
            AuthorDto author = authorService.createAuthor(command);
            apiKeys.add(author.apiKey());
        }
        return apiKeys;
    }

    private BookCommand createTestBookCommand(int index, List<String> authorApiKeys) {
        Random random = new Random(index);
        return new BookCommand(
                null,
                "Test Book " + index,
                LocalDate.now().minusDays(random.nextInt(1000)),
                random.nextBoolean(),
                Arrays.asList(BookType.values()[random.nextInt(BookType.values().length)].name()),
                random.nextInt(100000) + 10000,
                "Test description for book " + index,
                Arrays.asList(authorApiKeys.get(random.nextInt(authorApiKeys.size()))),
                Arrays.asList(BookGenre.values()[random.nextInt(BookGenre.values().length)].name())
        );
    }

    @Transactional
    public void cleanDatabases() {
        logger.info("Cleaning databases...");
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        bookMongoRepository.deleteAll();
        bookEmbeddedMongoRepository.deleteAll();
        authorMongoRepository.deleteAll();
        logger.info("Databases cleaned");
    }

    private void createPostgresIndexes() {
        logger.info("PostgreSQL indexes are managed by JPA annotations");
        // Indexes are defined in entity classes with @Index annotation
        // This method is placeholder for documentation
    }

    // ==================== RESULT CLASSES ====================

    public static class PerformanceTestResults {
        public TestSuiteResults withoutIndexes;
        public TestSuiteResults withIndexes;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== PERFORMANCE TEST RESULTS ===\n\n");
            sb.append("WITHOUT INDEXES:\n");
            sb.append(withoutIndexes.toString());
            sb.append("\n\nWITH INDEXES:\n");
            sb.append(withIndexes.toString());
            sb.append("\n\n=== INDEX IMPACT ANALYSIS ===\n");
            sb.append(compareResults(withoutIndexes, withIndexes));
            return sb.toString();
        }

        private String compareResults(TestSuiteResults without, TestSuiteResults with) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Write (100):   JPA: %.1f%% | MongoRef: %.1f%% | MongoEmb: %.1f%%\n",
                    calculateImprovement(without.writeTestSmall.jpaTime, with.writeTestSmall.jpaTime),
                    calculateImprovement(without.writeTestSmall.mongoReferencingTime, with.writeTestSmall.mongoReferencingTime),
                    calculateImprovement(without.writeTestSmall.mongoEmbeddingTime, with.writeTestSmall.mongoEmbeddingTime)));
            sb.append(String.format("Find All:      JPA: %.1f%% | MongoRef: %.1f%% | MongoEmb: %.1f%%\n",
                    calculateImprovement(without.findAllTest.jpaTime, with.findAllTest.jpaTime),
                    calculateImprovement(without.findAllTest.mongoReferencingTime, with.findAllTest.mongoReferencingTime),
                    calculateImprovement(without.findAllTest.mongoEmbeddingTime, with.findAllTest.mongoEmbeddingTime)));
            sb.append(String.format("Find Filtered: JPA: %.1f%% | MongoRef: %.1f%% | MongoEmb: %.1f%%\n",
                    calculateImprovement(without.findWithFilterTest.jpaTime, with.findWithFilterTest.jpaTime),
                    calculateImprovement(without.findWithFilterTest.mongoReferencingTime, with.findWithFilterTest.mongoReferencingTime),
                    calculateImprovement(without.findWithFilterTest.mongoEmbeddingTime, with.findWithFilterTest.mongoEmbeddingTime)));
            return sb.toString();
        }

        private double calculateImprovement(long withoutTime, long withTime) {
            if (withoutTime == 0) return 0;
            return ((double)(withoutTime - withTime) / withoutTime) * 100;
        }
    }

    public static class TestSuiteResults {
        public TestResults writeTestSmall;
        public TestResults writeTestMedium;
        public TestResults writeTestLarge;
        public TestResults findAllTest;
        public TestResults findWithFilterTest;
        public TestResults findWithProjectionTest;
        public TestResults findWithSortTest;
        public TestResults updateTest;
        public TestResults deleteTest;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(writeTestSmall).append("\n");
            sb.append(writeTestMedium).append("\n");
            if (writeTestLarge != null) sb.append(writeTestLarge).append("\n");
            sb.append(findAllTest).append("\n");
            sb.append(findWithFilterTest).append("\n");
            sb.append(findWithProjectionTest).append("\n");
            sb.append(findWithSortTest).append("\n");
            sb.append(updateTest).append("\n");
            sb.append(deleteTest).append("\n");
            return sb.toString();
        }
    }

    public static class TestResults {
        public String testName;
        public long jpaTime;
        public long mongoReferencingTime;
        public long mongoEmbeddingTime;
        public int jpaResultCount;
        public int mongoReferencingResultCount;
        public int mongoEmbeddingResultCount;
        public double jpaVsMongoRefImprovement;
        public double jpaVsMongoEmbImprovement;

        public TestResults(String testName) {
            this.testName = testName;
        }

        public void calculateImprovement() {
            if (jpaTime > 0) {
                jpaVsMongoRefImprovement = ((double)(jpaTime - mongoReferencingTime) / jpaTime) * 100;
                jpaVsMongoEmbImprovement = ((double)(jpaTime - mongoEmbeddingTime) / jpaTime) * 100;
            }
        }

        @Override
        public String toString() {
            return String.format("%s:\n  JPA: %dms (%d results)\n  MongoRef: %dms (%d results, %.1f%% vs JPA)\n  MongoEmb: %dms (%d results, %.1f%% vs JPA)",
                    testName, jpaTime, jpaResultCount,
                    mongoReferencingTime, mongoReferencingResultCount, jpaVsMongoRefImprovement,
                    mongoEmbeddingTime, mongoEmbeddingResultCount, jpaVsMongoEmbImprovement);
        }
    }
}