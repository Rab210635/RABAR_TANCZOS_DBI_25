package spengergasse.at.sj2425scherzerrabar.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.BookGenre;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.domain.EmailAddress;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.AuthorDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocument;
import spengergasse.at.sj2425scherzerrabar.domain.mongo.BookDocumentEmbedded;
import spengergasse.at.sj2425scherzerrabar.persistence.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceComparisonService {

    private final AuthorRepository jpaAuthorRepo;
    private final BookRepository jpaBookRepo;
    private final BookEmbeddedMongoRepository mongoEmbeddedRepo;
    private final AuthorMongoRepository mongoAuthorRepo;
    private final MongoTemplate mongoTemplate;
    private final TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public String runComparisonTable() {
        List<Integer> sizes = List.of(100, 1000, 10000, 100000);
        StringBuilder fullOutput = new StringBuilder();

        String headerRow = String.format("| %-13s | %-6s | %-9s | %-9s | %-9s | %-9s | %-9s | %-9s |",
                "Operation", "Size", "PG(No)", "PG(Idx)", "Emb(No)", "Emb(Idx)", "Ref(No)", "Ref(Idx)");
        String separatorRow = "|---------------|--------|-----------|-----------|-----------|-----------|-----------|-----------|";

        for (int size : sizes) {
            String block = runForSize(size, headerRow, separatorRow);
            log.info("\n" + block);
            fullOutput.append(block).append("\n");
        }

        return fullOutput.toString();
    }

    private String runForSize(int numberOfBooks, String headerRow, String separatorRow) {
        StringBuilder sb = new StringBuilder();
        log.info("Starting run for size: {}", numberOfBooks);

        // Header for this block
        sb.append(separatorRow).append("\n");
        sb.append(headerRow).append("\n");
        sb.append(separatorRow).append("\n");

        // 1. Run WITHOUT Indexes
        cleanup();
        dropIndexes();
        warmupJvm();
        Map<String, List<Double>> resultsNoIdx = runWorkload(numberOfBooks);

        // 2. Run WITH Indexes
        cleanup();
        createIndexes();
        warmupJvm();
        Map<String, List<Double>> resultsIdx = runWorkload(numberOfBooks);

        // 3. Print merged rows
        printMergedRow(sb, "INSERT", numberOfBooks, resultsNoIdx, resultsIdx);
        printMergedRow(sb, "READ (Simp)", numberOfBooks, resultsNoIdx, resultsIdx);
        printMergedRow(sb, "READ (Join)", numberOfBooks, resultsNoIdx, resultsIdx);
        printMergedRow(sb, "UPDATE (Sngl)", numberOfBooks, resultsNoIdx, resultsIdx);
        printMergedRow(sb, "UPDATE (Mass)", numberOfBooks, resultsNoIdx, resultsIdx);
        printMergedRow(sb, "DELETE", numberOfBooks, resultsNoIdx, resultsIdx);

        sb.append(separatorRow).append("\n");
        return sb.toString();
    }

    private Map<String, List<Double>> runWorkload(int numberOfBooks) {
        Map<String, List<Double>> results = new HashMap<>();

        // --- PREPARE DATA ---
        List<Author> authors = generateAuthors(Math.max(10, numberOfBooks / 10));
        List<Book> books = generateBooks(numberOfBooks, authors);
        List<BookDocumentEmbedded> mongoEmbeddedBooks = convertToEmbedded(books);
        List<AuthorDocument> mongoAuthors = convertToMongoAuthors(authors);
        assignMongoIds(mongoAuthors);
        List<BookDocument> mongoRefBooks = convertToRefBooks(books, authors, mongoAuthors);

        // Scenario 1: Rare Author
        Author rareAuthor = authors.get(authors.size() - 1);
        String rareApiKey = rareAuthor.getAuthorApiKey().toString();
        String rareMongoId = mongoAuthors.stream().filter(a -> a.getApiKey().equals(rareApiKey)).findFirst().map(AuthorDocument::getId).orElseThrow();

        // Scenario 2: Popular Author
        Author popularAuthor = authors.get(0);
        String popApiKey = popularAuthor.getAuthorApiKey().toString();
        String popMongoId = mongoAuthors.stream().filter(a -> a.getApiKey().equals(popApiKey)).findFirst().map(AuthorDocument::getId).orElseThrow();

        // Unique Names for Updates
        String newNameRare = "Rare " + UUID.randomUUID();
        String newNameMass = "Pop " + UUID.randomUUID();

        // --- 1. INSERT ---
        double tJpaIns = measure(() -> transactionTemplate.executeWithoutResult(s -> {
            jpaAuthorRepo.saveAll(authors);
            jpaBookRepo.saveAll(books);
        }));
        double tEmIns = measure(() -> mongoEmbeddedRepo.saveAll(mongoEmbeddedBooks));
        double tRefIns = measure(() -> {
            mongoAuthorRepo.saveAll(mongoAuthors);
            mongoTemplate.insertAll(mongoRefBooks);
        });
        results.put("INSERT", List.of(tJpaIns, tEmIns, tRefIns));

        // --- 2. READ (Simple) ---
        double tJpaReadSim = measureAvg(() -> jpaAuthorRepo.findAuthorByAuthorApiKey(new ApiKey(rareApiKey)));
        double tEmReadSim = measureAvg(() -> mongoEmbeddedRepo.findByAuthorApiKey(rareApiKey));
        double tRefReadSim = measureAvg(() -> mongoAuthorRepo.findById(rareMongoId));
        results.put("READ (Simp)", List.of(tJpaReadSim, tEmReadSim, tRefReadSim));

        // --- 3. READ (Join) ---
        double tJpaReadJoin = measureAvg(() -> jpaAuthorRepo.findAuthorWithBooks(new ApiKey(rareApiKey)));
        double tEmReadJoin = measureAvg(() -> mongoEmbeddedRepo.findByAuthorApiKey(rareApiKey));
        double tRefReadJoin = measureAvg(() -> {
            AuthorDocument ad = mongoAuthorRepo.findById(rareMongoId).orElse(null);
            if (ad != null) {
                Query q = new Query(Criteria.where("authorIds").is(rareMongoId));
                mongoTemplate.find(q, BookDocument.class).size();
            }
        });
        results.put("READ (Join)", List.of(tJpaReadJoin, tEmReadJoin, tRefReadJoin));

        // --- 4. UPDATE (Single) ---
        double tJpaUpS = measure(() -> {
            Author a = jpaAuthorRepo.findAuthorByAuthorApiKey(new ApiKey(rareApiKey)).orElseThrow();
            a.setPenname(newNameRare);
            jpaAuthorRepo.save(a);
        });
        double tEmUpS = measure(() -> {
            Query query = new Query(Criteria.where("authors.api_key").is(rareApiKey));
            Update update = new Update().set("authors.$.penname", newNameRare);
            mongoTemplate.updateMulti(query, update, BookDocumentEmbedded.class);
        });
        double tRefUpS = measure(() -> {
            AuthorDocument ad = mongoAuthorRepo.findById(rareMongoId).orElseThrow();
            ad.setPenname(newNameRare);
            mongoAuthorRepo.save(ad);
        });
        results.put("UPDATE (Sngl)", List.of(tJpaUpS, tEmUpS, tRefUpS));

        // --- 5. UPDATE (Mass) ---
        double tJpaUpM = measure(() -> {
            Author a = jpaAuthorRepo.findAuthorByAuthorApiKey(new ApiKey(popApiKey)).orElseThrow();
            a.setPenname(newNameMass);
            jpaAuthorRepo.save(a);
        });
        double tEmUpM = measure(() -> {
            Query query = new Query(Criteria.where("authors.api_key").is(popApiKey));
            Update update = new Update().set("authors.$.penname", newNameMass);
            mongoTemplate.updateMulti(query, update, BookDocumentEmbedded.class);
        });
        double tRefUpM = measure(() -> {
            AuthorDocument ad = mongoAuthorRepo.findById(popMongoId).orElseThrow();
            ad.setPenname(newNameMass);
            mongoAuthorRepo.save(ad);
        });
        results.put("UPDATE (Mass)", List.of(tJpaUpM, tEmUpM, tRefUpM));

        // --- 6. DELETE ---
        double tJpaDel = measure(() -> {
            jpaBookRepo.deleteAll();
            jpaAuthorRepo.deleteAll();
        });
        double tEmDel = measure(() -> mongoEmbeddedRepo.deleteAll());
        double tRefDel = measure(() -> {
            mongoTemplate.remove(new Query(), BookDocument.class);
            mongoAuthorRepo.deleteAll();
        });
        results.put("DELETE", List.of(tJpaDel, tEmDel, tRefDel));

        return results;
    }

    // --- MEASUREMENT HELPERS ---

    private double measure(Runnable r) {
        long start = System.nanoTime();
        try { r.run(); } catch (Exception e) { log.error("Error", e); }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private double measureAvg(Runnable r) {
        int iterations = 50;
        int warmup = 10;
        for (int i = 0; i < warmup; i++) {
            try { r.run(); } catch (Exception e) {}
        }
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try { r.run(); } catch (Exception e) { log.error("Error", e); }
        }
        long totalNs = System.nanoTime() - start;
        return (totalNs / iterations) / 1_000_000.0;
    }

    private void warmupJvm() {
        try { Thread.sleep(50); } catch (Exception e) {}
    }

    // --- DB MANAGEMENT ---

    private void createIndexes() {
        TransactionTemplate txNew = new TransactionTemplate(transactionTemplate.getTransactionManager());
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            txNew.executeWithoutResult(s -> {
                try { entityManager.createNativeQuery("CREATE INDEX idx_author_apikey ON author (author_api_key)").executeUpdate(); } catch (Exception ignored) {}
                try { entityManager.createNativeQuery("CREATE INDEX idx_join_auth ON authors_of_book (author_id)").executeUpdate(); } catch (Exception ignored) {}
            });
            try {
                mongoTemplate.indexOps(BookDocumentEmbedded.class).ensureIndex(new Index().on("authors.api_key", Sort.Direction.ASC));
                mongoTemplate.indexOps(BookDocument.class).ensureIndex(new Index().on("authorIds", Sort.Direction.ASC));
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private void dropIndexes() {
        TransactionTemplate txNew = new TransactionTemplate(transactionTemplate.getTransactionManager());
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            txNew.executeWithoutResult(s -> {
                try { entityManager.createNativeQuery("DROP INDEX IF EXISTS idx_author_apikey").executeUpdate(); } catch(Exception ignored){}
                try { entityManager.createNativeQuery("DROP INDEX IF EXISTS idx_join_auth").executeUpdate(); } catch(Exception ignored){}
            });
            try {
                mongoTemplate.indexOps(BookDocumentEmbedded.class).dropAllIndexes();
                mongoTemplate.indexOps(BookDocument.class).dropAllIndexes();
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private void cleanup() {
        try {
            mongoEmbeddedRepo.deleteAll();
            mongoAuthorRepo.deleteAll();
            mongoTemplate.remove(new Query(), BookDocument.class);
            jpaBookRepo.deleteAll();
            jpaAuthorRepo.deleteAll();
        } catch (Exception ignored) {}
    }

    private void printMergedRow(StringBuilder sb, String op, int size, Map<String, List<Double>> noIdx, Map<String, List<Double>> idx) {
        List<Double> tNo = noIdx.getOrDefault(op, List.of(-1.0, -1.0, -1.0));
        List<Double> tYes = idx.getOrDefault(op, List.of(-1.0, -1.0, -1.0));

        // Uses %9.2f for double output with 2 decimal places to avoid 0
        sb.append(String.format("| %-13s | %-6d | %9.2f | %9.2f | %9.2f | %9.2f | %9.2f | %9.2f |\n",
                op, size,
                tNo.get(0), tYes.get(0),
                tNo.get(1), tYes.get(1),
                tNo.get(2), tYes.get(2)));
    }

    // --- DATA GENERATION HELPERS ---

    private void assignMongoIds(List<AuthorDocument> authors) {
        for (AuthorDocument a : authors) {
            if(a.getId() == null) a.setId(UUID.randomUUID().toString());
        }
    }

    private List<AuthorDocument> convertToMongoAuthors(List<Author> authors) {
        return authors.stream().map(a -> new AuthorDocument(null, a.getFirstName(), a.getLastName(), a.getPenname(), a.getEmailAddress().toString(), a.getAuthorApiKey().toString())).collect(Collectors.toList());
    }

    private List<BookDocumentEmbedded> convertToEmbedded(List<Book> books) {
        return books.stream().map(b -> {
            BookDocumentEmbedded doc = new BookDocumentEmbedded(null, b.getName(), b.getReleaseDate(), b.getAvailableOnline(), b.getWordCount(), b.getDescription(), b.getBookApiKey().toString());
            doc.setGenres(b.getGenres().stream().map(Enum::name).toList());
            doc.setBookTypes(b.getBookTypes().stream().map(Enum::name).toList());
            doc.setAuthors(b.getAuthors().stream().map(a -> new BookDocumentEmbedded.EmbeddedAuthor(a.getAuthorApiKey().toString(), a.getPenname(), a.getFirstName(), a.getLastName(), a.getEmailAddress().toString())).toList());
            return doc;
        }).collect(Collectors.toList());
    }

    private List<BookDocument> convertToRefBooks(List<Book> books, List<Author> jpaAuthors, List<AuthorDocument> mongoAuthors) {
        Map<String, String> keyToIdMap = mongoAuthors.stream().collect(Collectors.toMap(AuthorDocument::getApiKey, AuthorDocument::getId));
        return books.stream().map(b -> {
            BookDocument doc = new BookDocument();
            doc.setName(b.getName());
            doc.setReleaseDate(b.getReleaseDate());
            doc.setAvailableOnline(b.getAvailableOnline());
            doc.setWordCount(b.getWordCount());
            doc.setDescription(b.getDescription());
            doc.setApiKey(b.getBookApiKey().toString());
            doc.setGenres(b.getGenres().stream().map(Enum::name).toList());
            doc.setBookTypes(b.getBookTypes().stream().map(Enum::name).toList());
            doc.setAuthorIds(b.getAuthors().stream().map(a -> keyToIdMap.get(a.getAuthorApiKey().toString())).filter(Objects::nonNull).collect(Collectors.toList()));
            return doc;
        }).collect(Collectors.toList());
    }

    private List<Author> generateAuthors(int count) {
        return IntStream.range(0, count).mapToObj(i -> new Author("First" + i, "Last" + i, new ArrayList<>(), new EmailAddress("auth" + i + "@test.com"), "Penname " + i)).collect(Collectors.toList());
    }

    private List<Book> generateBooks(int count, List<Author> authors) {
        Random random = new Random();
        Author popularAuthor = authors.get(0);
        return IntStream.range(0, count).mapToObj(i -> {
            Book b = new Book("Book " + i, LocalDate.now(), true, 1000, List.of(BookGenre.SCIENCE_FICTION), new ArrayList<>(), List.of(BookType.HARDCOVER), "Desc");
            if (i % 2 == 0) b.addAuthor(popularAuthor);
            else {
                int n = random.nextInt(3) + 1;
                for (int k = 0; k < n; k++) b.addAuthor(authors.get(random.nextInt(authors.size())));
            }
            return b;
        }).collect(Collectors.toList());
    }
}