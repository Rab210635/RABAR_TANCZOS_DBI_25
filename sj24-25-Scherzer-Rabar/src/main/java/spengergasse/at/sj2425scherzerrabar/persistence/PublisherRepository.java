package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Publisher;
import spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findPublisherByPublisherApiKey(ApiKey apiKey);

    Optional<Publisher> findPublisherByName(String name);

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto(
        p.publisherApiKey, p.name, p.address
    ) from Publisher p
    """)
    List<PublisherDto> findAllProjected();

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto(
        p.publisherApiKey, p.name, p.address
    ) from Publisher p where p.publisherApiKey.apiKey =  :apiKey
    """)
    Optional<PublisherDto> findProjectedByPublisherApiKey(String apiKey);

    @Query("""
    select new spengergasse.at.sj2425scherzerrabar.dtos.PublisherDto(
        p.publisherApiKey, p.name, p.address
    ) from Publisher p where p.name =  :name
    """)
    Optional<PublisherDto> findProjectedByName(String name);


}
