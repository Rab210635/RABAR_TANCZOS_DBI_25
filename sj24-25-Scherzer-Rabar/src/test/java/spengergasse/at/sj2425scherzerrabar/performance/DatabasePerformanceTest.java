package spengergasse.at.sj2425scherzerrabar.performance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

@SpringBootTest
public class DatabasePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceTest.class);

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Test
    void runPerformanceSuite() throws Exception {
        DatabasePerformanceTestRunner runner = new DatabasePerformanceTestRunner(authorService, bookService);
        runner.run();
        logger.info("✅ Database performance test suite completed successfully.");
    }
}
