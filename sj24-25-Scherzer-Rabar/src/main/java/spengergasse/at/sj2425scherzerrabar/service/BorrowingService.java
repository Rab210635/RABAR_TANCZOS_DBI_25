package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.BorrowingCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BorrowingRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CopyRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CustomerRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final CustomerRepository customerRepository;
    private final CopyRepository copyRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BorrowingService(BorrowingRepository borrowingRepository, CustomerRepository customerRepository, CopyRepository copyRepository) {
        this.borrowingRepository = borrowingRepository;
        this.customerRepository = customerRepository;
        this.copyRepository = copyRepository;
    }

    @Transactional
    public BorrowingDto createBorrowing(BorrowingCommand command) {
        logger.debug("entered createBorrowing");
        Optional<Customer> customer = customerRepository.findCustomerByCustomerApiKey(new ApiKey(command.customerApiKey()));
        if(customer.isEmpty()) {
            throw BorrowingServiceException.noCustomerForApikey(command.customerApiKey());
        }
        List<Copy> copies = command.copyApiKeys().stream()
                .map(ApiKey::new)
                .map(copyRepository::findCopyByCopyApiKey)
                .flatMap(Optional::stream)
                .toList();
        if (copies.isEmpty()) {
            throw BorrowingServiceException.noCopies();
        }
        return BorrowingDto.borrowingDtoFromBorrowing( borrowingRepository.save(new Borrowing(customer.get(),copies, command.fromDate(),0)));
    }


    @Transactional
    public void deleteBorrowing(String borrowingApiKey) {
        logger.debug("entered deleteBorrowing");
        Borrowing borrowing = borrowingRepository.findBorrowingByBorrowingApiKey(new ApiKey(borrowingApiKey))
                .orElseThrow(() -> BorrowingServiceException.noBorrowingForApiKey(borrowingApiKey));
        borrowingRepository.delete(borrowing);
    }

    @Transactional
    public BorrowingDto updateBorrowing(BorrowingCommand command) {
        logger.debug("entered updateBorrowing");
        return borrowingRepository.findBorrowingByBorrowingApiKey(new ApiKey(command.apiKey()))
                .map(borrowing -> {
                    if (!borrowing.getCustomer().getCustomerApiKey().apiKey().equals(command.customerApiKey())) {
                        Customer newCustomer = customerRepository.findCustomerByCustomerApiKey(new ApiKey(command.customerApiKey()))
                                .orElseThrow(() -> BorrowingServiceException.noCustomerForApikey(command.customerApiKey()));
                        borrowing.setCustomer(newCustomer);
                    }

                    List<Copy> newCopies = command.copyApiKeys().stream()
                            .map(ApiKey::new)
                            .map(copyRepository::findCopyByCopyApiKey)
                            .flatMap(Optional::stream)
                            .toList();

                    if (newCopies.isEmpty()) {
                        throw BorrowingServiceException.noCopies();
                    }

                    borrowing.setCopies(newCopies);
                    borrowing.setFromDate(command.fromDate());

                    return BorrowingDto.borrowingDtoFromBorrowing( borrowingRepository.save(borrowing));
                }).orElseThrow(() -> BorrowingServiceException.noBorrowingForApiKey(command.apiKey()));
    }

    public List<BorrowingDto> getAllBorrowings() {

        logger.debug("entered getAllBorrowings");
        return borrowingRepository.findAllProjected();
    }

    public List<BorrowingDto> getBorrowingsByCustomer(String customerApiKey) {
        logger.debug("entered getBorrowingsByCustomer");
        var customer = customerRepository.findProjectedCustomerByCustomerApiKey(customerApiKey)
                .orElseThrow(() -> BorrowingServiceException.noCustomerForApikey(customerApiKey));

        return borrowingRepository.findProjectedBorrowingsByCustomerByCustomer(customer.apiKey());
    }

    public List<BorrowingDto> getBorrowingsByCopy(String copyApiKey) {
        logger.debug("entered getBorrowingsByCopy");
        var copy = copyRepository.findProjectedByCopyApiKey(copyApiKey)
                .orElseThrow(() -> BorrowingServiceException.noCopyForApikey(copyApiKey));
        return borrowingRepository.findProjectedBorrowingsByCopiesContains(copy.apiKey());
    }

    public BorrowingDto getBorrowingByApiKey(String borrowingApiKey) {
        logger.debug("entered getBorrowingByApiKey");
        return borrowingRepository.findProjectedBorrowingByBorrowingApiKey(borrowingApiKey)
                .orElseThrow(() -> BorrowingServiceException.noBorrowingForApiKey(borrowingApiKey));
    }

    public static class BorrowingServiceException extends RuntimeException
    {
        public BorrowingServiceException(String message)
        {
            super(message);
        }

        public static BorrowingServiceException noBorrowingForApiKey(String apiKey)
        {
            return new BorrowingServiceException("Borrowing with api key (%s) not existent".formatted(apiKey));
        }

        public static BorrowingServiceException noCopies()
        {
            return new BorrowingServiceException("No Copies for Borrowing");
        }

        public static BorrowingServiceException noCustomerForApikey(String apiKey)
        {
            return new BorrowingServiceException("Customer with api key (%s) not existent".formatted(apiKey));
        }
        public static BorrowingServiceException noCopyForApikey(String apiKey)
        {
            return new BorrowingServiceException("Copy with api key (%s) not existent".formatted(apiKey));
        }
    }

}
