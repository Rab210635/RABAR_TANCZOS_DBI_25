package spengergasse.at.sj2425scherzerrabar.mapper;

import org.springframework.stereotype.Component;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;

import java.util.stream.Collectors;

@Component
public class AuthorMapper {

    /**
     * Konvertiert Author Domain-Objekt zu MongoDB Document
     */
    public AuthorDocument toMongoDocument(Author author) {
        if (author == null) {
            return null;
        }

        AuthorDocument doc = new AuthorDocument();

        if (author.getPersonId() != null) {
            doc.setPostgresId(author.getPersonId().id());
        }

        doc.setFirstName(author.getFirstName());
        doc.setLastName(author.getLastName());
        doc.setPenname(author.getPenname());

        if (author.getEmailAddress() != null) {
            doc.setEmail(author.getEmailAddress().email());
        }

        if (author.getAuthorApiKey() != null) {
            doc.setApiKey(author.getAuthorApiKey().apiKey());
        }

        if (author.getAddress() != null) {
            doc.setAddresses(
                    author.getAddress().stream()
                            .map(AuthorDocument.AddressMongo::fromAddress)
                            .collect(Collectors.toList())
            );
        }

        return doc;
    }

    /**
     * Konvertiert MongoDB Document zu Author Domain-Objekt
     * ACHTUNG: PersonId wird nicht gesetzt, da es @GeneratedValue ist
     */
    public Author fromMongoDocument(AuthorDocument doc) {
        if (doc == null) {
            return null;
        }

        // Author benötigt EmailAddress, nicht nur String
        spengergasse.at.sj2425scherzerrabar.domain.EmailAddress emailAddress =
                new spengergasse.at.sj2425scherzerrabar.domain.EmailAddress(doc.getEmail());

        Author author = new Author(
                doc.getFirstName(),
                doc.getLastName(),
                doc.getAddresses().stream()
                        .map(AuthorDocument.AddressMongo::toAddress)
                        .collect(Collectors.toList()),
                emailAddress,
                doc.getPenname()
        );

        // API Key manuell setzen (da im Konstruktor generiert wird)
        // Hier müsstest du in Author eine setAuthorApiKey Methode hinzufügen

        return author;
    }
}