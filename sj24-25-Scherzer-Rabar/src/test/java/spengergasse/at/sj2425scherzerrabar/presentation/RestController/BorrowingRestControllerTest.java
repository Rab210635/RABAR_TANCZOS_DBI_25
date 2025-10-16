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
import spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto;
import spengergasse.at.sj2425scherzerrabar.service.BorrowingService;

import java.time.LocalDate;
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
@WebMvcTest(BorrowingRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BorrowingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BorrowingService borrowingService;

    @Test
    public void should_return_borrowing_when_borrowing_exists() throws Exception {
        BorrowingDto dto = new BorrowingDto("borrow123", "customer456", List.of("copy789"),
                LocalDate.of(2025, 3, 1), 7);

        when(borrowingService.getBorrowingByApiKey(any())).thenReturn(dto);

        mockMvc.perform(get("/api/borrowings/borrowing").param("apiKey","validApiKeyToGet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$.customerApiKey").value(dto.customerApiKey()))
                .andExpect(jsonPath("$.copyApiKeys").isArray())
                .andExpect(jsonPath("$.copyApiKeys[0]").value(dto.copyApiKeys().getFirst()))
                .andExpect(jsonPath("$.fromDate").value(dto.fromDate().toString()))
                .andExpect(jsonPath("$.extendedByDays").value(dto.extendedByDays()))
                .andDo(document("borrowings/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the borrowing to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the borrowing"),
                                fieldWithPath("customerApiKey").description("API key of the customer who borrowed the items"),
                                fieldWithPath("copyApiKeys").description("List of API keys for borrowed copies"),
                                fieldWithPath("fromDate").description("Start date of the borrowing"),
                                fieldWithPath("extendedByDays").description("Number of days the borrowing was extended")
                        )));
    }

    @Test
    public void should_return_all_borrowings_by_copy() throws Exception {
        BorrowingDto dto = new BorrowingDto("borrow123", "customer456", List.of("copy789"),
                LocalDate.of(2025, 3, 1), 7);

        when(borrowingService.getBorrowingsByCopy(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/borrowings/copy").param("copy","validCopyApiKeyToGet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].customerApiKey").value(dto.customerApiKey()))
                .andExpect(jsonPath("$[0].copyApiKeys").isArray())
                .andExpect(jsonPath("$[0].copyApiKeys[0]").value(dto.copyApiKeys().getFirst()))
                .andExpect(jsonPath("$[0].fromDate").value(dto.fromDate().toString()))
                .andExpect(jsonPath("$[0].extendedByDays").value(dto.extendedByDays()))
                .andDo(document("borrowings/get-all-by-copy",
                        queryParameters(
                                parameterWithName("copy").description("The API key of the Copy to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the borrowing"),
                                fieldWithPath("[].customerApiKey").description("API key of the customer"),
                                fieldWithPath("[].copyApiKeys").description("List of API keys for borrowed copies"),
                                fieldWithPath("[].fromDate").description("Start date of the borrowing"),
                                fieldWithPath("[].extendedByDays").description("Number of days the borrowing was extended")
                        )));
    }

    @Test
    public void should_return_all_borrowings_by_customer() throws Exception {
        BorrowingDto dto = new BorrowingDto("borrow123", "customer456", List.of("copy789"),
                LocalDate.of(2025, 3, 1), 7);

        when(borrowingService.getBorrowingsByCustomer(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/borrowings/customer").param("customer","validCustomerApiKeyToGet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].customerApiKey").value(dto.customerApiKey()))
                .andExpect(jsonPath("$[0].copyApiKeys").isArray())
                .andExpect(jsonPath("$[0].copyApiKeys[0]").value(dto.copyApiKeys().getFirst()))
                .andExpect(jsonPath("$[0].fromDate").value(dto.fromDate().toString()))
                .andExpect(jsonPath("$[0].extendedByDays").value(dto.extendedByDays()))
                .andDo(document("borrowings/get-all-by-customer",
                        queryParameters(
                                parameterWithName("customer").description("The API key of the Customer to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the borrowing"),
                                fieldWithPath("[].customerApiKey").description("API key of the customer"),
                                fieldWithPath("[].copyApiKeys").description("List of API keys for borrowed copies"),
                                fieldWithPath("[].fromDate").description("Start date of the borrowing"),
                                fieldWithPath("[].extendedByDays").description("Number of days the borrowing was extended")
                        )));
    }




    @Test
    public void should_return_all_borrowings() throws Exception {
        BorrowingDto dto = new BorrowingDto("borrow123", "customer456", List.of("copy789"),
                LocalDate.of(2025, 3, 1), 7);

        when(borrowingService.getAllBorrowings()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/borrowings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(dto.apiKey()))
                .andExpect(jsonPath("$[0].customerApiKey").value(dto.customerApiKey()))
                .andExpect(jsonPath("$[0].copyApiKeys").isArray())
                .andExpect(jsonPath("$[0].copyApiKeys[0]").value(dto.copyApiKeys().getFirst()))
                .andExpect(jsonPath("$[0].fromDate").value(dto.fromDate().toString()))
                .andExpect(jsonPath("$[0].extendedByDays").value(dto.extendedByDays()))
                .andDo(document("borrowings/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the borrowing"),
                                fieldWithPath("[].customerApiKey").description("API key of the customer"),
                                fieldWithPath("[].copyApiKeys").description("List of API keys for borrowed copies"),
                                fieldWithPath("[].fromDate").description("Start date of the borrowing"),
                                fieldWithPath("[].extendedByDays").description("Number of days the borrowing was extended")
                        )));
    }

    @Test
    public void should_create_borrowing() throws Exception {
        BorrowingDto dto = new BorrowingDto("borrow123", "customer456", List.of("copy789"),
                LocalDate.of(2025, 3, 1), 7);

        Link expectedSelfLink = linkTo(methodOn(BorrowingRestController.class).getBorrowing(dto.apiKey())).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();
        when(borrowingService.createBorrowing(any())).thenReturn(dto);

        mockMvc.perform(post("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"customerApiKey\": \"customer456\", "
                                + "\"copyApiKeys\": [\"copy789\"], "
                                + "\"fromDate\": \"2025-03-01\", "
                                + "\"extendedByDays\": 7"
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("borrowings/post-create-borrowing",
                        requestFields(
                                fieldWithPath("customerApiKey").description("API key of the customer borrowing the copies"),
                                fieldWithPath("copyApiKeys").description("List of API keys of borrowed copies"),
                                fieldWithPath("fromDate").description("Start date of the borrowing"),
                                fieldWithPath("extendedByDays").description("Days the borrowing is extended")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("Generated API key for the borrowing"),
                                fieldWithPath("customerApiKey").description("API key of the customer"),
                                fieldWithPath("copyApiKeys").description("List of API keys of borrowed copies"),
                                fieldWithPath("fromDate").description("Start date of the borrowing"),
                                fieldWithPath("extendedByDays").description("Number of days the borrowing was extended")
                        )));
    }


    @Test
    public void should_update_borrowing() throws Exception {
        BorrowingDto dto = new BorrowingDto("borrow123", "customer456", List.of("copy789"),
                LocalDate.of(2025, 3, 1), 7);

        when(borrowingService.updateBorrowing(any())).thenReturn(dto);

        mockMvc.perform(put("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"customerApiKey\": \"customer456\", "
                                + "\"copyApiKeys\": [\"copy789\"], "
                                + "\"fromDate\": \"2025-03-01\", "
                                + "\"extendedByDays\": 7"
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(dto.apiKey()))
                .andDo(document("borrowings/put-update-borrowing",
                        requestFields(
                                fieldWithPath("customerApiKey").description("API key of the customer borrowing the copies"),
                                fieldWithPath("copyApiKeys").description("List of API keys of borrowed copies"),
                                fieldWithPath("fromDate").description("Start date of the borrowing"),
                                fieldWithPath("extendedByDays").description("Days the borrowing is extended")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("Generated API key for the borrowing"),
                                fieldWithPath("customerApiKey").description("API key of the customer"),
                                fieldWithPath("copyApiKeys").description("List of API keys of borrowed copies"),
                                fieldWithPath("fromDate").description("Start date of the borrowing"),
                                fieldWithPath("extendedByDays").description("Number of days the borrowing was extended")
                        )));
    }

    @Test
    public void should_delete_borrowing() throws Exception {
        mockMvc.perform(delete("/api/borrowings?apiKey=ValidToDeleteApiKey"))
                .andExpect(status().isNoContent())
                .andDo(document("borrowings/delete-delete-borrowing",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the borrowing to delete")
                        )));
    }

    @Test
    public void should_respond_with_correct_exceptions() throws Exception {
        when(borrowingService.getBorrowingByApiKey(any())).thenThrow(BorrowingService.BorrowingServiceException.noBorrowingForApiKey("InvalidApiKey"));

        mockMvc.perform(get("/api/borrowings/borrowing").param("apiKey","InvalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Borrowing Service Error"))
                .andExpect(jsonPath("$.detail").value("Borrowing with api key (InvalidApiKey) not existent"))
                .andDo(document("borrowings/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the borrowing to retrieve")
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
