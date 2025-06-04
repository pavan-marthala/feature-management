package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.feature.management.entity.FeatureStrategyEntity;


@EqualsAndHashCode(callSuper = true)
@Entity(name = "http_request_feature_strategy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("HTTP_REQUEST_FEATTURE_STRATEGY")
public class HTTPRequestFeatureStrategyEntity extends FeatureStrategyEntity {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "header_name")),
            @AttributeOverride(name = "value", column = @Column(name = "header_value"))
    })
    private HTTPHeader header;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "path", column = @Column(name = "request_body_path")),
            @AttributeOverride(name = "value", column = @Column(name = "request_body_value"))
    })
    private HTTPRequestBody requestBody;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "query_name")),
            @AttributeOverride(name = "value", column = @Column(name = "query_value"))
    })
    private HTTPQuery query;

    @PrePersist
    @PreUpdate
    private void validateExactlyOneSet() {
        int count = 0;
        if (header != null) count++;
        if (requestBody != null) count++;
        if (query != null) count++;
        if (count != 1) {
            throw new IllegalStateException("Exactly one of header, requestBody, or query must be set.");
        }
    }
}
