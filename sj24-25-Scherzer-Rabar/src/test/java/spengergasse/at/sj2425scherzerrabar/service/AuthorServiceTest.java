package spengergasse.at.sj2425scherzerrabar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {
    private @Mock AuthorRepository authorRepository;

    private AuthorService authorService;

    @BeforeEach
    void setUp(){
        assumeThat(authorRepository).isNotNull();
        authorService = new AuthorService(authorRepository);
    }

    @Test
    void cant_create_author_with_wrong_address() {
       assertThatThrownBy(()-> authorService.createAuthor( new AuthorCommand(
               "authorApiKey","Aaron",List.of(new Address("12","Wien",12).toString()),
               "Paron", "krabar",new EmailAddress("hoho@s.s").email()))).isInstanceOf(Address.AddressException.class);
    }

    @Test
    void can_create_author(){
        when(authorRepository.save(any(Author.class))).then(AdditionalAnswers.returnsFirstArg());

        var author = authorService.createAuthor( new AuthorCommand(
                "authorApiKey","Aaron",List.of(new Address("12","Wien",1212).toString()),
                "Paron", "krabar",new EmailAddress("hoho@sasd.at").email()));
        assertThat(author).isNotNull();
    }

    @Test
    void can_delete_existing_author(){
        Author author = FixturesFactory.author();
        when(authorRepository.findAuthorByAuthorApiKey(any())).thenReturn(Optional.of(author));

        authorService.deleteAuthor("validApiKey");

        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    void cant_delete_not_existing_author(){
        ApiKey apiKey = new ApiKey("authorApiKey");
        assertThatThrownBy(()->authorService.deleteAuthor(apiKey.apiKey()))
                .isInstanceOf(AuthorService.AuthorServiceException.class)
                .hasMessageContaining("Author with api key (authorApiKey) not existent");
    }

    @Test
    void can_update_existing_author(){
        Author author = FixturesFactory.author();
        when(authorRepository.findAuthorByAuthorApiKey(any())).thenReturn(Optional.of(author));

        AuthorDto a = authorService.updateAuthor(new AuthorCommand(
                "authorApiKey","Update",List.of(new Address("12","Wien",1212).toString()),
                "Mustermann", "Max",new EmailAddress("hoho@sasd.at").email()));

        assertThat(a.penname()).isEqualTo("Update");
        assertThat(a.lastname()).isEqualTo("Max");
        assertThat(a.firstname()).isEqualTo("Mustermann");
    }

    @Test
    void cant_update_not_existing_author(){
        assertThatThrownBy(()->authorService.updateAuthor(new AuthorCommand(
                "authorApiKey","Update",List.of(new Address("12","Wien",1212).toString()),
                "Mustermann", "Max",new EmailAddress("hoho@sasd.at").email())))
                .isInstanceOf(AuthorService.AuthorServiceException.class)
                .hasMessageContaining("Author with api key (authorApiKey) not existent");
    }

    @Test
    void can_get_existing_author_by_id(){
        Author author = FixturesFactory.author();
        when(authorRepository.findProjectedAuthorByAuthorApiKey(any()))
                .thenReturn(Optional.of(AuthorDto.authorDtoFromAuthor(author)));
        var author1 = authorService.getAuthor(author.getAuthorApiKey().apiKey());
        assertThat(author1).isEqualTo(AuthorDto.authorDtoFromAuthor(author));
        verify(authorRepository, times(1)).findProjectedAuthorByAuthorApiKey(any());
    }

    @Test
    void cant_get_not_existing_author_by_id(){
        assertThatThrownBy(()->authorService.getAuthor(new ApiKey("authorApiKey").apiKey()))
                .isInstanceOf(AuthorService.AuthorServiceException.class)
                .hasMessageContaining("Author with api key (authorApiKey) not existent");
    }

    @Test
    void can_get_existing_author_by_penname(){
        Author author = FixturesFactory.author();
        when(authorRepository.findProjectedAuthorByPenname(any()))
                .thenReturn(Optional.of(AuthorDto.authorDtoFromAuthor(author)));
        var author1 = authorService.getAuthorByPenname(author.getPenname());
        assertThat(author1).isNotNull();
        verify(authorRepository, times(1)).findProjectedAuthorByPenname(any());
    }

    @Test
    void cant_get_not_existing_author_by_penname(){
        assertThatThrownBy(()->authorService.getAuthorByPenname("A"))
                .isInstanceOf(AuthorService.AuthorServiceException.class)
                .hasMessageContaining("Author with penname (A) not existent");
    }

    @Test
    void can_get_authors() {
        Author author = FixturesFactory.author();
        Author author2 = FixturesFactory.author();
        when(authorRepository.findAllProjected())
                .thenReturn(List.of(AuthorDto.authorDtoFromAuthor(author),AuthorDto.authorDtoFromAuthor(author2)));

        var authors = authorService.getAuthors();
        assertThat(authors).hasSize(2);
    }

    @Test
    void can_get_existing_author_by_email_address(){
        Author author = FixturesFactory.author();
        when(authorRepository.findProjectedAuthorByEmailAddress_Email(any()))
                .thenReturn(Optional.of(AuthorDto.authorDtoFromAuthor(author)));
        var author1 = authorService.getAuthorByEmailAddress(author.getEmailAddress().email());
        assertThat(author1).isNotNull();
        verify(authorRepository, times(1)).findProjectedAuthorByEmailAddress_Email(any());
    }

    @Test
    void cant_get_not_existing_author_by_email_address(){
        assertThatThrownBy(()->authorService.getAuthorByEmailAddress("A"))
                .isInstanceOf(AuthorService.AuthorServiceException.class)
                .hasMessageContaining("Author with email (A) not existent");
    }
}