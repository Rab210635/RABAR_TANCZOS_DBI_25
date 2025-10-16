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
import spengergasse.at.sj2425scherzerrabar.commands.BranchCommand;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Branch;
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;
import spengergasse.at.sj2425scherzerrabar.dtos.LibraryDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BranchRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.LibraryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BranchServiceTest {
    private @Mock BranchRepository branchRepository;
    private @Mock LibraryRepository libraryRepository;

    private BranchService branchService;

    @BeforeEach
    void setUp() {
        branchService = new BranchService(branchRepository, libraryRepository);
    }


    @Test
    void can_create_branch() {
        var library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        var branch = FixturesFactory.filiale();

        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.of(library));
        when(branchRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());

        var createdBranch = branchService.createBranch(new BranchCommand(new ApiKey("BranchApiKey").apiKey(),
                library.getLibraryApiKey().apiKey(),FixturesFactory.address2().toString()));

        assertThat(createdBranch).isNotNull();
        assertThat(createdBranch.libraryApiKey()).isEqualTo(library.getLibraryApiKey().apiKey());
        assertThat(createdBranch.address()).isEqualTo(branch.getAddress().toString());
    }

    @Test
    void cant_create_branch_with_invalid_library() {
        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> branchService.createBranch(new BranchCommand(new ApiKey("BranchApiKey").apiKey(),
                new ApiKey("NonExistentApiKey").apiKey(),FixturesFactory.address2().toString())))
                .isInstanceOf(BranchService.BranchServiceException.class)
                .hasMessageContaining("Library with api key (NonExistentApiKey) not existent");
    }

    @Test
    void can_update_branch() {
        var branch = FixturesFactory.filiale();
        var library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        var newAddress = FixturesFactory.libraryAddress();

        when(branchRepository.findBranchByBranchApiKey(new ApiKey("validBranchApiKey"))).thenReturn(Optional.of(branch));
        when(branchRepository.save(any())).thenReturn(branch);
        when(libraryRepository.findLibraryByLibraryApiKey(any())).thenReturn(Optional.of(library));

        var updatedBranch = branchService.updateBranch(new BranchCommand(new ApiKey("validBranchApiKey").apiKey(),new ApiKey("validnewLibrary").apiKey(),newAddress.toString()));

        assertThat(updatedBranch).isNotNull();
        assertThat(updatedBranch.address()).isEqualTo(newAddress.toString());
    }

    @Test
    void cant_update_nonexistent_branch() {
        when(branchRepository.findBranchByBranchApiKey(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> branchService.updateBranch(new BranchCommand(new ApiKey("invalidKey").apiKey(),
                new ApiKey("not relevant").apiKey(), FixturesFactory.libraryAddress().toString())))
                .isInstanceOf(BranchService.BranchServiceException.class)
                .hasMessageContaining("Branch with api key (invalidKey) not existent");
    }

    @Test
    void can_delete_branch() {
        var branch = FixturesFactory.filiale();

        when(branchRepository.findBranchByBranchApiKey(new ApiKey("validBranchApiKey"))).thenReturn(Optional.of(branch));
        doNothing().when(branchRepository).delete(branch);

        branchService.deleteBranch("validBranchApiKey");

        verify(branchRepository, times(1)).delete(branch);
    }

    @Test
    void cant_delete_nonexistent_branch() {
        when(branchRepository.findBranchByBranchApiKey(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> branchService.deleteBranch("invalidBranchApiKey"))
                .isInstanceOf(BranchService.BranchServiceException.class)
                .hasMessageContaining("Branch with api key (invalidBranchApiKey) not existent");
    }

    @Test
    void can_get_all_branches() {
        var branch1 = FixturesFactory.filiale();
        var branch2 = FixturesFactory.filiale();

        when(branchRepository.findAllProjected()).thenReturn(List.of(BranchDto.branchDtoFromBranch(branch1),BranchDto.branchDtoFromBranch(branch2)));

        var branches = branchService.getAllBranches();

        assertThat(branches).hasSize(2);
    }

    @Test
    void can_get_branch_by_api_key() {
        var branch = FixturesFactory.filiale();
        when(branchRepository.findProjectedBranchByBranchApiKey("validBranchApiKey")).thenReturn(Optional.of(BranchDto.branchDtoFromBranch(branch)));

        var foundBranch = branchService.getBranchByApiKey("validBranchApiKey");

        assertThat(foundBranch).isNotNull();
        assertThat(foundBranch.libraryApiKey()).isEqualTo(branch.getLibrary().getLibraryApiKey().apiKey());
        assertThat(foundBranch.address()).isEqualTo(branch.getAddress().toString());
    }

    @Test
    void cant_get_branch_by_invalid_api_key() {
        assertThatThrownBy(() -> branchService.getBranchByApiKey("invalidBranchApiKey"))
                .isInstanceOf(BranchService.BranchServiceException.class)
                .hasMessageContaining("Branch with api key (invalidBranchApiKey) not existent");
    }

    @Test
    void can_get_branches_by_library() {
        var library = FixturesFactory.thalia(FixturesFactory.address2(), List.of(FixturesFactory.libBook(FixturesFactory.book(FixturesFactory.author()))));
        var branch1 = new Branch(library,FixturesFactory.libraryAddress());
        var branch2 = new Branch(library,FixturesFactory.address2());

        when(libraryRepository.findProjectedByLibraryApiKey("validLibraryApiKey")).thenReturn(Optional.of(LibraryDto.libraryDtoFromLibrary(library)));
        when(branchRepository.findProjectedBranchesByLibrary(library.getLibraryApiKey().apiKey())).thenReturn(List.of(BranchDto.branchDtoFromBranch(branch1),BranchDto.branchDtoFromBranch(branch2)));

        var branches = branchService.getBranchesByLibrary("validLibraryApiKey");

        assertThat(branches).hasSize(2);
        assertThat(branches.getFirst().libraryApiKey()).isEqualTo(library.getLibraryApiKey().apiKey());
    }

    @Test
    void cant_get_branches_by_invalid_library() {
        assertThatThrownBy(() -> branchService.getBranchesByLibrary("invalidLibraryApiKey"))
                .isInstanceOf(BranchService.BranchServiceException.class)
                .hasMessageContaining("Library with api key (invalidLibraryApiKey) not existent");
    }
}
