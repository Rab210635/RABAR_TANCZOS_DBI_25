package spengergasse.at.sj2425scherzerrabar.dtos;



import spengergasse.at.sj2425scherzerrabar.domain.*;

import java.time.LocalDate;
import java.util.List;

public record OrderDto(
        String apiKey, String customerApiKey,
        List<String> subscriptionsApiKeys, LocalDate date, List<String> booksApiKeys
) {

    public OrderDto (ApiKey apiKey, ApiKey customerApiKey, List<ApiKey> subscriptions, LocalDate date, List<ApiKey> booksApiKeys) {
        this(apiKey.apiKey(), customerApiKey.apiKey(), subscriptions.stream().map(ApiKey::apiKey).toList(),date,booksApiKeys.stream().map(ApiKey::apiKey).toList());
    }

    public OrderDto(Order o){
        this(o.getOrderApiKey().apiKey(),o.getCustomer().getCustomerApiKey().apiKey(),o.getSubscriptions().stream().map(LibrarySubscription::getLibrarySubscriptionApiKey).map(ApiKey::apiKey).toList(),o.getDate(),o.getBooks().stream().map(BuyableBook::getBuyableBookApiKey).map(ApiKey::apiKey).toList());
    }


    public static OrderDto orderDtoFromOrder(Order order) {
       return new OrderDto(order.getOrderApiKey().apiKey(),order.getCustomer().getCustomerApiKey().apiKey(),
                order.getSubscriptions().stream().map(librarySubscription -> librarySubscription.getLibrarySubscriptionApiKey().apiKey()).toList(),
                order.getDate(),order.getBooks().stream().map(buyableBook -> buyableBook.getBuyableBookApiKey().apiKey()).toList()
        );
    }
}
