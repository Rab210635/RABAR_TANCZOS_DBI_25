package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookEmbeddedMongoRepository extends MongoRepository<BookDocumentEmbedded, String> {

    Optional<BookDocumentEmbedded> findByPostgresId(Long postgresId);

    Optional<BookDocumentEmbedded> findByApiKey(String apiKey);

    /**
     * Sucht Books, die einen Author mit dem angegebenen API-Key enthalten
     * Funktioniert mit embedded Authors
     */
    @Query("{ 'authors.api_key': ?0 }")
    List<BookDocumentEmbedded> findByEmbeddedAuthorApiKey(String authorApiKey);

    /**
     * Sucht Books, die einen Author mit dem angegebenen Penname enthalten
     */
    @Query("{ 'authors.penname': ?0 }")
    List<BookDocumentEmbedded> findByEmbeddedAuthorPenname(String penname);

    void deleteByPostgresId(Long postgresId);
}