package spengergasse.at.sj2425scherzerrabar.presentation.www.books;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand2;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;


import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
public class CreateBookForm {

    public CreateBookForm() {

    }

    @NotNull
    private String name;
    private LocalDate releaseDate;
    @NotNull
    private Boolean availableOnline;
    private List<BookType> bookTypes;
    @NotNull
    @Min(100)
    @Max(Integer.MAX_VALUE)
    private Integer wordCount;
    private List<BookGenre> genres;
    private String description;
    private List<String> authors; //penname



    public BookCommand2 getBookCommand() {
        return new BookCommand2(
                null,
                name,
                releaseDate,
                availableOnline,
                bookTypes.stream().map(Enum::name).toList(),
                wordCount,
                description,
                authors,
                genres.stream().map(Enum::name).toList()

        );
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public @NotNull Boolean getAvailableOnline() {
        return availableOnline;
    }

    public void setAvailableOnline(@NotNull Boolean availableOnline) {
        this.availableOnline = availableOnline;
    }

    public List<BookType> getBookTypes() {
        return bookTypes;
    }

    public void setBookTypes(List<BookType> bookTypes) {
        this.bookTypes = bookTypes;
    }

    public @NotNull @Min(100) @Max(Integer.MAX_VALUE) Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(@NotNull @Min(100) @Max(Integer.MAX_VALUE) Integer wordCount) {
        this.wordCount = wordCount;
    }

    public List<BookGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<BookGenre> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
