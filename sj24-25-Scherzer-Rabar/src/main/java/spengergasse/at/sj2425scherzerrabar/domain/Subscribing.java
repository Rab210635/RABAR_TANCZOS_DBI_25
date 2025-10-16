package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class Subscribing {
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_subscribings_2_branch"))
    private Branch mainBranch;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_subscribings_2_library_subscription"))
    private LibrarySubscription librarySubscription;
}
