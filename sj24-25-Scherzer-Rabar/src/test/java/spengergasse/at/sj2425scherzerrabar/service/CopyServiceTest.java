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
import spengergasse.at.sj2425scherzerrabar.commands.CopyCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.domain.jpa.Book;
import spengergasse.at.sj2425scherzerrabar.dtos.BookDto;
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;
import spengergasse.at.sj2425scherzerrabar.dtos.CopyDto;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BranchRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CopyRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.PublisherRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class CopyServiceTest {

    private @Mock CopyRepository copyRepository;
    private @Mock BookRepository bookRepository;
    private @Mock PublisherRepository publisherRepository;
    private @Mock BranchRepository branchRepository;

    private CopyService copyService;

    @BeforeEach
    void setUp() {
        assumeThat(copyRepository).isNotNull();
        assumeThat(bookRepository).isNotNull();
        assumeThat(publisherRepository).isNotNull();
        assumeThat(branchRepository).isNotNull();
        copyService = new CopyService(copyRepository, bookRepository, publisherRepository,branchRepository);
    }

    @Test
    void can_create_copy() {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        Book book = FixturesFactory.book(FixturesFactory.author());
        Branch branch = FixturesFactory.filiale();
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(copyRepository.save(any(Copy.class))).then(AdditionalAnswers.returnsFirstArg());
        when(branchRepository.findBranchByBranchApiKey(any())).thenReturn(Optional.of(branch));


        var copy = copyService.createCopy(new CopyCommand(
                "apiKey", "publisherApiKey", BookType.EBOOK,12,"bookApiKey",100f,"branchApiKey"
        ));

        assertThat(copy).isNotNull();
    }

    @Test
    void cant_create_copy_with_missing_publisher() {
        Book book = FixturesFactory.book(FixturesFactory.author());

        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));

        assertThatThrownBy(()-> copyService.createCopy(new CopyCommand("apiKey", "publisherApiKey",
                BookType.EBOOK,12,"bookApiKey",100f,"branchApiKey")))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Publisher with api key (publisherApiKey) not existent");
    }

    @Test
    void cant_create_copy_with_missing_book() {
        assertThatThrownBy(()-> copyService.createCopy(new CopyCommand("apiKey", "publisherApiKey",
                BookType.EBOOK,12,"bookApiKey",100f,"branchApiKey")))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Book with api key (bookApiKey) not existent");
    }

    @Test
    void cant_create_copy_with_missing_branch() {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        Book book = FixturesFactory.book(FixturesFactory.author());
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));

        assertThatThrownBy(()-> copyService.createCopy(new CopyCommand("apiKey", "publisherApiKey",
                BookType.EBOOK,12,"bookApiKey",100f,"branchApiKey")))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Branch with api key (branchApiKey) not existent");
    }

    @Test
    void can_delete_existing_copy() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        when(copyRepository.findCopyByCopyApiKey(any())).thenReturn(Optional.of(copy));

        copyService.deleteCopy(new ApiKey("validApiKey").apiKey());

        verify(copyRepository,times(1)).delete(copy);
    }

    @Test
    void cant_delete_not_existing_copy() {
      assertThatThrownBy(()->copyService.deleteCopy(new ApiKey("validApiKey").apiKey()))
              .isInstanceOf(CopyService.CopyServiceException.class)
              .hasMessageContaining("Copy with api key (validApiKey) not existent");
    }

    @Test
    void can_update_existing_copy() {
        Publisher publisher = FixturesFactory.publisher(FixturesFactory.address2());
        Book book = FixturesFactory.book(FixturesFactory.author());
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        Branch branch = FixturesFactory.filiale();
        when(copyRepository.findCopyByCopyApiKey(any())).thenReturn(Optional.of(copy));
        when(publisherRepository.findPublisherByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(branchRepository.findBranchByBranchApiKey(any())).thenReturn(Optional.of(branch));
        when(copyRepository.save(any(Copy.class))).then(AdditionalAnswers.returnsFirstArg());

        copyService.updateCopy(new CopyCommand(
                "copyApiKey","publisherApiKey",BookType.EBOOK,12,"BookApiKey",100f,"branchApiKey"
        ));

        verify(copyRepository,times(1)).save(any(Copy.class));
        assertThat(copy.getPageCount()).isEqualTo(12);
        assertThat(copy.getBookType()).isEqualTo(BookType.EBOOK);
    }

    @Test
    void cant_update_not_existing_copy() {
        assertThatThrownBy(()-> copyService.updateCopy(new CopyCommand("copyApiKey",
                "publisherApiKey",BookType.EBOOK,12,"BookApiKey",100f,"branchApiKey")))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Copy with api key (copyApiKey) not existent");
    }

    @Test
    void cant_update_existing_copy_with_missing_publisher() {
        Book book = FixturesFactory.book(FixturesFactory.author());
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        Branch branch = FixturesFactory.filiale();
        when(copyRepository.findCopyByCopyApiKey(any())).thenReturn(Optional.of(copy));
        when(bookRepository.findBookByBookApiKey(any())).thenReturn(Optional.of(book));

        assertThatThrownBy(()-> copyService.updateCopy(new CopyCommand(
                "copyApiKey4","publisherApiKey2",BookType.EBOOK,12,"BookApiKey5",100f,"branchApiKey")))
        .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Publisher with api key (publisherApiKey2) not existent");
    }

    @Test
    void cant_update_existing_copy_with_missing_book() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);

        when(copyRepository.findCopyByCopyApiKey(any())).thenReturn(Optional.of(copy));

        assertThatThrownBy(()-> copyService.updateCopy(new CopyCommand(
                "copyApiKey","publisherApiKey",BookType.EBOOK,12,"BookApiKey",100f,"branchApiKey")))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Book with api key (BookApiKey) not existent");
    }

    @Test
    void can_get_existing_copy() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        CopyDto copyDto = CopyDto.copyDtoFromCopy(copy);
        when(copyRepository.findProjectedByCopyApiKey((any()))).thenReturn(Optional.of(copyDto));

        var copy1 = copyService.getCopy(copyDto.apiKey());
        assertThat(copy1).isEqualTo(copyDto);
        verify(copyRepository,times(1)).findProjectedByCopyApiKey(any());
    }

    @Test
    void cant_get_not_existing_copy() {
        assertThatThrownBy(()->copyService.getCopy("copyApiKey"))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Copy with api key (copyApiKey) not existent");
    }

    @Test
    void can_get_copies() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        var copy1 = FixturesFactory.copy(author);
        when(copyRepository.findAllProjected()).thenReturn(List.of(CopyDto.copyDtoFromCopy(copy),CopyDto.copyDtoFromCopy(copy1)));

        var copies = copyService.getCopies();

        assertThat(copies).hasSize(2);
    }

    @Test
    void can_get_copies_with_book() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        var copy1 = FixturesFactory.copy(author);
        BookDto book = BookDto.bookDtoFromBook(FixturesFactory.book(FixturesFactory.author()));
        when(bookRepository.findProjectedBookByBookApiKey(any())).thenReturn(Optional.of(book));
        when(copyRepository.findAllProjectedByBook_BookApiKey(any())).thenReturn(List.of(CopyDto.copyDtoFromCopy(copy),CopyDto.copyDtoFromCopy(copy1)));

        var copies = copyService.getCopiesByBook(book.apiKey());

        assertThat(copies).hasSize(2);
    }

    @Test
    void cant_get_copies_with_missing_book() {
       assertThatThrownBy(()->copyService.getCopiesByBook(new ApiKey("bookApiKey").apiKey()))
               .isInstanceOf(CopyService.CopyServiceException.class)
               .hasMessageContaining("Book with api key (bookApiKey) not existent");
    }

    @Test
    void can_get_copies_with_publisher() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        var copy1 = FixturesFactory.copy(author);
        PublisherDto publisher = PublisherDto.publisherDtoFromPublisher(FixturesFactory.publisher(FixturesFactory.address2()));
        when(publisherRepository.findProjectedByPublisherApiKey(any())).thenReturn(Optional.of(publisher));
        when(copyRepository.findAllProjectedByPublisher_PublisherApiKey(any())).thenReturn(List.of(CopyDto.copyDtoFromCopy(copy),CopyDto.copyDtoFromCopy(copy1)));

        var copies = copyService.getCopiesByPublisher(publisher.apiKey());

        assertThat(copies).hasSize(2);
    }

    @Test
    void cant_get_copies_with_missing_publisher() {
        assertThatThrownBy(()->copyService.getCopiesByPublisher(new ApiKey("publisherApiKey").apiKey()))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Publisher with api key (publisherApiKey) not existent");
    }

    @Test
    void can_get_copies_with_book_type() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        var copy1 = FixturesFactory.copy(author);
        when(copyRepository.findAllProjectedByBookType(BookType.PAPERBACK)).thenReturn(List.of(CopyDto.copyDtoFromCopy(copy),CopyDto.copyDtoFromCopy(copy1)));

        var copies = copyService.getCopiesByBookType(BookType.PAPERBACK.name());

        assertThat(copies).hasSize(2);
    }


    @Test
    void can_get_copies_with_branch() {
        var author = FixturesFactory.author();
        author.setPenname("aron");
        var copy = FixturesFactory.copy(author);
        var copy1 = FixturesFactory.copy(author);
        BranchDto branch = BranchDto.branchDtoFromBranch(FixturesFactory.filiale());
        when(branchRepository.findProjectedBranchByBranchApiKey(any())).thenReturn(Optional.of(branch));
        when(copyRepository.findAllProjectedByInBranch_BranchApiKey(branch.apiKey())).thenReturn(List.of(CopyDto.copyDtoFromCopy(copy), CopyDto.copyDtoFromCopy(copy1)));
        var copies = copyService.getCopiesByBranch(branch.apiKey());

        assertThat(copies).hasSize(2);
    }

    @Test
    void cant_get_copies_with_missing_branch() {
        assertThatThrownBy(()->copyService.getCopiesByPublisher("publisherApiKey"))
                .isInstanceOf(CopyService.CopyServiceException.class)
                .hasMessageContaining("Publisher with api key (publisherApiKey) not existent");
    }





}