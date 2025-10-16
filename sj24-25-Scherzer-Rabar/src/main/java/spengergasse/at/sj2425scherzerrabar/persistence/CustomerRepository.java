package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Customer;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByCustomerApiKey(ApiKey apiKey);

    Optional<Customer> findCustomersByEmailAddress_Email(String emailAddressEmail);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto(
            c
        ) from Customer c where c.customerApiKey.apiKey = :apiKey
        """)
    Optional<CustomerDto> findProjectedCustomerByCustomerApiKey(String apiKey);


    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto(
            c
        ) from Customer c
        """)
    List<CustomerDto> findAllProjected();


    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto(
            c
        ) from Customer c where c.emailAddress.email = :email
        """)
    Optional<CustomerDto> findProjectedCustomerByEmailAddress_Email(String email);
}
