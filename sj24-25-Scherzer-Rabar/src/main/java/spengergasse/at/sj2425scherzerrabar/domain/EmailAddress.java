package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.Embeddable;

@SuppressWarnings("JpaObjectClassSignatureInspection")
@Embeddable
public record EmailAddress(String email) {
    private static final String EMAIL_REGEX =  "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public EmailAddress(String email) {

        if(email == null || email.isEmpty()) {
            throw EmailAddressException.forNull();
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw EmailAddressException.forInvalidMail(email);
        }else {
            this.email = email;
        }

    }

    @Override
    public String toString() {
        return email;
    }

    public static class EmailAddressException extends RuntimeException {
        public EmailAddressException(String message) {
            super(message);
        }
        static EmailAddressException forNull() {
            final String message = "You have provided a null Value for a MailAdress";
            return new EmailAddressException(message);
        }
        static EmailAddressException forInvalidMail(String email) {
            final String message = "You have provided an invalid Value for a MailAdress (%s)".formatted(email);
            return new EmailAddressException(message);
        }
    }
}
