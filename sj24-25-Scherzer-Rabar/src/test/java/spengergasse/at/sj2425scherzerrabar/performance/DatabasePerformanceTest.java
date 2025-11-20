package spengergasse.at.sj2425scherzerrabar.performance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

/**
 * Performance test with disabled security
 * Uses TestSecurityConfig to bypass all authentication
 */
@SpringBootTest
public class DatabasePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceTest.class);

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Test
    void runPerformanceSuite() throws Exception {
        logger.info("Starting performance tests with security disabled...");
        DatabasePerformanceTestRunner runner = new DatabasePerformanceTestRunner(authorService, bookService);
        runner.runTests();
        logger.info("✅ Database performance test suite completed successfully.");
    }
}