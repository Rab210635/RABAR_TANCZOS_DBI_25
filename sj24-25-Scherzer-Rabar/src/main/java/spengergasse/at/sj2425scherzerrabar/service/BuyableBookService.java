package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.BuyableBookCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BuyableBookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.PublisherRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;


@Service
@Transactional(readOnly=true)
public class BuyableBookService {

    private final BuyableBookRepository buyableBookRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);


    public BuyableBookService(BuyableBookRepository buyableBookRepository, PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.buyableBookRepository = buyableBookRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BuyableBookDto createBuyableBook(BuyableBookCommand command) {
        logger.debug("entered createBuyableBook");
        var book = bookRepository.findBookByBookApiKey(new ApiKey(command.bookApiKey()))
                .orElseThrow(() -> BuyableBookServiceException.noBuyableBookForApiKey(command.bookApiKey()));

        var publisher = publisherRepository.findPublisherByPublisherApiKey(new ApiKey(command.publisherApiKey()))
                .orElseThrow(()-> BuyableBookServiceException.noPublisherForApikey(command.publisherApiKey()));

        return BuyableBookDto.buyableBookDtoFromBuyableBook(
                buyableBookRepository.save(new BuyableBook(publisher, BookType.valueOf(command.bookType()), command.pageCount(),book,command.price())));
    }

    @Transactional
    public BuyableBookDto updateBuyableBook(BuyableBookCommand command) {
        logger.debug("entered updateBuyableBook");
        BuyableBook buyableBook = buyableBookRepository.findBuyableBookByBuyableBookApiKey(new ApiKey(command.buyableBookApiKey()))
                .orElseThrow(() -> BuyableBookServiceException.noBuyableBookForApiKey(command.buyableBookApiKey()));

        var book = bookRepository.findBookByBookApiKey(new ApiKey(command.bookApiKey()))
                .orElseThrow(() -> BuyableBookServiceException.noBookForApikey(command.bookApiKey()));

        var publisher = publisherRepository.findPublisherByPublisherApiKey(new ApiKey(command.publisherApiKey()))
                .orElseThrow(() -> BuyableBookServiceException.noPublisherForApikey(command.publisherApiKey()));

        buyableBook.setPublisher(publisher);
        buyableBook.setPageCount(command.pageCount());
        buyableBook.setPrice(command.price());
        buyableBook.setBook(book);
        buyableBook.setBookType(BookType.valueOf(command.bookType()));

        buyableBookRepository.save(buyableBook);
        return BuyableBookDto.buyableBookDtoFromBuyableBook(buyableBook);
    }

    @Transactional
    public void deleteBuyableBook(String buyableBookApiKey) {
        logger.debug("entered deleteBuyableBook");
        BuyableBook buyableBook = buyableBookRepository.findBuyableBookByBuyableBookApiKey(new ApiKey(buyableBookApiKey))
                .orElseThrow(() -> BuyableBookServiceException.noBuyableBookForApiKey(buyableBookApiKey));
        buyableBookRepository.delete(buyableBook);
    }

    public List<BuyableBookDto> getAllBuyableBooks() {
        logger.debug("entered getAllBuyableBooks");
        return buyableBookRepository.findAllProjected();
    }

    public BuyableBookDto getBuyableBookByApiKey(String apiKey) {
        logger.debug("entered getBuyableBookByApiKey");
        return buyableBookRepository.findProjectedBuyableBookByBuyableBookApiKey(apiKey)
                .orElseThrow(() -> BuyableBookServiceException.noBuyableBookForApiKey(apiKey));
    }

    public List<BuyableBookDto> getAllBuyableBooksByPublisher(String publisherApiKey) {
        logger.debug("entered getAllBuyableBooksByPublisher");
        publisherRepository.findProjectedByPublisherApiKey(publisherApiKey)
                .orElseThrow(() -> BuyableBookServiceException.noPublisherForApikey(publisherApiKey));
        return buyableBookRepository.findProjectedByPublisher(publisherApiKey);
    }

    public List<BuyableBookDto> getAllBuyableBooksByPrice(Float price) {
        logger.debug("entered getAllBuyableBooksByPrice");
        return buyableBookRepository.findProjectedByPrice(price);
    }
    public List<BuyableBookDto> getAllBuyableBooksByBook(String bookApiKey) {
        logger.debug("entered getAllBuyableBooksByBook");
        var book = bookRepository.findProjectedBookByBookApiKey(bookApiKey)
                .orElseThrow(() -> BuyableBookServiceException.noBookForApikey(bookApiKey));
        return buyableBookRepository.findProjectedByBook(bookApiKey);
    }

    public List<BuyableBookDto> getAllBuyableBooksByBookType(String bookType) {
        logger.debug("entered getAllBuyableBooksByBookType");
        return buyableBookRepository.findProjectedByBookType(BookType.valueOf(bookType));
    }

    public static class BuyableBookServiceException extends RuntimeException
    {
        private BuyableBookServiceException(String message)
        {
            super(message);
        }

        public static BuyableBookServiceException noBuyableBookForApiKey(String apiKey)
        {
            return new BuyableBookServiceException("Buyable Book with api key (%s) not existent".formatted(apiKey));
        }

        public static BuyableBookServiceException noBookForApikey(String apiKey)
        {
            return new BuyableBookServiceException("Book with api key (%s) not existent".formatted(apiKey));
        }
        public static BuyableBookServiceException noPublisherForApikey(String apiKey)
        {
            return new BuyableBookServiceException("Publisher with api key (%s) not existent".formatted(apiKey));
        }
    }
}
