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
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;

    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Get all Books",
            description = "To get every Book object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))})})
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        logger.debug("entered bookrestcontroller getAllBooks");
        List<BookDto> books;
        books = bookService.getBooks(null);
        return ResponseEntity.ok(books);
    }


    @Operation(summary = "Get all Books by an Author",
            description = "To get all Books by their Author. The response is the requested Books objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Author not found",
                    content = @Content)})
    @GetMapping("/author")
    public ResponseEntity<List<BookDto>> getAllBooksByAuthor(@RequestParam String author) {
        logger.debug("entered bookrestcontroller getAllBooksByAuthor");
        List<BookDto> books;
        books = bookService.getBooks(author);
        return ResponseEntity.ok(books);
    }


    @Operation(summary = "Get a Book",
            description = "To get an specific Book by their Apikey. The response is the requested Book object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)})
    @GetMapping("/book")
    public ResponseEntity<BookDto> getBook(
            @RequestParam String apiKey
    ) throws BadRequestException {
        logger.debug("entered bookrestcontroller getBook");
        BookDto bookDto;

        bookDto = bookService.getBook(apiKey);

        return ResponseEntity.ok(bookDto);
    }

    @Operation(summary = "Create a Book",
            description = "Create a new Book to be saved in the database. The response is a link to the created Book object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))})})
    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookCommand bookCommand) throws BadRequestException {
        logger.debug("entered bookrestcontroller createBook");
        BookDto createdBook = bookService.createBook(bookCommand);

        Link selfLink = linkTo(methodOn(BookRestController.class).getBook(createdBook.apiKey())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(createdBook);
    }

    @Operation(summary = "Update a Book",
            description = "Update an existing Book. The response is an updated Book object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<BookDto> updateBook(@RequestBody BookCommand bookCommand) {
        logger.debug("entered bookrestcontroller updateBook");
        BookDto book = bookService.updateBook(bookCommand);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "Delete a Book",
            description = "Delete a existing Book.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<Void> deleteBook(@RequestParam String apiKey) {
        logger.debug("entered bookrestcontroller deleteBook");
        bookService.deleteBook(apiKey);
        return ResponseEntity.noContent().build();
    }

}
