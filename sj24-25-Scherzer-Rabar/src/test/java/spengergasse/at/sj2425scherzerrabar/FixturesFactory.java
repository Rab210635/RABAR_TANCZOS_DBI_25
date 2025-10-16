package spengergasse.at.sj2425scherzerrabar;

import spengergasse.at.sj2425scherzerrabar.domain.*;

import java.time.LocalDate;
import java.util.List;

public class FixturesFactory {

    public static Address libraryAddress() {
        return new Address("spengergasse 20","Vienna",1050);
    }

    public static Address address2() {
        return new Address("Reumanplatz 66","Vienna",1100);
    }

    public static Library thalia(Address address, List<BookInLibraries> books) {
        return new Library("Thalia",address, books);
    }

    public static EmailAddress emailAddress() {
        return new EmailAddress("mail@mail.com");
    }

    public static Customer customer() {
        return new Customer("Max","Mustermann", emailAddress(),List.of(libraryAddress()));
    }

    public static BuyableBook buyableBook() {
        return new BuyableBook(publisher(address2()),BookType.EBOOK,500,book(author()),4.5f);
    }

    public static BuyableBook buyableBook(Author author) {
        return new BuyableBook(publisher(address2()),BookType.EBOOK,500,book(author),4.5f);
    }

    public static Author author() {
        return new Author("Max","Mustermann",List.of(address2()), emailAddress(), "dada");
    }

    public static Book book(Author author){
        return new Book("dasd",
                LocalDate.of(2000,1,1),
                true,
                1250,
                List.of(BookGenre.ROMANCE),
                List.of(author),
                List.of(BookType.EBOOK),
                "Eine Beschreibung");
    }

    public static BookInLibraries libBook(Book b){
        return new BookInLibraries(b,31);
    }

    public static LibrarySubscription thaliaAll(Library library) {
        return new LibrarySubscription("ThaliaAll",
                "Access to all Online Books of Thalia",
                100.0,library);
    }

    public static Review review(){
        return new Review("dasds",5,"dasdsa",customer(),book(author()),filiale(),publisher(address2()));
    }
    public static Review review(Author author){
        return new Review("dasds",5,"dasdsa",customer(),book(author),filiale(),publisher(address2()));
    }

    public static Publisher publisher(Address address) {
        return new Publisher("Dornbund",address);
    }

    public static Branch filiale() {
        return new Branch(thalia(address2(),List.of(libBook(book(author())))), address2());
    }

    public static LibrarySubscription librarySubscription() {
        return new LibrarySubscription("Premium","dadasdasdsa",25.5,thalia(address2(), List.of(libBook(book(author())))));
    }

    public static Order order(){
        return new Order(customer(), List.of(librarySubscription()) , LocalDate.of(2025,2,5), List.of(buyableBook()));
    }

    public static Order order(BuyableBook buyableBook){
        return new Order(customer(), List.of(librarySubscription()) , LocalDate.of(2025,2,5), List.of(buyableBook));
    }

    public static Copy copy(){
        return new Copy(publisher(address2()),BookType.PAPERBACK,244,book(author()),filiale());
    }

    public static Copy copy(Author author){
        return new Copy(publisher(address2()),BookType.PAPERBACK,244,book(author),filiale());
    }

    public static Borrowing borrowing(Customer customer, List<Copy> copies){
        return new Borrowing(customer,copies,LocalDate.of(2000,2,2),0);
    }

}
