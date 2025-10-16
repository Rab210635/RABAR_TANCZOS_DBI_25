package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Branch;
import spengergasse.at.sj2425scherzerrabar.domain.Library;
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Long> {
    Optional<Branch> findBranchByBranchApiKey(ApiKey apiKey);
    List<Branch> findBranchesByLibrary(Library book);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BranchDto(
            b.branchApiKey,b.library.libraryApiKey,b.address
        ) from Branch b where b.branchApiKey.apiKey = :apiKey
        """)
    Optional<BranchDto> findProjectedBranchByBranchApiKey(String apiKey);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BranchDto(
            b.branchApiKey,b.library.libraryApiKey,b.address
        ) from Branch b
        
        """)
    List<BranchDto> findAllProjected();

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.BranchDto(
            b.branchApiKey,b.library.libraryApiKey,b.address
        ) from Branch b where b.library.libraryApiKey.apiKey = :apiKey
        """)
    List<BranchDto> findProjectedBranchesByLibrary(String apiKey);
}
