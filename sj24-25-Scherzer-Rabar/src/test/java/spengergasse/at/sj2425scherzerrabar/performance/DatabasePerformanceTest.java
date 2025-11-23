package spengergasse.at.sj2425scherzerrabar.performance;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spengergasse.at.sj2425scherzerrabar.mapper.BookEmbeddedMapper;
import spengergasse.at.sj2425scherzerrabar.mapper.BookMapper;
import spengergasse.at.sj2425scherzerrabar.persistence.*;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

/**
 * Comprehensive Performance Test
 * Tests: Authors, Books, Mixed Queries
 * Databases: JPA vs MongoDB (Embedding vs Referencing)
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
    private AuthorMongoRepository authorMongoRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookEmbeddedMapper bookEmbeddedMapper;

    @Test
    void runComprehensivePerformanceSuite() {
        logger.info("Starting COMPREHENSIVE performance tests");
        logger.info("Testing: Authors | Books | Mixed Queries");
        logger.info("Databases: JPA | MongoDB Embedding | MongoDB Referencing");

        ComprehensivePerformanceTestRunner runner = new ComprehensivePerformanceTestRunner(
                authorService,
                bookService,
                bookRepository,
                mongoReferencingRepo,
                mongoEmbeddingRepo,
                authorRepository,
                authorMongoRepository,
                bookMapper,
                bookEmbeddedMapper
        );

        runner.runTests();

        logger.info("✅ Comprehensive database performance test suite completed successfully.");
    }
}