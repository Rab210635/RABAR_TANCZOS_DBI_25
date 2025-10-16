package spengergasse.at.sj2425scherzerrabar.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.persistence.converter.BookTypeConverter;

@MappedSuperclass
public class BookSpecification {
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_copies_2_publisher"))
    protected Publisher publisher;

    @NotNull
    @Column(columnDefinition = BookTypeConverter.COLUMN_DEFINITION)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_copy_2_book_type"))
    protected BookType bookType;

    @NotNull
    @Min(3)
    @Max(Integer.MAX_VALUE)
    protected Integer pageCount;

    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_copies_2_book"))
    protected Book book;


    public BookSpecification(Publisher publisher, BookType bookType, Integer pageCount, Book book) {
        this.publisher = publisher;
        this.bookType = bookType;
        this.pageCount = pageCount;
        this.book = book;
    }

    public BookSpecification() {

    }

    public @NotNull Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(@NotNull Publisher publisher) {
        this.publisher = publisher;
    }

    public @NotNull BookType getBookType() {
        return bookType;
    }

    public void setBookType(@NotNull BookType bookType) {
        this.bookType = bookType;
    }

    public @NotNull @Min(3) @Max(Integer.MAX_VALUE) Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(@NotNull @Min(3) @Max(Integer.MAX_VALUE) Integer pageCount) {
        this.pageCount = pageCount;
    }

    public @NotNull Book getBook() {
        return book;
    }

    public void setBook(@NotNull Book book) {
        this.book = book;
    }




}
