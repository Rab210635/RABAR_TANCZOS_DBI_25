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