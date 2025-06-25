package org.feature.management.entity.strategies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.feature.management.entity.FeatureStrategyEntity;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaimFeatureStrategyEntity implements FeatureStrategyEntity {

    private List<JWTClaimEntry> claims = new ArrayList<>();

}
