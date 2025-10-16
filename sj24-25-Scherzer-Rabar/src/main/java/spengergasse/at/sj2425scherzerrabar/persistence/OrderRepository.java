package spengergasse.at.sj2425scherzerrabar.persistence;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Order;
import spengergasse.at.sj2425scherzerrabar.dtos.OrderDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByOrderApiKey(ApiKey apiKey);

    List<Order> findAllByCustomer_CustomerApiKey_ApiKey(String customerCustomerApiKeyApiKey);

    List<Order> findAllByDate(@NotNull @PastOrPresent LocalDate date);


    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.OrderDto(
            o
        ) from Order o
        """)
    List<OrderDto> findAllProjected();

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.OrderDto(
            o
        ) from Order o where o.orderApiKey.apiKey =  :apiKey
        """)
    Optional<OrderDto> findProjectedByOrderApiKey(String apiKey);



    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.OrderDto(
            o
        ) from Order o where o.customer.customerApiKey.apiKey = :apiKey
        """)
    List<OrderDto> findAllProjectedByCustomerApiKey(String apiKey);


    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.OrderDto(
        o
        ) from Order o where o.date =  :date
        """)
    List<OrderDto> findAllProjectedByDate(@NotNull @PastOrPresent LocalDate date);



}
