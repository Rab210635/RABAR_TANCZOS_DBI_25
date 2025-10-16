package spengergasse.at.sj2425scherzerrabar.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;

@Converter(autoApply = true)
public class BookGenreConverter implements AttributeConverter<BookGenre, String> {

    static final String VALID_VALUES = "'MY','TH','CR','RO','FA','SF','HF','CF','YA','BI','AU','ME','SH','TC','HI','SC','TE','PH','RE','SP','GN','CO','PO','HO'";
    public static final String COLUMN_DEFINITION = "char(2) in (" + VALID_VALUES + ")";

    @Override
    public String convertToDatabaseColumn(BookGenre bookGenre) {

        return switch (bookGenre) {
            case MYSTERY -> "MY";
            case THRILLER -> "TH";
            case CRIME -> "CR";
            case ROMANCE -> "RO";
            case FANTASY -> "FA";
            case SCIENCE_FICTION -> "SF";
            case HISTORICAL_FICTION -> "HF";
            case CONTEMPORARY_FICTION -> "CF";
            case YOUNG_ADULT -> "YA";
            case BIOGRAPHY -> "BI";
            case AUTOBIOGRAPHY -> "AU";
            case MEMOIR -> "ME";
            case SELF_HELP -> "SH";
            case TRUE_CRIME -> "TC";
            case HISTORY -> "HI";
            case SCIENCE -> "SC";
            case TECHNOLOGY -> "TE";
            case PHILOSOPHY -> "PH";
            case RELIGION -> "RE";
            case SPIRITUALITY -> "SP";
            case GRAPHIC_NOVELS -> "GN";
            case COMICS -> "CO";
            case POETRY -> "PO";
            case HORROR -> "HO";
            case null -> throw new NullPointerException("BookGenre is null");
        };
    }

    @Override
    public BookGenre convertToEntityAttribute(String s) {
        if (s == null) throw new NullPointerException("BookGenre is null");

        return switch (s.toUpperCase()) {
            case "MY" -> BookGenre.MYSTERY;
            case "TH" -> BookGenre.THRILLER;
            case "CR" -> BookGenre.CRIME;
            case "RO" -> BookGenre.ROMANCE;
            case "FA" -> BookGenre.FANTASY;
            case "SF" -> BookGenre.SCIENCE_FICTION;
            case "HF" -> BookGenre.HISTORICAL_FICTION;
            case "CF" -> BookGenre.CONTEMPORARY_FICTION;
            case "YA" -> BookGenre.YOUNG_ADULT;
            case "BI" -> BookGenre.BIOGRAPHY;
            case "AU" -> BookGenre.AUTOBIOGRAPHY;
            case "ME" -> BookGenre.MEMOIR;
            case "SH" -> BookGenre.SELF_HELP;
            case "TC" -> BookGenre.TRUE_CRIME;
            case "HI" -> BookGenre.HISTORY;
            case "SC" -> BookGenre.SCIENCE;
            case "TE" -> BookGenre.TECHNOLOGY;
            case "PH" -> BookGenre.PHILOSOPHY;
            case "RE" -> BookGenre.RELIGION;
            case "SP" -> BookGenre.SPIRITUALITY;
            case "GN" -> BookGenre.GRAPHIC_NOVELS;
            case "CO" -> BookGenre.COMICS;
            case "PO" -> BookGenre.POETRY;
            case "HO" -> BookGenre.HORROR;
            default -> throw BookGenreException.withInvalidDatabaseValue(s);
        };
    }
    public static class BookGenreException extends RuntimeException {
        public BookGenreException(String message) {
            super(message);
        }
        public static BookGenreException withInvalidDatabaseValue(String value){
            String message = "The value provided is not valid: (%s)".formatted(value);
            return new BookGenreException(message);
        }
    }

}
