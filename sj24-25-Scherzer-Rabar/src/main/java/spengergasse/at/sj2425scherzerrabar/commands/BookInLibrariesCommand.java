package spengergasse.at.sj2425scherzerrabar.commands;

public record BookInLibrariesCommand(
        String bookApiKey,
        Integer borrowLengthDays
) {
}
