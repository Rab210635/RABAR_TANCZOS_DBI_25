package spengergasse.at.sj2425scherzerrabar.domain.jpa;

import jakarta.persistence.*;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;
import spengergasse.at.sj2425scherzerrabar.domain.Person;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

import java.util.List;

@Entity
@Table(name = "author")
public class Author extends Person {
    private String penname;


    @ElementCollection
    @JoinTable(name = "addresses_in_authors", foreignKey = @ForeignKey(name = "FK_adresses_2_author"))
    protected List<Address> address;
    
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "author_api_key"))
    private ApiKey authorApiKey;



    public Author(String firstName, String lastName, List<Address> address, EmailAddress emailAddress, String penname) {
        super(firstName, lastName, emailAddress);
        this.penname = penname;
        this.address = address;

        this.authorApiKey = new ApiKeyFactory().generate(30);
    }

    public Author() {
        super();

        this.authorApiKey = new ApiKeyFactory().generate(30);
    }

    public String getPenname() {
        return penname;
    }

    public void setPenname(String penname) {
        this.penname = penname;
    }

    public ApiKey getAuthorApiKey() {
        return authorApiKey;
    }


    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }
}
