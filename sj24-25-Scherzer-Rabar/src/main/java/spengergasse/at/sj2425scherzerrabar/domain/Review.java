package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

@Entity
public class Review {

    @EmbeddedId
    private ReviewId reviewId;

    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "review_api_key"))
    private ApiKey reviewApiKey;

    private String title;
    @NotNull
    private Integer rating;
    private String description;

    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_reviews_2_customer"))
    private Customer customer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_reviews_2_book"))
    private Book book;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_reviews_2_branch"))
    private Branch branch;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_reviews_2_publisher"))
    private Publisher publisher;


    public Review(String title, Integer rating, String description, Customer customer, Book book, Branch branch, Publisher publisher) {
        this.reviewApiKey = new ApiKeyFactory().generate(30);
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.customer = customer;
        this.book = book;
        this.branch = branch;
        this.publisher = publisher;
    }

    public Review() {
        this.reviewApiKey = new ApiKeyFactory().generate(30);
    }

    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record ReviewId( @GeneratedValue @NotNull Long reviewId) {

    }


}
