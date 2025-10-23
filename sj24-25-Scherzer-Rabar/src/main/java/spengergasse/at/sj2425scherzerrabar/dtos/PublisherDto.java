package spengergasse.at.sj2425scherzerrabar.dtos;


import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;

public record PublisherDto(String apiKey, String name, String address) {

    public PublisherDto(ApiKey apiKey, String name, Address address) {
        this(apiKey.apiKey(), name, address.toString());
    }

    public static PublisherDto publisherDtoFromPublisher(Publisher publisher) {
        return new PublisherDto(
                publisher.getPublisherApiKey().apiKey(),publisher.getName(),publisher.getAddress().toString()
        );
    }
}
