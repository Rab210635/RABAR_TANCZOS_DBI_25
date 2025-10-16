package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

import java.util.List;

@Entity
@Table(name = "library")
public class Library {
    @EmbeddedId
    private LibraryId libraryId;
    @NotNull
    private String name;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "library_api_key"))
    private ApiKey libraryApiKey;

    @NotNull
    @Embedded
    private Address headquarters;

    @ElementCollection
    @JoinTable(name = "books_in_library",
            joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_books_in_libraries_2_library")))
    private List<BookInLibraries> booksInLibraries;

    public Library() {
        this.libraryApiKey = new ApiKeyFactory().generate(30);

    }

    public Library( String name, Address headquarters, List<BookInLibraries> booksInLibraries) {
        this.libraryApiKey = new ApiKeyFactory().generate(30);
        this.name = name;
        this.headquarters = headquarters;
        this.booksInLibraries = booksInLibraries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiKey getLibraryApiKey() {
        return libraryApiKey;
    }


    public Address getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(Address headquarters) {
        this.headquarters = headquarters;
    }

    public List<BookInLibraries> getBooksInLibraries() {
        return booksInLibraries;
    }

    public void setBooksInLibraries(List<BookInLibraries> booksInLibraries) {
        this.booksInLibraries = booksInLibraries;
    }

    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record LibraryId (@GeneratedValue @NotNull Long id){}

}
