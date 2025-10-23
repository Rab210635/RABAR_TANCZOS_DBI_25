package spengergasse.at.sj2425scherzerrabar.dtos;


import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;


import java.util.List;

public record AuthorDto(
        String apiKey,
        String penname,
        List<String> address,
        String firstname,
        String lastname,
        String emailAddress
) {

    public AuthorDto (Author author) {
        this(author.getAuthorApiKey().apiKey(),author.getPenname(),author.getAddress().stream().map(Address::toString).toList(),author.getFirstName(),author.getLastName(),author.getEmailAddress().email());
    }

    public static AuthorDto authorDtoFromAuthor(Author author) {
        return new AuthorDto(
                author.getAuthorApiKey().apiKey(), author.getPenname(),
                author.getAddress().stream().map((Address::toString)).toList(),
                author.getFirstName(), author.getLastName(), author.getEmailAddress().email()
        );
    }
}
