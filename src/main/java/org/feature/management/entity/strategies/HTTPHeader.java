package org.feature.management.entity.strategies;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HTTPHeader {
    private String name;
    private String value;
}
