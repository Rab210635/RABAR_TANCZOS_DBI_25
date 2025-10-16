package spengergasse.at.sj2425scherzerrabar.domain;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailAddressTest {

    @Test
    public void create_mail_with_valid_values(){
        EmailAddress mail = new EmailAddress("dasdasdas@gmail.com");
        assertThat(mail).isEqualTo(new EmailAddress("dasdasdas@gmail.com"));
    }

    @Nested
    public class create_mail_invalid_values{
        @Test
        public void create_mail_with_null_and_empty_values(){
            assertThatThrownBy(() -> new EmailAddress(null)).isInstanceOf(EmailAddress.EmailAddressException.class).hasMessageContaining("null");
            assertThatThrownBy(() -> new EmailAddress("")).isInstanceOf(EmailAddress.EmailAddressException.class).hasMessageContaining("null");
        }
        @Test
        public void create_mail_with_invalid_values(){
            assertThatThrownBy(() -> new EmailAddress("teadsad")).isInstanceOf(EmailAddress.EmailAddressException.class).hasMessageContaining("invalid");
        }
    }


}