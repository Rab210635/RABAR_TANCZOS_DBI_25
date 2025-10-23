package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

@Entity
@Table(name = "publisher")
public class Publisher  {
    @EmbeddedId
    private PublisherId publisherId;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "publisher_api_key"))
    private ApiKey publisherApiKey;

    @NotNull
    private String name;
    @NotNull
    @Embedded
    private Address address;

    public Publisher() {
        this.publisherApiKey = new ApiKeyFactory().generate(30);
    }

    public Publisher(String name, Address address) {
        this.publisherApiKey = new ApiKeyFactory().generate(30);
        this.name = name;
        this.address = address;
    }

    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record PublisherId (@GeneratedValue @NotNull Long id){}

    public PublisherId getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(PublisherId publisherId) {
        this.publisherId = publisherId;
    }

    public ApiKey getPublisherApiKey() {
        return publisherApiKey;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
