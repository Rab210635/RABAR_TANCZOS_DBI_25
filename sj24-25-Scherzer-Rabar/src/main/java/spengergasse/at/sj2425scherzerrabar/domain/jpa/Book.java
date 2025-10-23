package spengergasse.at.sj2425scherzerrabar.domain.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;
import spengergasse.at.sj2425scherzerrabar.persistence.converter.BookGenreConverter;
import spengergasse.at.sj2425scherzerrabar.persistence.converter.BookTypeConverter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "book")
public class Book {
    @EmbeddedId
    private BookId bookId;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "book_api_key"))
    private ApiKey bookApiKey;
    @NotNull
    private String name;
    private LocalDate releaseDate;
    @NotNull
    private Boolean availableOnline;
    @ElementCollection
    @JoinTable(name = "BookTypes", foreignKey = @ForeignKey(name = "FK_book_types_2_book"))
    @Column(columnDefinition = BookTypeConverter.COLUMN_DEFINITION)
    private List<BookType> bookTypes;
    @NotNull
    @Min(100)
    @Max(Integer.MAX_VALUE)
    private Integer wordCount;
    @ElementCollection
    @JoinTable(name = "genres_of_book", foreignKey = @ForeignKey(name = "FK_genres_2_book"))
    @Column(name = "genre_code", columnDefinition = BookGenreConverter.COLUMN_DEFINITION)
    private List<BookGenre> genres;
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST
    )
    @JoinTable(name = "authors_of_book", joinColumns = @JoinColumn(name = "book_id",
            foreignKey = @ForeignKey(name = "FK_books_2_authors")),
            inverseJoinColumns = @JoinColumn(name = "author_id",
                    foreignKey = @ForeignKey(name = "FK_authors_2_books"))
    )
    private List<Author> authors;

    public Book() {
        this.bookApiKey = new ApiKeyFactory().generate(30);
    }

    public Book(String name, LocalDate releaseDate, Boolean availableOnline, Integer wordCount, List<BookGenre> genres, List<Author> authors, List<BookType> bookTypes, String description) {
        this.bookApiKey = new ApiKeyFactory().generate(30);
        this.name = name;
        this.releaseDate = releaseDate;
        this.availableOnline = availableOnline;
        this.wordCount = wordCount;
        this.genres = genres;
        this.authors = authors;
        this.bookTypes = bookTypes;
        this.description = description;
    }


    public void addAuthor(Author a){
        this.authors.add(a);
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public ApiKey getBookApiKey() {
        return bookApiKey;
    }





    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    public record BookId (@GeneratedValue @NotNull Long id){}
    

}
