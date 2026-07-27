package com.opticpatientmanager.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String fiscalCode;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
}
