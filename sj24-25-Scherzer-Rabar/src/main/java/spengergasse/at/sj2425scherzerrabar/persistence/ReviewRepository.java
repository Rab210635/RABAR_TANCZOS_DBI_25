package spengergasse.at.sj2425scherzerrabar.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spengergasse.at.sj2425scherzerrabar.domain.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findReviewByReviewApiKey(String apiKey);
}

// An den Herr Lehrern: Wir haben uns dazu entschieden, dass wir Review und Subscription
// erstmals nicht umsetzten um uns auf die Richtigkeit und Vollständigkeit
// der anderen Klassen fokussieren zu können