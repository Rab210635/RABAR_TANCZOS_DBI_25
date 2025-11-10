package spengergasse.at.sj2425scherzerrabar.mapper;

import org.springframework.stereotype.Component;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;

import java.util.stream.Collectors;

@Component
public class BookEmbeddedMapper {

    /**
     * Konvertiert Book zu MongoDB Document mit EMBEDDED Authors
     */
    public BookDocumentEmbedded toMongoDocument(Book book) {
        BookDocumentEmbedded doc = new BookDocumentEmbedded();
        doc.setPostgresId(book.getBookId().id());
        doc.setApiKey(book.getBookApiKey().apiKey());
        doc.setName(book.getName());
        doc.setReleaseDate(book.getReleaseDate());
        doc.setAvailableOnline(book.getAvailableOnline());
        doc.setWordCount(book.getWordCount());
        doc.setDescription(book.getDescription());

        // Convert enums to strings
        doc.setBookTypes(
                book.getBookTypes().stream()
                        .map(Enum::name)
                        .toList()
        );
        doc.setGenres(
                book.getGenres().stream()
                        .map(Enum::name)
                        .toList()
        );

        // Embed complete author data
        doc.setAuthors(
                book.getAuthors().stream()
                        .map(this::toEmbeddedAuthor)
                        .collect(Collectors.toList())
        );

        return doc;
    }

    /**
     * Konvertiert Author Domain-Objekt zu Embedded Author
     */
    private BookDocumentEmbedded.EmbeddedAuthor toEmbeddedAuthor(Author author) {
        BookDocumentEmbedded.EmbeddedAuthor embedded = new BookDocumentEmbedded.EmbeddedAuthor(
                author.getAuthorApiKey().apiKey(),
                author.getPenname(),
                author.getFirstName(),
                author.getLastName(),
                author.getEmailAddress() != null ? author.getEmailAddress().email() : null
        );

        // Convert addresses
        if (author.getAddress() != null) {
            embedded.setAddresses(
                    author.getAddress().stream()
                            .map(AuthorDocument.AddressMongo::fromAddress)
                            .collect(Collectors.toList())
            );
        }

        return embedded;
    }
}