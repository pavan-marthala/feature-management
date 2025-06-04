package org.feature.management.entity.strategies;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaimScopes implements Serializable {
    @ElementCollection
    private List<String> scopes;
}


