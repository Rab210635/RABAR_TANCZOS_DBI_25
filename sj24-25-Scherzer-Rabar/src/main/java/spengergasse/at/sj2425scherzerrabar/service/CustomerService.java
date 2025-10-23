package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.CustomerCommand;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Customer;
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;
import spengergasse.at.sj2425scherzerrabar.persistence.CustomerRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class CustomerService {
    private final CustomerRepository customerRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerDto createCustomer(CustomerCommand command) {
        logger.debug("entered createCustomer");
        Customer customer = new Customer(
         command.firstName(),command.lastName(),new EmailAddress(command.emailAddress()),
                command.addresses().stream().map(Address::addressFromString).toList()
        );
        customerRepository.save(customer);
        return CustomerDto.customerDtoFromCustomer(customer);
    }

    @Transactional
    public void deleteCustomer(String apiKey) {
        logger.debug("entered deleteCustomer");
        var customer = customerRepository.findCustomerByCustomerApiKey(new ApiKey(apiKey))
                .orElseThrow(()->CustomerServiceException.noCustomerForApiKey(apiKey));
        customerRepository.delete(customer);
    }

    @Transactional
    public CustomerDto updateCustomer(CustomerCommand command) {
        logger.debug("entered updateCustomer");
       Customer customer = customerRepository.findCustomerByCustomerApiKey(new ApiKey(command.apiKey())).map((Customer c)->{
            if (!command.firstName().equals(c.getFirstName())) {
                c.setFirstName(command.firstName());
            }
            if (!command.lastName().equals(c.getLastName())) {
                c.setLastName(command.lastName());
            }
            if (!command.emailAddress().equals(c.getEmailAddress().email())) {
                c.setEmailAddress(new EmailAddress(command.emailAddress()));
            }
            c.setAddress(command.addresses().stream().map(Address::addressFromString).toList());

            customerRepository.save(c);
            return c;
        }).orElseThrow(()->CustomerServiceException.noCustomerForApiKey(command.apiKey()));
       return CustomerDto.customerDtoFromCustomer(customer);
    }

    public List<CustomerDto> getCustomers() {
        logger.debug("entered getCustomers");
        return customerRepository.findAllProjected();
    }

    public CustomerDto getCustomer(String apiKey) {
        logger.debug("entered getCustomer");
        return customerRepository.findProjectedCustomerByCustomerApiKey(apiKey)
                .orElseThrow(() -> CustomerServiceException.noCustomerForApiKey(apiKey));
    }

    public CustomerDto getCustomerByEMail(String emailAddress) {
        logger.debug("entered getCustomerByEMail");
        return  customerRepository.findProjectedCustomerByEmailAddress_Email(emailAddress)
                .orElseThrow(() -> CustomerServiceException.noCustomerForEmail(emailAddress));
    }

    public static class CustomerServiceException extends RuntimeException
    {
        private CustomerServiceException(String message)
        {
            super(message);
        }

        public static CustomerServiceException noCustomerForApiKey(String apiKey)
        {
            return new CustomerServiceException("Customer with api key (%s) not existent".formatted(apiKey));
        }

        static CustomerServiceException noCustomerForEmail(String email)
        {
            return new CustomerServiceException("Customer with email (%s) not existent".formatted(email));
        }
    }
}
