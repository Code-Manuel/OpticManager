package com.opticmanager.dto;

import com.opticmanager.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PatientDTO {

    private UUID id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    private LocalDate birthDate;
    private Gender gender;

    @Email(message = "Formato email non valido")
    private String email;

    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String fiscalCode;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
