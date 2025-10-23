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
import spengergasse.at.sj2425scherzerrabar.commands.BuyableBookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BuyableBookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.PublisherRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class BuyableBookServiceTest {
    private @Mock BuyableBookRepository buyableBookRepository;
    private @Mock PublisherRepository publisherRepository;
    private @Mock BookRepository bookRepository;

    private BuyableBookService buyableBookService;

    @BeforeEach
    void setUp() {
        assumeThat(buyableBookRepository).isNotNull();
        assumeThat(publisherRepository).isNotNull();
        assumeThat(bookRepository).isNotNull();
        buyableBookService = new BuyableBookService(buyableBookRepository, publisherRepository, bookRepository);
    }

    @Test
    void can_create_buyable_book() {
        var publisher = FixturesFactory.publisher(FixturesFactory.address2());
        var book = FixturesFactory.book(FixturesFactory.author());

        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(buyableBookRepository.save(any(BuyableBook.class))).then(AdditionalAnswers.returnsFirstArg());

        BuyableBookDto buyableBook = buyableBookService.createBuyableBook(
                new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,publisher.getPublisherApiKey().apiKey(),
                        BookType.HARDCOVER.name(), 100,book.getBookApiKey().apiKey()));

        assertThat(buyableBook).isNotNull();
        assertThat(buyableBook.bookApiKey()).isEqualTo(book.getBookApiKey().apiKey());
        assertThat(buyableBook.publisherApiKey()).isEqualTo(publisher.getPublisherApiKey().apiKey());
    }

    @Test
    void cant_create_buyable_book_with_missing_publisher() {
        var book = FixturesFactory.book(FixturesFactory.author());

        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));

       assertThatThrownBy(() ->buyableBookService.createBuyableBook(new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,
               "invalidApiKey", BookType.HARDCOVER.name(), 100,book.getBookApiKey().apiKey())))
               .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
               .hasMessageContaining("Publisher with api key (invalidApiKey) not existent");
    }

    @Test
    void cant_create_buyable_book_with_missing_book() {
        assertThatThrownBy(() ->buyableBookService.createBuyableBook(new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,
                "validApiKey", BookType.HARDCOVER.name(), 100,"invalidApiKey")))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Book with api key (invalidApiKey) not existent");
    }

    @Test
    void can_update_buyable_book() {
        var publisher = FixturesFactory.publisher(FixturesFactory.address2());
        var book = FixturesFactory.book(FixturesFactory.author());
        var buyableBook = FixturesFactory.buyableBook();

        when(buyableBookRepository.findBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(buyableBook));
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(buyableBookRepository.save(any(BuyableBook.class))).then(AdditionalAnswers.returnsFirstArg());

        BuyableBookDto updatedBuyableBook = buyableBookService.updateBuyableBook(
                new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,publisher.getPublisherApiKey().apiKey(),
                        BookType.HARDCOVER.name(), 100,book.getBookApiKey().apiKey()));

        assertThat(updatedBuyableBook).isNotNull();
        assertThat(updatedBuyableBook.bookType()).isEqualTo(BookType.HARDCOVER.name());
    }

    @Test
    void cant_update_not_existing_buyable_book() {
        var publisher = FixturesFactory.publisher(FixturesFactory.address2());
        var book = FixturesFactory.book(FixturesFactory.author());

        assertThatThrownBy(()->buyableBookService.updateBuyableBook(
                new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,publisher.getPublisherApiKey().apiKey(),
                        BookType.HARDCOVER.name(), 100,book.getBookApiKey().apiKey())))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Buyable Book with api key (BuyableBookApiKey) not existent");
    }

    @Test
    void cant_update_buyable_book_with_missing_publisher() {
        var book = FixturesFactory.book(FixturesFactory.author());
        var buyableBook = FixturesFactory.buyableBook();

        when(buyableBookRepository.findBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(buyableBook));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));

        assertThatThrownBy(()->buyableBookService.updateBuyableBook(
                new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,"invalidApiKey",
                        BookType.HARDCOVER.name(), 100,book.getBookApiKey().apiKey())))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Publisher with api key (invalidApiKey) not existent");
    }

    @Test
    void cant_update_buyable_book_with_missing_book() {
        var publisher = FixturesFactory.publisher(FixturesFactory.address2());
        var buyableBook = FixturesFactory.buyableBook();

        when(buyableBookRepository.findBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(buyableBook));

        assertThatThrownBy(()->buyableBookService.updateBuyableBook(
                new BuyableBookCommand(new ApiKey("BuyableBookApiKey").apiKey(),10f,publisher.getPublisherApiKey().apiKey(),
                        BookType.HARDCOVER.name(), 100,"invalidApiKey")))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Book with api key (invalidApiKey) not existent");
    }

    @Test
    void can_delete_buyable_book() {
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        when(buyableBookRepository.findBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(buyableBook));

        buyableBookService.deleteBuyableBook(new ApiKey("validApiKey").apiKey());

        verify(buyableBookRepository, times(1)).delete(buyableBook);
    }

    @Test
    void cant_delete_non_existing_buyable_book() {
        assertThatThrownBy(()->buyableBookService.deleteBuyableBook(new ApiKey("invalidApiKey").apiKey()))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Buyable Book with api key (invalidApiKey) not existent");
    }

    @Test
    void can_get_existing_buyable_book_by_id(){
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        when(buyableBookRepository.findProjectedBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook)));

        var buyableBook1 = buyableBookService.getBuyableBookByApiKey(buyableBook.getBuyableBookApiKey().apiKey());
        assertThat(buyableBook1).isEqualTo(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook));
        verify(buyableBookRepository, times(1)).findProjectedBuyableBookByBuyableBookApiKey(any());
    }

    @Test
    void cant_get_not_existing_buyable_book_by_id() {
        assertThatThrownBy(()->buyableBookService.getBuyableBookByApiKey(new ApiKey("invalidApiKey").apiKey()))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Buyable Book with api key (invalidApiKey) not existent");
    }

    @Test
    void can_get_buyable_books() {
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        when(buyableBookRepository.findAllProjected()).thenReturn(List.of(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook),BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook)));

        var buyableBooks = buyableBookService.getAllBuyableBooks();
        assertThat(buyableBooks).hasSize(2);
    }

    @Test
    void can_get_buyable_book_by_price() {
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        BuyableBook buyableBook1 = FixturesFactory.buyableBook();
        when(buyableBookRepository.findProjectedByPrice(10f)).thenReturn(List.of(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook),BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook1)));

        var buyableBooks = buyableBookService.getAllBuyableBooksByPrice(10f);
        assertThat(buyableBooks).hasSize(2);
    }

    @Test
    void can_get_buyable_book_by_book_type() {
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        BuyableBook buyableBook1 = FixturesFactory.buyableBook();
        when(buyableBookRepository.findProjectedByBookType(BookType.EBOOK)).thenReturn(List.of(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook),BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook1)));

        var buyableBooks = buyableBookService.getAllBuyableBooksByBookType(BookType.EBOOK.name());
        assertThat(buyableBooks).hasSize(2);
    }

    @Test
    void can_get_buyable_book_by_book() {
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        BuyableBook buyableBook1 = FixturesFactory.buyableBook();
        Book book = FixturesFactory.book(FixturesFactory.author());

        when(bookRepository.findProjectedBookByBookApiKey(any())).thenReturn(Optional.of(BookDto.bookDtoFromBook(book)));
        when(buyableBookRepository.findProjectedByBook("bookApiKey")).thenReturn(List.of(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook),BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook1)));

        var buyableBooks = buyableBookService.getAllBuyableBooksByBook("bookApiKey");
        assertThat(buyableBooks).hasSize(2);
    }

    @Test
    void cant_get_buyable_book_by_book_with_missing_book() {
        assertThatThrownBy(()->buyableBookService.getAllBuyableBooksByBook("bookApiKey"))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Book with api key (bookApiKey) not existent");
    }

    @Test
    void can_get_buyable_book_by_publisher() {
        BuyableBook buyableBook = FixturesFactory.buyableBook();
        BuyableBook buyableBook1 = FixturesFactory.buyableBook();
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());

        when(publisherRepository.findProjectedByPublisherApiKey(any())).thenReturn(Optional.of(PublisherDto.publisherDtoFromPublisher(publisher)));
        when(buyableBookRepository.findProjectedByPublisher("publisherApiKey")).thenReturn(List.of(BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook),BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook1)));

        var buyableBooks = buyableBookService.getAllBuyableBooksByPublisher("publisherApiKey");
        assertThat(buyableBooks).hasSize(2);
    }

    @Test
    void cant_get_buyable_book_by_publisher_with_missing_publisher() {
        assertThatThrownBy(()->buyableBookService.getAllBuyableBooksByPublisher(""))
                .isInstanceOf(BuyableBookService.BuyableBookServiceException.class)
                .hasMessageContaining("Publisher with api key () not existent");
    }
}