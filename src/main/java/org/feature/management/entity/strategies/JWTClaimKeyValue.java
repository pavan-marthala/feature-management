package org.feature.management.entity.strategies;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaimKeyValue implements Serializable {
    private String name;
    private String value;
}
