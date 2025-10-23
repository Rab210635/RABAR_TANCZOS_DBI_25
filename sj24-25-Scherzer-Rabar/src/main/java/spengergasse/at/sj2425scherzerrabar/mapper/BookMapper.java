package spengergasse.at.sj2425scherzerrabar.mapper;

import org.springframework.stereotype.Component;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;

import java.util.stream.Collectors;

@Component
public class BookMapper {

    /**
     * Konvertiert Book Domain-Objekt zu MongoDB Document
     */
    public BookDocument toMongoDocument(Book book) {
        if (book == null) {
            return null;
        }

        BookDocument doc = new BookDocument();

        if (book.getBookId() != null) {
            doc.setPostgresId(book.getBookId().id());
        }

        doc.setName(book.getName());
        doc.setReleaseDate(book.getReleaseDate());
        doc.setAvailableOnline(book.getAvailableOnline());
        doc.setWordCount(book.getWordCount());
        doc.setDescription(book.getDescription());

        if (book.getBookApiKey() != null) {
            doc.setApiKey(book.getBookApiKey().apiKey());
        }

        if (book.getBookTypes() != null) {
            doc.setBookTypes(
                    book.getBookTypes().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList())
            );
        }

        if (book.getGenres() != null) {
            doc.setGenres(
                    book.getGenres().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList())
            );
        }

        if (book.getAuthors() != null) {
            doc.setAuthorApiKeys(
                    book.getAuthors().stream()
                            .map(author -> author.getAuthorApiKey().apiKey())
                            .collect(Collectors.toList())
            );
        }

        return doc;
    }

    /**
     * Konvertiert MongoDB Document zu Book Domain-Objekt
     * ACHTUNG: Vollständige Rekonstruktion nicht möglich ohne Author-Daten zu laden
     */
    public Book fromMongoDocument(BookDocument doc) {
        if (doc == null) {
            return null;
        }

        // Dies ist eine vereinfachte Version
        // In der Praxis würdest du die Authors aus der DB laden müssen
        Book book = new Book();
        book.setName(doc.getName());
        book.setReleaseDate(doc.getReleaseDate());
        book.setAvailableOnline(doc.getAvailableOnline());
        book.setWordCount(doc.getWordCount());
        book.setDescription(doc.getDescription());

        return book;
    }
}