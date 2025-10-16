package spengergasse.at.sj2425scherzerrabar.domain;

import jakarta.persistence.Embeddable;

@SuppressWarnings("JpaObjectClassSignatureInspection")
@Embeddable
public record ApiKey (String apiKey){
    @Override
    public String toString() {
        return apiKey;
    }
};
