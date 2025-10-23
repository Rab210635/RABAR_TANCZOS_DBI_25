package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;

@Data
@Embeddable
public class BookInLibraries {
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "books_in_libraries_2_book"))
    private Book book;
    @NotNull
    @Min(0)
    @Max(365)
    private Integer borrowLengthDays;

    public BookInLibraries(Book book, Integer borrowLengthDays) {
        this.book = book;
        this.borrowLengthDays = borrowLengthDays;
    }
    public  BookInLibraries(){}

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getBorrowLengthDays() {
        return borrowLengthDays;
    }

    public void setBorrowLengthDays(Integer borrowLengthDays) {
        this.borrowLengthDays = borrowLengthDays;
    }
}
