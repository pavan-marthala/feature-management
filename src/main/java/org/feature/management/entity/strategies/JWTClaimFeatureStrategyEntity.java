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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaimFeatureStrategyEntity extends FeatureStrategyEntity {

    private List<JWTClaimEntry> claims = new ArrayList<>();

}
