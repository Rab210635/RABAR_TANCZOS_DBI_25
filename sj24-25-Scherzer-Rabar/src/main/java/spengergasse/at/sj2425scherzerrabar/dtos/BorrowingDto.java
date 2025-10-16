package spengergasse.at.sj2425scherzerrabar.dtos;


import spengergasse.at.sj2425scherzerrabar.domain.Borrowing;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record BorrowingDto(String apiKey,String customerApiKey, List<String> copyApiKeys,
                           LocalDate fromDate, Integer extendedByDays) {
    public BorrowingDto(Borrowing borrowing) {
        this(borrowing.getBorrowingApiKey().apiKey(),borrowing.getCustomer().getCustomerApiKey().apiKey(),
                borrowing.getCopies().stream().map(copy -> copy.getCopyApiKey().apiKey()).collect(Collectors.toList())
                ,borrowing.getFromDate(), borrowing.getExtendedByDays()
        );
    }

    public static BorrowingDto borrowingDtoFromBorrowing(Borrowing borrowing) {
        return new BorrowingDto(
          borrowing.getBorrowingApiKey().apiKey(),borrowing.getCustomer().getCustomerApiKey().apiKey(),
          borrowing.getCopies().stream().map(copy -> copy.getCopyApiKey().apiKey()).toList(),
          borrowing.getFromDate(),borrowing.getExtendedByDays()
        );
    }
}