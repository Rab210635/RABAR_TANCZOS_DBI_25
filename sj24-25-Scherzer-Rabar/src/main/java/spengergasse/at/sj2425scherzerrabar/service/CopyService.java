package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.CopyCommand;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.BookType;
import spengergasse.at.sj2425scherzerrabar.domain.Copy;
import spengergasse.at.sj2425scherzerrabar.dtos.CopyDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BookRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.BranchRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CopyRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.PublisherRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class CopyService {

    private final CopyRepository copyRepository;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BranchRepository branchRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public CopyService(CopyRepository copyRepository, BookRepository bookRepository, PublisherRepository publisherRepository, BranchRepository branchRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public CopyDto createCopy(CopyCommand command) {
        logger.debug("entered createCopy");
        var book = bookRepository.findBookByBookApiKey(new ApiKey(command.bookApiKey()));
        if(book.isEmpty()) {
            throw CopyServiceException.noBookForApikey(command.bookApiKey());
        }
        var publisher = publisherRepository.findPublisherByPublisherApiKey(new ApiKey(command.publisherApiKey()));
        if(publisher.isEmpty()) {
            throw CopyServiceException.noPublisherForApikey(command.publisherApiKey());
        }
        var branch = branchRepository.findBranchByBranchApiKey(new ApiKey(command.branchApiKey()));
        if(branch.isEmpty()) {
            throw CopyServiceException.noBranchForApikey(command.branchApiKey());
        }
        Copy copy = new Copy(publisher.get(), command.bookType(),command.pageCount(),book.get(),branch.get());
        copyRepository.save(copy);
        return CopyDto.copyDtoFromCopy(copy);
    }

    @Transactional
    public void deleteCopy(String apiKey) {
        logger.debug("entered deleteCopy");
        Copy copy = copyRepository.findCopyByCopyApiKey(new ApiKey(apiKey))
                .orElseThrow(()->CopyServiceException.noCopyForApiKey(apiKey));
        copyRepository.delete(copy);
    }

    @Transactional
    public CopyDto updateCopy(CopyCommand command) {
        logger.debug("entered updateCopy");
       Copy copy = copyRepository.findCopyByCopyApiKey(new ApiKey(command.apiKey())).map((Copy c)->{
                c.setBookType(command.bookType());

                c.setPageCount(command.pageCount());

                   bookRepository.findBookByBookApiKey(new ApiKey(command.bookApiKey()))
                           .ifPresentOrElse(c::setBook,()->{throw CopyServiceException.noBookForApikey(command.bookApiKey());} );

                publisherRepository.findPublisherByPublisherApiKey(new ApiKey(command.publisherApiKey()))
                        .ifPresentOrElse(c::setPublisher,()->{throw CopyServiceException.noPublisherForApikey(command.publisherApiKey());} );

                branchRepository.findBranchByBranchApiKey(new ApiKey(command.branchApiKey()))
                        .ifPresentOrElse(c::setInBranch,()->{throw CopyServiceException.noBranchForApikey(command.branchApiKey());} );

            copyRepository.save(c);
            return c;
        }).orElseThrow(()->CopyServiceException.noCopyForApiKey(command.apiKey()));
       return CopyDto.copyDtoFromCopy(copy);
    }

    public CopyDto getCopy(String apiKey) {
        logger.debug("entered getCopy");
        return copyRepository.findProjectedByCopyApiKey(apiKey)
                .orElseThrow(()->CopyServiceException.noCopyForApiKey(apiKey));
    }

    public List<CopyDto> getCopies() {
        logger.debug("entered getCopies");
        return copyRepository.findAllProjected();
    }

    public List<CopyDto> getCopiesByBook(String bookApiKey) {
        logger.debug("entered getCopiesByBook");
        var book = bookRepository.findProjectedBookByBookApiKey(bookApiKey)
                .orElseThrow(()->CopyServiceException.noBookForApikey(bookApiKey));
        return copyRepository.findAllProjectedByBook_BookApiKey(book.apiKey());
}

    public List<CopyDto> getCopiesByPublisher(String publisherApiKey) {
        logger.debug("entered getCopiesByPublisher");
        var publisher = publisherRepository.findProjectedByPublisherApiKey(publisherApiKey)
                .orElseThrow(()->CopyServiceException.noPublisherForApikey(publisherApiKey));
        return copyRepository.findAllProjectedByPublisher_PublisherApiKey(publisher.apiKey());
    }

    public List<CopyDto> getCopiesByBranch(String branchApiKey) {
        logger.debug("entered getCopiesByBranch");
        var branch = branchRepository.findProjectedBranchByBranchApiKey(branchApiKey)
                .orElseThrow(()->CopyServiceException.noBranchForApikey(branchApiKey));
        return copyRepository.findAllProjectedByInBranch_BranchApiKey(branch.apiKey());
    }

    public List<CopyDto> getCopiesByBookType(String bookType) {
        logger.debug("entered getCopiesByBookType");
        return copyRepository.findAllProjectedByBookType(BookType.valueOf(bookType));
    }

    public static class CopyServiceException extends RuntimeException
    {
        private CopyServiceException(String message)
        {
            super(message);
        }

        public static CopyServiceException noCopyForApiKey(String apiKey)
        {
            return new CopyServiceException("Copy with api key (%s) not existent".formatted(apiKey));
        }

        public static CopyServiceException noBookForApikey(String apiKey)
        {
            return new CopyServiceException("Book with api key (%s) not existent".formatted(apiKey));
        }
        public static CopyServiceException noPublisherForApikey(String apiKey)
        {
            return new CopyServiceException("Publisher with api key (%s) not existent".formatted(apiKey));
        }
        public static CopyServiceException noBranchForApikey(String apiKey)
        {
            return new CopyServiceException("Branch with api key (%s) not existent".formatted(apiKey));
        }
    }
}
