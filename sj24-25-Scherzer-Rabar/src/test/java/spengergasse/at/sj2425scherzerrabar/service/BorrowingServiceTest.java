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
import spengergasse.at.sj2425scherzerrabar.commands.BorrowingCommand;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto;
import spengergasse.at.sj2425scherzerrabar.dtos.CopyDto;
import spengergasse.at.sj2425scherzerrabar.dtos.CustomerDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BorrowingRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CopyRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.CustomerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.*;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {
    private @Mock BorrowingRepository borrowingRepository;
    private @Mock CustomerRepository customerRepository;
    private @Mock CopyRepository copyRepository;

    private BorrowingService borrowingService;

    @BeforeEach
    void setUp() {
        assumeThat(borrowingRepository).isNotNull();
        assumeThat(customerRepository).isNotNull();
        assumeThat(copyRepository).isNotNull();
        borrowingService = new BorrowingService(borrowingRepository, customerRepository, copyRepository);
    }

    @Test
    void cant_create_borrowing_with_missing_customer() {
        assertThatThrownBy(() -> borrowingService.createBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(),new ApiKey("invalidCustomer").apiKey(), List.of(new ApiKey("copyApiKey").apiKey()), LocalDate.now(),0)))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Customer with api key (invalidCustomer) not existent");
    }

    @Test
    void cant_create_borrowing_with_missing_copies() {
        var customer = FixturesFactory.customer();
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> borrowingService.createBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(),new ApiKey("customerApiKey").apiKey(), List.of(new ApiKey("invalidCopy").apiKey()), LocalDate.now(),0)))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("No Copies for Borrowing");
    }

    @Test
    void can_create_borrowing() {
        var customer = FixturesFactory.customer();
        var copy = FixturesFactory.copy(FixturesFactory.author());

        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));
        when(copyRepository.findCopyByCopyApiKey(any())).thenReturn(Optional.of(copy));
        when(borrowingRepository.save(any(Borrowing.class))).then(AdditionalAnswers.returnsFirstArg());

        BorrowingDto borrowing = borrowingService.createBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(),new ApiKey("customerApiKey").apiKey(), List.of(new ApiKey("copyApiKey").apiKey()), LocalDate.now(),0));

        assertThat(borrowing).isNotNull();
        assertThat(borrowing.customerApiKey()).isEqualTo(customer.getCustomerApiKey().apiKey());
        assertThat(borrowing.copyApiKeys()).contains(copy.getCopyApiKey().apiKey());
    }

    @Test
    void cant_update_borrowing_with_missing_borrowing() {
        assertThatThrownBy(() -> borrowingService.updateBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(), new ApiKey("customerApiKey").apiKey(), List.of(new ApiKey("copyApiKey").apiKey()), LocalDate.now(), 0)))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Borrowing with api key (BorrowingApiKey) not existent");
    }

    @Test
    void cant_update_borrowing_with_missing_customer() {
        var borrowing = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));
        when(borrowingRepository.findBorrowingByBorrowingApiKey(any())).thenReturn(Optional.of(borrowing));

        assertThatThrownBy(() -> borrowingService.updateBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(), new ApiKey("invalidCustomerApiKey").apiKey(), List.of(new ApiKey("copyApiKey").apiKey()), LocalDate.now(), 0)))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Customer with api key (invalidCustomerApiKey) not existent");
    }

    @Test
    void cant_update_borrowing_with_missing_copies() {
        var borrowing = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));
        var customer = FixturesFactory.customer();
        when(borrowingRepository.findBorrowingByBorrowingApiKey(any())).thenReturn(Optional.of(borrowing));
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> borrowingService.updateBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(), new ApiKey("customerApiKey").apiKey(), List.of(new ApiKey("invalidCopy").apiKey()), LocalDate.now(), 0)))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("No Copies for Borrowing");
    }

    @Test
    void can_update_borrowing() {
        var existingBorrowing = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));
        var newCustomer = FixturesFactory.customer();
        var newCopy = FixturesFactory.copy();

        when(borrowingRepository.findBorrowingByBorrowingApiKey(any())).thenReturn(Optional.of(existingBorrowing));
        when(customerRepository.findCustomerByCustomerApiKey(any())).thenReturn(Optional.of(newCustomer));
        when(copyRepository.findCopyByCopyApiKey(any())).thenReturn(Optional.of(newCopy));
        when(borrowingRepository.save(any(Borrowing.class))).then(AdditionalAnswers.returnsFirstArg());

        var updatedBorrowing = borrowingService.updateBorrowing(
                new BorrowingCommand(new ApiKey("BorrowingApiKey").apiKey(), newCustomer.getCustomerApiKey().apiKey(), List.of(newCopy.getCopyApiKey().apiKey()), LocalDate.now(), 0));

        assertThat(updatedBorrowing).isNotNull();
        assertThat(updatedBorrowing.customerApiKey()).isEqualTo(newCustomer.getCustomerApiKey().apiKey());
        assertThat(updatedBorrowing.copyApiKeys()).contains(newCopy.getCopyApiKey().apiKey());
    }

    @Test
    void cant_delete_borrowing_with_missing_borrowing() {
        assertThatThrownBy(() -> borrowingService.deleteBorrowing(new ApiKey("invalidBorrowingApiKey").apiKey()))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Borrowing with api key (invalidBorrowingApiKey) not existent");
    }

    @Test
    void can_delete_borrowing() {
        var borrowing = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));
        when(borrowingRepository.findBorrowingByBorrowingApiKey(any())).thenReturn(Optional.of(borrowing));

        borrowingService.deleteBorrowing(borrowing.getBorrowingApiKey().apiKey());

        verify(borrowingRepository, times(1)).delete(borrowing);
    }



    @Test
    void can_get_all_borrowings() {
        var borrowing1 = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));
        var borrowing2 = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));

        when(borrowingRepository.findAllProjected())
                .thenReturn(List.of(BorrowingDto.borrowingDtoFromBorrowing(borrowing1), BorrowingDto.borrowingDtoFromBorrowing(borrowing2)));

        var borrowings = borrowingService.getAllBorrowings();

        assertThat(borrowings).hasSize(2);
    }

    @Test
    void can_get_borrowings_by_customer() {
        var customer = FixturesFactory.customer();
        var borrowing = FixturesFactory.borrowing(customer, List.of(FixturesFactory.copy()));

        when(customerRepository.findProjectedCustomerByCustomerApiKey(any())).thenReturn(Optional.of(CustomerDto.customerDtoFromCustomer(customer)));
        when(borrowingRepository.findProjectedBorrowingsByCustomerByCustomer(any())).thenReturn(List.of(BorrowingDto.borrowingDtoFromBorrowing(borrowing)));

        var borrowings = borrowingService.getBorrowingsByCustomer(new ApiKey("customerApiKey").apiKey());

        assertThat(borrowings).hasSize(1);
        assertThat(borrowings.getFirst().customerApiKey()).isEqualTo(customer.getCustomerApiKey().apiKey());
    }

    @Test
    void cant_get_borrowings_by_non_existing_customer() {
        assertThatThrownBy(() -> borrowingService.getBorrowingsByCustomer(new ApiKey("invalidApiKey").apiKey()))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Customer with api key (invalidApiKey) not existent");
    }

    @Test
    void can_get_borrowings_by_copy() {
        var copy = FixturesFactory.copy();
        var borrowing = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(copy));

        when(copyRepository.findProjectedByCopyApiKey(any())).thenReturn(Optional.of(CopyDto.copyDtoFromCopy(copy)));
        when(borrowingRepository.findProjectedBorrowingsByCopiesContains(any())).thenReturn(List.of(BorrowingDto.borrowingDtoFromBorrowing(borrowing)));

        var borrowings = borrowingService.getBorrowingsByCopy(new ApiKey("copyApiKey").apiKey());

        assertThat(borrowings).hasSize(1);
        assertThat(borrowings.getFirst().copyApiKeys()).contains(copy.getCopyApiKey().apiKey());
    }

    @Test
    void cant_get_borrowings_by_non_existing_copy() {
        assertThatThrownBy(() -> borrowingService.getBorrowingsByCopy(new ApiKey("invalidCopyApiKey").apiKey()))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Copy with api key (invalidCopyApiKey) not existent");
    }

    @Test
    void can_get_existing_author_by_id(){
        Borrowing borrowing = FixturesFactory.borrowing(FixturesFactory.customer(), List.of(FixturesFactory.copy()));
        when(borrowingRepository.findProjectedBorrowingByBorrowingApiKey(any())).thenReturn(Optional.of(BorrowingDto.borrowingDtoFromBorrowing(borrowing)));

        var borrowing1 = borrowingService.getBorrowingByApiKey(borrowing.getBorrowingApiKey().apiKey());
        assertThat(borrowing1).isEqualTo(BorrowingDto.borrowingDtoFromBorrowing(borrowing));
        verify(borrowingRepository, times(1)).findProjectedBorrowingByBorrowingApiKey(any());
    }

    @Test
    void cant_get_not_existing_author_by_id(){
        assertThatThrownBy(()->borrowingService.getBorrowingByApiKey(new ApiKey("borrowingApiKey").apiKey()))
                .isInstanceOf(BorrowingService.BorrowingServiceException.class)
                .hasMessageContaining("Borrowing with api key (borrowingApiKey) not existent");
    }
}
