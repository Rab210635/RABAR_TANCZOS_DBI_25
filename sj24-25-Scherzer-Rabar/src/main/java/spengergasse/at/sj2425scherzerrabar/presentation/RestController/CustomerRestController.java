package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.CustomerCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Customer;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;
import spengergasse.at.sj2425scherzerrabar.service.CustomerService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {
    private final CustomerService customerService;
    public CustomerRestController(CustomerService customerService) {this.customerService = customerService;}
    Logger logger = LoggerFactory.getLogger(LoggingController.class);


    @Operation(summary = "Get all Customers",
            description = "To get every Customer object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class))})})
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        logger.debug("entered customerrestcontroller getAllCustomers");
        List<CustomerDto> response = customerService.getCustomers();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a Customer",
            description = "To get an specific Customer by either their Apikey or Email-Address. The response is the requested Customer object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @GetMapping("/customer")
    public ResponseEntity<CustomerDto> getCustomer(@RequestParam Optional<String> apiKey, @RequestParam Optional<String> email) throws BadRequestException {
        logger.debug("entered customerrestcontroller getCustomer");
        CustomerDto response;
        if(apiKey.isPresent()){
            response = customerService.getCustomer(apiKey.get());
        }else if(email.isPresent()){
            response = customerService.getCustomerByEMail(email.get());
        }else {
            throw new BadRequestException("Bad Request, Parameter for get Customer needed");
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a Customer",
            description = "Create a new Customer to be saved in the database. The response is a link to the created Customer object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class))})})
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCommand command) throws BadRequestException {
        logger.debug("entered customerrestcontroller createCustomer");
        CustomerDto response = customerService.createCustomer(command);

        Link selfLink = linkTo(methodOn(CustomerRestController.class).getCustomer(Optional.of(response.apiKey()),null)).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(response);    }

    @Operation(summary = "Update a Customer",
            description = "Update an existing Customer. The response is an updated Customer object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody CustomerCommand command) {
        logger.debug("entered customerrestcontroller updateCustomer");
        CustomerDto response = customerService.updateCustomer(command);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a Customer",
            description = "Delete a existing Customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<CustomerDto> deleteOrder(@RequestParam String apiKey) {
        logger.debug("entered customerrestcontroller deleteOrder");
        customerService.deleteCustomer(apiKey);
        return ResponseEntity.noContent().build();
    }
}
