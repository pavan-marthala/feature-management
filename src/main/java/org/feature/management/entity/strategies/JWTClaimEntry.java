package org.feature.management.entity.strategies;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTClaimEntry implements Serializable {

    private String name;
    private String value;

    private List<String> roles;

    private List<String> scopes;

}
