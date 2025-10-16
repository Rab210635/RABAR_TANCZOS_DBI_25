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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.dtos.BuyableBookDto;
import spengergasse.at.sj2425scherzerrabar.service.BuyableBookService;

import java.util.List;

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
@WebMvcTest(BuyableBookRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BuyableBookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuyableBookService buyableBookService;

    @Test
    public void should_return_buyable_book_when_buyable_book_exists() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.getBuyableBookByApiKey(any())).thenReturn(dto);


        mockMvc.perform(get("/api/buyableBooks/buyableBook").param("apiKey","ValidApiKeyForBook")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$.price").value(dto.price()))
                .andExpect(jsonPath("$.publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$.bookType").value(dto.bookType()))
                .andExpect(jsonPath("$.pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$.bookApiKey").value(dto.bookApiKey()))

                .andDo(document("buyable_books/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the BuyableBook to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("price").description("The Price of the book"),
                                fieldWithPath("publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("pageCount").description("The number of pages in the book"),
                                fieldWithPath("bookApiKey").description("API key of the associated library Book")
                        )));
    }

    @Test
    public void should_return_all_buyable_books() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.getAllBuyableBooks()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/buyableBooks", dto.buyableBookApiKey())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$[0].price").value(dto.price()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))

                .andDo(document("buyable_books/get-all",

                        responseFields(
                                fieldWithPath("[].buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("[].price").description("The Price of the book"),
                                fieldWithPath("[].publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("[].bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the book"),
                                fieldWithPath("[].bookApiKey").description("API key of the associated library Book")
                        )));
    }

    @Test
    public void should_return_all_buyable_books_by_publisher() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.getAllBuyableBooksByPublisher(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/buyableBooks/publisher").param("publisher","ValidPublisherApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$[0].price").value(dto.price()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))

                .andDo(document("buyable_books/get-all-by-book",
                        queryParameters(
                                parameterWithName("publisher").description("The API key of the Publisher from the buyable book to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("[].buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("[].price").description("The Price of the book"),
                                fieldWithPath("[].publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("[].bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the book"),
                                fieldWithPath("[].bookApiKey").description("API key of the associated library Book")
                        )));
    }

    @Test
    public void should_return_all_buyable_books_by_book_type() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.getAllBuyableBooksByBookType(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/buyableBooks/booktype").param("bookType","ValidBookType")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$[0].price").value(dto.price()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))

                .andDo(document("buyable_books/get-all-by-book-type",
                        queryParameters(
                                parameterWithName("bookType").description("The book type of the BuyableBook to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("[].buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("[].price").description("The Price of the book"),
                                fieldWithPath("[].publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("[].bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the book"),
                                fieldWithPath("[].bookApiKey").description("API key of the associated library Book")
                        )));
    }

    @Test
    public void should_return_all_buyable_books_by_price() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.getAllBuyableBooksByPrice(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/buyableBooks/price").param("price", String.valueOf(dto.price()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$[0].price").value(dto.price()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))

                .andDo(document("buyable_books/get-all-by-price",
                        queryParameters(
                                parameterWithName("price").description("The Price that the buyablebook has to be below of")
                        ),
                        responseFields(
                                fieldWithPath("[].buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("[].price").description("The Price of the book"),
                                fieldWithPath("[].publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("[].bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the book"),
                                fieldWithPath("[].bookApiKey").description("API key of the associated library Book")
                        )));
    }


    @Test
    public void should_return_all_buyable_books_by_book() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.getAllBuyableBooksByBook(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/buyableBooks/book").param("book","ValidBookApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$[0].price").value(dto.price()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))

                .andDo(document("buyable_books/get-all-by-publisher",
                        queryParameters(
                                parameterWithName("book").description("The API key of the Book to retrieve Buyablebooks")
                        ),
                        responseFields(
                                fieldWithPath("[].buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("[].price").description("The Price of the book"),
                                fieldWithPath("[].publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("[].bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the book"),
                                fieldWithPath("[].bookApiKey").description("API key of the associated library Book")
                        )));
    }


    @Test
    public void should_create_buyable_book() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        Link expectedSelfLink = linkTo(methodOn(BuyableBookRestController.class).getBuyableBook(dto.buyableBookApiKey())).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        when(buyableBookService.createBuyableBook(any())).thenReturn(dto);

        mockMvc.perform(post("/api/buyableBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"price\": 50, "
                                + "\"publisherApiKey\": \"validPublisherApiKey\", "
                                + "\"bookType\": \"Hardcover\", "
                                + "\"pageCount\": 50000, "
                                + "\"bookApiKey\": \"BookApiKey\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$.price").value(dto.price()))
                .andExpect(jsonPath("$.publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$.bookType").value(dto.bookType()))
                .andExpect(jsonPath("$.pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$.bookApiKey").value(dto.bookApiKey()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("buyable_books/post-create-buyableBook",
                        requestFields(
                                fieldWithPath("price").description("The Price of the book"),
                                fieldWithPath("publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("pageCount").description("The number of pages in the book"),
                                fieldWithPath("bookApiKey").description("API key of the associated library Book")
                        ),
                        responseFields(
                                fieldWithPath("buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("price").description("The Price of the book"),
                                fieldWithPath("publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("pageCount").description("The number of pages in the book"),
                                fieldWithPath("bookApiKey").description("API key of the associated library Book")
                        )));

    }

    @Test
    public void should_update_buyable_book() throws Exception {
        BuyableBookDto dto = BuyableBookDto.buyableBookDtoFromBuyableBook(FixturesFactory.buyableBook());

        when(buyableBookService.updateBuyableBook(any())).thenReturn(dto);

        mockMvc.perform(put("/api/buyableBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"price\": 50, "
                                + "\"publisherApiKey\": \"validPublisherApiKey\", "
                                + "\"bookType\": \"Hardcover\", "
                                + "\"pageCount\": 50000, "
                                + "\"bookApiKey\": \"BookApiKey\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.buyableBookApiKey").value(dto.buyableBookApiKey()))
                .andExpect(jsonPath("$.price").value(dto.price()))
                .andExpect(jsonPath("$.publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$.bookType").value(dto.bookType()))
                .andExpect(jsonPath("$.pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$.bookApiKey").value(dto.bookApiKey()))
                .andDo(document("buyable_books/put-update-buyableBook",
                        requestFields(
                                fieldWithPath("price").description("The Price of the book"),
                                fieldWithPath("publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("pageCount").description("The number of pages in the book"),
                                fieldWithPath("bookApiKey").description("API key of the associated library Book")
                        ),
                        responseFields(
                                fieldWithPath("buyableBookApiKey").description("The API key of the BuyableBook "),
                                fieldWithPath("price").description("The Price of the book"),
                                fieldWithPath("publisherApiKey").description("API key of the associated publisher"),
                                fieldWithPath("bookType").description("The Type of the book (example: Ebook)"),
                                fieldWithPath("pageCount").description("The number of pages in the book"),
                                fieldWithPath("bookApiKey").description("API key of the associated library Book")
                        )));

    }

    @Test
    public void should_delete_buyable_book() throws Exception {
        mockMvc.perform(delete("/api/buyableBooks?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("buyable_books/delete-delete-buyableBook",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the buyable book to delete")
                        )
                ));
    }


    @Test
    public void should_respond_with_correct_exceptions() throws Exception {
        when(buyableBookService.getBuyableBookByApiKey(any())).thenThrow(BuyableBookService.BuyableBookServiceException.noBuyableBookForApiKey("InvalidApiKey"));

        mockMvc.perform(get("/api/buyableBooks/buyableBook").param("apiKey","InvaidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("BuyableBook Service Error"))
                .andExpect(jsonPath("$.detail").value("Buyable Book with api key (InvalidApiKey) not existent"))
                .andDo(document("buyable_books/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the buyableBook to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("Error type"),
                                fieldWithPath("instance").description("The request URL")
                        )));
    }






}