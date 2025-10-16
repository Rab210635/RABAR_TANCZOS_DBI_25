package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "libraryorder")
public class Order {
    @EmbeddedId
    private OrderId id;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "libraryorder_api_key"))
    private ApiKey orderApiKey;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @NotNull
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_customer_order"))
    private Customer customer;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "subscriptions_in_order",
            joinColumns = @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "FK_ordersubscription")),
            inverseJoinColumns = @JoinColumn(name = "subscription_id",foreignKey = @ForeignKey(name = "FK_subscription")))
    private List<LibrarySubscription> subscriptions;

    @NotNull
    @PastOrPresent
    private LocalDate date;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_in_order",
            joinColumns = @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "FK_book")),
            inverseJoinColumns = @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "FK_orderbook"))
    )
    private List<BuyableBook> books;

    public Order() {
        this.orderApiKey = new ApiKeyFactory().generate(30);
    }
    public Order(Customer customer,  List<LibrarySubscription> subscriptions, LocalDate date, List<BuyableBook> books) {
        this.customer = customer;
        this.subscriptions = subscriptions;
        this.date = date;
        this.books = books;
        this.orderApiKey = new ApiKeyFactory().generate(30);

    }

    public ApiKey getOrderApiKey() {
        return orderApiKey;
    }


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<LibrarySubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<LibrarySubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<BuyableBook> getBooks() {
        return books;
    }

    public void setBooks(List<BuyableBook> books) {
        this.books = books;
    }

    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record OrderId(@GeneratedValue @NotNull Long id){}
}
