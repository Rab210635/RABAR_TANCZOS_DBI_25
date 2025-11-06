package spengergasse.at.sj2425scherzerrabar.mapper;

import org.springframework.stereotype.Component;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;

@Component
public class BookMapper {

    public BookDocument toMongoDocument(Book book) {
        BookDocument doc = new BookDocument();
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

        // Store author API keys
        doc.setAuthorApiKeys(
                book.getAuthors().stream()
                        .map(author -> author.getAuthorApiKey().apiKey())
                        .toList()
        );

        return doc;
    }
}