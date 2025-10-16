package spengergasse.at.sj2425scherzerrabar.presentation.www.authors;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.dtos.AuthorDto;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthorController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void should_show_all_authors() throws Exception {
        AuthorDto author = AuthorDto.authorDtoFromAuthor(FixturesFactory.author());
        when(authorService.getAuthors()).thenReturn(List.of(author));

        mockMvc.perform(get("/www/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/index"))
                .andExpect(model().attributeExists("authors"));
    }

    @Test
    void should_show_add_form() throws Exception {
        mockMvc.perform(get("/www/authors/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/create"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    void should_create_author_and_redirect() throws Exception {
        mockMvc.perform(post("/www/authors/add")
                        .param("penname", "Pen")
                        .param("firstname", "First")
                        .param("lastname", "Last")
                        .param("emailAddress", "email@example.com")
                        .param("streetAndNumber[0]", "123 Main St")
                        .param("city[0]", "City")
                        .param("plz[0]", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/www/authors"));
    }

    @Test
    void should_return_to_create_form_on_validation_error() throws Exception {
        mockMvc.perform(post("/www/authors/add")
                        .param("penname", "")
                        .param("firstname", "")
                        .param("lastname", "")
                        .param("emailAddress", "notanemail")
                        .param("streetAndNumber[0]", "")
                        .param("city[0]", "")
                        .param("plz[0]", "abc"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/create"))
                .andExpect(model().attributeHasFieldErrors(
                        "form", "penname", "firstname", "lastname", "emailAddress",
                        "streetAndNumber[0]", "city[0]", "plz[0]"
                ));
    }

    @Test
    void should_show_edit_form() throws Exception {
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(FixturesFactory.author());
        when(authorService.getAuthor("123")).thenReturn(dto);

        mockMvc.perform(get("/www/authors/edit/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/edit"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("apiKey", "123"));
    }

    @Test
    void should_edit_author_and_redirect() throws Exception {
        mockMvc.perform(post("/www/authors/edit/123")
                        .param("penname", "Pen")
                        .param("firstname", "First")
                        .param("lastname", "Last")
                        .param("emailAddress", "email@example.com")
                        .param("streetAndNumber[0]", "123 Main St")
                        .param("city[0]", "City")
                        .param("plz[0]", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/www/authors"));
    }

    @Test
    void should_return_to_edit_form_on_validation_error() throws Exception {
        mockMvc.perform(post("/www/authors/edit/123")
                        .param("penname", "")
                        .param("firstname", "")
                        .param("lastname", "")
                        .param("emailAddress", "notanemail")
                        .param("streetAndNumber[0]", "")
                        .param("city[0]", "")
                        .param("plz[0]", "abc"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/edit"))
                .andExpect(model().attributeHasFieldErrors(
                        "form", "penname", "firstname", "lastname", "emailAddress",
                        "streetAndNumber[0]", "city[0]", "plz[0]"
                ));
    }

    @Test
    void should_delete_author_and_redirect() throws Exception {
        mockMvc.perform(post("/www/authors/delete")
                        .param("apiKey","123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/www/authors"));
    }


    @Test
    void should_return_to_create_form_when_plz_is_empty() throws Exception {
        mockMvc.perform(post("/www/authors/add")
                        .param("penname", "Pen")
                        .param("firstname", "First")
                        .param("lastname", "Last")
                        .param("emailAddress", "email@example.com")
                        .param("streetAndNumber[0]", "123 Main St")
                        .param("city[0]", "City")
                        .param("plz[0]", "")) // Leere PLZ
                .andExpect(status().isOk())
                .andExpect(view().name("authors/create"))
                .andExpect(model().attributeHasFieldErrors("form", "plz[0]"));
    }

    @Test
    void should_return_to_create_form_when_plz_is_invalid_number_format() throws Exception {
        mockMvc.perform(post("/www/authors/add")
                        .param("penname", "Pen")
                        .param("firstname", "First")
                        .param("lastname", "Last")
                        .param("emailAddress", "email@example.com")
                        .param("streetAndNumber[0]", "123 Main St")
                        .param("city[0]", "City")
                        .param("plz[0]", "12A45")) // Ungültige PLZ
                .andExpect(status().isOk())
                .andExpect(view().name("authors/create"))
                .andExpect(model().attributeHasFieldErrors("form", "plz[0]"));
    }

    @Test
    void should_return_to_create_form_when_street_is_null() throws Exception {
        mockMvc.perform(post("/www/authors/add")
                        .param("penname", "Pen")
                        .param("firstname", "First")
                        .param("lastname", "Last")
                        .param("emailAddress", "email@example.com")
                        .param("city[0]", "City")
                        .param("plz[0]", "12345")
                        .param("streetAndNumber[0]", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/create"))
                .andExpect(model().attributeHasFieldErrors("form", "streetAndNumber[0]"));
    }





    @Test
    void should_return_to_index_with_error_when_deletion_fails() throws Exception {
        // Simuliere Exception beim Löschen
        doThrow(new RuntimeException("Cannot delete")).when(authorService).deleteAuthor("123");

        AuthorDto author = AuthorDto.authorDtoFromAuthor(FixturesFactory.author());
        when(authorService.getAuthors()).thenReturn(List.of(author));

        mockMvc.perform(post("/www/authors/delete").param("apiKey","123"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/index"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void should_return_500_when_show_author_fails() throws Exception {
        when(authorService.getAuthor("123")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/www/authors/show/123"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void should_return_500_when_edit_author_fails() throws Exception {
        when(authorService.getAuthor("123")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/www/authors/edit/123"))
                .andExpect(status().is5xxServerError());
    }


    @Test
    void should_show_author_detail_page() throws Exception {
        AuthorDto dto = AuthorDto.authorDtoFromAuthor(FixturesFactory.author());
        when(authorService.getAuthor("123")).thenReturn(dto);

        mockMvc.perform(get("/www/authors/show/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/show"))
                .andExpect(model().attributeExists("author"));
    }
}
