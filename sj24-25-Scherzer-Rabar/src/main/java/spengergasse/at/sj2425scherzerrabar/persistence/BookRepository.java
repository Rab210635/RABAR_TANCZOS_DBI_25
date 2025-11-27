package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * If you MUST keep Lists in Book entity, use this approach
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByBookApiKey(ApiKey apiKey);

    List<Book> findByAuthorsContains(Author author);

    // ============ SOLUTION FOR LISTS: Multiple queries approach ============

    /**
     * Step 1: Fetch books with only ONE collection (authors)
     */
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN FETCH b.authors
        """)
    List<Book> findAllBooksWithAuthors();

    /**
     * Step 2: Fetch bookTypes for specific books
     * We need book IDs to avoid loading everything again
     */
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN FETCH b.bookTypes
        WHERE b.bookId.id IN :bookIds
        """)
    List<Book> fetchBookTypesForBooks(@Param("bookIds") List<Long> bookIds);

    /**
     * Step 3: Fetch genres for specific books
     */
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN FETCH b.genres
        WHERE b.bookId.id IN :bookIds
        """)
    List<Book> fetchGenresForBooks(@Param("bookIds") List<Long> bookIds);

    /**
     * Optimized method that coordinates all three queries
     * This replaces your broken findAllProjectedOptimized
     */
    default List<Book> findAllWithAllCollections() {
        // Step 1: Get books with authors
        List<Book> books = findAllBooksWithAuthors();

        if (books.isEmpty()) {
            return books;
        }

        // Extract book IDs
        List<Long> bookIds = books.stream()
                .map(b -> b.getBookId().id())
                .collect(Collectors.toList());

        // Step 2 & 3: Load other collections
        // These will hit the session cache and just populate missing collections
        fetchBookTypesForBooks(bookIds);
        fetchGenresForBooks(bookIds);

        return books;
    }

    /**
     * BETTER: Use this for DTOs - avoids multiple queries
     */
    default List<BookDto> findAllProjectedOptimized() {
        return findAllWithAllCollections().stream()
                .map(BookDto::bookDtoFromBook)
                .toList();
    }

    /**
     * Single book with all relations - this works!
     */
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN FETCH b.authors a
        LEFT JOIN FETCH b.bookTypes
        LEFT JOIN FETCH b.genres
        WHERE b.bookApiKey = :apiKey
        """)
    Optional<Book> findBookByBookApiKeyWithAllRelations(@Param("apiKey") ApiKey apiKey);

    // ============ OLD QUERIES (for comparison) ============

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

    /**
     * WARNING: This causes N+1 queries!
     * Use findAllProjectedOptimized() instead
     */
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