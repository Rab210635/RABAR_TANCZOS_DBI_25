package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.CopyCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Copy;
import spengergasse.at.sj2425scherzerrabar.dtos.CopyDto;
import spengergasse.at.sj2425scherzerrabar.service.CopyService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/copies")
public class CopyRestController {
    private final CopyService copyService;
    public CopyRestController(CopyService copyService) {
        this.copyService = copyService;
    }
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @Operation(summary = "Get all Copies",
            description = "To get every Copy object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))})})
    @GetMapping
    public ResponseEntity<List<CopyDto>> getAllCopies() {
        logger.debug("entered copyrestcontroller getAllCopies");
        List<CopyDto> response = copyService.getCopies();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a Copy",
            description = "To get an specific Copy by their Apikey. The response is the requested Copy object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))}),
            @ApiResponse(responseCode = "404", description = "Copy not found",
                    content = @Content)})
    @GetMapping("/copy")
    public ResponseEntity<CopyDto> getCopy(@RequestParam String apiKey) {
        logger.debug("entered copyrestcontroller getCopy");
        CopyDto response = copyService.getCopy(apiKey);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Copies by a Book",
            description = "To get all Copies by their Book. The response is the requested Copies objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)})
    @GetMapping("/book")
    public ResponseEntity<List<CopyDto>> getAllCopiesByBook(@RequestParam String book) {
        logger.debug("entered copyrestcontroller getAllCopiesByBook");
        List<CopyDto> response = copyService.getCopiesByBook(book);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Copies by a Publisher",
            description = "To get all Copies by their Publisher. The response is the requested Copies objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))}),
            @ApiResponse(responseCode = "404", description = "Publisher not found",
                    content = @Content)})
    @GetMapping("/publisher")
    public ResponseEntity<List<CopyDto>> getAllCopiesByPublisher(@RequestParam String publisher) {
        logger.debug("entered copyrestcontroller getAllCopiesByPublisher");
        List<CopyDto> response = copyService.getCopiesByPublisher(publisher);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Copies by a Branch",
            description = "To get all Copies by their Branch. The response is the requested Copies objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))}),
            @ApiResponse(responseCode = "404", description = "Branch not found",
                    content = @Content)})
    @GetMapping("/branch")
    public ResponseEntity<List<CopyDto>> getAllCopiesByBranch(@RequestParam String branch) {
        logger.debug("entered copyrestcontroller getAllCopiesByBranch");
        List<CopyDto> response = copyService.getCopiesByBranch(branch);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Copies by a Booktype",
            description = "To get all Copies by their Booktype. The response is the requested Copies objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))})})
    @GetMapping("/bookType")
    public ResponseEntity<List<CopyDto>> getAllCopiesByBookType(@RequestParam String bookType) {
        logger.debug("entered copyrestcontroller getAllCopiesByBookType");
        List<CopyDto> response = copyService.getCopiesByBookType(bookType);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a Copy",
            description = "Create a new Copy to be saved in the database. The response is a link to the created Copy object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))})})
    @PostMapping
    public ResponseEntity<CopyDto> createCopy(@RequestBody CopyCommand command) {
        logger.debug("entered copyrestcontroller createCopy");
        CopyDto response = copyService.createCopy(command);

        Link selfLink = linkTo(methodOn(CopyRestController.class).getCopy(response.apiKey())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(response);
    }

    @Operation(summary = "Update a Copy",
            description = "Update an existing Copy. The response is an updated Copy object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))}),
            @ApiResponse(responseCode = "404", description = "Copy not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<CopyDto> updateCopy(@RequestBody CopyCommand command) {
        logger.debug("entered copyrestcontroller updateCopy");
        CopyDto response = copyService.updateCopy(command);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a Copy",
            description = "Delete a existing Copy.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Copy.class))}),
            @ApiResponse(responseCode = "404", description = "Copy not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<CopyDto> deleteCopy(@RequestParam String apiKey) {
        logger.debug("entered copyrestcontroller deleteCopy");
        copyService.deleteCopy(apiKey);
        return ResponseEntity.noContent().build();
    }
}
