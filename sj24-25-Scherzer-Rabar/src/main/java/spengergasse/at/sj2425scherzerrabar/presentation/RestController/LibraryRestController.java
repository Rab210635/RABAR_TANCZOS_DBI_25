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
import spengergasse.at.sj2425scherzerrabar.commands.LibraryCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Library;
import spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto;
import spengergasse.at.sj2425scherzerrabar.service.LibraryService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/libraries")
public class LibraryRestController {
    private final LibraryService libraryService;
    public LibraryRestController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @Operation(summary = "Get all Libraries",
            description = "To get every Library object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Library.class))})})
    @GetMapping
    public ResponseEntity<List<LibraryDto>> getAllLibraries() {
        logger.debug("entered libraryrestcontroller getAllLibraries");
        List<LibraryDto> response = libraryService.getLibraries();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a Library",
            description = "To get an specific Library by either their Apikey or Name. The response is the requested Library object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Library.class))}),
            @ApiResponse(responseCode = "404", description = "Library not found",
                    content = @Content)})
    @GetMapping("/library")
    public ResponseEntity<LibraryDto> getLibrary(@RequestParam Optional<String> apiKey, @RequestParam Optional<String> name) throws BadRequestException {
        logger.debug("entered libraryrestcontroller getLibrary");
        LibraryDto response;
        if(apiKey.isPresent()){
            response = libraryService.getLibrary(apiKey.get());
        } else if (name.isPresent()) {
            response = libraryService.getLibraryByName(name.get());
        } else {
            throw new BadRequestException("Bad Request, Parameter for get Library needed");
        }
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Create a Library",
            description = "Create a new Library to be saved in the database. The response is a link to the created Library object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Library.class))})})
    @PostMapping
    public ResponseEntity<LibraryDto> createLibrary(@RequestBody LibraryCommand command) throws BadRequestException {
        logger.debug("entered libraryrestcontroller createLibrary");
        LibraryDto response = libraryService.createLibrary(command);

        Link selfLink = linkTo(methodOn(LibraryRestController.class).getLibrary(Optional.of(response.apiKey()),null)).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(response);
    }

    @Operation(summary = "Update a Library",
            description = "Update an existing Library. The response is an updated Library object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Library.class))}),
            @ApiResponse(responseCode = "404", description = "Library not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<LibraryDto> updateLibrary(@RequestBody LibraryCommand command) {
        logger.debug("entered libraryrestcontroller updateLibrary");

        LibraryDto response = libraryService.updateLibrary(command);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a Library",
            description = "Delete a existing Library.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Library.class))}),
            @ApiResponse(responseCode = "404", description = "Library not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<LibraryDto> deleteLibrary(@RequestParam String apiKey) {
        logger.debug("entered libraryrestcontroller deleteLibrary");
        libraryService.deleteLibrary(apiKey);
        return ResponseEntity.noContent().build();
    }
}
