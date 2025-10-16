package spengergasse.at.sj2425scherzerrabar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spengergasse.at.sj2425scherzerrabar.commands.BranchCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Address;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Branch;
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;
import spengergasse.at.sj2425scherzerrabar.persistence.BranchRepository;
import spengergasse.at.sj2425scherzerrabar.persistence.LibraryRepository;
import spengergasse.at.sj2425scherzerrabar.presentation.RestController.LoggingController;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BranchService {

    private final BranchRepository branchRepository;
    private final LibraryRepository libraryRepository;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);


    public BranchService(BranchRepository branchRepository, LibraryRepository libraryRepository) {
        this.branchRepository = branchRepository;
        this.libraryRepository = libraryRepository;
    }

    @Transactional
    public BranchDto createBranch(BranchCommand branchDto) {
        logger.debug("entered createBranch");
        var library = libraryRepository.findLibraryByLibraryApiKey(new ApiKey(branchDto.libraryApiKey()))
                .orElseThrow(()-> BranchServiceException.noLibraryForApikey(branchDto.libraryApiKey()));
        return BranchDto.branchDtoFromBranch(
                branchRepository.save(new Branch(library, Address.addressFromString(branchDto.address()))));
    }

    @Transactional
    public BranchDto updateBranch(BranchCommand command) {
        logger.debug("entered updateBranch");
        Branch branch = branchRepository.findBranchByBranchApiKey(new ApiKey(command.apiKey()))
                .orElseThrow(() -> BranchServiceException.noBranchForApiKey(command.apiKey()));

        var library = libraryRepository.findLibraryByLibraryApiKey(new ApiKey(command.libraryApiKey()))
                .orElseThrow(() ->BranchServiceException.noLibraryForApikey(command.libraryApiKey()));

        branch.setLibrary(library);
        branch.setAddress(Address.addressFromString(command.address()));

        branch = branchRepository.save(branch);
        return BranchDto.branchDtoFromBranch(branch);
    }

    @Transactional
    public void deleteBranch(String branchApiKey) {
        logger.debug("entered deleteBranch");
        Branch branch = branchRepository.findBranchByBranchApiKey(new ApiKey(branchApiKey))
                .orElseThrow(() -> BranchServiceException.noBranchForApiKey(branchApiKey));
        branchRepository.delete(branch);
    }

    public List<BranchDto> getAllBranches() {
        logger.debug("entered getAllBranches");
        return branchRepository.findAllProjected();
    }

    public BranchDto getBranchByApiKey(String branchApiKey) {
        logger.debug("entered getBranchByApiKey");
        return branchRepository.findProjectedBranchByBranchApiKey(branchApiKey)
                .orElseThrow(() -> BranchServiceException.noBranchForApiKey(branchApiKey));
    }

    public List<BranchDto> getBranchesByLibrary(String libraryApiKey) {
        logger.debug("entered getBranchesByLibrary");
        var library = libraryRepository.findProjectedByLibraryApiKey(libraryApiKey)
                .orElseThrow(() -> BranchServiceException.noLibraryForApikey(libraryApiKey));
        return branchRepository.findProjectedBranchesByLibrary(library.apiKey());
    }

    public static class BranchServiceException extends RuntimeException
    {
        public BranchServiceException(String message)
        {
            super(message);
        }

        public static BranchServiceException noBranchForApiKey(String apiKey)
        {
            return new BranchServiceException("Branch with api key (%s) not existent".formatted(apiKey));
        }

        public static BranchServiceException noLibraryForApikey(String apiKey)
        {
            return new BranchServiceException("Library with api key (%s) not existent".formatted(apiKey));
        }
    }
}
