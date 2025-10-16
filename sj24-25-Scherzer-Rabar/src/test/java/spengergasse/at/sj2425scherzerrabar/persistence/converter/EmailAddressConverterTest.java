package spengergasse.at.sj2425scherzerrabar.persistence.converter;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)

class EmailAddressConverterTest {

    @Test
    void convert_valid_mail_to_db() {
        EmailConverter converter = new EmailConverter();
        EmailAddress emailAddress = new EmailAddress("tesxt@gmail.com");
        String convertedValue = converter.convertToDatabaseColumn(emailAddress);
        assertThat(convertedValue).isEqualTo("tesxt@gmail.com");
    }

    @Test
    void convert_valid_db_to_mail() {
        String dbValue = "tesxt@gmail.com";
        EmailConverter converter = new EmailConverter();
        EmailAddress emailAddress = converter.convertToEntityAttribute(dbValue);
        assertThat(emailAddress).isEqualTo(new EmailAddress("tesxt@gmail.com"));
    }
}