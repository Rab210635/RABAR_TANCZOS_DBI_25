package spengergasse.at.sj2425scherzerrabar.dtos;

import spengergasse.at.sj2425scherzerrabar.domain.Author;
import spengergasse.at.sj2425scherzerrabar.domain.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record BookDto2(String apiKey, String name, LocalDate releaseDate,
                       Boolean availableOnline, List<String> types, Integer wordCount,
                       String description, List<String> authorPennames, List<String> genres)
{
    public BookDto2(Book b){
        this(b.getBookApiKey().apiKey(),b.getName(),b.getReleaseDate(),b.getAvailableOnline()
                ,b.getBookTypes().stream().map(Enum::name).collect(Collectors.toList())
                ,b.getWordCount(), b.getDescription(),
                b.getAuthors().stream().map(Author::getPenname).collect(Collectors.toList()),
                b.getGenres().stream().map(Enum::name).collect(Collectors.toList()));
    }

    public static BookDto2 bookDtoFromBook(Book book) {
        return new BookDto2(
          book.getBookApiKey().apiKey(),book.getName(),book.getReleaseDate(),book.getAvailableOnline(),
                book.getBookTypes().stream().map(Enum::name).toList(), book.getWordCount(),book.getDescription(),
                book.getAuthors().stream().map(Author::getPenname).toList(),
                book.getGenres().stream().map(Enum::name).toList()
        );
    }
}
