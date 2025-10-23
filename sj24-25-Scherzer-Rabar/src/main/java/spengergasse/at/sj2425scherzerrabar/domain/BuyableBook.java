package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

@Entity
@Table(name = "buyableBook")
public class BuyableBook extends BookSpecification {
        @EmbeddedId
        private BuyableBookId buyableBookId;
        @Embedded
        @AttributeOverride(name = "apiKey", column = @Column(name = "buyablebook_api_key"))
        private ApiKey buyableBookApiKey;
        @Max(Integer.MAX_VALUE)
        @Min(1)
        private Float price;


        @SuppressWarnings("JpaObjectClassSignatureInspection")
        @Embeddable
        record BuyableBookId(@GeneratedValue @NotNull Long buyableBookId) {}

        public BuyableBook(){
                this.buyableBookApiKey = new ApiKeyFactory().generate(30);
        }
        public BuyableBook(Publisher publisher, BookType bookType, Integer pageCount, Book book, Float price) {
                super(publisher, bookType, pageCount, book);
                this.price = price;
                this.buyableBookApiKey = new ApiKeyFactory().generate(30);
                this.book = book;
        }

        public Float getPrice() {
                return price;
        }

        public void setPrice(Float price) {
                this.price = price;
        }

        public ApiKey getBuyableBookApiKey() {
                return buyableBookApiKey;
        }

}
