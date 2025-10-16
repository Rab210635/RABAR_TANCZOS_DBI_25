package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

import java.util.List;

@Entity
@Table(name = "customer")
public class Customer extends Person {
    @ElementCollection
    @JoinTable(name = "addresses_in_customers", foreignKey = @ForeignKey(name = "FK_adresses_2_customer"))
    protected List<Address> address;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "customer_api_key"))
    private ApiKey customerApiKey;

    public Customer(String firstName, String lastName, EmailAddress emailAddress, List<Address> address) {
        super(firstName, lastName, emailAddress);
        this.address = address;
        this.customerApiKey = new ApiKeyFactory().generate(30);
    }
    public Customer(){
        super();
        customerApiKey = new ApiKeyFactory().generate(30);
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public ApiKey getCustomerApiKey() {
        return customerApiKey;
    }

}
