package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.Link;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.domain.Author;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WithMockUser(username = "jaaron", authorities = {"read"})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({SpringExtension.class})
@WebMvcTest(AuthorRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthorRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AuthorService authorService;

    @Test
    public void should_return_author_when_author_exists() throws Exception {
        // Setup test data
        Author author = FixturesFactory.author();
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(author);

        // Mock service call
        when(authorService.getAuthor(any())).thenReturn(dto);

        // Perform GET request and validate response
        mockMvc.perform(get("/api/authors/author").param("apiKey","ValidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.penname").value(dto.penname()))
                .andExpect(jsonPath("$.firstname").value(dto.firstname()))
                .andExpect(jsonPath("$.lastname").value(dto.lastname()))
                .andExpect(jsonPath("$.emailAddress").value(dto.emailAddress()))
                .andExpect(jsonPath("$.address").isArray())
                .andDo(document("authors/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the author to search for")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the author"),
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("firstname").description("The author's first name"),
                                fieldWithPath("lastname").description("The author's last name"),
                                fieldWithPath("emailAddress").description("The author's email address"),
                                fieldWithPath("address").description("List of author address")
                        )));

    }

    @Test
    public void should_return_author_by_penname_when_author_exists() throws Exception {
        // Setup test data
        Author author = FixturesFactory.author();
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(author);

        // Mock service call
        when(authorService.getAuthorByPenname(any())).thenReturn(dto);

        // Perform GET request and validate response
        mockMvc.perform(get("/api/authors/author")
                        .param("penname","ValidPenname")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.penname").value(dto.penname()))
                .andExpect(jsonPath("$.firstname").value(dto.firstname()))
                .andExpect(jsonPath("$.lastname").value(dto.lastname()))
                .andExpect(jsonPath("$.emailAddress").value(dto.emailAddress()))
                .andExpect(jsonPath("$.address").isArray())
                .andDo(document("authors/get-by-penname",
                        queryParameters(
                                parameterWithName("penname").description("The Email of the author to search for")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the author"),
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("firstname").description("The author's first name"),
                                fieldWithPath("lastname").description("The author's last name"),
                                fieldWithPath("emailAddress").description("The author's email address"),
                                fieldWithPath("address").description("List of author address")
                        )));

    }

    @Test
    public void should_return_author_by_email_when_author_exists() throws Exception {
        // Setup test data
        Author author = FixturesFactory.author();
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(author);

        // Mock service call
        when(authorService.getAuthorByEmailAddress(any())).thenReturn(dto);

        // Perform GET request and validate response
        mockMvc.perform(get("/api/authors/author").param("email","ValidEmailAddress")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.penname").value(dto.penname()))
                .andExpect(jsonPath("$.firstname").value(dto.firstname()))
                .andExpect(jsonPath("$.lastname").value(dto.lastname()))
                .andExpect(jsonPath("$.emailAddress").value(dto.emailAddress()))
                .andExpect(jsonPath("$.address").isArray())
                .andDo(document("authors/get-by-email",
                        queryParameters(
                                parameterWithName("email").description("The Email of the author to search for")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the author"),
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("firstname").description("The author's first name"),
                                fieldWithPath("lastname").description("The author's last name"),
                                fieldWithPath("emailAddress").description("The author's email address"),
                                fieldWithPath("address").description("List of author address")
                        )));

    }



    @Test
    public void should_return_all_authors() throws Exception {
        // Setup test data
        Author author = FixturesFactory.author();
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(author);

        // Mock service call
        when(authorService.getAuthors()).thenReturn(List.of(dto));

        // Perform GET request and validate response
        mockMvc.perform(get("/api/authors")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].penname").value(dto.penname()))
                .andExpect(jsonPath("$[0].firstname").value(dto.firstname()))
                .andExpect(jsonPath("$[0].lastname").value(dto.lastname()))
                .andExpect(jsonPath("$[0].emailAddress").value(dto.emailAddress()))
                .andExpect(jsonPath("$[0].address").isArray())
                .andExpect(jsonPath("$[0].address[0]").value(dto.address().getFirst()))
                .andDo(document("authors/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the author"),
                                fieldWithPath("[].penname").description("The pen name of the author"),
                                fieldWithPath("[].firstname").description("The author's first name"),
                                fieldWithPath("[].lastname").description("The author's last name"),
                                fieldWithPath("[].emailAddress").description("The author's email address"),
                                fieldWithPath("[].address").description("List of author address")
                        )));

    }





    @Test
    public void should_create_author() throws Exception {
        AuthorCommand command = new AuthorCommand("newApiKey", "PenName", List.of("123 Main St"), "First", "Last", "author@example.com");
        AuthorDto dto = new AuthorDto("newApiKey", "PenName", List.of("123 Main St"), "First", "Last", "author@example.com");
        Link expectedSelfLink = linkTo(methodOn(AuthorRestController.class).getAuthor(Optional.of(dto.apiKey()),null,null)).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        // Mock service to return created author
        when(authorService.createAuthor(any())).thenReturn(dto);

        // Perform POST request and validate response
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"penname\": \"PenName\", "
                                + "\"address\": [\"123 Main St\"], "
                                + "\"firstname\": \"First\", "
                                + "\"lastname\": \"Last\", "
                                + "\"emailAddress\": \"author@example.com\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.penname").value(dto.penname()))
                .andExpect(jsonPath("$.firstname").value(dto.firstname()))
                .andExpect(jsonPath("$.lastname").value(dto.lastname()))
                .andExpect(jsonPath("$.emailAddress").value(dto.emailAddress()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("authors/post-create-author",
                        requestFields(
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("address").description("List of author addresses, WARNING! The Adress has to be formmated in upcoming format: 'street and number-City-PLZ'"),
                                fieldWithPath("firstname").description("The first name of the author"),
                                fieldWithPath("lastname").description("The last name of the author"),
                                fieldWithPath("emailAddress").description("The author's email address")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the author"),
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("firstname").description("The author's first name"),
                                fieldWithPath("lastname").description("The author's last name"),
                                fieldWithPath("emailAddress").description("The author's email address"),
                                fieldWithPath("address").description("List of author address")
                        )));

    }

    @Test
    public void should_update_author() throws Exception {
        AuthorCommand command = new AuthorCommand("updatedApiKey", "UpdatedPenName", List.of("456 Other St"), "UpdatedFirst", "UpdatedLast", "updated@example.com");
        AuthorDto dto = new AuthorDto("updatedApiKey", "UpdatedPenName", List.of("456 Other St"), "UpdatedFirst", "UpdatedLast", "updated@example.com");

        // Mock service to update author
        when(authorService.updateAuthor(any())).thenReturn(dto);

        // Perform PUT request and validate response
        mockMvc.perform(put("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"apiKey\": \"updatedApiKey\", "
                                + "\"penname\": \"UpdatedPenName\", "
                                + "\"address\": [\"456 Other St\"], "
                                + "\"firstname\": \"UpdatedFirst\", "
                                + "\"lastname\": \"UpdatedLast\", "
                                + "\"emailAddress\": \"updated@example.com\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.penname").value(dto.penname()))
                .andExpect(jsonPath("$.firstname").value(dto.firstname()))
                .andExpect(jsonPath("$.lastname").value(dto.lastname()))
                .andExpect(jsonPath("$.emailAddress").value(dto.emailAddress()))
                .andDo(document("authors/put-update-author",
                        requestFields(
                                fieldWithPath("apiKey").description("The API key of the author"),
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("address").description("List of author addresses"),
                                fieldWithPath("firstname").description("The first name of the author"),
                                fieldWithPath("lastname").description("The last name of the author"),
                                fieldWithPath("emailAddress").description("The author's email address")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the author"),
                                fieldWithPath("penname").description("The pen name of the author"),
                                fieldWithPath("firstname").description("The author's first name"),
                                fieldWithPath("lastname").description("The author's last name"),
                                fieldWithPath("emailAddress").description("The author's email address"),
                                fieldWithPath("address").description("List of author address")
                        )));

    }


    @Test
    public void should_delete_author() throws Exception {
        Author author = FixturesFactory.author();
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(author);

        // Perform DELETE request
        mockMvc.perform(delete("/api/authors?apiKey=ValidApiKeyToDelete}"))
                .andExpect(status().isNoContent())
                .andDo(document("authors/delete-delete-author",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the author to delete")
                        )
                        ));
    }

    @Test
    public void should_return_exception_for_non_existent_author() throws Exception {
        when(authorService.getAuthor(any())).thenThrow(AuthorService.AuthorServiceException.noAuthorForApiKey("InvalidApiKey"));

        mockMvc.perform(get("/api/authors/author").param("apiKey","ValidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Author Service Error"))
                .andExpect(jsonPath("$.detail").value("Author with api key (InvalidApiKey) not existent"))
                .andDo(document("authors/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the author to delete")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("The url used for the request")
                        )));
    }

    @Test
    public void should_respond_with_correct_exceptions_for_no_param_in_get() throws Exception {

        mockMvc.perform(get("/api/authors/author")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Bad Request, Parameter for get Author needed"))
                .andDo(document("authors/get-with-invalid-params",
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }
}
