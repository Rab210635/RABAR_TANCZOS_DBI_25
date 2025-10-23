package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand;
import spengergasse.at.sj2425scherzerrabar.commands.BookCommand2;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Author;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto2;
import spengergasse.at.sj2425scherzerrabar.persistence.AuthorRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public BookDto createBook(BookCommand command) {
        logger.debug("entered createBook");
        List<Author> authors = command.authorIds().stream()
                    .map(ApiKey::new)
                    .map(authorRepository::findAuthorByAuthorApiKey)
                    .flatMap(Optional::stream)
                    .toList();

        if (authors.isEmpty()) {
            throw BookServiceException.noAuthors();
        }
        Book book = new Book(
                command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                command.genre().stream().map(BookGenre::valueOf).toList()
                , authors, command.types().stream().map(BookType::valueOf).toList(), command.description()
        );
        return BookDto.bookDtoFromBook( bookRepository.save(book));
    }

    @Transactional
    public BookDto createBook2(BookCommand2 command) {
        List<Author>  authors = command.authorPennames().stream()
                    .map(authorRepository::findAuthorsByPenname)
                    .flatMap(Optional::stream)
                    .toList();

        if (authors.isEmpty()) {
            throw BookServiceException.noAuthors();
        }
        Book book = new Book(
                command.name(), command.releaseDate(), command.availableOnline(), command.wordCount(),
                command.genre().stream().map(BookGenre::valueOf).toList()
                , authors, command.types().stream().map(BookType::valueOf).toList(), command.description()
        );
        return BookDto.bookDtoFromBook(bookRepository.save(book));
    }

    /*
    @Transactional
    public Book createBook(CreateBookForm bookForm, @Valid CreateAuthorForm authorForm) {
        Author author = (authorForm.getOwnerType().isExisting()) ? authorRepository.findByA

        Book newBook = new Book("")
    }

     */

    @Transactional
    public void deleteBook(String bookApiKey) {
        logger.debug("entered deleteBook");
        Book book = bookRepository.findBookByBookApiKey(new ApiKey(bookApiKey))
                .orElseThrow(()-> BookServiceException.noBookForApiKey(bookApiKey));
        bookRepository.delete(book);
    }


    @Transactional
    public BookDto updateBook(BookCommand command) {
        logger.debug("entered updateBook");
       Book book = bookRepository.findBookByBookApiKey(new ApiKey(command.apiKey())).map((Book b)->{
            List<Author> authors = command.authorIds().stream()
                    .map(ApiKey::new)
                    .map(authorRepository::findAuthorByAuthorApiKey)
                    .flatMap(Optional::stream)
                    .toList();
            if(authors.isEmpty()){
                throw BookServiceException.noAuthors();
            }
            if(!b.getName().equals(command.name()))
                b.setName(command.name());
            if(b.getAvailableOnline() != command.availableOnline()) {
                b.setAvailableOnline(command.availableOnline());

            }
            if(!b.getDescription().equals(command.description()))
                b.setDescription(command.description());
            if(b.getReleaseDate() != command.releaseDate())
                b.setReleaseDate(command.releaseDate());

            b.setBookTypes(command.types().stream().map(BookType::valueOf).toList());
            b.setGenres(command.genre().stream().map(BookGenre::valueOf).toList());
            b.setAuthors(authors);
            bookRepository.save(b);
            return b;
        }).orElseThrow(()-> BookServiceException.noBookForApiKey(command.apiKey()));
       return BookDto.bookDtoFromBook(book);
    }

    @Transactional
    public BookDto updateBook2(BookCommand2 command) {
        Book book = bookRepository.findBookByBookApiKey(new ApiKey(command.apiKey())).map((Book b)->{
            List<Author> authors = command.authorPennames().stream()
                    .map(authorRepository::findAuthorsByPenname)
                    .flatMap(Optional::stream)
                    .toList();
            if(authors.isEmpty()){
                throw BookServiceException.noAuthors();
            }
            if(!b.getName().equals(command.name()))
                b.setName(command.name());
            if(b.getAvailableOnline() != command.availableOnline()) {
                b.setAvailableOnline(command.availableOnline());

            }
            if(!b.getDescription().equals(command.description()))
                b.setDescription(command.description());
            if(b.getReleaseDate() != command.releaseDate())
                b.setReleaseDate(command.releaseDate());

            b.setBookTypes(command.types().stream().map(BookType::valueOf).toList());
            b.setGenres(command.genre().stream().map(BookGenre::valueOf).toList());
            b.setAuthors(authors);
            return b;
        }).orElseThrow(()-> BookServiceException.noBookForApiKey(command.apiKey()));
        return BookDto.bookDtoFromBook(book);
    }


    public List<BookDto> getBooks(String authorApiKey) {
        logger.debug("entered getBooks");
        if (authorApiKey == null) {
            return bookRepository.findAllProjected();
        }
        var author = authorRepository.findProjectedAuthorByAuthorApiKey(authorApiKey)
                .orElseThrow(()-> BookServiceException.noAuthorForApikey(authorApiKey));
        return bookRepository.findProjectedBooksByAuthorsContains(author.apiKey());
    }

    public List<BookDto2> getBooks2() {
        return bookRepository.findAllProjected2();
    }
    public BookDto2 getBook2(String bookApiKey) {
        return bookRepository.findProjectedBookByBookApiKey2(bookApiKey)
                .orElseThrow(()-> BookServiceException.noBookForApiKey(bookApiKey));
    }

    public BookDto getBook(String bookApiKey) {
        logger.debug("entered getBook");
       return bookRepository.findProjectedBookByBookApiKey(bookApiKey)
               .orElseThrow(()-> BookServiceException.noBookForApiKey(bookApiKey));
    }

    public static class BookServiceException extends RuntimeException
    {
        public BookServiceException(String message)
        {
            super(message);
        }

        public static BookServiceException noBookForApiKey(String apiKey)
        {
            return new BookServiceException("Book with api key (%s) not existent".formatted(apiKey));
        }

        public static BookServiceException noAuthors()
        {
            return new BookServiceException("No Authors for Book");
        }

        public static BookServiceException noAuthorForApikey(String apiKey)
        {
            return new BookServiceException("Author with api key (%s) not existent".formatted(apiKey));
        }

    }
}
