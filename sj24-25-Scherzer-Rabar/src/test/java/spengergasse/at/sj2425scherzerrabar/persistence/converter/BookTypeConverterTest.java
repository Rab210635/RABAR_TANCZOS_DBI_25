package spengergasse.at.sj2425scherzerrabar.persistence.converter;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookTypeConverterTest {

    @Nested
    public class  convert_enum_to_database {
        @Test
        void convert_null_class_to_database() {
            BookTypeConverter bookTypeConverter = new BookTypeConverter();

            assertThatThrownBy(()->bookTypeConverter.convertToDatabaseColumn(null))
                    .isInstanceOf(BookTypeConverter.BookTypeException.class)
                    .hasMessageContaining("The value provided is an invalid null");
        }

        @ParameterizedTest
        @MethodSource
        void convert_valid_class_to_database(BookType bookType, Character databaseColumn) {
            BookTypeConverter bookTypeConverter = new BookTypeConverter();
            Character convertedValue = bookTypeConverter.convertToDatabaseColumn(bookType);

            assertThat(convertedValue).isEqualTo(databaseColumn);
        }
        static Stream<Arguments> convert_valid_class_to_database() {
            return Stream.of(
            Arguments.of(BookType.HARDCOVER,'H'),
            Arguments.of(BookType.PAPERBACK,'P'),
            Arguments.of(BookType.EBOOK,'E')
            );
        }
    }

    @Nested
    public class  convert_database_to_enum {
        @Test
        void convert_null_database_to_class() {
            BookTypeConverter bookTypeConverter = new BookTypeConverter();

            assertThatThrownBy(()->bookTypeConverter.convertToEntityAttribute(null))
                    .isInstanceOf(BookTypeConverter.BookTypeException.class)
                    .hasMessageContaining("The value provided is an invalid null");
        }

        @Test
        void convert_invalid_database_to_class() {
            BookTypeConverter bookTypeConverter = new BookTypeConverter();

            assertThatThrownBy(()->bookTypeConverter.convertToEntityAttribute('q'))
                    .isInstanceOf(BookTypeConverter.BookTypeException.class)
                    .hasMessageContaining("The value provided is invalid: (q)");
        }

        @ParameterizedTest
        @MethodSource
        void convert_valid_database_to_class(Character databaseColumn, BookType bookType) {
            BookTypeConverter bookTypeConverter = new BookTypeConverter();
            BookType convertedValue = bookTypeConverter.convertToEntityAttribute(databaseColumn);

            assertThat(convertedValue).isEqualTo(bookType);
        }

        static Stream<Arguments> convert_valid_database_to_class() {
            return Stream.of(
                    Arguments.of('H',BookType.HARDCOVER),
                    Arguments.of('h',BookType.HARDCOVER),
                    Arguments.of('P',BookType.PAPERBACK),
                    Arguments.of('p',BookType.PAPERBACK),
                    Arguments.of('E',BookType.EBOOK),
                    Arguments.of('e',BookType.EBOOK)
            );
        }
    }
}