package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.feature.management.entity.FeatureStrategyEntity;
import org.feature.management.enums.StrategyType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "jwt_claim_feature_strategy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("JWT_CLAIM_FEATURE_STRATEGY")
public class JWTClaimFeatureStrategyEntity extends FeatureStrategyEntity {

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<JWTClaimEntry> claims;

    @Transient
    private final StrategyType type = StrategyType.JWT_CLAIM_FEATURE_STRATEGY;

    public StrategyType getType() {
        return type;
    }

    public void addClaim(JWTClaimEntry claim) {
        claims.add(claim);
        claim.setStrategy(this);
    }

    public void removeClaim(JWTClaimEntry claim) {
        claims.remove(claim);
        claim.setStrategy(null);
    }
}
