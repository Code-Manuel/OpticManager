package com.opticpatientmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients", uniqueConstraints = {
        @UniqueConstraint(name = "uk_patient_fiscal_code", columnNames = "fiscal_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_seq")
    @SequenceGenerator(name = "patient_seq", sequenceName = "patient_id_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 100)
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 100)
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Codice Fiscale italiano – 16 caratteri alfanumerici nel formato standard.
     * Regex: 6 lettere + 2 cifre + 1 lettera + 2 cifre + 1 lettera + 3 alfanumerici + 1 lettera
     */
    @NotBlank(message = "Il codice fiscale è obbligatorio")
    @Pattern(
            regexp = "^[A-Z]{6}[0-9]{2}[A-EHLMPRST]{1}[0-9]{2}[A-Z]{1}[0-9]{3}[A-Z]{1}$",
            message = "Formato codice fiscale non valido"
    )
    @Column(name = "fiscal_code", nullable = false, length = 16)
    private String fiscalCode;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Pattern(regexp = "^(\\+39)?[0-9\\s\\-]{6,15}$", message = "Numero di telefono non valido")
    @Column(name = "phone", length = 20)
    private String phone;

    @Email(message = "Indirizzo e-mail non valido")
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OpticalPrescription> prescriptions = new ArrayList<>();
}
