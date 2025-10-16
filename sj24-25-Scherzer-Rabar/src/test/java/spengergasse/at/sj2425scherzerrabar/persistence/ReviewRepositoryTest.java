package spengergasse.at.sj2425scherzerrabar.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.TestcontainersConfiguration;
import spengergasse.at.sj2425scherzerrabar.domain.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository repository;
    private static Review review;

    @BeforeAll
    static void setUp() {
        var author = FixturesFactory.author();
        author.setPenname("Aaron");
        author.setEmailAddress(new EmailAddress("aaron@gmail.com"));
         review = FixturesFactory.review(author);    }

    @Test
    void can_save(){
        //act
        var saved = repository.saveAndFlush(review);
        //assert
        assertNotNull(saved);
    }

    @Test
    void default_constr(){
        Review defaultconstructed = new Review();
        assertNotNull(defaultconstructed);
    }
}