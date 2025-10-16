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
import spengergasse.at.sj2425scherzerrabar.dtos.CopyDto;
import spengergasse.at.sj2425scherzerrabar.service.CopyService;

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
@WebMvcTest(CopyRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CopyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CopyService copyService;

    @Test
    public void should_return_copy_when_copy_exists() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.getCopy(any())).thenReturn(dto);

        mockMvc.perform(get("/api/copies/copy").param("apiKey","ValidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$.bookType").value(dto.bookType()))
                .andExpect(jsonPath("$.pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$.bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$.branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the copy to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the copy"),
                                fieldWithPath("publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("pageCount").description("The number of pages in the copy"),
                                fieldWithPath("bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("branchApiKey").description("The API key of the branch where the copy is located")
                        )));
    }

    @Test
    public void should_return_all_copies() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.getCopies()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/copies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$[0].branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the copy"),
                                fieldWithPath("[].publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("[].bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the copy"),
                                fieldWithPath("[].bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("[].branchApiKey").description("The API key of the branch where the copy is located")
                        )));
    }

    @Test
    public void should_return_all_copies_by_book() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.getCopiesByBook(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/copies/book").param("book","ValidBookApiKey")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$[0].branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/get-all-by-book",
                        queryParameters(
                                parameterWithName("book").description("The API key of the book that contains the copies which should be returned")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the copy"),
                                fieldWithPath("[].publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("[].bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the copy"),
                                fieldWithPath("[].bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("[].branchApiKey").description("The API key of the branch where the copy is located")
                        )));
    }

    @Test
    public void should_return_all_copies_by_branch() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.getCopiesByBranch(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/copies/branch").param("branch","ValidBranchApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$[0].branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/get-all-by-branch",
                        queryParameters(
                                parameterWithName("branch").description("The API key of the branch that contains the copies which should be returned")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the copy"),
                                fieldWithPath("[].publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("[].bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the copy"),
                                fieldWithPath("[].bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("[].branchApiKey").description("The API key of the branch where the copy is located")
                        )));
    }

    @Test
    public void should_return_all_copies_by_publisher() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.getCopiesByPublisher(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/copies/publisher").param("publisher","ValidPublisherApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$[0].branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/get-all-by-publisher",
                        queryParameters(
                                parameterWithName("publisher").description("The API key of the publisher that released the copies which should be returned")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the copy"),
                                fieldWithPath("[].publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("[].bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the copy"),
                                fieldWithPath("[].bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("[].branchApiKey").description("The API key of the branch where the copy is located")
                        )));
    }

    @Test
    public void should_return_all_copies_by_book_type() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.getCopiesByBookType(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/copies/bookType").param("bookType","ValidBookType")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$[0].bookType").value(dto.bookType()))
                .andExpect(jsonPath("$[0].pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$[0].bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$[0].branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/get-all-by-book-type",
                        queryParameters(
                                parameterWithName("bookType").description("The Booktype, that have to be included in the returned copies")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the copy"),
                                fieldWithPath("[].publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("[].bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("[].pageCount").description("The number of pages in the copy"),
                                fieldWithPath("[].bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("[].branchApiKey").description("The API key of the branch where the copy is located")
                        )));
    }

    @Test
    public void should_create_copy() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        Link expectedSelfLink = linkTo(methodOn(CopyRestController.class).getCopy(dto.apiKey())).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        when(copyService.createCopy(any())).thenReturn(dto);

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"publisherApiKey\": \"publisherApiKey\", "
                                + "\"bookType\": \"HARDCOVER\", "
                                + "\"pageCount\": 300, "
                                + "\"bookApiKey\": \"bookApiKey\", "
                                + "\"price\": 15.99, "
                                + "\"branchApiKey\": \"branchApiKey\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$.bookType").value(dto.bookType()))
                .andExpect(jsonPath("$.pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$.bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$.branchApiKey").value(dto.branchApiKey()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("copies/post-create-copy",
                        requestFields(
                                fieldWithPath("publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("pageCount").description("The number of pages in the copy"),
                                fieldWithPath("bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("price").description("Price of the copy"),
                                fieldWithPath("branchApiKey").description("The API key of the branch where the copy is located")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the copy"),
                                fieldWithPath("publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("bookType").description("The type of the book (e.g., Hardcover)"),
                                fieldWithPath("pageCount").description("The number of pages in the copy"),
                                fieldWithPath("bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("branchApiKey").description("The API key of the branch where the copy is located")
                        )));

    }

    @Test
    public void should_update_copy() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        when(copyService.updateCopy(any())).thenReturn(dto);

        mockMvc.perform(put("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"publisherApiKey\": \"publisherApiKey\", "
                                + "\"bookType\": \"PAPERBACK\", "
                                + "\"pageCount\": 350, "
                                + "\"bookApiKey\": \"bookApiKey\", "
                                + "\"price\": 17.99, "
                                + "\"branchApiKey\": \"branchApiKey\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.publisherApiKey").value(dto.publisherApiKey()))
                .andExpect(jsonPath("$.bookType").value(dto.bookType()))
                .andExpect(jsonPath("$.pageCount").value(dto.pageCount()))
                .andExpect(jsonPath("$.bookApiKey").value(dto.bookApiKey()))
                .andExpect(jsonPath("$.branchApiKey").value(dto.branchApiKey()))
                .andDo(document("copies/put-update-copy",
                        requestFields(
                                fieldWithPath("publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("bookType").description("The type of the book (e.g., Paperback)"),
                                fieldWithPath("pageCount").description("The number of pages in the copy"),
                                fieldWithPath("bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("price").description("Price of the copy"),
                                fieldWithPath("branchApiKey").description("The API key of the branch where the copy is located")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the copy"),
                                fieldWithPath("publisherApiKey").description("The API key of the publisher"),
                                fieldWithPath("bookType").description("The type of the book (e.g., Paperback)"),
                                fieldWithPath("pageCount").description("The number of pages in the copy"),
                                fieldWithPath("bookApiKey").description("The API key of the book associated with the copy"),
                                fieldWithPath("branchApiKey").description("The API key of the branch where the copy is located")
                        )));

    }

    @Test
    public void should_delete_copy() throws Exception {
        CopyDto dto = new CopyDto("apiKey", "publisherApiKey", "Hardcover", 300, "bookApiKey", "branchApiKey");

        mockMvc.perform(delete("/api/copies?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("copies/delete-delete-copy",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the copy to delete")
                        )));
    }

    @Test
    public void should_respond_with_correct_exceptions() throws Exception {
        when(copyService.getCopy(any())).thenThrow(CopyService.CopyServiceException.noCopyForApiKey("InvalidApiKey"));

        mockMvc.perform(get("/api/copies/copy").param("apiKey","invalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Copy Service Error"))
                .andExpect(jsonPath("$.detail").value("Copy with api key (InvalidApiKey) not existent"))
                .andDo(document("copies/get-not-found",
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
