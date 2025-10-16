package spengergasse.at.sj2425scherzerrabar.persistence;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.domain.BuyableBook;
import spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyableBookRepository extends JpaRepository<BuyableBook, Long> {
    Optional<BuyableBook> findBuyableBookByBuyableBookApiKey(ApiKey apiKey);

    List<BuyableBook> findAllByPublisher_PublisherApiKey_ApiKey(String publisherPublisherApiKeyApiKey);

    List<BuyableBook> findAllByBook_BookApiKey_ApiKey(String bookBookApiKeyApiKey);

    List<BuyableBook> findAllByPrice(Float price);

    List<BuyableBook> findAllByBookType(@NotNull BookType bookType);


    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto(
            bb
        ) from BuyableBook bb where bb.buyableBookApiKey.apiKey = :apiKey
        """)
    Optional<BuyableBookDto> findProjectedBuyableBookByBuyableBookApiKey(String apiKey);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto(
            bb
        ) from BuyableBook bb
        """)
    List<BuyableBookDto> findAllProjected();

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto(
            bb
        ) from BuyableBook bb where bb.publisher.publisherApiKey.apiKey = :apiKey
        """)
    List<BuyableBookDto> findProjectedByPublisher(String apiKey);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto(
            bb
        ) from BuyableBook bb where bb.bookType = :bookType
        """)
    List<BuyableBookDto> findProjectedByBookType(BookType bookType);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto(
            bb
        ) from BuyableBook bb where bb.price <= :price
        """)
    List<BuyableBookDto> findProjectedByPrice(Float price);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto(
            bb
        ) from BuyableBook bb where bb.book.bookApiKey.apiKey = :apiKey
        """)
    List<BuyableBookDto> findProjectedByBook(String apiKey);

}
