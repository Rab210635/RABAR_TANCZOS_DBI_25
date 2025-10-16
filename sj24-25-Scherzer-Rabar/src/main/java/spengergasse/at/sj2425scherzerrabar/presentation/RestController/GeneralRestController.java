package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping
public class GeneralRestController {

    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @Operation(summary = "Get Hello Message", description = "Displays a simple hello message and some instructions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the hello message"),
    })
    @GetMapping
    public String getHelloMessage() {
        logger.debug("entered generalrestcontroller getHelloMessage");
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Hello! Welcome to the LibraryApp API</h2>" +
                "<p>Here are some instructions:</p>" +
                "<ul>" +
                "<li><b>Swagger UI:</b> Use <a href='http://localhost:8080/swagger-ui/index.html'>Swagger UI</a> to explore the API. <b>Information:</b> You have to get an AuthToken with /token and use it for the other requests.</li>" +
                "<li><b>Rest API Documentation:</b> You can also review the API documentation under: <a href='http://localhost:8080/docs/index.html'>RestApiDoc</a>.</li>" +
                "<li><b>Web Api:</b> You can observe the Web Api under: <a href='http://localhost:8080/www'>WEB API</a>.</li>" +
                "</ul>" +
                "<p>Enjoy exploring the API!</p>" +
                "</body>" +
                "</html>";
    }
}
