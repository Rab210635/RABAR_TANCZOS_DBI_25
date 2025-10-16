package spengergasse.at.sj2425scherzerrabar.presentation.www.authors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import spengergasse.at.sj2425scherzerrabar.commands.AuthorCommand;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateAuthorForm {
    @NotBlank
    private String penname;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @Email
    @NotBlank
    private String emailAddress;

    @NotEmpty
    private List<String> streetAndNumber = new ArrayList<>();

    @NotEmpty
    private List<String> city = new ArrayList<>();

    @NotEmpty
    private List<String> plz = new ArrayList<>();

    // Wandelt die Felder in zusammengesetzte Adressen um
    public List<String> getAddress() {
        List<String> addresses = new ArrayList<>();
        for (int i = 0; i < streetAndNumber.size(); i++) {
            addresses.add(streetAndNumber.get(i) + "-" + city.get(i) + "-" + plz.get(i));
        }
        return addresses;
    }

    public AuthorCommand getAuthorCommand() {
        return new AuthorCommand(
                null,
                penname,
                getAddress(),
                firstname,
                lastname,
                emailAddress
        );
    }

    public AuthorCommand getAuthorCommand(String apiKey) {
        return new AuthorCommand(
                apiKey,
                penname,
                getAddress(),
                firstname,
                lastname,
                emailAddress
        );
    }

    public String getPenname() {
        return penname;
    }

    public void setPenname(String penname) {
        this.penname = penname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<String> getStreetAndNumber() {
        return streetAndNumber;
    }

    public void setStreetAndNumber(List<String> streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(List<String> city) {
        this.city = city;
    }

    public List<String> getPlz() {
        return plz;
    }

    public void setPlz(List<String> plz) {
        this.plz = plz;
    }
}
