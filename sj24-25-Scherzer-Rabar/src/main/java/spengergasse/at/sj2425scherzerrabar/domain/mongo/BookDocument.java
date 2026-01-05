package spengergasse.at.sj2425scherzerrabar.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "books")
public class BookDocument {

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

    @Field("author_api_keys")
    private List<String> authorApiKeys = new ArrayList<>();
    private List<String> authorIds;

    public BookDocument() {
    }

    public BookDocument(Long postgresId, String name, LocalDate releaseDate,
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

    public List<String> getAuthorApiKeys() {
        return authorApiKeys;
    }

    public void setAuthorApiKeys(List<String> authorApiKeys) {
        this.authorApiKeys = authorApiKeys;
    }

    public List<String> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }
}