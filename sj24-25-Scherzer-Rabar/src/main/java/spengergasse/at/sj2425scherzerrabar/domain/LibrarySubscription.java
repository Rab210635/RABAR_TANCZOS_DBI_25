package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

@Entity
@Table(name = "librarysubscription")
public class LibrarySubscription {
    @EmbeddedId
    private LibrarySubscriptionId librarySubscriptionId;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "librarysubscription_api_key"))
    private ApiKey librarySubscriptionApiKey;
    @NotNull
    private String name;
    private String description;
    @NotNull
    @Min(0)
    @Max(10000)
    private Double monthlyCost;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "library_subscriptions_2_library"))
    @NotNull
    private Library library;

    public LibrarySubscription() {
        this.librarySubscriptionApiKey = new ApiKeyFactory().generate(30);
    }

    public LibrarySubscription(String name, String description, Double monthlyCost, Library library) {
        this.librarySubscriptionApiKey = new ApiKeyFactory().generate(30);
        this.name = name;
        this.description = description;
        this.monthlyCost = monthlyCost;
        this.library = library;
    }

    public ApiKey getLibrarySubscriptionApiKey() {
        return librarySubscriptionApiKey;
    }



    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record LibrarySubscriptionId (@GeneratedValue @NotNull Long id){}
}
