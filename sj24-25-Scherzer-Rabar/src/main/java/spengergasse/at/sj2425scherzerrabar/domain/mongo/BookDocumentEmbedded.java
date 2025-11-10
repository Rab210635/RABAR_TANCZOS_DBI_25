package spengergasse.at.sj2425scherzerrabar.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB Document für Books mit EMBEDDED Authors
 * (Im Gegensatz zu BookDocument, das nur Author-API-Keys speichert)
 */
@Document(collection = "books_with_embedded_authors")
public class BookDocumentEmbedded {

    @Id
    private String id;

    @Field("postgres_id")
    private Long postgresId;

    @Field("name")
    private String name;

    @Field("release_date")
    private LocalDate releaseDate;

    @Field("available_online")
    private Boolean availableOnline;

    @Field("word_count")
    private Integer wordCount;

    @Field("description")
    private String description;

    @Field("api_key")
    private String apiKey;

    @Field("book_types")
    private List<String> bookTypes = new ArrayList<>();

    @Field("genres")
    private List<String> genres = new ArrayList<>();

    // EMBEDDED AUTHORS statt nur API-Keys!
    @Field("authors")
    private List<EmbeddedAuthor> authors = new ArrayList<>();

    public BookDocumentEmbedded() {
    }

    public BookDocumentEmbedded(Long postgresId, String name, LocalDate releaseDate,
                                Boolean availableOnline, Integer wordCount, String description,
                                String apiKey) {
        this.postgresId = postgresId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.availableOnline = availableOnline;
        this.wordCount = wordCount;
        this.description = description;
        this.apiKey = apiKey;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPostgresId() {
        return postgresId;
    }

    public void setPostgresId(Long postgresId) {
        this.postgresId = postgresId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Boolean getAvailableOnline() {
        return availableOnline;
    }

    public void setAvailableOnline(Boolean availableOnline) {
        this.availableOnline = availableOnline;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<String> getBookTypes() {
        return bookTypes;
    }

    public void setBookTypes(List<String> bookTypes) {
        this.bookTypes = bookTypes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<EmbeddedAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<EmbeddedAuthor> authors) {
        this.authors = authors;
    }

    /**
     * Embedded Author für MongoDB
     * Enthält alle Author-Daten direkt im Book-Document
     */
    public static class EmbeddedAuthor {
        @Field("api_key")
        private String apiKey;

        @Field("penname")
        private String penname;

        @Field("first_name")
        private String firstName;

        @Field("last_name")
        private String lastName;

        @Field("email")
        private String email;

        @Field("addresses")
        private List<AuthorDocument.AddressMongo> addresses = new ArrayList<>();

        public EmbeddedAuthor() {
        }

        public EmbeddedAuthor(String apiKey, String penname, String firstName,
                              String lastName, String email) {
            this.apiKey = apiKey;
            this.penname = penname;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        // Getter und Setter
        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getPenname() {
            return penname;
        }

        public void setPenname(String penname) {
            this.penname = penname;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<AuthorDocument.AddressMongo> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<AuthorDocument.AddressMongo> addresses) {
            this.addresses = addresses;
        }
    }
}