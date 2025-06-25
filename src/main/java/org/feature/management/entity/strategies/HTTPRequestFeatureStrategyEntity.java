package org.feature.management.entity.strategies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.feature.management.entity.FeatureStrategyEntity;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HTTPRequestFeatureStrategyEntity implements FeatureStrategyEntity {

    private HTTPHeader header;

    private HTTPRequestBody requestBody;

    private HTTPQuery query;

}
