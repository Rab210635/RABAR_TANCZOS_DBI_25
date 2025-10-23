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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.util.List;

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

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(BookRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;


    @Test
    public void should_return_book_when_book_exists() throws Exception {
        Author author = FixturesFactory.author();
        Book book = FixturesFactory.book(author);
        BookDto dto = BookDto.bookDtoFromBook(book);

        when(bookService.getBook(any())).thenReturn(dto);

        mockMvc.perform(get("/api/books/book")
                        .param("apiKey","ValidApiKeyForBook")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.releaseDate").value(dto.releaseDate().toString()))
                .andExpect(jsonPath("$.availableOnline").value(dto.availableOnline()))
                .andExpect(jsonPath("$.types").isArray())
                .andExpect(jsonPath("$.types[0]").value(dto.types().getFirst()))
                .andExpect(jsonPath("$.wordCount").value(dto.wordCount()))
                .andExpect(jsonPath("$.description").value(dto.description()))
                .andExpect(jsonPath("$.authorIds").isArray())
                .andExpect(jsonPath("$.authorIds[0]").value(dto.authorIds().getFirst()))
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.genres[0]").value(dto.genres().getFirst()))
                .andDo(document("books/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the book to search for")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the book"),
                                fieldWithPath("name").description("The name of the book"),
                                fieldWithPath("releaseDate").description("The release date of the book"),
                                fieldWithPath("availableOnline").description("Availability status of the book"),
                                fieldWithPath("types").description("List of book types"),
                                fieldWithPath("wordCount").description("Total word count of the book"),
                                fieldWithPath("description").description("Short description of the book"),
                                fieldWithPath("authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("genres").description("List of book genres")
                        )));



    }


    @Test
    public void should_return_books_by_author() throws Exception {
        Author author = FixturesFactory.author();
        Book book = FixturesFactory.book(author);
        BookDto dto = BookDto.bookDtoFromBook(book);

        when(bookService.getBooks(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/books/author").param("author","ValidApiKeyOfAuthor")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].name").value(dto.name()))
                .andExpect(jsonPath("$[0].releaseDate").value(dto.releaseDate().toString()))
                .andExpect(jsonPath("$[0].availableOnline").value(dto.availableOnline()))
                .andExpect(jsonPath("$[0].types").isArray())
                .andExpect(jsonPath("$[0].types[0]").value(dto.types().getFirst()))
                .andExpect(jsonPath("$[0].wordCount").value(dto.wordCount()))
                .andExpect(jsonPath("$[0].description").value(dto.description()))
                .andExpect(jsonPath("$[0].authorIds").isArray())
                .andExpect(jsonPath("$[0].authorIds[0]").value(dto.authorIds().getFirst()))
                .andExpect(jsonPath("$[0].genres").isArray())
                .andExpect(jsonPath("$[0].genres[0]").value(dto.genres().getFirst()))
                .andDo(document("books/get-by-author",
                        queryParameters(
                                parameterWithName("author").description("The API key of the book to search for")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the book"),
                                fieldWithPath("[].name").description("The name of the book"),
                                fieldWithPath("[].releaseDate").description("The release date of the book"),
                                fieldWithPath("[].availableOnline").description("Availability status of the book"),
                                fieldWithPath("[].types").description("List of book types"),
                                fieldWithPath("[].wordCount").description("Total word count of the book"),
                                fieldWithPath("[].description").description("Short description of the book"),
                                fieldWithPath("[].authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("[].genres").description("List of book genres")
                        )));
    }



    @Test
    public void should_return_all_books() throws Exception {
        Author author = FixturesFactory.author();
        Book book = FixturesFactory.book(author);
        BookDto dto = BookDto.bookDtoFromBook(book);
        when(bookService.getBooks(null)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].name").value(dto.name()))
                .andExpect(jsonPath("$[0].releaseDate").value(dto.releaseDate().toString()))
                .andExpect(jsonPath("$[0].availableOnline").value(dto.availableOnline()))
                .andExpect(jsonPath("$[0].types").isArray())
                .andExpect(jsonPath("$[0].types[0]").value(dto.types().getFirst()))
                .andExpect(jsonPath("$[0].wordCount").value(dto.wordCount()))
                .andExpect(jsonPath("$[0].description").value(dto.description()))
                .andExpect(jsonPath("$[0].authorIds").isArray())
                .andExpect(jsonPath("$[0].authorIds[0]").value(dto.authorIds().getFirst()))
                .andExpect(jsonPath("$[0].genres").isArray())
                .andExpect(jsonPath("$[0].genres[0]").value(dto.genres().getFirst()))
                .andDo(document("books/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the book"),
                                fieldWithPath("[].name").description("The name of the book"),
                                fieldWithPath("[].releaseDate").description("The release date of the book"),
                                fieldWithPath("[].availableOnline").description("Availability status of the book"),
                                fieldWithPath("[].types").description("List of book types"),
                                fieldWithPath("[].wordCount").description("Total word count of the book"),
                                fieldWithPath("[].description").description("Short description of the book"),
                                fieldWithPath("[].authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("[].genres").description("List of book genres")
                        )));
    }


    @Test
    public void should_create_book() throws Exception {
        Author author = FixturesFactory.author();
        Book book = FixturesFactory.book(author);
        BookDto dto = BookDto.bookDtoFromBook(book);
        when(bookService.createBook(any())).thenReturn(dto);
        Link expectedSelfLink = linkTo(methodOn(BookRestController.class).getBook(dto.apiKey())).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"name\": \"dasd\", "
                                + "\"releaseDate\": \"2025-03-01\", "
                                + "\"availableOnline\": true, "
                                + "\"types\": [\"Hardcover\", \"eBook\"], "
                                + "\"wordCount\": 50000, "
                                + "\"description\": \"A test book\", "
                                + "\"authorIds\": [\"author1\", \"author2\"], "
                                + "\"genres\": [\"Science Fiction\", \"Fantasy\"]"
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.releaseDate").value(dto.releaseDate().toString()))
                .andExpect(jsonPath("$.availableOnline").value(dto.availableOnline()))
                .andExpect(jsonPath("$.types").isArray())
                .andExpect(jsonPath("$.types[0]").value(dto.types().getFirst()))
                .andExpect(jsonPath("$.wordCount").value(dto.wordCount()))
                .andExpect(jsonPath("$.description").value(dto.description()))
                .andExpect(jsonPath("$.authorIds").isArray())
                .andExpect(jsonPath("$.authorIds[0]").value(dto.authorIds().getFirst()))
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.genres[0]").value(dto.genres().getFirst()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("books/post-create-book",
                        requestFields(
                                fieldWithPath("name").description("The name of the book"),
                                fieldWithPath("releaseDate").description("The release date of the book"),
                                fieldWithPath("availableOnline").description("Availability status of the book"),
                                fieldWithPath("types").description("List of book types (e.g., hardcover, eBook)"),
                                fieldWithPath("wordCount").description("The total word count of the book"),
                                fieldWithPath("description").description("A short description of the book"),
                                fieldWithPath("authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("genres").description("List of genres associated with the book")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the book (automatically generated)"),
                                fieldWithPath("name").description("The name of the book"),
                                fieldWithPath("releaseDate").description("The release date of the book"),
                                fieldWithPath("availableOnline").description("Availability status of the book"),
                                fieldWithPath("types").description("List of book types"),
                                fieldWithPath("wordCount").description("The total word count of the book"),
                                fieldWithPath("description").description("A short description of the book"),
                                fieldWithPath("authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("genres").description("List of genres associated with the book")
                        )));

    }


    @Test
    public void should_update_book() throws Exception {
        Author author = FixturesFactory.author();
        Book book = FixturesFactory.book(author);
        BookDto dto = BookDto.bookDtoFromBook(book);
        when(bookService.updateBook(any())).thenReturn(dto);

        mockMvc.perform(put("/api/books", dto.apiKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"name\": \"dasd2\", "
                                + "\"releaseDate\": \"2025-03-01\", "
                                + "\"availableOnline\": true, "
                                + "\"types\": [\"Hardcover\", \"eBook\"], "
                                + "\"wordCount\": 50000, "
                                + "\"description\": \"A test book\", "
                                + "\"authorIds\": [\"author1\", \"author2\"], "
                                + "\"genres\": [\"Science Fiction\", \"Fantasy\"]"
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.releaseDate").value(dto.releaseDate().toString()))
                .andExpect(jsonPath("$.availableOnline").value(dto.availableOnline()))
                .andExpect(jsonPath("$.types").isArray())
                .andExpect(jsonPath("$.types[0]").value(dto.types().getFirst()))
                .andExpect(jsonPath("$.wordCount").value(dto.wordCount()))
                .andExpect(jsonPath("$.description").value(dto.description()))
                .andExpect(jsonPath("$.authorIds").isArray())
                .andExpect(jsonPath("$.authorIds[0]").value(dto.authorIds().getFirst()))
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.genres[0]").value(dto.genres().getFirst()))
                .andDo(document("books/put-update-book",
                        requestFields(
                                fieldWithPath("name").description("The name of the book"),
                                fieldWithPath("releaseDate").description("The release date of the book"),
                                fieldWithPath("availableOnline").description("Availability status of the book"),
                                fieldWithPath("types").description("List of book types (e.g., hardcover, eBook)"),
                                fieldWithPath("wordCount").description("The total word count of the book"),
                                fieldWithPath("description").description("A short description of the book"),
                                fieldWithPath("authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("genres").description("List of genres associated with the book")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the book (automatically generated)"),
                                fieldWithPath("name").description("The name of the book"),
                                fieldWithPath("releaseDate").description("The release date of the book"),
                                fieldWithPath("availableOnline").description("Availability status of the book"),
                                fieldWithPath("types").description("List of book types"),
                                fieldWithPath("wordCount").description("The total word count of the book"),
                                fieldWithPath("description").description("A short description of the book"),
                                fieldWithPath("authorIds").description("List of author API keys associated with the book"),
                                fieldWithPath("genres").description("List of genres associated with the book")
                        )));
    }


    @Test
    public void should_delete_book() throws Exception {
        Author author = FixturesFactory.author();
        Book book = FixturesFactory.book(author);
        BookDto dto = BookDto.bookDtoFromBook(book);

        mockMvc.perform(delete("/api/books?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("books/delete-delete-book",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the book to delete")
                        )));
    }


    @Test
    public void should_respond_with_correct_exceptions() throws Exception {
        when(bookService.getBook(any())).thenThrow(BookService.BookServiceException.noBookForApiKey("InvalidApiKey"));

        mockMvc.perform(get("/api/books/book").param("apiKey","InvalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book Service Error"))
                .andExpect(jsonPath("$.detail").value("Book with api key (InvalidApiKey) not existent"))
                .andDo(document("books/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the book to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }

}