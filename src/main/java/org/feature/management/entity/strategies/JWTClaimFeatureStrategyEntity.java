package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.feature.management.entity.FeatureStrategyEntity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "jwt_claim_feature_strategy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("JWT_CLAIM_FEATURE_STRATEGY")
public class JWTClaimFeatureStrategyEntity extends FeatureStrategyEntity {

    @ElementCollection
    private List<JWTClaimKeyValue> claims;

    @Embedded
    private JWTClaimRoles roles;

    @Embedded
    private JWTClaimScopes scopes;
}
