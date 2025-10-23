package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;

import java.util.Optional;

@Repository
public interface AuthorMongoRepository extends MongoRepository<AuthorDocument, String> {

    Optional<AuthorDocument> findByPostgresId(Long postgresId);

    Optional<AuthorDocument> findByApiKey(String apiKey);

    Optional<AuthorDocument> findByPenname(String penname);

    Optional<AuthorDocument> findByEmail(String email);

    void deleteByPostgresId(Long postgresId);
}