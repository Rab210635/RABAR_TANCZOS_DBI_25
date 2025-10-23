package spengergasse.at.sj2425scherzerrabar.dtos;


import spengergasse.at.sj2425scherzerrabar.domain.jpa.Address;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import spengergasse.at.sj2425scherzerrabar.domain.Branch;

public record BranchDto(String apiKey, String libraryApiKey, String address) {
    public static BranchDto branchDtoFromBranch(Branch branch) {
      return new BranchDto(
                branch.getBranchApiKey().apiKey(),branch.getLibrary().getLibraryApiKey().apiKey(),branch.getAddress().toString()
        );
    }

    public BranchDto(ApiKey apiKey, ApiKey libraryApiKey, Address address) {
        this(apiKey.apiKey(), libraryApiKey.apiKey(), address.toString());
    }
}
