package spengergasse.at.sj2425scherzerrabar;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;

@Component
public class MongoDBIndexConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MongoDBIndexConfiguration.class);

    private final MongoTemplate mongoTemplate;

    public MongoDBIndexConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {
        log.info("Creating MongoDB indexes...");

        createAuthorIndexes();
        createBookIndexes();
        createBookEmbeddedIndexes();

        log.info("MongoDB indexes created successfully");
    }

    /**
     * Creates indexes for the Author collection
     */
    private void createAuthorIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(AuthorDocument.class);

        // Index on API Key (unique, most common query)
        indexOps.ensureIndex(new Index()
                .on("api_key", Sort.Direction.ASC)
                .unique()
                .named("idx_author_api_key"));

        // Index on Postgres ID for sync operations
        indexOps.ensureIndex(new Index()
                .on("postgres_id", Sort.Direction.ASC)
                .named("idx_author_postgres_id"));

        // Index on Penname (common query)
        indexOps.ensureIndex(new Index()
                .on("pen_name", Sort.Direction.ASC)
                .named("idx_author_penname"));

        // Index on Email (for lookups)
        indexOps.ensureIndex(new Index()
                .on("email", Sort.Direction.ASC)
                .named("idx_author_email"));

        // Compound index on first and last name (for name searches)
        indexOps.ensureIndex(new Index()
                .on("first_name", Sort.Direction.ASC)
                .on("last_name", Sort.Direction.ASC)
                .named("idx_author_fullname"));

        log.info("Created indexes for Author collection");
    }

    /**
     * Creates indexes for the Book collection (Referencing)
     */
    private void createBookIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(BookDocument.class);

        // Index on API Key (unique, most common query)
        indexOps.ensureIndex(new Index()
                .on("api_key", Sort.Direction.ASC)
                .unique()
                .named("idx_book_api_key"));

        // Index on Postgres ID for sync operations
        indexOps.ensureIndex(new Index()
                .on("postgres_id", Sort.Direction.ASC)
                .named("idx_book_postgres_id"));

        // Index on Name (for sorting and searching)
        indexOps.ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .named("idx_book_name"));

        // Index on Release Date (for date range queries and sorting)
        indexOps.ensureIndex(new Index()
                .on("release_date", Sort.Direction.DESC)
                .named("idx_book_release_date"));

        // Index on Author API Keys (for finding books by author)
        indexOps.ensureIndex(new Index()
                .on("author_api_keys", Sort.Direction.ASC)
                .named("idx_book_author_keys"));

        // Index on Genres (for filtering by genre)
        indexOps.ensureIndex(new Index()
                .on("genres", Sort.Direction.ASC)
                .named("idx_book_genres"));

        // Index on Book Types (for filtering by type)
        indexOps.ensureIndex(new Index()
                .on("book_types", Sort.Direction.ASC)
                .named("idx_book_types"));

        // Index on Available Online (for filtering)
        indexOps.ensureIndex(new Index()
                .on("available_online", Sort.Direction.ASC)
                .named("idx_book_available"));

        // Compound index for common query: available online + genre
        indexOps.ensureIndex(new Index()
                .on("available_online", Sort.Direction.ASC)
                .on("genres", Sort.Direction.ASC)
                .named("idx_book_available_genre"));

        // Text index on name and description for full-text search
        indexOps.ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .on("description", Sort.Direction.ASC)
                .named("idx_book_text_search"));

        log.info("Created indexes for Book collection (Referencing)");
    }

    /**
     * Creates indexes for the BookEmbedded collection (Embedding)
     */
    private void createBookEmbeddedIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(BookDocumentEmbedded.class);

        // Index on API Key (unique, most common query)
        indexOps.ensureIndex(new Index()
                .on("api_key", Sort.Direction.ASC)
                .unique()
                .named("idx_book_emb_api_key"));

        // Index on Postgres ID
        indexOps.ensureIndex(new Index()
                .on("postgres_id", Sort.Direction.ASC)
                .named("idx_book_emb_postgres_id"));

        // Index on Name
        indexOps.ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .named("idx_book_emb_name"));

        // Index on Release Date
        indexOps.ensureIndex(new Index()
                .on("release_date", Sort.Direction.DESC)
                .named("idx_book_emb_release_date"));

        // Index on Embedded Author API Key (for finding books by author)
        indexOps.ensureIndex(new Index()
                .on("authors.api_key", Sort.Direction.ASC)
                .named("idx_book_emb_author_apikey"));

        // Index on Embedded Author Penname (for finding books by penname)
        indexOps.ensureIndex(new Index()
                .on("authors.penname", Sort.Direction.ASC)
                .named("idx_book_emb_author_penname"));

        // Index on Genres
        indexOps.ensureIndex(new Index()
                .on("genres", Sort.Direction.ASC)
                .named("idx_book_emb_genres"));

        // Index on Book Types
        indexOps.ensureIndex(new Index()
                .on("book_types", Sort.Direction.ASC)
                .named("idx_book_emb_types"));

        // Index on Available Online
        indexOps.ensureIndex(new Index()
                .on("available_online", Sort.Direction.ASC)
                .named("idx_book_emb_available"));

        // Compound index on embedded author fields
        indexOps.ensureIndex(new Index()
                .on("authors.first_name", Sort.Direction.ASC)
                .on("authors.last_name", Sort.Direction.ASC)
                .named("idx_book_emb_author_name"));

        log.info("Created indexes for BookEmbedded collection (Embedding)");
    }

    /**
     * Method to drop all indexes (useful for testing)
     */
    public void dropAllIndexes() {
        log.warn("Dropping all MongoDB indexes...");

        mongoTemplate.indexOps(AuthorDocument.class).dropAllIndexes();
        mongoTemplate.indexOps(BookDocument.class).dropAllIndexes();
        mongoTemplate.indexOps(BookDocumentEmbedded.class).dropAllIndexes();

        log.info("All MongoDB indexes dropped");
    }
}