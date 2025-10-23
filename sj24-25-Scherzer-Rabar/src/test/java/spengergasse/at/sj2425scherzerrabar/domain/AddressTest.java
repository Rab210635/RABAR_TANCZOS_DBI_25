package spengergasse.at.sj2425scherzerrabar.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AddressTest {



    @Nested
    public class test_get_methods_address{
        Address address;
        @BeforeEach
        public void init() {
             address = FixturesFactory.libraryAddress();
        }
        @Test
        public void get_city_test(){
            assertThat(address.city()).isEqualTo("Vienna");
        }
        @Test
        public void get_house_nr_and_street_test(){
            assertThat(address.streetAndNumber()).isEqualTo("spengergasse 20");
        }
        @Test
        public void get_zip_test(){
            assertThat(address.zip()).isEqualTo(1050);
        }
    }

    @Nested
    public class test_address_values{

        @Test
        public void null_street_number_test(){
            assertThatThrownBy(() -> new Address(null,"Vienna",1010))
                    .isInstanceOf(Address.AddressException.class)
                    .hasMessageContaining("You have provided a null Value for a Address");
        }
        @Test
        public void empty_street_number_test(){
            assertThatThrownBy(() -> new Address(" ","Vienna",1010))
                    .isInstanceOf(Address.AddressException.class)
                    .hasMessageContaining("You have provided a null Value for a Address");
        }

        @Test
        public void null_city_test(){
            assertThatThrownBy(() -> new Address("12",null,1010))
                    .isInstanceOf(Address.AddressException.class)
                    .hasMessageContaining("You have provided a null Value for a Address");
        }

        @Test
        public void empty_city_test(){
            assertThatThrownBy(() -> new Address("12"," ",1010))
                    .isInstanceOf(Address.AddressException.class)
                    .hasMessageContaining("You have provided a null Value for a Address");
        }

        @Test
        public void null_zip_test(){
            assertThatThrownBy(() -> new Address("12","Vienna",null))
                    .isInstanceOf(Address.AddressException.class)
                    .hasMessageContaining("You have provided a null Value for a Address");
        }

        @ParameterizedTest
        @MethodSource
        void invalid_zip_test(Integer zip) {
            assertThatThrownBy(() -> new Address("12","Vienna",zip))
                    .isInstanceOf(Address.AddressException.class)
                    .hasMessageContaining("You have provided an invalid Value for a Address (12 Vienna %s)".formatted(zip));
        }
        static Stream<Arguments> invalid_zip_test() {
            return Stream.of(
                    Arguments.of(1009),
                    Arguments.of(1000)
            );
        }
    }
}