package spengergasse.at.sj2425scherzerrabar.dtos;

import spengergasse.at.sj2425scherzerrabar.domain.BuyableBook;

public record BuyableBookDto(
        String buyableBookApiKey,Float price, String publisherApiKey,
        String bookType, Integer pageCount, String bookApiKey
) {
    public static BuyableBookDto buyableBookDtoFromBuyableBook(BuyableBook buyableBook) {
        return new BuyableBookDto(
          buyableBook.getBuyableBookApiKey().apiKey(),buyableBook.getPrice(),
                buyableBook.getPublisher().getPublisherApiKey().apiKey(), buyableBook.getBookType().name(),
                buyableBook.getPageCount(),buyableBook.getBook().getBookApiKey().apiKey()
        );
    }
    public BuyableBookDto(BuyableBook buyableBook) {
        this(buyableBook.getBuyableBookApiKey().apiKey(),buyableBook.getPrice(),buyableBook.getPublisher().getPublisherApiKey().apiKey(),buyableBook.getBookType().name(),buyableBook.getPageCount(),buyableBook.getBook().getBookApiKey().apiKey());
    }
}
