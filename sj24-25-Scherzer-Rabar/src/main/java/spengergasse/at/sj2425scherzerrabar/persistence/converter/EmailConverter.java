package spengergasse.at.sj2425scherzerrabar.persistence.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<EmailAddress, String> {

    @Override
    public String convertToDatabaseColumn(EmailAddress emailAddress) {
        return emailAddress.toString();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String s) {
        return switch (s){
            case null -> null;
            default -> new EmailAddress(s);
        };
    }
}
