package com.opticpatientmanager.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    @Pattern(
            regexp = "^[A-Z]{6}[0-9]{2}[A-EHLMPRST]{1}[0-9]{2}[A-Z]{1}[0-9]{3}[A-Z]{1}$",
            message = "Formato codice fiscale non valido"
    )
    private String fiscalCode;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate birthDate;

    @Pattern(regexp = "^(\\+39)?[0-9\\s\\-]{6,15}$", message = "Numero di telefono non valido")
    private String phone;

    @Email(message = "Indirizzo e-mail non valido")
    @Size(max = 150)
    private String email;
}
