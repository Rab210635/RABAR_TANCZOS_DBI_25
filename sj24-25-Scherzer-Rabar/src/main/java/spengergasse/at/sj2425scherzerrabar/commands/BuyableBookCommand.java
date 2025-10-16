package spengergasse.at.sj2425scherzerrabar.commands;

public record BuyableBookCommand(
        String buyableBookApiKey,Float price, String publisherApiKey,
        String bookType, Integer pageCount, String bookApiKey) {
}
