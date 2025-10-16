package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Library;
import spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto;

import java.util.List;
import java.util.Optional;

@Repository

public interface LibraryRepository extends JpaRepository<Library, Integer> {
    Optional<Library> findLibraryByLibraryApiKey(ApiKey apiKey);

    Optional<Library> findLibraryByName(String name);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto(
            l
        ) from Library l
        """)
    List<LibraryDto> findAllProjected();

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto(
            l
        ) from Library l where l.libraryApiKey.apiKey = :apiKey
        """)
    Optional<LibraryDto> findProjectedByLibraryApiKey(String apiKey);

    @Query("""
        select new spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto(
            l
        ) from Library l where l.name = :name
        """)
    Optional<LibraryDto> findProjectedByName(String name);
}
