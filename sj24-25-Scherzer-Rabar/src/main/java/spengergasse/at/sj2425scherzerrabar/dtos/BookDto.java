package spengergasse.at.sj2425scherzerrabar.dtos;

import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record BookDto(String apiKey, String name, LocalDate releaseDate,
                      Boolean availableOnline, List<String> types, Integer wordCount,
                      String description, List<String> authorIds, List<String> genres)
{
    public BookDto(Book b){
        this(b.getBookApiKey().apiKey(),b.getName(),b.getReleaseDate(),b.getAvailableOnline()
                ,b.getBookTypes().stream().map(Enum::name).collect(Collectors.toList())
                ,b.getWordCount(), b.getDescription(),
                b.getAuthors().stream().map(s -> s.getAuthorApiKey().apiKey()).collect(Collectors.toList()),
                b.getGenres().stream().map(Enum::name).collect(Collectors.toList()));
    }

    public static BookDto bookDtoFromBook(Book book) {
        return new BookDto(
          book.getBookApiKey().apiKey(),book.getName(),book.getReleaseDate(),book.getAvailableOnline(),
                book.getBookTypes().stream().map(Enum::name).toList(), book.getWordCount(),book.getDescription(),
                book.getAuthors().stream().map(author -> author.getAuthorApiKey().apiKey()).toList(),
                book.getGenres().stream().map(Enum::name).toList()
        );
    }
}
