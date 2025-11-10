package spengergasse.at.sj2425scherzerrabar.presentation.www.books;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand2;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.presentation.www.RedirectForwardSupport;
import spengergasse.at.sj2425scherzerrabar.presentation.www.authors.CreateAuthorForm;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.time.Duration;

@Controller
@RequestMapping(BookController.BASE_URL)
public class BookController implements RedirectForwardSupport {
    private final BookService bookService;
    private final AuthorService authorService;

    public static final String BASE_URL = "/www/books";
    public static final String ROUTE_INDEX = "";
    public static final String ROUTE_SHOW = "/show";
    public static final String ROUTE_EDIT = "/edit";
    public static final String ROUTE_DELETE = "/delete";
    public static final String ROUTE_NEW = "/add";

    private Logger logger = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @GetMapping(ROUTE_INDEX)
    public String getAllBooks(Model model) {
        logger.debug("entered Web BookController getAllBooks");

        var books = bookService.getBooks2();

        if (books.size() == 1) {
            model.addAttribute("book", books.getFirst());
            return "books/show";
        } else {
            model.addAttribute("books", books);
            return "books/index";
        }
    }

    @GetMapping(ROUTE_SHOW)
    public String showBook(Model model, @RequestParam String apiKey) {
        logger.debug("entered Web BookController showBook");
        model.addAttribute("book", bookService.getBook2(apiKey));
        return "books/show";
    }

    @GetMapping(ROUTE_NEW)
    public String showCreateForm(Model model) {
        logger.debug("entered Web BookController showCreateForm");
        model.addAttribute("existingAuthors", authorService.getAuthors());
        model.addAttribute("newAuthor", new CreateAuthorForm());
        model.addAttribute("newBook", new CreateBookForm());
        return "books/create";
    }

    @PostMapping(ROUTE_NEW)
    public String handleCreateForm(@Valid @ModelAttribute("newBook") CreateBookForm book, BindingResult brNewBook, Model model) {
        logger.debug("entered Web BookController handleCreateForm");
        if (brNewBook.hasErrors()) {
            model.addAttribute("existingAuthors", authorService.getAuthors());
            return "books/create";
        }

        // Ruft die Standard-Methode auf, die ÜBERALL speichert (JPA + MongoDB Referencing + Embedding)
        bookService.createBook2(book.getBookCommand());
        return redirectTo(BASE_URL);
    }

    @GetMapping(ROUTE_EDIT)
    public String showEditForm(@RequestParam String apiKey, Model model) {
        logger.debug("entered Web BookController showEditForm");
        var book = bookService.getBook2(apiKey);
        model.addAttribute("existingAuthors", authorService.getAuthors());

        CreateBookForm bookForm = new CreateBookForm();
        bookForm.setName(book.name());
        bookForm.setDescription(book.description());
        bookForm.setAvailableOnline(book.availableOnline());
        bookForm.setWordCount(book.wordCount());
        bookForm.setReleaseDate(book.releaseDate());
        bookForm.setBookTypes(book.types().stream().map(BookType::valueOf).toList());
        bookForm.setGenres(book.genres().stream().map(BookGenre::valueOf).toList());
        bookForm.setAuthors(book.authorPennames());

        model.addAttribute("form", bookForm);
        model.addAttribute("apiKey", apiKey);
        return "books/edit";
    }

    @PostMapping(ROUTE_EDIT)
    public String handleEditForm(@RequestParam String apiKey, @Valid @ModelAttribute("form") CreateBookForm form, BindingResult result, Model model) {
        logger.debug("entered Web BookController handleEditForm");
        if (result.hasErrors()) {
            model.addAttribute("existingAuthors", authorService.getAuthors());
            model.addAttribute("apiKey", apiKey);
            return "books/edit";
        }

        var command = new BookCommand2(
                apiKey,
                form.getName(),
                form.getReleaseDate(),
                form.getAvailableOnline(),
                form.getBookTypes().stream().map(Enum::name).toList(),
                form.getWordCount(),
                form.getDescription(),
                form.getAuthors(),
                form.getGenres().stream().map(Enum::name).toList()
        );
        logger.debug("entered Web BookController handleEditForm 2");
        // Ruft die Standard-Methode auf, die ÜBERALL updated (JPA + MongoDB Referencing + Embedding)
        bookService.updateBook2(command);
        return redirectTo(BASE_URL);
    }

    @PostMapping(ROUTE_DELETE)
    public String deleteBook(@RequestParam String apiKey, Model model) {
        logger.debug("entered Web BookController deleteBook");
        try {
            // Ruft die Standard-Methode auf, die ÜBERALL löscht (JPA + MongoDB Referencing + Embedding)
            bookService.deleteBook(apiKey);
        } catch (Exception e) {
            logger.error("Error while deleting book", e);
            model.addAttribute("deleteError", "The Book can not be deleted, it might still be used in a Library.");
            return getAllBooks(model);
        }
        return redirectTo(BASE_URL);
    }

    @GetMapping("/duration")
    public String handleDurationSubmission(@RequestParam Duration duration) {
        logger.debug("entered Web BookController handleDurationSubmission");
        return ROUTE_INDEX;
    }
}