package org.feature.management.models;

import lombok.*;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMessage {
    private int statusCode;
    private String message;
    private String details;
    private Date timestamp;
}
