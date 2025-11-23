package spengergasse.at.sj2425scherzerrabar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.stereotype.Service;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;

import java.util.List;

@Service
public class MongoIndexManager {

    private static final Logger log = LoggerFactory.getLogger(MongoIndexManager.class);
    private final MongoTemplate mongoTemplate;

    public MongoIndexManager(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Lists all indexes for all collections
     */
    public void printAllIndexes() {
        log.info("\n" + "=".repeat(80));
        log.info("MONGODB INDEXES OVERVIEW");
        log.info("=".repeat(80));

        printIndexesForCollection("Authors", AuthorDocument.class);
        printIndexesForCollection("Books (Referencing)", BookDocument.class);
        printIndexesForCollection("Books (Embedding)", BookDocumentEmbedded.class);

        log.info("=".repeat(80) + "\n");
    }

    /**
     * Prints indexes for a specific collection
     */
    private <T> void printIndexesForCollection(String collectionName, Class<T> entityClass) {
        log.info("\n{} Collection:", collectionName);
        log.info("-".repeat(80));

        List<IndexInfo> indexes = mongoTemplate.indexOps(entityClass).getIndexInfo();

        if (indexes.isEmpty()) {
            log.info("  No indexes found");
            return;
        }

        for (IndexInfo indexInfo : indexes) {
            log.info("  Index: {}", indexInfo.getName());
            log.info("    Fields: {}", indexInfo.getIndexFields());
            log.info("    Unique: {}", indexInfo.isUnique());
            log.info("    Sparse: {}", indexInfo.isSparse());

            if (indexInfo.getPartialFilterExpression() != null) {
                log.info("    Partial Filter: {}", indexInfo.getPartialFilterExpression());
            }
            log.info("");
        }
    }

    /**
     * Gets index statistics
     */
    public IndexStatistics getIndexStatistics(Class<?> entityClass) {
        List<IndexInfo> indexes = mongoTemplate.indexOps(entityClass).getIndexInfo();

        int totalIndexes = indexes.size();
        long uniqueIndexes = indexes.stream().filter(IndexInfo::isUnique).count();
        long sparseIndexes = indexes.stream().filter(IndexInfo::isSparse).count();

        return new IndexStatistics(
                entityClass.getSimpleName(),
                totalIndexes,
                (int) uniqueIndexes,
                (int) sparseIndexes
        );
    }

    /**
     * Gets statistics for all collections
     */
    public void printIndexStatistics() {
        log.info("\n" + "=".repeat(80));
        log.info("INDEX STATISTICS");
        log.info("=".repeat(80));

        IndexStatistics authorStats = getIndexStatistics(AuthorDocument.class);
        IndexStatistics bookStats = getIndexStatistics(BookDocument.class);
        IndexStatistics bookEmbStats = getIndexStatistics(BookDocumentEmbedded.class);

        log.info("\n{}", authorStats);
        log.info("{}", bookStats);
        log.info("{}", bookEmbStats);

        log.info("\nTotal Indexes: {}",
                authorStats.totalIndexes + bookStats.totalIndexes + bookEmbStats.totalIndexes);
        log.info("=".repeat(80) + "\n");
    }

    /**
     * Drops all indexes for a collection (except _id)
     */
    public void dropAllIndexesForCollection(Class<?> entityClass) {
        String collectionName = entityClass.getSimpleName();
        log.warn("Dropping all indexes for collection: {}", collectionName);

        mongoTemplate.indexOps(entityClass).dropAllIndexes();

        log.info("Indexes dropped for {}", collectionName);
    }

    /**
     * Drops a specific index
     */
    public void dropIndex(Class<?> entityClass, String indexName) {
        log.warn("Dropping index '{}' from collection: {}", indexName, entityClass.getSimpleName());

        mongoTemplate.indexOps(entityClass).dropIndex(indexName);

        log.info("Index '{}' dropped", indexName);
    }

    /**
     * Checks if an index exists
     */
    public boolean indexExists(Class<?> entityClass, String indexName) {
        List<IndexInfo> indexes = mongoTemplate.indexOps(entityClass).getIndexInfo();
        return indexes.stream().anyMatch(idx -> idx.getName().equals(indexName));
    }

    /**
     * Gets detailed information about a specific index
     */
    public IndexInfo getIndexInfo(Class<?> entityClass, String indexName) {
        List<IndexInfo> indexes = mongoTemplate.indexOps(entityClass).getIndexInfo();
        return indexes.stream()
                .filter(idx -> idx.getName().equals(indexName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Compares indexes between collections
     */
    public void compareIndexes() {
        log.info("\n" + "=".repeat(80));
        log.info("INDEX COMPARISON");
        log.info("=".repeat(80));

        List<IndexInfo> bookRefIndexes = mongoTemplate.indexOps(BookDocument.class).getIndexInfo();
        List<IndexInfo> bookEmbIndexes = mongoTemplate.indexOps(BookDocumentEmbedded.class).getIndexInfo();

        log.info("\nBooks (Referencing) has {} indexes", bookRefIndexes.size());
        log.info("Books (Embedding) has {} indexes", bookEmbIndexes.size());

        log.info("\nIndexes only in Referencing:");
        bookRefIndexes.forEach(refIdx -> {
            boolean existsInEmb = bookEmbIndexes.stream()
                    .anyMatch(embIdx -> embIdx.getName().equals(refIdx.getName()));
            if (!existsInEmb) {
                log.info("  - {}", refIdx.getName());
            }
        });

        log.info("\nIndexes only in Embedding:");
        bookEmbIndexes.forEach(embIdx -> {
            boolean existsInRef = bookRefIndexes.stream()
                    .anyMatch(refIdx -> refIdx.getName().equals(embIdx.getName()));
            if (!existsInRef) {
                log.info("  - {}", embIdx.getName());
            }
        });

        log.info("=".repeat(80) + "\n");
    }

    /**
     * Statistics class
     */
    public static class IndexStatistics {
        private final String collection;
        private final int totalIndexes;
        private final int uniqueIndexes;
        private final int sparseIndexes;

        public IndexStatistics(String collection, int totalIndexes, int uniqueIndexes, int sparseIndexes) {
            this.collection = collection;
            this.totalIndexes = totalIndexes;
            this.uniqueIndexes = uniqueIndexes;
            this.sparseIndexes = sparseIndexes;
        }

        @Override
        public String toString() {
            return String.format("%-30s | Total: %2d | Unique: %2d | Sparse: %2d",
                    collection, totalIndexes, uniqueIndexes, sparseIndexes);
        }

        public String getCollection() {
            return collection;
        }

        public int getTotalIndexes() {
            return totalIndexes;
        }

        public int getUniqueIndexes() {
            return uniqueIndexes;
        }

        public int getSparseIndexes() {
            return sparseIndexes;
        }
    }
}