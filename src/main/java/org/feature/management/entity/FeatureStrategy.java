package org.feature.management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.feature.management.enums.StrategyType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@ToString(exclude = "feature")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureStrategy {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private StrategyType strategyType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    @JsonBackReference
    private Feature feature;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
}
