package spengergasse.at.sj2425scherzerrabar.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;

@Converter(autoApply = true)
public class BookTypeConverter implements AttributeConverter<BookType, Character>   {
    static final String VALID_VALUES = "'H','P','E'";
    public static final String COLUMN_DEFINITION = "enum ( "+ VALID_VALUES+")";
    
    @Override
    public Character convertToDatabaseColumn(BookType bookType) {

        return switch (bookType) {
            case HARDCOVER -> 'H';
            case PAPERBACK -> 'P';
            case EBOOK -> 'E';
            case null -> throw BookTypeException.forNullValues();
        };
    }

    @Override
    public BookType convertToEntityAttribute(Character dbData) {
        return switch (dbData) {
            case 'H', 'h' -> BookType.HARDCOVER;
            case 'P', 'p' -> BookType.PAPERBACK;
            case 'E', 'e' -> BookType.EBOOK;
            case null -> throw BookTypeException.forNullValues();
            default -> throw BookTypeException.forInvalidDatabaseValues(dbData);
        };
    }
    public static class BookTypeException extends RuntimeException {

        public BookTypeException(String message) {
            super(message);
        }
        static BookTypeException forInvalidDatabaseValues(Character bookType) {
            String message = "The value provided is invalid: (%c)".formatted(bookType);
            return new BookTypeException(message);
        }
        static BookTypeException forNullValues() {
            String message = "The value provided is an invalid null";
            return new BookTypeException(message);
        }
    }
}
