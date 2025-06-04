package org.feature.management.entity.strategies;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HTTPRequestBody {
    private String path;
    private String value;
}

