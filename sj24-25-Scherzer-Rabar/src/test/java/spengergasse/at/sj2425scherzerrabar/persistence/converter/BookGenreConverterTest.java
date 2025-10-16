package spengergasse.at.sj2425scherzerrabar.persistence.converter;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookGenreConverterTest {

    @Nested
    public class convert_enum_to_database{
        @ParameterizedTest
        @MethodSource
        void convert_valid_class_to_database(String databaseValue, BookGenre bookGenre) {
            BookGenreConverter converter = new BookGenreConverter();
            String convertedValue = converter.convertToDatabaseColumn(bookGenre);
            assertThat(convertedValue).isEqualTo(databaseValue);
        }

        static Stream<Arguments> convert_valid_class_to_database(){
                return Stream.of(
                        Arguments.of("MY", BookGenre.MYSTERY),
                        Arguments.of("TH", BookGenre.THRILLER),
                        Arguments.of("CR", BookGenre.CRIME),
                        Arguments.of("RO", BookGenre.ROMANCE),
                        Arguments.of("FA", BookGenre.FANTASY),
                        Arguments.of("SF", BookGenre.SCIENCE_FICTION),
                        Arguments.of("HF", BookGenre.HISTORICAL_FICTION),
                        Arguments.of("CF", BookGenre.CONTEMPORARY_FICTION),
                        Arguments.of("YA", BookGenre.YOUNG_ADULT),
                        Arguments.of("BI", BookGenre.BIOGRAPHY),
                        Arguments.of("AU", BookGenre.AUTOBIOGRAPHY),
                        Arguments.of("ME", BookGenre.MEMOIR),
                        Arguments.of("SH", BookGenre.SELF_HELP),
                        Arguments.of("TC", BookGenre.TRUE_CRIME),
                        Arguments.of("HI", BookGenre.HISTORY),
                        Arguments.of("SC", BookGenre.SCIENCE),
                        Arguments.of("TE", BookGenre.TECHNOLOGY),
                        Arguments.of("PH", BookGenre.PHILOSOPHY),
                        Arguments.of("RE", BookGenre.RELIGION),
                        Arguments.of("SP", BookGenre.SPIRITUALITY),
                        Arguments.of("GN", BookGenre.GRAPHIC_NOVELS),
                        Arguments.of("CO", BookGenre.COMICS),
                        Arguments.of("PO", BookGenre.POETRY),
                        Arguments.of("HO", BookGenre.HORROR)
            );
        }
        @Test
        void convert_null_value_to_database(){
            BookGenreConverter converter = new BookGenreConverter();
            assertThatThrownBy(()->converter.convertToDatabaseColumn(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("BookGenre is null");
        }
    }

    @Nested
    public class convert_database_value_to_enum{
        @ParameterizedTest
        @MethodSource
        void convert_valid_database_values_to_class(String databaseValue, BookGenre genre) {
            BookGenreConverter converter = new BookGenreConverter();
            assertThat(converter.convertToEntityAttribute(databaseValue)).isEqualTo(genre);
        }
        static Stream<Arguments> convert_valid_database_values_to_class(){
            return Stream.of(
                    Arguments.of("co",BookGenre.COMICS),
                    Arguments.of("Co",BookGenre.COMICS),
                    Arguments.of("cO",BookGenre.COMICS),
                    Arguments.of("MY", BookGenre.MYSTERY),
                    Arguments.of("TH", BookGenre.THRILLER),
                    Arguments.of("CR", BookGenre.CRIME),
                    Arguments.of("RO", BookGenre.ROMANCE),
                    Arguments.of("FA", BookGenre.FANTASY),
                    Arguments.of("SF", BookGenre.SCIENCE_FICTION),
                    Arguments.of("HF", BookGenre.HISTORICAL_FICTION),
                    Arguments.of("CF", BookGenre.CONTEMPORARY_FICTION),
                    Arguments.of("YA", BookGenre.YOUNG_ADULT),
                    Arguments.of("BI", BookGenre.BIOGRAPHY),
                    Arguments.of("AU", BookGenre.AUTOBIOGRAPHY),
                    Arguments.of("ME", BookGenre.MEMOIR),
                    Arguments.of("SH", BookGenre.SELF_HELP),
                    Arguments.of("TC", BookGenre.TRUE_CRIME),
                    Arguments.of("HI", BookGenre.HISTORY),
                    Arguments.of("SC", BookGenre.SCIENCE),
                    Arguments.of("TE", BookGenre.TECHNOLOGY),
                    Arguments.of("PH", BookGenre.PHILOSOPHY),
                    Arguments.of("RE", BookGenre.RELIGION),
                    Arguments.of("SP", BookGenre.SPIRITUALITY),
                    Arguments.of("GN", BookGenre.GRAPHIC_NOVELS),
                    Arguments.of("CO", BookGenre.COMICS),
                    Arguments.of("PO", BookGenre.POETRY),
                    Arguments.of("HO", BookGenre.HORROR)
            );
        }


        @Test
        void convert_invalid_database_to_class() {
            String dbValue = "ABC";
            BookGenreConverter converter = new BookGenreConverter();
            assertThatThrownBy(()->converter.convertToEntityAttribute(dbValue))
                    .isInstanceOf(BookGenreConverter.BookGenreException.class)
                    .hasMessageContaining("provided is not valid");
        }

        @Test
        void convert_null_database_to_class(){
            String dbValue = null;
            BookGenreConverter converter = new BookGenreConverter();
            assertThatThrownBy(()->converter.convertToEntityAttribute(dbValue))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("is null");
        }
    }


}