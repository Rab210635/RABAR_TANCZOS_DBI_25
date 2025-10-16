package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

@Entity
@Table(name = "copy")
public class Copy extends BookSpecification {
    @EmbeddedId
    private CopyId copyId;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "copy_api_key"))
    private ApiKey copyApiKey;



    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "branch_id",foreignKey = @ForeignKey(name = "FK_copy_2_branch"))
    private Branch inBranch;

    public Copy(Publisher publisher, BookType bookType, Integer pageCount, Book book, Branch inBranch) {
        super(publisher,bookType,pageCount,book);
        this.copyApiKey = new ApiKeyFactory().generate(30);
        this.inBranch = inBranch;
    }



    public Copy() {
        this.copyApiKey = new ApiKeyFactory().generate(30);
    }

    public ApiKey getCopyApiKey() {
        return copyApiKey;
    }



    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Branch getInBranch() {
        return inBranch;
    }

    public void setInBranch(Branch inBranch) {
        this.inBranch = inBranch;
    }

    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record CopyId (@GeneratedValue @NotNull Long id){}
}
