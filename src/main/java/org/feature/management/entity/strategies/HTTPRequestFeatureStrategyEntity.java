package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.*;
import org.feature.management.entity.FeatureStrategyEntity;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HTTPRequestFeatureStrategyEntity extends FeatureStrategyEntity {

    private HTTPHeader header;

    private HTTPRequestBody requestBody;

    private HTTPQuery query;

}
