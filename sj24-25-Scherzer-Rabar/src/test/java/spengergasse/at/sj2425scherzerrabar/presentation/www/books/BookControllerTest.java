package spengergasse.at.sj2425scherzerrabar.presentation.www.books;

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
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;
import spengergasse.at.sj2425scherzerrabar.service.AuthorService;
import spengergasse.at.sj2425scherzerrabar.service.BookService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void should_show_all_books() throws Exception {
        BookDto book = BookDto.bookDtoFromBook(FixturesFactory.book(FixturesFactory.author()));
        when(bookService.getBooks(null)).thenReturn(List.of(book));

        mockMvc.perform(get("/www/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/index"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void should_show_add_form() throws Exception {
        mockMvc.perform(get("/www/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/create"))
                .andExpect(model().attributeExists("newBook"));
    }

    @Test
    void should_create_book_and_redirect() throws Exception {
        mockMvc.perform(post("/www/books/add")
                        .param("name", "Asylum")
                        .param("releaseDate", "2020-02-02")
                        .param("availableOnline", "true")
                        .param("bookTypes", "EBOOK","PAPERBACK")
                        .param("wordCount", "1500")
                        .param("genres", "HORROR", "THRILLER")
                        .param("description", "A thrilling asylum story")
                        .param("authors", "AuthorPenname1", "AuthorPenname2")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/www/books"));
    }

    @Test
    void should_return_to_create_form_on_validation_error() throws Exception {
        mockMvc.perform(post("/www/books/add")
                        .param("name", "") // invalid (empty)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("books/create"));
    }

    @Test
    void should_show_edit_form() throws Exception {
        BookDto2 dto = BookDto2.bookDtoFromBook(FixturesFactory.book(FixturesFactory.author()));
        when(bookService.getBook2("123")).thenReturn(dto);


        mockMvc.perform(get("/www/books/edit")
                .param("apiKey", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("apiKey", "123"));
    }

    @Test
    void should_edit_book_and_redirect() throws Exception {
        mockMvc.perform(post("/www/books/edit")
                .param("apiKey", "123")
                .param("name", "Asylum")
                .param("releaseDate", "2020-02-02")
                .param("availableOnline", "true")
                .param("bookTypes", "EBOOK","PAPERBACK")
                .param("wordCount", "1500")
                .param("genres", "HORROR", "THRILLER")
                .param("description", "A thrilling asylum story")
                .param("authors", "AuthorPenname1", "AuthorPenname2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/www/books"));
    }

    @Test
    void should_return_to_edit_form_on_validation_error() throws Exception {
        mockMvc.perform(post("/www/books/edit")
                .param("apiKey", "123")
                        .param("penname", "") // invalid
                )
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"));
    }

    @Test
    void should_delete_book_and_redirect() throws Exception {
        mockMvc.perform(post("/www/books/delete")
                .param("apiKey", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/www/books"));
    }

    @Test
    void should_show_book_detail_page() throws Exception {
        BookDto2 dto = BookDto2.bookDtoFromBook(FixturesFactory.book(FixturesFactory.author()));
        when(bookService.getBook2("123")).thenReturn(dto);

        mockMvc.perform(get("/www/books/show")
                .param("apiKey", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/show"))
                .andExpect(model().attributeExists("book"));
    }
}