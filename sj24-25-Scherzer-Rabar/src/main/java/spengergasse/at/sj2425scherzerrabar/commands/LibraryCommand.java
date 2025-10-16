package spengergasse.at.sj2425scherzerrabar.commands;

import java.util.List;

public record LibraryCommand(
        String apiKey,
        String name,
        String headquarters,
        List<BookInLibrariesCommand> booksInLibraries
) {}