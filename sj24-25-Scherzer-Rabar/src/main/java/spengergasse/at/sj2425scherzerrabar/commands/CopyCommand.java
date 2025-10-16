package spengergasse.at.sj2425scherzerrabar.commands;

import spengergasse.at.sj2425scherzerrabar.domain.BookType;

public record CopyCommand(String apiKey, String publisherApiKey, BookType bookType, Integer pageCount, String bookApiKey, Float price, String branchApiKey) {}