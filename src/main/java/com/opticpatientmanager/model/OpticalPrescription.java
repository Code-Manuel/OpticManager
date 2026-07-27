package com.opticpatientmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "optical_prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpticalPrescription {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prescription_seq")
    @SequenceGenerator(name = "prescription_seq", sequenceName = "prescription_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_prescription_patient"))
    private Patient patient;

    @NotNull(message = "La data della visita è obbligatoria")
    @PastOrPresent(message = "La data della visita non può essere futura")
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    // ── Occhio destro (OD = Oculus Dexter) ──

    /** Sfera OD in diottrie, es. -2.50 */
    @DecimalMin(value = "-20.00", message = "Sfera OD fuori range")
    @DecimalMax(value = "20.00",  message = "Sfera OD fuori range")
    @Column(name = "sphere_od", precision = 4, scale = 2)
    private BigDecimal sphereOD;

    /** Cilindro OD in diottrie */
    @DecimalMin(value = "-10.00", message = "Cilindro OD fuori range")
    @DecimalMax(value = "10.00",  message = "Cilindro OD fuori range")
    @Column(name = "cylinder_od", precision = 4, scale = 2)
    private BigDecimal cylinderOD;

    /** Asse OD in gradi (0-180) */
    @Min(value = 0,   message = "Asse OD deve essere >= 0")
    @Max(value = 180, message = "Asse OD deve essere <= 180")
    @Column(name = "axis_od")
    private Integer axisOD;

    // ── Occhio sinistro (OS = Oculus Sinister) ──

    @DecimalMin(value = "-20.00", message = "Sfera OS fuori range")
    @DecimalMax(value = "20.00",  message = "Sfera OS fuori range")
    @Column(name = "sphere_os", precision = 4, scale = 2)
    private BigDecimal sphereOS;

    @DecimalMin(value = "-10.00", message = "Cilindro OS fuori range")
    @DecimalMax(value = "10.00",  message = "Cilindro OS fuori range")
    @Column(name = "cylinder_os", precision = 4, scale = 2)
    private BigDecimal cylinderOS;

    @Min(value = 0,   message = "Asse OS deve essere >= 0")
    @Max(value = 180, message = "Asse OS deve essere <= 180")
    @Column(name = "axis_os")
    private Integer axisOS;

    /** Distanza interpupillare in mm */
    @DecimalMin(value = "50.0", message = "PD fuori range fisiologico")
    @DecimalMax(value = "80.0", message = "PD fuori range fisiologico")
    @Column(name = "pupillary_distance", precision = 4, scale = 1)
    private BigDecimal pupillaryDistance;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @OneToOne(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Document document;
}
