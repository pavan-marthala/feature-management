package org.feature.management.models;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@ToString()
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerModel {

    @NotBlank(message = "user name is mandatory")
    private String username;
    @NotBlank(message = "email is mandatory")
    @Email(message = "should be a valid email address")
    private String email;
}
