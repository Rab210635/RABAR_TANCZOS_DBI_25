package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Borrowing;
import spengergasse.at.sj2425scherzerrabar.domain.Copy;
import spengergasse.at.sj2425scherzerrabar.domain.Customer;
import spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Optional<Borrowing> findBorrowingByBorrowingApiKey(ApiKey apiKey);
    List<Borrowing> findBorrowingsByCopiesContaining(Copy copy);
    List<Borrowing> findBorrowingsByCustomer(Customer customer);


    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto(b) from Borrowing b
            where b.borrowingApiKey.apiKey = :apiKey
    """)
    Optional<BorrowingDto> findProjectedBorrowingByBorrowingApiKey(String apiKey);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto(b) from Borrowing b
    """)
    List<BorrowingDto> findAllProjected();

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto(b) from Borrowing b
            where b.customer.customerApiKey.apiKey = :customerApiKey
    """)
    List<BorrowingDto> findProjectedBorrowingsByCustomerByCustomer(String customerApiKey);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BorrowingDto(b) from Borrowing b
            where EXISTS (SELECT c FROM b.copies c WHERE c.copyApiKey.apiKey = :copyApiKey)
    """)
    List<BorrowingDto> findProjectedBorrowingsByCopiesContains(String copyApiKey);
}
