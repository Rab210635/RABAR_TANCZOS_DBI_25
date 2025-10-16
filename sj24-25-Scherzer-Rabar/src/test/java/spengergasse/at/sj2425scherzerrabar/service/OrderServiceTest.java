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
import spengergasse.at.sj2425scherzerrabar.commands.OrderCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;
import spengergasse.at.sj2425scherzerrabar.dtos.OrderDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BuyableBookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CustomerRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.LibrarySubscriptionRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.OrderRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private @Mock CustomerRepository customerRepository;
    private @Mock OrderRepository orderRepository;
    private @Mock BuyableBookRepository buyableBookRepository;
    private @Mock LibrarySubscriptionRepository librarySubscriptionRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        assumeThat(customerRepository).isNotNull();
        assumeThat(orderRepository).isNotNull();
        assumeThat(buyableBookRepository).isNotNull();
        assumeThat(librarySubscriptionRepository).isNotNull();

        orderService = new OrderService(orderRepository,customerRepository,buyableBookRepository,librarySubscriptionRepository);
    }

    @Test
    void can_create_order() {
        var customer = FixturesFactory.customer();
        var buyableBook = FixturesFactory.buyableBook();
        var librarySubscription = FixturesFactory.librarySubscription();

        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(librarySubscriptionRepository.findLibrarySubscriptionByLibrarySubscriptionApiKey(any())).thenReturn(Optional.of(librarySubscription));
        when(buyableBookRepository.findBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(buyableBook));

        OrderDto orderDto = orderService.createOrder(
                new OrderCommand(new ApiKey("orderApiKey").apiKey(),customer.getCustomerApiKey().apiKey(),
                        List.of(librarySubscription.getLibrarySubscriptionApiKey().apiKey(),librarySubscription.getLibrarySubscriptionApiKey().apiKey()),
                        LocalDate.MIN, List.of(buyableBook.getBuyableBookApiKey().apiKey(),buyableBook.getBuyableBookApiKey().apiKey())
                ));

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.customerApiKey()).isEqualTo(customer.getCustomerApiKey().apiKey());
        assertThat(orderDto.subscriptionsApiKeys()).isEqualTo(List.of(librarySubscription.getLibrarySubscriptionApiKey().apiKey(),librarySubscription.getLibrarySubscriptionApiKey().apiKey()));
        assertThat(orderDto.booksApiKeys()).isEqualTo(List.of(buyableBook.getBuyableBookApiKey().apiKey(),buyableBook.getBuyableBookApiKey().apiKey()));
    }

    @Test
    void cant_create_order_with_missing_buyable_book() {
        var customer = FixturesFactory.customer();
        var librarySubscription = FixturesFactory.librarySubscription();

        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(librarySubscriptionRepository.findLibrarySubscriptionByLibrarySubscriptionApiKey(any())).thenReturn(Optional.of(librarySubscription));

        assertThatThrownBy(() ->orderService.createOrder(new OrderCommand(new ApiKey("orderApiKey").apiKey(),customer.getCustomerApiKey().apiKey(),
                List.of(librarySubscription.getLibrarySubscriptionApiKey().apiKey(),librarySubscription.getLibrarySubscriptionApiKey().apiKey()),
                LocalDate.MIN, List.of("bbApiKey","bbApiKey1")
        )) )
                .isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Buyable Book with api key (bbApiKey) not existent");
    }

    @Test
    void cant_create_order_with_missing_subscription() {
        var customer = FixturesFactory.customer();

        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() ->orderService.createOrder(new OrderCommand(new ApiKey("orderApiKey").apiKey(),customer.getCustomerApiKey().apiKey(),
                List.of("lsApiKey","lsApiKey"), LocalDate.MIN, List.of("bbApiKey","bbApiKey")
        )) )
                .isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Library Subscription with api key (lsApiKey) not existent");
    }

    @Test
    void cant_create_order_with_missing_customer() {
        assertThatThrownBy(() ->orderService.createOrder(new OrderCommand("orderApiKey","customerApiKey",
                List.of("lsApiKey","lsApiKey"), LocalDate.MIN, List.of("bbApiKey","bbApiKey1")
        )) )
                .isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Customer with api key (customerApiKey) not existent");
    }

    @Test
    void can_update_order() {
        var order = FixturesFactory.order();
        var customer = FixturesFactory.customer();
        var librarySubscription = FixturesFactory.librarySubscription();
        var buyableBook = FixturesFactory.buyableBook();


        when(orderRepository.findOrderByOrderApiKey(any())).thenReturn(Optional.of(order));
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(librarySubscriptionRepository.findLibrarySubscriptionByLibrarySubscriptionApiKey(any())).thenReturn(Optional.of(librarySubscription));
        when(buyableBookRepository.findBuyableBookByBuyableBookApiKey(any())).thenReturn(Optional.of(buyableBook));
        when(orderRepository.save(any(Order.class))).then(AdditionalAnswers.returnsFirstArg());

        OrderDto updatedOrder = orderService.updateOrder(
                new OrderCommand(new ApiKey("orderApiKey").apiKey(),customer.getCustomerApiKey().apiKey(),
                        List.of(librarySubscription.getLibrarySubscriptionApiKey().apiKey(),librarySubscription.getLibrarySubscriptionApiKey().apiKey()),
                        LocalDate.MIN, List.of(buyableBook.getBuyableBookApiKey().apiKey(),buyableBook.getBuyableBookApiKey().apiKey())
                ));

        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.date()).isEqualTo(LocalDate.MIN);
    }

    @Test
    void cant_update_order_with_missing_buyable_book() {
        var order = FixturesFactory.order();
        var customer = FixturesFactory.customer();
        var librarySubscription = FixturesFactory.librarySubscription();

        when(orderRepository.findOrderByOrderApiKey(any())).thenReturn(Optional.of(order));
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(librarySubscriptionRepository.findLibrarySubscriptionByLibrarySubscriptionApiKey(any())).thenReturn(Optional.of(librarySubscription));

        assertThatThrownBy(() -> orderService.updateOrder(
                new OrderCommand(new ApiKey("orderApiKey").apiKey(),customer.getCustomerApiKey().apiKey(),
                        List.of(librarySubscription.getLibrarySubscriptionApiKey().apiKey(),librarySubscription.getLibrarySubscriptionApiKey().apiKey()),
                        LocalDate.MIN, List.of("bbApiKey","bbApiKey1")
                ))).isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Buyable Book with api key (bbApiKey) not existent");
    }

    @Test
    void cant_update_order_with_missing_subscription() {
        var order = FixturesFactory.order();
        var customer = FixturesFactory.customer();

        when(orderRepository.findOrderByOrderApiKey(any())).thenReturn(Optional.of(order));
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> orderService.updateOrder(
                new OrderCommand(new ApiKey("orderApiKey").apiKey(),customer.getCustomerApiKey().apiKey(),
                        List.of("lsApiKey","lsApiKey1"),
                        LocalDate.MIN, List.of("bbApiKey","bbApiKey1")
                ))).isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Library Subscription with api key (lsApiKey) not existent");
    }

    @Test
    void cant_update_order_with_missing_customer() {
        var order = FixturesFactory.order();


        when(orderRepository.findOrderByOrderApiKey(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrder(
                new OrderCommand(new ApiKey("orderApiKey").apiKey(),"customerApiKey",
                        List.of("lsApiKey","lsApiKey1"),
                        LocalDate.MIN, List.of("bbApiKey","bbApiKey1")
                ))).isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Customer with api key (customerApiKey) not existent");
    }

    @Test
    void cant_update_order_with_missing_order() {
        assertThatThrownBy(() -> orderService.updateOrder(
                new OrderCommand(new ApiKey("orderApiKey").apiKey(),"customerApiKey",
                        List.of("lsApiKey","lsApiKey1"), LocalDate.MIN, List.of("bbApiKey","bbApiKey1")
                ))).isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("Order with api key (orderApiKey) not existent");
    }

    @Test
    void can_delete_existing_order() {
        Order order = FixturesFactory.order();
        when(orderRepository.findOrderByOrderApiKey(any())).thenReturn(Optional.of(order));

        orderService.deleteOrder(new ApiKey("validApiKey").apiKey());

        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void cant_delete_not_existing_order() {
        assertThatThrownBy(() -> orderService.deleteOrder(new ApiKey("invalidApiKey").apiKey()))
        .isInstanceOf(OrderService.OrderServiceException.class)
        .hasMessageContaining("Order with api key (invalidApiKey) not existent");
    }

    @Test
    void can_get_orders() {
        OrderDto order = OrderDto.orderDtoFromOrder(FixturesFactory.order());
        when(orderRepository.findAllProjected()).thenReturn(List.of(order,order));

        var orders = orderService.getAllOrders();
        assertThat(orders).hasSize(2);
    }

    @Test
    void can_get_order_by_apiKey() {
        OrderDto order = OrderDto.orderDtoFromOrder(FixturesFactory.order());
        when(orderRepository.findProjectedByOrderApiKey(any())).thenReturn(Optional.of(order));
        var order1 = orderService.getOrderByApiKey(order.apiKey());

        assertThat(order1).isEqualTo(order);
        verify(orderRepository, times(1)).findProjectedByOrderApiKey(any());
    }

    @Test
    void cant_get_not_existing_order_by_apiKey() {
        assertThatThrownBy(()->orderService.getOrderByApiKey(new ApiKey("validApikey").apiKey()))
        .isInstanceOf(OrderService.OrderServiceException.class)
        .hasMessageContaining("Order with api key (validApikey) not existent");
    }

    @Test
    void can_get_orders_by_date() {
        OrderDto order = OrderDto.orderDtoFromOrder(FixturesFactory.order());
        when(orderRepository.findAllProjectedByDate(any())).thenReturn(List.of(order,order));

        var orders = orderService.getAllOrdersByDate(LocalDate.of(2025,2,5));
        assertThat(orders).hasSize(2);
    }

    @Test
    void can_get_orders_by_customer() {
        CustomerDto customer = CustomerDto.customerDtoFromCustomer(FixturesFactory.customer()) ;
        OrderDto order = OrderDto.orderDtoFromOrder(FixturesFactory.order());
        when(customerRepository.findProjectedCustomerByCustomerApiKey(customer.apiKey())).thenReturn(Optional.of(customer));
        when(orderRepository.findAllProjectedByCustomerApiKey(customer.apiKey())).thenReturn(List.of(order,order));

        var orders = orderService.getAllOrdersByCustomer(customer.apiKey());
        assertThat(orders).hasSize(2);
    }

    @Test
    void cant_get_orders_by_not_existing_customer() {
        CustomerDto customer = CustomerDto.customerDtoFromCustomer(FixturesFactory.customer());
        when(customerRepository.findProjectedCustomerByCustomerApiKey(customer.apiKey())).thenReturn(Optional.of(customer));
        assertThatThrownBy(() -> orderService.getAllOrdersByCustomer(customer.apiKey()))
                .isInstanceOf(OrderService.OrderServiceException.class)
                .hasMessageContaining("No Copies for Order");
    }
}