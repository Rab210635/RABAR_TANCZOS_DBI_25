package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LoggingController {
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @RequestMapping
    public String index(){
        logger.debug("entered loggingcontroller index");
        logger.trace("a trace message");
        logger.debug("a debug message");
        logger.info("a info message");
        logger.warn("a warn message");
        logger.error("a error message");

        return "Look at the Logs to see the output...";
    }
}
