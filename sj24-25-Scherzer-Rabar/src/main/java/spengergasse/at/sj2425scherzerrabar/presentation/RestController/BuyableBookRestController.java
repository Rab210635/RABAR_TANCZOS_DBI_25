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
import spengergasse.at.sj2425scherzerrabar.commands.BuyableBookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.BuyableBook;
import spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto;
import spengergasse.at.sj2425scherzerrabar.service.BuyableBookService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/buyableBooks")
public class BuyableBookRestController {
    private final BuyableBookService buyableBookService;
    public BuyableBookRestController(BuyableBookService buyableBookService) {
        this.buyableBookService = buyableBookService;
    }
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @Operation(summary = "Get all buyable Books",
            description = "To get every buyable Book object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))})})
    @GetMapping
    public ResponseEntity<List<BuyableBookDto>> getAllBuyableBooks() {
        logger.debug("entered buyablebookrestcontroller getAllBuyableBooks");
        List<BuyableBookDto> response = buyableBookService.getAllBuyableBooks();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a buyable Book",
            description = "To get an specific buyable Book by their Apikey. The response is the requested buyable Book object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))}),
            @ApiResponse(responseCode = "404", description = "Buyable Book not found",
                    content = @Content)})
    @GetMapping("/buyableBook")
    public ResponseEntity<BuyableBookDto> getBuyableBook(@RequestParam String apiKey) {
        logger.debug("entered buyablebookrestcontroller getBuyableBook");
        BuyableBookDto response = buyableBookService.getBuyableBookByApiKey(apiKey);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all buyable Books by a Publisher",
            description = "To get all buyable Books by their Publisher. The response is the requested buyable Books objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))}),
            @ApiResponse(responseCode = "404", description = "Publisher not found",
                    content = @Content)})
    @GetMapping("/publisher")
    public ResponseEntity<List<BuyableBookDto>> getAllBuyableBooksByPublisher(@RequestParam String publisher) {
        logger.debug("entered buyablebookrestcontroller getAllBuyableBooksByPublisher");
        List<BuyableBookDto> response = buyableBookService.getAllBuyableBooksByPublisher(publisher);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Get all buyable Books by a Book",
            description = "To get all buyable Books by their Book. The response is the requested buyable Books objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)})
    @GetMapping("/book")
    public ResponseEntity<List<BuyableBookDto>> getAllBuyableBooksByBook(@RequestParam String book) {
        logger.debug("entered buyablebookrestcontroller getAllBuyableBooksByBook");
        List<BuyableBookDto> response = buyableBookService.getAllBuyableBooksByBook(book);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Get all buyable Books by a Price",
            description = "To get all buyable Books by their Price. The response is the requested buyable Books objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))})})
    @GetMapping("/price")
    public ResponseEntity<List<BuyableBookDto>> getAllBuyableBooksByPrice(@RequestParam Float price) {
        logger.debug("entered buyablebookrestcontroller getAllBuyableBooksByPrice");
        List<BuyableBookDto> response = buyableBookService.getAllBuyableBooksByPrice(price);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Get all buyable Books by a Booktype",
            description = "To get all buyable Books by their Booktype. The response is the requested buyable Books objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))})})
    @GetMapping("/booktype")
    public ResponseEntity<List<BuyableBookDto>> getAllBuyableBooksByBookType(@RequestParam String bookType) {
        logger.debug("entered buyablebookrestcontroller getAllBuyableBooksByBookType");
        List<BuyableBookDto> response = buyableBookService.getAllBuyableBooksByBookType(bookType);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a buyable Book",
            description = "Create a new buyable Book to be saved in the database. The response is a link to the created buyable Book object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))})})
    @PostMapping
    public ResponseEntity<BuyableBookDto> createBuyableBook(@RequestBody BuyableBookCommand command) {
        logger.debug("entered buyablebookrestcontroller createBuyableBook");
        BuyableBookDto response = buyableBookService.createBuyableBook(command);

        Link selfLink = linkTo(methodOn(BuyableBookRestController.class).getBuyableBook(response.buyableBookApiKey())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(response);
    }

    @Operation(summary = "Update a buyable Book",
            description = "Update an existing buyable Book. The response is an updated buyable Book object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))}),
            @ApiResponse(responseCode = "404", description = "buyable Book not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<BuyableBookDto> updateBuyableBook(@RequestBody BuyableBookCommand command) {
        logger.debug("entered buyablebookrestcontroller updateBuyableBook");
        BuyableBookDto response = buyableBookService.updateBuyableBook(command);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a buyable Book",
            description = "Delete a existing buyable Book.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BuyableBook.class))}),
            @ApiResponse(responseCode = "404", description = "buyable Book not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<BuyableBookDto> deleteBuyableBook(@RequestParam String apiKey) {
        logger.debug("entered buyablebookrestcontroller deleteBuyableBook");
        buyableBookService.deleteBuyableBook(apiKey);
        return ResponseEntity.noContent().build();
    }
}
