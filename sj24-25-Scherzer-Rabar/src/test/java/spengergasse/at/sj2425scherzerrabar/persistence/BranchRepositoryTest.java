package spengergasse.at.sj2425scherzerrabar.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import spengergasse.at.sj2425scherzerrabar.FixturesFactory;
import spengergasse.at.sj2425scherzerrabar.TestcontainersConfiguration;
import spengergasse.at.sj2425scherzerrabar.domain.*;
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BranchRepositoryTest {

    @Autowired
    private BranchRepository branchRepository;

    private Branch branch;

    @BeforeEach
    void setUp() {
        var libraryAddress = FixturesFactory.libraryAddress();
        var books = List.of(new BookInLibraries(FixturesFactory.book(FixturesFactory.author()), 53));
        var library = FixturesFactory.thalia(libraryAddress, books);
        var address2 = FixturesFactory.address2();

        branch = new Branch(library, address2);
        branchRepository.saveAndFlush(branch); // save the branch for reuse in tests
    }

    @Test
    void can_save() {
        assertThat(branch.getBranchApiKey()).isNotNull();
    }

    @Test
    void default_constructor() {
        Branch defaultConstructed = new Branch();
        assertThat(defaultConstructed).isNotNull();
    }

    @Test
    void can_find_by_branchApiKey() {
        var foundBranch = branchRepository.findProjectedBranchByBranchApiKey(branch.getBranchApiKey().apiKey());

        assertThat(foundBranch).isPresent();
        assertThat(foundBranch.get().apiKey()).isEqualTo(branch.getBranchApiKey().apiKey());
        assertThat(foundBranch.get().libraryApiKey()).isEqualTo(branch.getLibrary().getLibraryApiKey().apiKey());
    }

    @Test
    void can_find_all_projected() {
        List<BranchDto> found = branchRepository.findAllProjected();

        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(dto -> dto.apiKey().equals(branch.getBranchApiKey().apiKey()));
    }

    @Test
    void can_find_by_library_api_key() {
        var foundBranches = branchRepository.findProjectedBranchesByLibrary(branch.getLibrary().getLibraryApiKey().apiKey());

        assertThat(foundBranches).isNotEmpty();
        assertThat(foundBranches).anyMatch(dto -> dto.libraryApiKey().equals(branch.getLibrary().getLibraryApiKey().apiKey()));
    }

    @Test
    void cannot_find_by_invalid_branchApiKey() {
        var found = branchRepository.findProjectedBranchByBranchApiKey("invalid-api-key");

        assertThat(found).isEmpty();
    }

    @Test
    void cannot_find_by_invalid_library_api_key() {
        var foundBranches = branchRepository.findProjectedBranchesByLibrary("invalid-api-key");

        assertThat(foundBranches).isEmpty();
    }

}
