package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public class Person {
    @EmbeddedId
    protected PersonId personId;
    protected String firstName;
    protected String lastName;


    @Embedded
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_email_2_person"))
    protected EmailAddress emailAddress;

    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    public record PersonId(
            @GeneratedValue @NotNull Long id){
    }


    public Person(String firstName, String lastName, EmailAddress mail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = mail;
    }

    public Person(){

    }

    public PersonId getPersonId() {
        return personId;
    }

    public void setPersonId(PersonId personId) {
        this.personId = personId;
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

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }
}
