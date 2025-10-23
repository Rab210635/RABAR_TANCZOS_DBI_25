package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookMongoRepository extends MongoRepository<BookDocument, String> {

    Optional<BookDocument> findByPostgresId(Long postgresId);

    Optional<BookDocument> findByApiKey(String apiKey);

    List<BookDocument> findByAuthorApiKeysContaining(String authorApiKey);

    void deleteByPostgresId(Long postgresId);
}