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

import spengergasse.at.sj2425scherzerrabar.domain.Order;
import spengergasse.at.sj2425scherzerrabar.dtos.OrderDto;
import spengergasse.at.sj2425scherzerrabar.service.OrderService;

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
@WebMvcTest(OrderRestController.class)
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    public void return_order_when_existent() throws Exception {
       Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        when(orderService.getOrderByApiKey(any())).thenReturn(orderDto);

        mvc.perform(get("/api/orders/order").param("apiKey","ValidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(orderDto.apiKey()))
                .andExpect(jsonPath("$.customerApiKey").value(orderDto.customerApiKey()))
                .andExpect(jsonPath("$.subscriptionsApiKeys").isArray())
                .andExpect(jsonPath("$.subscriptionsApiKeys[0]").value(orderDto.subscriptionsApiKeys().getFirst()))
                .andExpect(jsonPath("$.date").value(orderDto.date().toString()))
                .andExpect(jsonPath("$.booksApiKeys").isArray())
                .andExpect(jsonPath("$.booksApiKeys[0]").value(orderDto.booksApiKeys().getFirst()))
                .andDo(document("orders/get-by-api-key",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the order to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the order"),
                                fieldWithPath("customerApiKey").description("The customer of the order"),
                                fieldWithPath("subscriptionsApiKeys").description("List of subscription API keys associated with the order"),
                                fieldWithPath("date").description("The date of the order"),
                                fieldWithPath("booksApiKeys").description("List of book API keys associated with the order")
                                )));
    }

    @Test
    public void return_orders_by_date() throws Exception {
        Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        when(orderService.getAllOrdersByDate(any())).thenReturn(List.of(orderDto));

        mvc.perform(get("/api/orders/date").param("date",orderDto.date().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(orderDto.apiKey()))
                .andExpect(jsonPath("$[0].customerApiKey").value(orderDto.customerApiKey()))
                .andExpect(jsonPath("$[0].subscriptionsApiKeys").isArray())
                .andExpect(jsonPath("$[0].subscriptionsApiKeys[0]").value(orderDto.subscriptionsApiKeys().getFirst()))
                .andExpect(jsonPath("$[0].date").value(orderDto.date().toString()))
                .andExpect(jsonPath("$[0].booksApiKeys").isArray())
                .andExpect(jsonPath("$[0].booksApiKeys[0]").value(orderDto.booksApiKeys().getFirst()))
                .andDo(document("orders/get-all-by-date",
                        queryParameters(
                                parameterWithName("date").description("The date of the order to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the order"),
                                fieldWithPath("[].customerApiKey").description("The customer of the order"),
                                fieldWithPath("[].subscriptionsApiKeys").description("List of subscription API keys associated with the order"),
                                fieldWithPath("[].date").description("The date of the order"),
                                fieldWithPath("[].booksApiKeys").description("List of book API keys associated with the order")
                        )));
    }

    @Test
    public void return_orders_by_customer() throws Exception {
        Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        when(orderService.getAllOrdersByCustomer(any())).thenReturn(List.of(orderDto));

        mvc.perform(get("/api/orders/customer").param("customer","ValidCustomerApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(orderDto.apiKey()))
                .andExpect(jsonPath("$[0].customerApiKey").value(orderDto.customerApiKey()))
                .andExpect(jsonPath("$[0].subscriptionsApiKeys").isArray())
                .andExpect(jsonPath("$[0].subscriptionsApiKeys[0]").value(orderDto.subscriptionsApiKeys().getFirst()))
                .andExpect(jsonPath("$[0].date").value(orderDto.date().toString()))
                .andExpect(jsonPath("$[0].booksApiKeys").isArray())
                .andExpect(jsonPath("$[0].booksApiKeys[0]").value(orderDto.booksApiKeys().getFirst()))
                .andDo(document("orders/get-all-by-customer",
                        queryParameters(
                                parameterWithName("customer").description("The customer Api Key of the order to retrieve")
                        ),
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the order"),
                                fieldWithPath("[].customerApiKey").description("The customer of the order"),
                                fieldWithPath("[].subscriptionsApiKeys").description("List of subscription API keys associated with the order"),
                                fieldWithPath("[].date").description("The date of the order"),
                                fieldWithPath("[].booksApiKeys").description("List of book API keys associated with the order")
                        )));
    }

    @Test
    public void return_orders() throws Exception {
        Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        when(orderService.getAllOrders()).thenReturn(List.of(orderDto));

        mvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].apiKey").value(orderDto.apiKey()))
                .andExpect(jsonPath("$[0].customerApiKey").value(orderDto.customerApiKey()))
                .andExpect(jsonPath("$[0].subscriptionsApiKeys").isArray())
                .andExpect(jsonPath("$[0].subscriptionsApiKeys[0]").value(orderDto.subscriptionsApiKeys().getFirst()))
                .andExpect(jsonPath("$[0].date").value(orderDto.date().toString()))
                .andExpect(jsonPath("$[0].booksApiKeys").isArray())
                .andExpect(jsonPath("$[0].booksApiKeys[0]").value(orderDto.booksApiKeys().getFirst()))
                .andDo(document("orders/get-all",
                        responseFields(
                                fieldWithPath("[].apiKey").description("The API key of the order"),
                                fieldWithPath("[].customerApiKey").description("The customer of the order"),
                                fieldWithPath("[].subscriptionsApiKeys").description("List of subscription API keys associated with the order"),
                                fieldWithPath("[].date").description("The date of the order"),
                                fieldWithPath("[].booksApiKeys").description("List of book API keys associated with the order")
                        )));
    }

    @Test
    public void should_create_orders() throws Exception {
        Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        Link expectedSelfLink = linkTo(methodOn(OrderRestController.class).getOrder(orderDto.apiKey())).withSelfRel();
        String expectedLocation = expectedSelfLink.toUri().toString();

        when(orderService.createOrder(any())).thenReturn(orderDto);

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"customerApiKey\": \"apiKey\", "
                                + "\"subscriptionsApiKeys\": [\"Hardcover\", \"eBook\"], "
                                + "\"date\": \"2025-02-05\", "
                                + "\"booksApiKeys\": [\"Hardcover\", \"eBook\"] "
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiKey").value(orderDto.apiKey()))
                .andExpect(jsonPath("$.customerApiKey").value(orderDto.customerApiKey()))
                .andExpect(jsonPath("$.subscriptionsApiKeys").isArray())
                .andExpect(jsonPath("$.subscriptionsApiKeys[0]").value(orderDto.subscriptionsApiKeys().getFirst()))
                .andExpect(jsonPath("$.date").value(orderDto.date().toString()))
                .andExpect(jsonPath("$.booksApiKeys").isArray())
                .andExpect(jsonPath("$.booksApiKeys[0]").value(orderDto.booksApiKeys().getFirst()))
                .andExpect(header().string("Location", containsString(expectedLocation)))
                .andDo(document("orders/post-create-order",
                        requestFields(
                                fieldWithPath("customerApiKey").description("The customer Api Key of the order"),
                                fieldWithPath("subscriptionsApiKeys").description("List of subscriptions Api Keys of the order"),
                                fieldWithPath("date").description("The date of the order"),
                                fieldWithPath("booksApiKeys").description("List of book Api Keys of the order")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the order (automatically generated)"),
                                fieldWithPath("customerApiKey").description("The customer Api Key of the order"),
                                fieldWithPath("subscriptionsApiKeys").description("List of subscriptions Api Keys of the order"),
                                fieldWithPath("date").description("The date of the order"),
                                fieldWithPath("booksApiKeys").description("List of book Api Keys of the order")
                        )));

    }


    @Test
    public void update_order() throws Exception {
        Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        when(orderService.updateOrder(any())).thenReturn(orderDto);

       mvc.perform(put("/api/orders", orderDto.apiKey())
                        .contentType(MediaType.APPLICATION_JSON)
                       .content("{"
                               + "\"customerApiKey\": \"apiKey\", "
                               + "\"subscriptionsApiKeys\": [\"Hardcover\", \"eBook\"], "
                               + "\"date\": \"2025-02-05\", "
                               + "\"booksApiKeys\": [\"Hardcover\", \"eBook\"] "
                               + "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.apiKey").value(orderDto.apiKey()))
               .andExpect(jsonPath("$.customerApiKey").value(orderDto.customerApiKey()))
               .andExpect(jsonPath("$.subscriptionsApiKeys").isArray())
               .andExpect(jsonPath("$.subscriptionsApiKeys[0]").value(orderDto.subscriptionsApiKeys().getFirst()))
               .andExpect(jsonPath("$.date").value(orderDto.date().toString()))
               .andExpect(jsonPath("$.booksApiKeys").isArray())
               .andExpect(jsonPath("$.booksApiKeys[0]").value(orderDto.booksApiKeys().getFirst()))
                .andDo(document("orders/put-update-order",
                        requestFields(
                                fieldWithPath("customerApiKey").description("The customer Api Key of the order"),
                                fieldWithPath("subscriptionsApiKeys").description("List of subscriptions Api Keys of the order"),
                                fieldWithPath("date").description("The date of the order"),
                                fieldWithPath("booksApiKeys").description("List of book Api Keys of the order")
                        ),
                        responseFields(
                                fieldWithPath("apiKey").description("The API key of the order (automatically generated)"),
                                fieldWithPath("customerApiKey").description("The customer Api Key of the order"),
                                fieldWithPath("subscriptionsApiKeys").description("List of subscriptions Api Keys of the order"),
                                fieldWithPath("date").description("The date of the order"),
                                fieldWithPath("booksApiKeys").description("List of book Api Keys of the order")
                        )));
    }


    @Test
    public void should_delete_book() throws Exception {
        Order order = FixturesFactory.order();
        OrderDto orderDto = OrderDto.orderDtoFromOrder(order);

        mvc.perform(delete("/api/orders?apiKey=ValidApiKeyToDelete"))
                .andExpect(status().isNoContent())
                .andDo(document("orders/delete-delete-order",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the order to delete")
                        )));
    }


    @Test
    public void should_respond_with_correct_exceptions() throws Exception {
        when(orderService.getOrderByApiKey(any())).thenThrow(OrderService.OrderServiceException.noOrderForApikey("non-existent-api-key"));

        mvc.perform(get("/api/orders/order").param("apiKey","InvalidApiKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order Service Error"))
                .andExpect(jsonPath("$.detail").value("Order with api key (non-existent-api-key) not existent"))
                .andDo(document("orders/get-not-found",
                        queryParameters(
                                parameterWithName("apiKey").description("The API key of the order to retrieve")
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