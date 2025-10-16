package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.OrderCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.OrderDto;
import spengergasse.at.sj2425scherzerrabar.persistence.*;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly=true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BuyableBookRepository buyableBookRepository;
    private final LibrarySubscriptionRepository librarySubscriptionRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public OrderService(OrderRepository orderRepository,CustomerRepository CustomerRepository,BuyableBookRepository buyableBookRepository,LibrarySubscriptionRepository librarySubscriptionRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = CustomerRepository;
        this.buyableBookRepository = buyableBookRepository;
        this.librarySubscriptionRepository = librarySubscriptionRepository;
    }

    @Transactional
    public OrderDto createOrder(OrderCommand command) {
        logger.debug("entered createOrder");
        var customer = customerRepository.findCustomerByCustomerApiKey(new ApiKey(command.customerApikey()))
                .orElseThrow(()-> OrderServiceException.noCustomerForApikey(command.customerApikey()));
       var subscriptions = command.subscriptionsApiKeys().stream()
                .map(s -> librarySubscriptionRepository.findLibrarySubscriptionByLibrarySubscriptionApiKey(s)
                        .orElseThrow(()->OrderServiceException.noLibrarySubscriptionForApikey(s))).toList();

       var buyablebooks = command.booksApiKeys().stream().map(bb-> buyableBookRepository.findBuyableBookByBuyableBookApiKey(new ApiKey(bb))
               .orElseThrow(()-> OrderServiceException.noBuyableBookForApiKey(bb))).toList();
       Order order = new Order(customer,subscriptions,command.date(),buyablebooks);
       orderRepository.save(order);
       return OrderDto.orderDtoFromOrder(order);
    }

    @Transactional
    public OrderDto updateOrder(OrderCommand command) {
        logger.debug("entered updateOrder");
        Order order = orderRepository.findOrderByOrderApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(()->OrderServiceException.noOrderForApikey(command.apiKey()));

        var customer = customerRepository.findCustomerByCustomerApiKey(new ApiKey(command.customerApikey()))
                .orElseThrow(()->OrderServiceException.noCustomerForApikey(command.customerApikey()));
        var subscriptions = command.subscriptionsApiKeys().stream()
                .map(s -> librarySubscriptionRepository.findLibrarySubscriptionByLibrarySubscriptionApiKey(s)
                        .orElseThrow(()->OrderServiceException.noLibrarySubscriptionForApikey(s))).toList();

        var buyablebooks = command.booksApiKeys().stream().map(bb-> buyableBookRepository.findBuyableBookByBuyableBookApiKey(new ApiKey(bb))
                .orElseThrow(()->OrderServiceException.noBuyableBookForApiKey(bb))).toList();

        order.setCustomer(customer);
        order.setSubscriptions(subscriptions);
        order.setBooks(buyablebooks);
        order.setDate(command.date());

        orderRepository.save(order);
        return OrderDto.orderDtoFromOrder(order);
    }

    @Transactional
    public void deleteOrder(String orderApiKey) {
        logger.debug("entered deleteOrder");
        Order order = orderRepository.findOrderByOrderApiKey(new ApiKey(orderApiKey))
                .orElseThrow(() -> OrderServiceException.noOrderForApikey(orderApiKey));
        orderRepository.delete(order);
    }

    public List<OrderDto> getAllOrders() {
        logger.debug("entered getAllOrders");
        return orderRepository.findAllProjected();
    }

    public OrderDto getOrderByApiKey(String apiKey) {
        logger.debug("entered getOrderByApiKey");
        return orderRepository.findProjectedByOrderApiKey(apiKey)
                .orElseThrow(() -> OrderServiceException.noOrderForApikey(apiKey));
    }

    public List<OrderDto> getAllOrdersByCustomer(String customerApiKey) {
        logger.debug("entered getAllOrdersByCustomer");
        customerRepository.findProjectedCustomerByCustomerApiKey(customerApiKey)
                .orElseThrow(()-> OrderServiceException.noCustomerForApikey(customerApiKey));
        List<OrderDto> orders = orderRepository.findAllProjectedByCustomerApiKey(customerApiKey);
        if(orders.isEmpty()) {
            throw OrderServiceException.noCopies();
        }
        return orders;
    }

    public List<OrderDto> getAllOrdersByDate(LocalDate date) {
        logger.debug("entered getAllOrdersByDate");
        return orderRepository.findAllProjectedByDate(date);
    }

    public static class OrderServiceException extends RuntimeException
    {
        private OrderServiceException(String message)
        {
            super(message);
        }

        public static OrderServiceException noOrderForApikey(String apiKey)
        {
            return new OrderServiceException("Order with api key (%s) not existent".formatted(apiKey));
        }

        static OrderServiceException noBuyableBookForApiKey(String apiKey)
        {
            return new OrderServiceException("Buyable Book with api key (%s) not existent".formatted(apiKey));
        }

        static OrderServiceException noCopies()
        {
            return new OrderServiceException("No Copies for Order");
        }

        static OrderServiceException noCustomerForApikey(String apiKey)
        {
            return new OrderServiceException("Customer with api key (%s) not existent".formatted(apiKey));
        }
        static OrderServiceException noLibrarySubscriptionForApikey(String apiKey)
        {
            return new OrderServiceException("Library Subscription with api key (%s) not existent".formatted(apiKey));
        }
    }
}
