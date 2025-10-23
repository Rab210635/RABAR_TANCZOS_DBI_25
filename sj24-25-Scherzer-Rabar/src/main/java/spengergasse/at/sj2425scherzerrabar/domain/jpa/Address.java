package spengergasse.at.sj2425scherzerrabar.domain.jpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String streetAndNumber;
    private String city;
    private Integer zip;

    protected Address() {
    }

    public Address(String streetAndNumber, String city, Integer zip) {
        if (streetAndNumber == null || streetAndNumber.trim().isEmpty()) {
            throw AddressException.forNull();
        }
        if (city == null || city.trim().isEmpty()) {
            throw AddressException.forNull();
        }
        if (zip == null) {
            throw AddressException.forNull();
        } else if (zip < 1010 || zip > 9999) {
            throw AddressException.forInvalidAddress(streetAndNumber + " " + city + " " + zip);
        }
        this.streetAndNumber = streetAndNumber;
        this.city = city;
        this.zip = zip;
    }

    public String streetAndNumber() {
        return streetAndNumber;
    }

    public String city() {
        return city;
    }

    public Integer zip() {
        return zip;
    }

    @Override
    public String toString() {
        return streetAndNumber + "-" + city + '-' + zip;
    }

    public static Address addressFromString(String address) {
        String[] addressAttributes = address.split("-");
        return new Address(addressAttributes[0], addressAttributes[1], Integer.parseInt(addressAttributes[2]));
    }

    public static class AddressException extends RuntimeException {
        public AddressException(String message) {
            super(message);
        }
        static AddressException forNull() {
            final String message = "You have provided a null Value for a Address";
            return new AddressException(message);
        }
        static AddressException forInvalidAddress(String address) {
            final String message = "You have provided an invalid Value for a Address (%s)".formatted(address);
            return new AddressException(message);
        }
    }
}
