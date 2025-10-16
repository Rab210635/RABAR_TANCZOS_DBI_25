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
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto;
import spengergasse.at.sj2425scherzerrabar.commands.BorrowingCommand;
import spengergasse.at.sj2425scherzerrabar.service.BorrowingService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingRestController {

    private final BorrowingService borrowingService;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BorrowingRestController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @Operation(summary = "Get all Borrowings",
            description = "To get every Borrowing object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))})})
    @GetMapping
    public ResponseEntity<List<BorrowingDto>> getAllBorrowings() {
        logger.debug("entered borrowingrestcontroller getAllBorrowings");
        List<BorrowingDto> borrowings = borrowingService.getAllBorrowings();
        return ResponseEntity.ok(borrowings);
    }

    @Operation(summary = "Get a Borrowing",
            description = "To get an specific Borrowing by their Apikey. The response is the requested Borrowing object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))}),
            @ApiResponse(responseCode = "404", description = "Borrowing not found",
                    content = @Content)})
    @GetMapping("/borrowing")
    public ResponseEntity<BorrowingDto> getBorrowing(@RequestParam String apiKey) {
        logger.debug("entered borrowingrestcontroller getBorrowing");
        BorrowingDto borrowingDto;
        borrowingDto = borrowingService.getBorrowingByApiKey(apiKey);
        return ResponseEntity.ok(borrowingDto);
    }

    @Operation(summary = "Get all Borrowings by a Copy",
            description = "To get all Borrowings by their Copy. The response is the requested Borrowings objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))}),
            @ApiResponse(responseCode = "404", description = "Copy not found",
                    content = @Content)})
    @GetMapping("/copy")
    public ResponseEntity<List<BorrowingDto>> getBorrowingsByCopy(@RequestParam String copy) {
        logger.debug("entered borrowingrestcontroller getBorrowingsByCopy");
        List<BorrowingDto> books;
        books = borrowingService.getBorrowingsByCopy(copy);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get all Borrowings by a Customer",
            description = "To get all Borrowings by their Customer. The response is the requested Borrowings objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @GetMapping("/customer")
    public ResponseEntity<List<BorrowingDto>> getBorrowingsByCustomer(@RequestParam String customer) {
        logger.debug("entered borrowingrestcontroller getBorrowingsByCustomer");
        List<BorrowingDto> books;
        books = borrowingService.getBorrowingsByCustomer(customer);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Create a Borrowing",
            description = "Create a new Borrowing to be saved in the database. The response is a link to the created Borrowing object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))})})
    @PostMapping
    public ResponseEntity<BorrowingDto> createBorrowing(@RequestBody BorrowingCommand borrowingCommand) {
        logger.debug("entered borrowingrestcontroller createBorrowing");
        BorrowingDto createdBorrowing = borrowingService.createBorrowing(borrowingCommand);

       Link selfLink = linkTo(methodOn(BorrowingRestController.class).getBorrowing(createdBorrowing.apiKey())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(createdBorrowing);
    }

    @Operation(summary = "Update a Borrowing",
            description = "Update an existing Borrowing. The response is an updated Borrowing object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))}),
            @ApiResponse(responseCode = "404", description = "Borrowing not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<BorrowingDto> updateBorrowing(@RequestBody BorrowingCommand borrowingCommand) {
        logger.debug("entered borrowingrestcontroller updateBorrowing");
        return ResponseEntity.ok(borrowingService.updateBorrowing(borrowingCommand));
    }

    @Operation(summary = "Delete a Borrowing",
            description = "Delete a existing Borrowing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Borrowing.class))}),
            @ApiResponse(responseCode = "404", description = "Borrowing not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<Void> deleteBorrowing(@RequestParam String apiKey) {
        logger.debug("entered borrowingrestcontroller deleteBorrowing");
        borrowingService.deleteBorrowing(apiKey);
        return ResponseEntity.noContent().build();
    }
}
