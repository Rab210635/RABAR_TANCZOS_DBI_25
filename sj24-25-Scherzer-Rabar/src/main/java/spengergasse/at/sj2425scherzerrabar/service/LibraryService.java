package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.LibraryCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.LibraryRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public LibraryService(LibraryRepository libraryRepository, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public LibraryDto createLibrary(LibraryCommand command) {
        logger.debug("entered createLibrary");
        List<Book> books = new ArrayList<>();
        command.booksInLibraries().stream().map(
                (bookInLibrariesCommand -> {
                    books.add(bookRepository.findBookByBookApiKey(new ApiKey(bookInLibrariesCommand.bookApiKey()))
                            .orElseThrow(() -> LibraryServiceException.noBookForApikey(bookInLibrariesCommand.bookApiKey())));
                    return true;
                })
        ).forEach(x -> {});


        Library library = new Library(
                command.name(),
                Address.addressFromString(command.headquarters()),
                command.booksInLibraries().stream()
                        .map(binlc -> {
                                    var book = books.stream().filter(b -> b.getBookApiKey().apiKey().equals(binlc.bookApiKey())).findFirst().get();
                                    return new BookInLibraries(book,binlc.borrowLengthDays());
                                }
                        ).toList()
        );
        library = libraryRepository.save(library);
        return LibraryDto.libraryDtoFromLibrary(library);
    }

    @Transactional
    public void deleteLibrary(String libraryApiKey) {
        logger.debug("entered deleteLibrary");
        Library library = libraryRepository.findLibraryByLibraryApiKey(new ApiKey(libraryApiKey))
                .orElseThrow(() -> LibraryServiceException.noLibraryForApiKey(libraryApiKey));
        libraryRepository.delete(library);
    }

    @Transactional
    public LibraryDto updateLibrary(LibraryCommand command) {
        logger.debug("entered updateLibrary");
        Library library = libraryRepository.findLibraryByLibraryApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> LibraryServiceException.noLibraryForApiKey(command.apiKey()));

        library.setName(command.name());
        library.setHeadquarters(Address.addressFromString(command.headquarters()));

        List<Book> books = new ArrayList<>();
        command.booksInLibraries().stream().map(
                (bookInLibrariesCommand -> {
                    books.add(bookRepository.findBookByBookApiKey(new ApiKey(bookInLibrariesCommand.bookApiKey()))
                            .orElseThrow(() -> LibraryServiceException.noBookForApikey(bookInLibrariesCommand.bookApiKey())));
                    return true;
                })
        ).forEach(x -> {});
       library.setBooksInLibraries(command.booksInLibraries().stream()
               .map(binlc -> {
                   var book = books.stream().filter(b -> b.getBookApiKey().apiKey().equals(binlc.bookApiKey())).findFirst().get();
                           return new BookInLibraries(book,binlc.borrowLengthDays());
               }
               ).toList()
       );

        library = libraryRepository.save(library);
        return LibraryDto.libraryDtoFromLibrary(library);
    }

    public List<LibraryDto> getLibraries() {
        logger.debug("entered getLibraries");
        return libraryRepository.findAllProjected();
    }

    public LibraryDto getLibrary(String libraryApiKey) {
        logger.debug("entered getLibrary");
        return libraryRepository.findProjectedByLibraryApiKey(libraryApiKey)
                .orElseThrow(() -> LibraryServiceException.noLibraryForApiKey(libraryApiKey));
    }

    public LibraryDto getLibraryByName(String name) {
        logger.debug("entered getLibraryByName");
        return libraryRepository.findProjectedByName(name)
                .orElseThrow(() -> LibraryServiceException.noLibraryForName(name));
    }


    public static class LibraryServiceException extends RuntimeException
    {
        private LibraryServiceException(String message)
        {
            super(message);
        }

        public static LibraryServiceException noLibraryForApiKey(String apiKey)
        {
            return new LibraryServiceException("Library with api key (%s) not existent".formatted(apiKey));
        }

        static LibraryServiceException noBookForApikey(String apiKey)
        {
            return new LibraryServiceException("Book with api key (%s) not existent".formatted(apiKey));
        }
        static LibraryServiceException noLibraryForName(String name)
        {
            return new LibraryServiceException("Library with name (%s) not existent".formatted(name));
        }
    }
}
