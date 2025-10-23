//package spengergasse.at.sj2425scherzerrabar.domain.mongo;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import spengergasse.at.sj2425scherzerrabar.domain.*;
//import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
//import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
//
//import java.util.List;
//
//@Document(collection = "authors")
//public class AuthorDocument {
//    @Id
//    private String id;  // MongoDB ObjectId
//
//    private Long postgresId;  // Referenz zur JPA-ID
//    private String firstName;
//    private String lastName;
//    private String penname;
//    private String email;
//    private List<Address> addresses;  // Address funktioniert als POJO
//    private String apiKey;
//
//    // Konstruktoren, Getter, Setter...
//
//    public static AuthorDocument fromAuthor(Author author) {
//        AuthorDocument doc = new AuthorDocument();
//        if (author.getPersonId() != null) {
//            doc.postgresId = author.getPersonId().id();
//        }
//        doc.firstName = author.getFirstName();
//        doc.lastName = author.getLastName();
//        doc.penname = author.getPenname();
//        doc.email = author.getEmailAddress().email();
//        doc.addresses = author.getAddress();
//        doc.apiKey = author.getAuthorApiKey().apiKey();
//        return doc;
//    }
//}