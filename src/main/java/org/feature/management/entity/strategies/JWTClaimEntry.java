package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jwt_claim_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"strategy"})
@EqualsAndHashCode(exclude = {"strategy"})
public class JWTClaimEntry implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String value;

    @ElementCollection
    @CollectionTable(name = "jwt_claim_roles", joinColumns = @JoinColumn(name = "claim_id"))
    @Column(name = "role")
    private List<String> roles;

    @ElementCollection
    @CollectionTable(name = "jwt_claim_scopes", joinColumns = @JoinColumn(name = "claim_id"))
    @Column(name = "scope")
    private List<String> scopes;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id")
    private JWTClaimFeatureStrategyEntity strategy;
}
