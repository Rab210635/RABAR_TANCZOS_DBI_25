package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import spengergasse.at.sj2425scherzerrabar.foundation.ApiKeyFactory;

@Entity
@Table(name = "branch")
public class Branch {
    @EmbeddedId
    BranchId branchId;
    @Embedded
    @AttributeOverride(name = "apiKey", column = @Column(name = "branch_api_key"))

    private ApiKey branchApiKey;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_branches_2_library"))
    private Library library;

    @Embedded
    private Address address;



    @SuppressWarnings("JpaObjectClassSignatureInspection")
    @Embeddable
    record BranchId (@GeneratedValue @NotNull Long id){}

    public Branch() {
        this.branchApiKey = new ApiKeyFactory().generate(30);
    }

    public Branch( Library library, Address address) {
        this.branchApiKey = new ApiKeyFactory().generate(30);
        this.library = library;
        this.address = address;
    }

    public ApiKey getBranchApiKey() {
        return branchApiKey;
    }


    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
