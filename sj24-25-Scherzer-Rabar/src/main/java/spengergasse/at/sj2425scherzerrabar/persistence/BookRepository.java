package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByBookApiKey(ApiKey apiKey);

    List<Book> findByAuthorsContains(Author author);

    // ============ OPTIMIZED QUERIES WITH EAGER FETCHING ============

    /**
     * CRITICAL FIX: Uses @EntityGraph WITHOUT @Query
     * This is the trick - let Spring Data JPA generate the query
     */
    @EntityGraph(attributePaths = {"authors", "bookTypes", "genres"})
    List<Book> findAllBy();


    @Query("""
select distinct new spengergasse.at.sj2425scherzerrabar.dtos.BookDto(b)
from Book b
left join fetch b.authors
left join fetch b.bookTypes
left join fetch b.genres
""")
    List<BookDto> findAllOptimizedProjection();

    /**
     * Wrapper für Projection - nutzt optimierte Query mit EntityGraph
     * Lädt authors, bookTypes, und genres eagerly
     */
    default List<BookDto> findAllProjectedOptimized() {
        return findAllBy().stream()
                .map(BookDto::bookDtoFromBook)
                .toList();
    }

    /**
     * Single book with all relations
     */
    @EntityGraph(attributePaths = {"authors", "bookTypes", "genres"})
    Optional<Book> findByBookApiKey(ApiKey apiKey);

    // ============ ALTE QUERIES (für Vergleich) ============

    @Query(""" 
    SELECT new spengergasse.at.sj2425scherzerrabar.dtos.BookDto(b) 
    FROM Book b 
    WHERE b.bookApiKey.apiKey = :bookApiKey
    """)
    Optional<BookDto> findProjectedBookByBookApiKey(String bookApiKey);

    @Query(""" 
    SELECT new spengergasse.at.sj2425scherzerrabar.dtos.BookDto2(b) 
    FROM Book b 
    WHERE b.bookApiKey.apiKey = :bookApiKey
    """)
    Optional<BookDto2> findProjectedBookByBookApiKey2(String bookApiKey);

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.BookDto(b) from Book b
    """)
    List<BookDto> findAllProjected();

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.BookDto2(b) from Book b
    """)
    List<BookDto2> findAllProjected2();

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.BookDto(b) from Book b
    WHERE exists (select a from b.authors a where a.authorApiKey.apiKey = :authorApiKey)
    """)
    List<BookDto> findProjectedBooksByAuthorsContains(String authorApiKey);
}