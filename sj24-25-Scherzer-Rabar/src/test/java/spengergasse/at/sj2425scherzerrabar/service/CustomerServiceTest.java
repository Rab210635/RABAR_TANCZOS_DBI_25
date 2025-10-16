package spengergasse.at.sj2425scherzerrabar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.commands.CustomerCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;
import spengergasse.at.sj2425scherzerrabar.persistence.CustomerRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock private CustomerRepository customerRepository;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        assumeThat(customerRepository).isNotNull();
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void can_create_customer(){
        when(customerRepository.save(any(Customer.class))).then(AdditionalAnswers.returnsFirstArg());

        var customer = customerService.createCustomer( new CustomerCommand(
                new ApiKey("customerApiKey").apiKey(),List.of(new Address("12","Wien",1212).toString()),
                "Paron", "krabar",new EmailAddress("hoho@sasd.at").email()));
        assertThat(customer).isNotNull();
    }

    @Test
    void can_delete_existing_customer(){
        Customer customer = FixturesFactory.customer();
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(new ApiKey("validApiKey").apiKey());

        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void cant_delete_not_existing_customer(){
        ApiKey apiKey = new ApiKey("customerApiKey");
        assertThatThrownBy(()->customerService.deleteCustomer(apiKey.apiKey()))
                .isInstanceOf(CustomerService.CustomerServiceException.class)
                .hasMessageContaining("Customer with api key (customerApiKey) not existent");
    }

    @Test
    void can_update_existing_customer(){
        Customer customer = FixturesFactory.customer();
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).then(AdditionalAnswers.returnsFirstArg());

        customerService.updateCustomer(new CustomerCommand(
                new ApiKey("customerApiKey").apiKey(),List.of(new Address("123","Wien2",1212).toString()),
                "Mustermannaer", "Maxi",new EmailAddress("hohoha@sasd.at").email()));

        verify(customerRepository, times(1)).save(customer);
        assertThat(customer.getLastName()).isEqualTo("Maxi");
        assertThat(customer.getFirstName()).isEqualTo("Mustermannaer");
    }

    @Test
    void can_update_existing_customer_with_same_values_for_coverage(){
        Customer customer = FixturesFactory.customer();
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).then(AdditionalAnswers.returnsFirstArg());

        customerService.updateCustomer(new CustomerCommand(
                new ApiKey("customerApiKey").apiKey(),List.of(new Address("1","Wien2",1212).toString()),
                customer.getFirstName(), customer.getLastName(),customer.getEmailAddress().email()));

        verify(customerRepository, times(1)).save(customer);
        assertThat(customer.getLastName()).isEqualTo("Mustermann");
        assertThat(customer.getFirstName()).isEqualTo("Max");
    }

    @Test
    void cant_update_not_existing_customer(){
        assertThatThrownBy(()->customerService.updateCustomer(new CustomerCommand(
                new ApiKey("customerApiKey").apiKey(),List.of(new Address("12","Wien",1212).toString()),
                "Mustermann", "Max",new EmailAddress("hoho@sasd.at").email())))
                .isInstanceOf(CustomerService.CustomerServiceException.class)
                .hasMessageContaining("Customer with api key (customerApiKey) not existent");
    }

    @Test
    void can_get_existing_customer(){
        CustomerDto customer = CustomerDto.customerDtoFromCustomer(FixturesFactory.customer());
        when(customerRepository.findProjectedCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        var customer1 = customerService.getCustomer(customer.apiKey());
        assertThat(customer1).isEqualTo(customer);
        verify(customerRepository, times(1)).findProjectedCustomerByCustomerApiKey(any());
    }

    @Test
    void cant_get_not_existing_customer(){
        assertThatThrownBy(()->customerService.getCustomer(new ApiKey("customerApiKey").apiKey()))
                .isInstanceOf(CustomerService.CustomerServiceException.class)
                .hasMessageContaining("Customer with api key (customerApiKey) not existent");
    }

    @Test
    void can_get_customers() {
        CustomerDto customer = CustomerDto.customerDtoFromCustomer(FixturesFactory.customer());
        CustomerDto customer2 = CustomerDto.customerDtoFromCustomer(FixturesFactory.customer());
        when(customerRepository.findAllProjected()).thenReturn(List.of(customer,customer2));

        var customers = customerService.getCustomers();
        assertThat(customers).hasSize(2);
    }

    @Test
    void can_get_existing_customer_by_email_address(){
        CustomerDto customer = CustomerDto.customerDtoFromCustomer(FixturesFactory.customer());
        when(customerRepository.findProjectedCustomerByEmailAddress_Email(any())).thenReturn(Optional.of(customer));
        var customer1 = customerService.getCustomerByEMail(customer.emailAddress());
        assertThat(customer1).isNotNull();
        verify(customerRepository, times(1)).findProjectedCustomerByEmailAddress_Email(any());
    }

    @Test
    void cant_get_not_existing_customer_by_email_address(){
        assertThatThrownBy(()->customerService.getCustomerByEMail("A"))
                .isInstanceOf(CustomerService.CustomerServiceException.class)
                .hasMessageContaining("Customer with email (A) not existent");
    }
}