package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findAuthorByAuthorApiKey(ApiKey apiKey);

    Optional<Author> findAuthorsByPenname(String penname);

    Optional<Author> findAuthorByEmailAddress_Email(String emailAddressEmail);


    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto(
        a
    ) from Author a where a.authorApiKey.apiKey = :apiKey
    """)
    Optional<AuthorDto> findProjectedAuthorByAuthorApiKey(String apiKey);

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto(
        a
    ) from Author a
    """)
    List<AuthorDto> findAllProjected();

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto(
        a
    ) from Author a where a.penname = :penname
    """)
    Optional<AuthorDto> findProjectedAuthorByPenname(String penname);

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto(
        a
    ) from Author a where a.emailAddress.email = :email
    """)
    Optional<AuthorDto> findProjectedAuthorByEmailAddress_Email(String email);
}
