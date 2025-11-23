package spengergasse.at.sj2425scherzerrabar.performance;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spengergasse.at.sj2425scherzerrabar.mapper.BookEmbeddedMapper;
import spengergasse.at.sj2425scherzerrabar.mapper.BookMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookEmbeddedMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookMongoRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

/**
 * Performance test comparing JPA, MongoDB Embedding, and MongoDB Referencing
 * Tests CRUD operations at different scales: 100, 1000, 10000 records
 */
@SpringBootTest
public class DatabasePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceTest.class);

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMongoRepository mongoReferencingRepo;

    @Autowired
    private BookEmbeddedMongoRepository mongoEmbeddingRepo;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookEmbeddedMapper bookEmbeddedMapper;

    @Test
    void runPerformanceSuite() {
        logger.info("Starting performance tests: JPA vs MongoDB Embedding vs MongoDB Referencing");

        DatabasePerformanceTestRunner runner = new DatabasePerformanceTestRunner(
                authorService,
                bookService,
                bookRepository,
                mongoReferencingRepo,
                mongoEmbeddingRepo,
                authorRepository,
                bookMapper,
                bookEmbeddedMapper
        );

        runner.runTests();

        logger.info("✅ Database performance test suite completed successfully.");
    }
}