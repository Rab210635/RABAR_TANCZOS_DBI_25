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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.domain.Library;
import spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto;
import spengergasse.at.sj2425scherzerrabar.service.LibraryService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(LibraryRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibraryRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private LibraryService libraryService;

    @Test
    public void return_library_when_existent() throws Exception {
        Library library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        LibraryDto libraryDto = LibraryDto.libraryDtoFromLibrary(library);

        when(libraryService.getLibrary(any())).thenReturn(libraryDto);

        mvc.perform(get("/api/libraries/library").param("apiKey","ValidApiKey")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(libraryDto.apiKey()))
                .andExpect(jsonPath("$.name").value(libraryDto.name()))
                .andExpect(jsonPath("$.headquarters").value(libraryDto.headquarters()))
                .andExpect(jsonPath("$.booksInLibraries").isArray())
                .andExpect(jsonPath("$.booksInLibraries[0].bookApiKey").value(libraryDto.booksInLibraries().getFirst().bookApiKey()))
                .andExpect(jsonPath("$.booksInLibraries[0].borrowLengthDays").value(libraryDto.booksInLibraries().getFirst().borrowLengthDays()))
                .andDo(document("libraries/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the library to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the library"),
                                fieldWithPath("name").description("The name of the library"),
                                fieldWithPath("headquarters").description("The headquarters location of the library"),
                                fieldWithPath("booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        )));
    }

    @Test
    public void return_library_by_name_when_existent() throws Exception {
        Library library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        LibraryDto libraryDto = LibraryDto.libraryDtoFromLibrary(library);

        when(libraryService.getLibraryByName(any())).thenReturn(libraryDto);

        mvc.perform(get("/api/libraries/library").param("name","ValidLibaryName")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(libraryDto.apiKey()))
                .andExpect(jsonPath("$.name").value(libraryDto.name()))
                .andExpect(jsonPath("$.headquarters").value(libraryDto.headquarters()))
                .andExpect(jsonPath("$.booksInLibraries").isArray())
                .andExpect(jsonPath("$.booksInLibraries[0].bookApiKey").value(libraryDto.booksInLibraries().getFirst().bookApiKey()))
                .andExpect(jsonPath("$.booksInLibraries[0].borrowLengthDays").value(libraryDto.booksInLibraries().getFirst().borrowLengthDays()))
                .andDo(document("libraries/get-by-name",
                        queryParameters(
                                parameterWithName("name").description("The Name of the library to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the library"),
                                fieldWithPath("name").description("The name of the library"),
                                fieldWithPath("headquarters").description("The headquarters location of the library"),
                                fieldWithPath("booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        )));
    }

    @Test
    public void return_libraries() throws Exception {
        Library library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        LibraryDto libraryDto = LibraryDto.libraryDtoFromLibrary(library);

        when(libraryService.getLibraries()).thenReturn(List.of(libraryDto));

        mvc.perform(get("/api/libraries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(libraryDto.apiKey()))
                .andExpect(jsonPath("$[0].name").value(libraryDto.name()))
                .andExpect(jsonPath("$[0].headquarters").value(libraryDto.headquarters()))
                .andExpect(jsonPath("$[0].booksInLibraries").isArray())
                .andExpect(jsonPath("$[0].booksInLibraries[0].bookApiKey").value(libraryDto.booksInLibraries().getFirst().bookApiKey()))
                .andExpect(jsonPath("$[0].booksInLibraries[0].borrowLengthDays").value(libraryDto.booksInLibraries().getFirst().borrowLengthDays()))
                .andDo(document("libraries/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the library"),
                                fieldWithPath("[].name").description("The name of the library"),
                                fieldWithPath("[].headquarters").description("The headquarters location of the library"),
                                fieldWithPath("[].booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("[].booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("[].booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        )));
    }

    @Test
    public void should_create_libraries() throws Exception {
        Library library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        LibraryDto libraryDto = LibraryDto.libraryDtoFromLibrary(library);

        Link expectedSelfLink = linkTo(methodOn(LibraryRestController.class).getLibrary(Optional.of(libraryDto.apiKey()),null)).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        when(libraryService.createLibrary(any())).thenReturn(libraryDto);

        mvc.perform(post("/api/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"name\": \"apiKey\", "
                                + "\"headquarters\": \"Reumanplatz-wine-122 \", "
                                + "\"booksInLibraries\": ["
                                + "{\"bookApiKey\": \"Book1\", \"borrowLengthDays\": 14},"
                                + "{\"bookApiKey\": \"Book2\", \"borrowLengthDays\": 30}"
                                + "]"
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(libraryDto.apiKey()))
                .andExpect(jsonPath("$.name").value(libraryDto.name()))
                .andExpect(jsonPath("$.headquarters").value(libraryDto.headquarters()))
                .andExpect(jsonPath("$.booksInLibraries").isArray())
                .andExpect(jsonPath("$.booksInLibraries[0].bookApiKey").value(libraryDto.booksInLibraries().getFirst().bookApiKey()))
                .andExpect(jsonPath("$.booksInLibraries[0].borrowLengthDays").value(libraryDto.booksInLibraries().getFirst().borrowLengthDays()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("libraries/post-create-library",
                        requestFields(
                                fieldWithPath("name").description("The name of the library"),
                                fieldWithPath("headquarters").description("The headquarters location of the library"),
                                fieldWithPath("booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the library"),
                                fieldWithPath("name").description("The name of the library"),
                                fieldWithPath("headquarters").description("The headquarters location of the library"),
                                fieldWithPath("booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        )));

    }


    @Test
    public void update_order() throws Exception {
        Library library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        LibraryDto libraryDto = LibraryDto.libraryDtoFromLibrary(library);

        when(libraryService.updateLibrary(any())).thenReturn(libraryDto);

        mvc.perform(put("/api/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"name\": \"apiKey\", "
                                + "\"headquarters\": \"Reumanplatz-wine-122 \", "
                                + "\"booksInLibraries\": ["
                                + "{\"bookApiKey\": \"Book1\", \"borrowLengthDays\": 14},"
                                + "{\"bookApiKey\": \"Book2\", \"borrowLengthDays\": 30}"
                                + "]"
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(libraryDto.apiKey()))
                .andExpect(jsonPath("$.name").value(libraryDto.name()))
                .andExpect(jsonPath("$.headquarters").value(libraryDto.headquarters()))
                .andExpect(jsonPath("$.booksInLibraries").isArray())
                .andExpect(jsonPath("$.booksInLibraries[0].bookApiKey").value(libraryDto.booksInLibraries().getFirst().bookApiKey()))
                .andExpect(jsonPath("$.booksInLibraries[0].borrowLengthDays").value(libraryDto.booksInLibraries().getFirst().borrowLengthDays()))
                .andDo(document("libraries/put-update-library",
                        requestFields(
                                fieldWithPath("name").description("The name of the library"),
                                fieldWithPath("headquarters").description("The headquarters location of the library"),
                                fieldWithPath("booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the library"),
                                fieldWithPath("name").description("The name of the library"),
                                fieldWithPath("headquarters").description("The headquarters location of the library"),
                                fieldWithPath("booksInLibraries").description("List of books available in the library"),
                                fieldWithPath("booksInLibraries[].bookApiKey").description("The API key of the book in the library"),
                                fieldWithPath("booksInLibraries[].borrowLengthDays").description("The days borrowed of the book in the library")
                        )));
    }


    @Test
    public void should_delete_book() throws Exception {
        Library library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        LibraryDto libraryDto = LibraryDto.libraryDtoFromLibrary(library);

        mvc.perform(delete("/api/libraries?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("libraries/delete-delete-library",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the order to library")
                        )));
    }


    @Test
    public void should_respond_with_correct_exceptions() throws Exception {

        when(libraryService.getLibrary(any())).thenThrow(LibraryService.LibraryServiceException.noLibraryForApiKey("non-existent-api-key"));

        mvc.perform(get("/api/libraries/library").param("apiKey", "InvalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Library Service Error"))
                .andExpect(jsonPath("$.detail").value("Library with api key (non-existent-api-key) not existent"))
                .andDo(document("libraries/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the library to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }


    @Test
    public void should_respond_with_correct_exceptions_for_no_param_in_get() throws Exception {

        mvc.perform(get("/api/libraries/library")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Bad Request, Parameter for get Library needed"))
                .andDo(document("libraries/get-with-invalid-params",
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }
}