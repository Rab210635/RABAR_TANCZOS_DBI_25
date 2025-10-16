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
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Author;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/authors")
public class AuthorRestController {

    Logger logger = LoggerFactory.getLogger(LoggingController.class);
    private final AuthorService authorService;

    public AuthorRestController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Get all Authors",
            description = "To get every Author object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Author.class))})})
    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        logger.debug("entered authorrestcontroller getAllAuthors");
        List<AuthorDto> authors = authorService.getAuthors();
        return ResponseEntity.ok(authors);
    }

    @Operation(summary = "Get an Author",
            description = "To get an specific Author by either their Apikey, Penname or Email-Adresse. The response is the requested Author object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Author.class))}),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content)})
    @GetMapping("/author")
    public ResponseEntity<AuthorDto> getAuthor(
            @RequestParam Optional<String> apiKey,
            @RequestParam Optional<String> penname,
            @RequestParam Optional<String> email) throws BadRequestException {
        logger.debug("entered authorrestcontroller getAuthor");
        AuthorDto author;
        if(apiKey.isPresent()) {
             author = authorService.getAuthor(apiKey.get());
        }else if(penname.isPresent()) {
            author = authorService.getAuthorByPenname(penname.get());
        }else if(email.isPresent()) {
            author = authorService.getAuthorByEmailAddress(email.get());
        }else {
            throw new BadRequestException("Bad Request, Parameter for get Author needed");
        }

        return ResponseEntity.ok(author);
    }

    @Operation(summary = "Create an Author",
            description = "Create a new Author to be saved in the database. The response is a link to the created Author object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Author.class))})})
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorCommand authorCommand) throws BadRequestException {
        logger.debug("entered authorrestcontroller createAuthor");
        AuthorDto createdAuthor = authorService.createAuthor(authorCommand);

        Link selfLink = linkTo(methodOn(AuthorRestController.class).getAuthor(Optional.of(createdAuthor.apiKey()),Optional.empty(),Optional.empty())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(createdAuthor);
    }

    @Operation(summary = "Update an Author",
            description = "Update an existing author. The response is an updated Author object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Author.class))}),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<AuthorDto> updateAuthor(@RequestBody AuthorCommand authorCommand) {
        logger.debug("entered authorrestcontroller updateAuthor");
        AuthorDto updatedAuthor = authorService.updateAuthor(authorCommand);
        return ResponseEntity.ok(updatedAuthor);
    }

    @Operation(summary = "Delete an author",
            description = "Delete an existing author.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Author.class))}),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<Void> deleteAuthor(
            @RequestParam String apiKey) {
        logger.debug("entered authorrestcontroller deleteAuthor");
        authorService.deleteAuthor(apiKey);
        return ResponseEntity.noContent().build();
    }
}
