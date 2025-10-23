package spengergasse.at.sj2425scherzerrabar.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "authors")
public class AuthorDocument {

    @Id
    private String id;

    @Field("postgres_id")
    private Long postgresId;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("pen_name")
    private String penname;

    @Field("email")
    private String email;

    @Field("addresses")
    private List<AddressMongo> addresses = new ArrayList<>();

    @Field("api_key")
    private String apiKey;

    public AuthorDocument() {
    }

    public AuthorDocument(Long postgresId, String firstName, String lastName,
                          String penname, String email, String apiKey) {
        this.postgresId = postgresId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.penname = penname;
        this.email = email;
        this.apiKey = apiKey;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPostgresId() {
        return postgresId;
    }

    public void setPostgresId(Long postgresId) {
        this.postgresId = postgresId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPenname() {
        return penname;
    }

    public void setPenname(String penname) {
        this.penname = penname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AddressMongo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressMongo> addresses) {
        this.addresses = addresses;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Embedded Address für MongoDB
     */
    public static class AddressMongo {
        private String streetAndNumber;
        private String city;
        private Integer zip;

        public AddressMongo() {
        }

        public AddressMongo(String streetAndNumber, String city, Integer zip) {
            this.streetAndNumber = streetAndNumber;
            this.city = city;
            this.zip = zip;
        }

        public static AddressMongo fromAddress(spengergasse.at.sj2425scherzerrabar.domain.jpa.Address address) {
            return new AddressMongo(
                    address.streetAndNumber(),
                    address.city(),
                    address.zip()
            );
        }

        public Address toAddress() {
            return new Address(streetAndNumber, city, zip);
        }

        public String getStreetAndNumber() {
            return streetAndNumber;
        }

        public void setStreetAndNumber(String streetAndNumber) {
            this.streetAndNumber = streetAndNumber;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Integer getZip() {
            return zip;
        }

        public void setZip(Integer zip) {
            this.zip = zip;
        }
    }
}