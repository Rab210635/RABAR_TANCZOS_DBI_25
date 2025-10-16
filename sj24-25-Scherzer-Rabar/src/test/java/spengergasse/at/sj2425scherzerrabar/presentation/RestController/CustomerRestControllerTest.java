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
import spengergasse.at.sj2425scherzerrabar.domain.Customer;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;
import spengergasse.at.sj2425scherzerrabar.service.CustomerService;


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
@WebMvcTest(CustomerRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CustomerRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    public void return_customer_when_existent() throws Exception {
        Customer customer = FixturesFactory.customer();
        CustomerDto customerDto = CustomerDto.customerDtoFromCustomer(customer);

        when(customerService.getCustomer(any())).thenReturn(customerDto);

        mvc.perform(get("/api/customers/customer").param("apiKey","ValidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(customerDto.apiKey()))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses[0]").value(customerDto.addresses().getFirst()))
                .andExpect(jsonPath("$.firstName").value(customerDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(customerDto.lastName()))
                .andExpect(jsonPath("$.emailAddress").value(customerDto.emailAddress()))
                .andDo(document("customers/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the customer to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the customer"),
                                fieldWithPath("addresses").description("List of addresses of the customer"),
                                fieldWithPath("firstName").description("The firstname of the customer"),
                                fieldWithPath("lastName").description("The lastname of the customer"),
                                fieldWithPath("emailAddress").description("The email address of the customer")
                        )));
    }

    @Test
    public void return_customer_by_email_when_existent() throws Exception {
        Customer customer = FixturesFactory.customer();
        CustomerDto customerDto = CustomerDto.customerDtoFromCustomer(customer);

        when(customerService.getCustomerByEMail(any())).thenReturn(customerDto);

        mvc.perform(get("/api/customers/customer").param("email","valid@email.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(customerDto.apiKey()))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses[0]").value(customerDto.addresses().getFirst()))
                .andExpect(jsonPath("$.firstName").value(customerDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(customerDto.lastName()))
                .andExpect(jsonPath("$.emailAddress").value(customerDto.emailAddress()))
                .andDo(document("customers/get-by-email",
                        queryParameters(
                                parameterWithName("email").description("The Email of the customer to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the customer"),
                                fieldWithPath("addresses").description("List of addresses of the customer"),
                                fieldWithPath("firstName").description("The firstname of the customer"),
                                fieldWithPath("lastName").description("The lastname of the customer"),
                                fieldWithPath("emailAddress").description("The email address of the customer")
                        )));
    }

    @Test
    public void return_customers() throws Exception {
        Customer customer = FixturesFactory.customer();
        CustomerDto customerDto = CustomerDto.customerDtoFromCustomer(customer);

        when(customerService.getCustomers()).thenReturn(List.of(customerDto));

        mvc.perform(get("/api/customers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(customerDto.apiKey()))
                .andExpect(jsonPath("$[0].addresses").isArray())
                .andExpect(jsonPath("$[0].addresses[0]").value(customerDto.addresses().getFirst()))
                .andExpect(jsonPath("$[0].firstName").value(customerDto.firstName()))
                .andExpect(jsonPath("$[0].lastName").value(customerDto.lastName()))
                .andExpect(jsonPath("$[0].emailAddress").value(customerDto.emailAddress()))
                .andDo(document("customers/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the customer"),
                                fieldWithPath("[].addresses").description("List of addresses of the customer"),
                                fieldWithPath("[].firstName").description("The firstname of the customer"),
                                fieldWithPath("[].lastName").description("The lastname of the customer"),
                                fieldWithPath("[].emailAddress").description("The email address of the customer")
                        )));
    }

    @Test
    public void create_customer() throws Exception {
        Customer customer = FixturesFactory.customer();
        CustomerDto customerDto = CustomerDto.customerDtoFromCustomer(customer);

        Link expectedSelfLink = linkTo(methodOn(CustomerRestController.class).getCustomer(Optional.of(customerDto.apiKey()),null)).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        when(customerService.createCustomer(any())).thenReturn(customerDto);

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"addresses\": [\"Reumanplatz 66-Vienna-11001\",\"Reumanplatz 66-Vienna-11001\"],"
                                + "\"firstname\": \"Hey\","
                                + "\"lastname\": \"Ho\","
                                + "\"emailAddress\": \"ho@ho.com\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(customerDto.apiKey()))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses[0]").value(customerDto.addresses().getFirst()))
                .andExpect(jsonPath("$.firstName").value(customerDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(customerDto.lastName()))
                .andExpect(jsonPath("$.emailAddress").value(customerDto.emailAddress()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("customers/post-create-customer",
                        requestFields(
                                fieldWithPath("addresses").description("List of addresses of the customer"),
                                fieldWithPath("firstname").description("The firstname of the customer"),
                                fieldWithPath("lastname").description("The lastname of the customer"),
                                fieldWithPath("emailAddress").description("The email address of the customer")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the customer"),
                                fieldWithPath("addresses").description("List of addresses of the customer"),
                                fieldWithPath("firstName").description("The firstname of the customer"),
                                fieldWithPath("lastName").description("The lastname of the customer"),
                                fieldWithPath("emailAddress").description("The email address of the customer")
                        )));
    }

    @Test
    public void update_publisher() throws Exception {
        Customer customer = FixturesFactory.customer();
        CustomerDto customerDto = CustomerDto.customerDtoFromCustomer(customer);
        when(customerService.updateCustomer(any())).thenReturn(customerDto);

        mvc.perform(put("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"apiKey\": \"Hey\","
                                + "\"addresses\": [\"Reumanplatz 66-Vienna-11001\",\"Reumanplatz 66-Vienna-11001\"],"
                                + "\"firstname\": \"Hey\","
                                + "\"lastname\": \"Ho\","
                                + "\"emailAddress\": \"ho@ho.com\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(customerDto.apiKey()))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses[0]").value(customerDto.addresses().getFirst()))
                .andExpect(jsonPath("$.firstName").value(customerDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(customerDto.lastName()))
                .andExpect(jsonPath("$.emailAddress").value(customerDto.emailAddress()))
                .andDo(document("customers/put-update-customer",
                        requestFields(
                                fieldWithPath("apiKey").description("The API key of the customer to update"),
                                fieldWithPath("addresses").description("List of addresses of the customer"),
                                fieldWithPath("firstname").description("The firstname of the customer"),
                                fieldWithPath("lastname").description("The lastname of the customer"),
                                fieldWithPath("emailAddress").description("The email address of the customer")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the customer"),
                                fieldWithPath("addresses").description("List of addresses of the customer"),
                                fieldWithPath("firstName").description("The firstname of the customer"),
                                fieldWithPath("lastName").description("The lastname of the customer"),
                                fieldWithPath("emailAddress").description("The email address of the customer")
                        )));
    }

    @Test
    public void delete_publisher() throws Exception {
        Customer customer = FixturesFactory.customer();
        CustomerDto customerDto = CustomerDto.customerDtoFromCustomer(customer);

        mvc.perform(delete("/api/customers?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("customers/delete-delete-customer",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the customer to delete")
                        )));
    }

    @Test
    public void respond_with_correct_exceptions() throws Exception {
        when(customerService.getCustomer(any())).thenThrow(CustomerService.CustomerServiceException.noCustomerForApiKey("non-existent-api-key"));

        mvc.perform(get("/api/customers/customer").param("apiKey","invalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Customer Service Error"))
                .andExpect(jsonPath("$.detail").value("Customer with api key (non-existent-api-key) not existent"))
                .andDo(document("customers/get-not-found",
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

    @Test
    public void should_respond_with_correct_exceptions_for_no_param_in_get() throws Exception {

        mvc.perform(get("/api/customers/customer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Bad Request, Parameter for get Customer needed"))
                .andDo(document("customers/get-with-invalid-params",
                        responseFields(
                                fieldWithPath("title").description("Error title"),
                                fieldWithPath("status").description("HTTP status code"),
                                fieldWithPath("detail").description("Error detail"),
                                fieldWithPath("type").description("THE TYPE"),
                                fieldWithPath("instance").description("the url used for the request")
                        )));
    }
}