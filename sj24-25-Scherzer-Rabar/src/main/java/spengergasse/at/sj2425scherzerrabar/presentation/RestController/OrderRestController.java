package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.OrderCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Order;
import spengergasse.at.sj2425scherzerrabar.dtos.OrderDto;
import spengergasse.at.sj2425scherzerrabar.service.OrderService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final OrderService orderService;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get all Orders",
            description = "To get every Order object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))})})
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        logger.debug("entered orderrestcontroller getAllOrders");
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    @Operation(summary = "Get all Orders by a Date",
            description = "To get all Orders by their Date. The response is the requested Orders objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))})})
    @GetMapping("/date")
    public ResponseEntity<List<OrderDto>> getAllOrdersByDate(@RequestParam LocalDate date) {
        logger.debug("entered orderrestcontroller getAllOrdersByDate");
        List<OrderDto> orders = orderService.getAllOrdersByDate(date);
        return ResponseEntity.ok(orders);
    }
    @Operation(summary = "Get all Orders by a Customer",
            description = "To get all Orders by their Customer. The response is the requested Orders objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @GetMapping("/customer")
    public ResponseEntity<List<OrderDto>> getAllOrdersByCustomer(@RequestParam String customer) {
        logger.debug("entered orderrestcontroller getAllOrdersByCustomer");
        List<OrderDto> orders = orderService.getAllOrdersByCustomer(customer);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get a Order",
            description = "To get an specific Order by their Apikey. The response is the requested Order object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)})
    @GetMapping("/order")
    public ResponseEntity<OrderDto> getOrder(@RequestParam String apiKey) {
        logger.debug("entered orderrestcontroller getOrder");
        OrderDto order = orderService.getOrderByApiKey(apiKey);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Create a Order",
            description = "Create a new Order to be saved in the database. The response is a link to the created Order object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))})})
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderCommand command) {
        logger.debug("entered orderrestcontroller createOrder");
        OrderDto response = orderService.createOrder(command);

        Link selfLink = linkTo(methodOn(OrderRestController.class).getOrder(response.apiKey())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(response);
    }

    @Operation(summary = "Update a Order",
            description = "Update an existing Order. The response is an updated Order object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderCommand command) {
        logger.debug("entered orderrestcontroller updateOrder");
        OrderDto response = orderService.updateOrder(command);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a Order",
            description = "Delete a existing Order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<OrderDto> deleteOrder(@RequestParam String apiKey) {
        logger.debug("entered orderrestcontroller deleteOrder");
        orderService.deleteOrder(apiKey);
        return ResponseEntity.noContent().build();
    }
}
