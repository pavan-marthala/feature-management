package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import org.feature.management.entity.FeatureStrategyEntity;
import org.feature.management.enums.StrategyType;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "jwt_claim_feature_strategy")
@ToString(exclude = {"claims"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("JWT_CLAIM_FEATURE_STRATEGY")
public class JWTClaimFeatureStrategyEntity extends FeatureStrategyEntity {

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<JWTClaimEntry> claims = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (claims != null) {
            claims.forEach(claim -> {
                if (claim.getStrategy() == null) {
                    claim.setStrategy(this);
                }
            });
        }
    }

    public void addClaim(JWTClaimEntry claim) {
        if (claims == null) {
            claims = new ArrayList<>();
        }
        claims.add(claim);
        claim.setStrategy(this);
    }

    public void removeClaim(JWTClaimEntry claim) {
        if (claims != null) {
            claims.remove(claim);

        }
    }
    public void setClaims(List<JWTClaimEntry> claims) {
        // Clear existing claims
        if (this.claims != null) {
            this.claims.forEach(claim -> claim.setStrategy(null));
        }

        this.claims = claims != null ? new ArrayList<>(claims) : new ArrayList<>();

        // Set strategy reference for all claims
        this.claims.forEach(claim -> claim.setStrategy(this));
    }
}
