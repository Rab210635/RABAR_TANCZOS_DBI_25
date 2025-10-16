package spengergasse.at.sj2425scherzerrabar.dtos;

import spengergasse.at.sj2425scherzerrabar.domain.BookInLibraries;

public record BookInLibrariesDto(
        String bookApiKey, Integer borrowLengthDays
) {
    public static BookInLibrariesDto bookInLibrariesDtoFromBookInLibraries(BookInLibraries bookInLibraries) {
        return new BookInLibrariesDto(
          bookInLibraries.getBook().getBookApiKey().apiKey(), bookInLibraries.getBorrowLengthDays()
        );
    }
}
