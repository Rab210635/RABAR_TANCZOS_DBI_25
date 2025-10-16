package spengergasse.at.sj2425scherzerrabar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.commands.BookInLibrariesCommand;
import spengergasse.at.sj2425scherzerrabar.commands.LibraryCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.BookInLibrariesDto;
import spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.LibraryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LibraryServiceTest {
    private @Mock LibraryRepository libraryRepository;
    private @Mock BookRepository bookRepository;


    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(libraryRepository,bookRepository);
    }


    @Test
    void can_create_library() {
        Book b1 = FixturesFactory.book(FixturesFactory.author());
        BookInLibraries bookInLibraries = FixturesFactory.libBook(b1);
        var command = new LibraryCommand(new ApiKey("LibraryKey").apiKey(), "Thalia", FixturesFactory.libraryAddress().toString(),
                List.of(new BookInLibrariesCommand(bookInLibraries.getBook().getBookApiKey().apiKey(),bookInLibraries.getBorrowLengthDays())));
        when(libraryRepository.save(any(Library.class))).then(AdditionalAnswers.returnsFirstArg());
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(b1));
        LibraryDto createdLibrary = libraryService.createLibrary(command);

        assertThat(createdLibrary).isNotNull();
        assertThat(createdLibrary.name()).isEqualTo("Thalia");
        assertThat(createdLibrary.headquarters()).isEqualTo(FixturesFactory.libraryAddress().toString());
        assertThat(createdLibrary.booksInLibraries()).contains(BookInLibrariesDto.bookInLibrariesDtoFromBookInLibraries(bookInLibraries)); //TODO Error
    }

    @Test
    void cant_delete_library_with_missing_library() {
        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.deleteLibrary("invalidLibraryApiKey"))
                .isInstanceOf(LibraryService.LibraryServiceException.class)
                .hasMessageContaining("Library with api key (invalidLibraryApiKey) not existent");
    }

    @Test
    void can_delete_library() {
        Library library = FixturesFactory.thalia(FixturesFactory.libraryAddress(),List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.of(library));

        libraryService.deleteLibrary(library.getLibraryApiKey().apiKey());

        verify(libraryRepository, times(1)).delete(library);
    }

    @Test
    void cant_update_library_with_missing_library() {
        BookInLibraries bookInLibraries = FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()));
        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.empty());

        var command = new LibraryCommand("LibraryKey", "Thalia", FixturesFactory.libraryAddress().toString(),
                List.of(new BookInLibrariesCommand(bookInLibraries.getBook().getBookApiKey().apiKey(),bookInLibraries.getBorrowLengthDays())));

        assertThatThrownBy(() -> libraryService.updateLibrary( command))
                .isInstanceOf(LibraryService.LibraryServiceException.class)
                .hasMessageContaining("Library with api key (LibraryKey) not existent");
    }

    @Test
    void can_update_library() {
        Book b1 = FixturesFactory.book(FixturesFactory.author());

        BookInLibraries bookInLibraries = FixturesFactory.libBook(b1);
        Library library = FixturesFactory.thalia(FixturesFactory.libraryAddress(),List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));

        var command = new LibraryCommand("LibraryKey", "UpdatedLibrary", FixturesFactory.libraryAddress().toString(),
                List.of(new BookInLibrariesCommand(bookInLibraries.getBook().getBookApiKey().apiKey(),bookInLibraries.getBorrowLengthDays())));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(b1));
        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.of(library));
        when(libraryRepository.save(any(Library.class))).then(AdditionalAnswers.returnsFirstArg());

        LibraryDto updatedLibrary = libraryService.updateLibrary(command);

        assertThat(updatedLibrary).isNotNull();
        assertThat(updatedLibrary.name()).isEqualTo("UpdatedLibrary");
    }

    @Test
    void can_get_all_libraries() {
        LibraryDto library1 = LibraryDto.libraryDtoFromLibrary(FixturesFactory.thalia(FixturesFactory.libraryAddress(),List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author())))));
        LibraryDto library2 = LibraryDto.libraryDtoFromLibrary(FixturesFactory.thalia(FixturesFactory.address2(),List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))))); ;
        when(libraryRepository.findAllProjected()).thenReturn(List.of(library1, library2));

        List<LibraryDto> libraries = libraryService.getLibraries();

        assertThat(libraries).hasSize(2);
    }

    @Test
    void cant_get_library_with_missing_library() {

        assertThatThrownBy(() -> libraryService.getLibrary("invalidLibraryApiKey"))
                .isInstanceOf(LibraryService.LibraryServiceException.class)
                .hasMessageContaining("Library with api key (invalidLibraryApiKey) not existent");
    }

    @Test
    void can_get_library() {
        LibraryDto library = LibraryDto.libraryDtoFromLibrary(FixturesFactory.thalia(FixturesFactory.libraryAddress(),List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author())))));
        when(libraryRepository.findProjectedByLibraryApiKey(any())).thenReturn(Optional.of(library));

        LibraryDto libraryDto = libraryService.getLibrary(library.apiKey());

        assertThat(libraryDto).isNotNull();
        assertThat(libraryDto.name()).isEqualTo(library.name());
    }

    @Test
    void cant_get_library_by_name_with_missing_library() {
        when(libraryRepository.findProjectedByName(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.getLibraryByName("NonExistingLibrary"))
                .isInstanceOf(LibraryService.LibraryServiceException.class)
                .hasMessageContaining("Library with name (NonExistingLibrary) not existent");
    }

    @Test
    void can_get_library_by_name() {
        Library library = FixturesFactory.thalia(FixturesFactory.libraryAddress(),List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        when(libraryRepository.findProjectedByName(any())).thenReturn(Optional.of(LibraryDto.libraryDtoFromLibrary(library)));

        LibraryDto libraryDto = libraryService.getLibraryByName("Thalia");

        assertThat(libraryDto).isNotNull();
        assertThat(libraryDto.name()).isEqualTo(library.getName());
    }
}
