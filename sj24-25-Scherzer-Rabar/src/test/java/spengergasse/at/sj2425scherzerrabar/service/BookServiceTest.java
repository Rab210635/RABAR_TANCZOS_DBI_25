package spengergasse.at.sj2425scherzerrabar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand2;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookServiceTest {
    private @Mock BookRepository bookRepository;
    private @Mock AuthorRepository authorRepository;

    private BookService bookService;


    @BeforeEach
    void setUp(){
        assumeThat(bookRepository).isNotNull();
        assumeThat(authorRepository).isNotNull();
        bookService = new BookService(bookRepository,authorRepository);
    }

    @Test
    void cant_create_book_with_missing_author(){
        assertThatThrownBy(()-> bookService.createBook(new BookCommand(new ApiKey("BookApiKeys").apiKey(),"name", LocalDate.now(),true, List.of(BookType.EBOOK.name()),489,"cooler Book", List.of(new ApiKey("AuthorApiKey").apiKey()),List.of(BookGenre.COMICS.name()))))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("No Authors for Book");
    }

    @Test
    void can_create_book(){
        var author = FixturesFactory.author();

        when(authorRepository.findAuthorByAuthorApiKey(any())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).then(AdditionalAnswers.returnsFirstArg());

        var Book = bookService.createBook( new BookCommand(
                new ApiKey("bookApiKey").apiKey(),"name",LocalDate.of(2025,2,2),true,
                List.of(BookType.EBOOK.name()),489,"cooler Book",
                List.of(new ApiKey("authorApiKey").apiKey()),List.of(BookGenre.COMICS.name())));
        assertThat(Book).isNotNull();
    }

    @Test
    void cant_create_book2_with_missing_author(){
        assertThatThrownBy(()-> bookService.createBook2(new BookCommand2(new ApiKey("BookApiKeys").apiKey(),"name", LocalDate.now(),true, List.of(BookType.EBOOK.name()),489,"cooler Book", List.of("pennames"),List.of(BookGenre.COMICS.name()))))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("No Authors for Book");
    }

    @Test
    void can_create_book2(){
        var author = FixturesFactory.author();

        when(authorRepository.findAuthorsByPenname(any())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).then(AdditionalAnswers.returnsFirstArg());

        var Book = bookService.createBook2( new BookCommand2(
                new ApiKey("bookApiKey").apiKey(),"name",LocalDate.of(2025,2,2),true,
                List.of(BookType.EBOOK.name()),489,"cooler Book",
                List.of("pennames"),List.of(BookGenre.COMICS.name())));
        assertThat(Book).isNotNull();
    }

    @Test
    void cant_delete_non_existing_book() {
        assertThatThrownBy(() -> bookService.deleteBook(new ApiKey("invalidApiKey").apiKey()))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("Book with api key (invalidApiKey) not existent");
    }

    @Test
    void can_delete_existing_book() {

        var book = FixturesFactory.book(FixturesFactory.author());
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));

        bookService.deleteBook(new ApiKey("validApiKey").apiKey());

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void cant_update2_non_existing_book() {
        assertThatThrownBy(() -> bookService.updateBook2(new BookCommand2(
                new ApiKey("bookApiKey").apiKey(), "Updated Name", LocalDate.now(), true,
                List.of(BookType.EBOOK.name()), 500, "Updated Description",
                List.of("penname"), List.of(BookGenre.COMICS.name()))))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("Book with api key (bookApiKey) not existent");
    }

    @Test
    void can_not_update2_with_missing_author() {
        var book = FixturesFactory.book(FixturesFactory.author());
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(authorRepository.findAuthorsByPenname(any())).thenReturn(Optional.empty());


        var command = new BookCommand2(
                new ApiKey("bookApiKey").apiKey(), "Updated Name", LocalDate.of(2000,2,5), false,
                List.of(BookType.HARDCOVER.name()), 510, "Updated Description",
                List.of("penname"), List.of(BookGenre.YOUNG_ADULT.name()));

        assertThatThrownBy(() -> bookService.updateBook2(command))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("No Authors for Book");
    }

    @Test
    void can_update2_existing_book() {
        var author = FixturesFactory.author();
        var book = FixturesFactory.book(author);
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(authorRepository.findAuthorsByPenname(any())).thenReturn(Optional.of(author));

        var command = new BookCommand2(
                new ApiKey("bookApiKey").apiKey(), "Updated Name", LocalDate.of(2000,2,5), false,
                List.of(BookType.HARDCOVER.name()), 510, "Updated Description",
                List.of("penname"), List.of(BookGenre.YOUNG_ADULT.name()));

        bookService.updateBook2(command);

        assertThat(book.getName()).isEqualTo("Updated Name");
        assertThat(book.getDescription()).isEqualTo("Updated Description");
        assertThat(book.getAvailableOnline()).isFalse();
    }

    @Test
    void cant_update_non_existing_book() {
        assertThatThrownBy(() -> bookService.updateBook(new BookCommand(
                new ApiKey("bookApiKey").apiKey(), "Updated Name", LocalDate.now(), true,
                List.of(BookType.EBOOK.name()), 500, "Updated Description",
                List.of(new ApiKey("authorApiKey").apiKey()), List.of(BookGenre.COMICS.name()))))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("Book with api key (bookApiKey) not existent");
    }

    @Test
    void can_not_update_with_missing_author() {
        var book = FixturesFactory.book(FixturesFactory.author());
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(authorRepository.findAuthorByAuthorApiKey(any())).thenReturn(Optional.empty());


        var command = new BookCommand(
                new ApiKey("bookApiKey").apiKey(), "Updated Name", LocalDate.of(2000,2,5), false,
                List.of(BookType.HARDCOVER.name()), 510, "Updated Description",
                List.of(new ApiKey("authorApiKeyOther").apiKey()), List.of(BookGenre.YOUNG_ADULT.name()));

        assertThatThrownBy(() -> bookService.updateBook(command))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("No Authors for Book");
    }

    @Test
    void can_update_existing_book() {
        var author = FixturesFactory.author();
        var book = FixturesFactory.book(author);
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(authorRepository.findAuthorByAuthorApiKey(any())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).then(AdditionalAnswers.returnsFirstArg());

        var command = new BookCommand(
                new ApiKey("bookApiKey").apiKey(), "Updated Name", LocalDate.of(2000,2,5), false,
                List.of(BookType.HARDCOVER.name()), 510, "Updated Description",
                List.of(new ApiKey("authorApiKeyOther").apiKey()), List.of(BookGenre.YOUNG_ADULT.name()));

        bookService.updateBook(command);

        assertThat(book.getName()).isEqualTo("Updated Name");
        assertThat(book.getDescription()).isEqualTo("Updated Description");
        assertThat(book.getAvailableOnline()).isFalse();
    }

    @Test
    void can_update_existing_book_same_values_for_test_coverage() {
        var author = FixturesFactory.author();
        var book = FixturesFactory.book(author);
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(authorRepository.findAuthorByAuthorApiKey(any())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).then(AdditionalAnswers.returnsFirstArg());

        var command = new BookCommand(
                new ApiKey("bookApiKey").apiKey(), book.getName(), book.getReleaseDate(), book.getAvailableOnline(),
                book.getBookTypes().stream().map(BookType::name).toList(), book.getWordCount(), book.getDescription(),
                book.getAuthors().stream().map(Author::getAuthorApiKey).map(ApiKey::apiKey).toList(), book.getGenres().stream().map(BookGenre::name).toList());

        bookService.updateBook(command);

        assertThat(book.getName()).isEqualTo("dasd");
        assertThat(book.getDescription()).isEqualTo(command.description());
        assertThat(book.getAvailableOnline()).isEqualTo(command.availableOnline());
    }

    @Test
    void can_get_books_without_author_filter() {
        var book1 = FixturesFactory.book(FixturesFactory.author());
        var book2 = FixturesFactory.book(FixturesFactory.author());

        when(bookRepository.findAllProjected()).thenReturn(List.of(BookDto.bookDtoFromBook(book1), BookDto.bookDtoFromBook(book2)));

        var books = bookService.getBooks(null);

        assertThat(books).hasSize(2);
    }

    @Test
    void can_get_books2() {
        var book1 = FixturesFactory.book(FixturesFactory.author());
        var book2 = FixturesFactory.book(FixturesFactory.author());

        when(bookRepository.findAllProjected2()).thenReturn(List.of(BookDto2.bookDtoFromBook(book1), BookDto2.bookDtoFromBook(book2)));

        var books = bookService.getBooks2();

        assertThat(books).hasSize(2);
    }

    @Test
    void can_get_books_by_author() {
        var author = FixturesFactory.author();
        var book1 = FixturesFactory.book(author);

        when(authorRepository.findProjectedAuthorByAuthorApiKey(any())).thenReturn(Optional.of(AuthorDto.authorDtoFromAuthor(author)));
        when(bookRepository.findProjectedBooksByAuthorsContains(any())).thenReturn(List.of(BookDto.bookDtoFromBook(book1)));

        var books = bookService.getBooks(new ApiKey("authorApiKey").apiKey());

        assertThat(books).hasSize(1);
        assertThat(books.getFirst().name()).isEqualTo("dasd");
    }

    @Test
    void cant_get_books_by_non_existing_author() {
        assertThatThrownBy(() -> bookService.getBooks(new ApiKey("invalidApiKey").apiKey()))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("Author with api key (invalidApiKey) not existent");
    }

    @Test
    void can_get_single_book() {
        var author = FixturesFactory.author();
        var book = FixturesFactory.book(author);

        when(bookRepository.findProjectedBookByBookApiKey(any())).thenReturn(Optional.of(BookDto.bookDtoFromBook(book)));

        var bookDto = bookService.getBook(new ApiKey("bookApiKey").apiKey());

        assertThat(bookDto.name()).isEqualTo("dasd");
    }

    @Test
    void can_get_single_book2() {
        var author = FixturesFactory.author();
        var book = FixturesFactory.book(author);

        when(bookRepository.findProjectedBookByBookApiKey2(any())).thenReturn(Optional.of(BookDto2.bookDtoFromBook(book)));

        var bookDto = bookService.getBook2(new ApiKey("bookApiKey").apiKey());

        assertThat(bookDto.name()).isEqualTo("dasd");
    }

    @Test
    void cant_get_non_existing_book() {
        assertThatThrownBy(() -> bookService.getBook(new ApiKey("invalidApiKey").apiKey()))
                .isInstanceOf(BookService.BookServiceException.class)
                .hasMessageContaining("Book with api key (invalidApiKey) not existent");
    }

}