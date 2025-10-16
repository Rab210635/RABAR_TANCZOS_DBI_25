package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.PublisherCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;
import spengergasse.at.sj2425scherzerrabar.service.PublisherService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/publishers")
public class PublisherRestController {
    private final PublisherService publisherService;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public PublisherRestController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Operation(summary = "Get all Publishers",
            description = "To get every Publisher object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Publisher.class))})})
    @GetMapping
    public ResponseEntity<List<PublisherDto>> getAllPublishers() {
        logger.debug("entered publisherrestcontroller getAllPublishers");
        List<PublisherDto> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    @Operation(summary = "Get a Publisher",
            description = "To get an specific Publisher by either their Apikey or Name. The response is the requested Publisher object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Publisher.class))}),
            @ApiResponse(responseCode = "404", description = "Publisher not found",
                    content = @Content)})
    @GetMapping("/publisher")
    public ResponseEntity<PublisherDto> getPublisher(@RequestParam Optional<String> apiKey, @RequestParam Optional<String> name) throws BadRequestException {

        logger.debug("entered publisherrestcontroller getPublisher");
        PublisherDto publisher;
        if(apiKey.isPresent()) {
            publisher = publisherService.getPublisherByApiKey(apiKey.get());
        } else if (name.isPresent()) {
            publisher = publisherService.getPublisherByName(name.get());
        } else {
            throw new BadRequestException("Bad Request, Parameter for get Publisher needed");
        }

        return ResponseEntity.ok(publisher);
    }

    @Operation(summary = "Create a Publisher",
            description = "Create a new Publisher to be saved in the database. The response is a link to the created Publisher object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Publisher.class))})})
    @PostMapping
    public ResponseEntity<PublisherDto> createPublisher(@RequestBody PublisherCommand command) throws BadRequestException {
        logger.debug("entered publisherrestcontroller createPublisher");
        PublisherDto publisher = publisherService.createPublisher(command);

        Link selfLink = linkTo(methodOn(PublisherRestController.class).getPublisher(Optional.of(publisher.apiKey()),null)).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(publisher);
    }

    @Operation(summary = "Update a Publisher",
            description = "Update an existing Publisher. The response is an updated Publisher object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Publisher.class))}),
            @ApiResponse(responseCode = "404", description = "Publisher not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<PublisherDto> updatePublisher(@RequestBody PublisherCommand command) {
        logger.debug("entered publisherrestcontroller updatePublisher");
        PublisherDto publisher = publisherService.updatePublisher(command);
        return ResponseEntity.ok(publisher);
    }

    @Operation(summary = "Delete a Publisher",
            description = "Delete a existing Publisher.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Publisher.class))}),
            @ApiResponse(responseCode = "404", description = "Publisher not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<PublisherDto> deletePublisher(@RequestParam String apiKey) {
        logger.debug("entered publisherrestcontroller deletePublisher");
        publisherService.deletePublisherByApiKey(apiKey);
        return ResponseEntity.noContent().build();
    }
}
