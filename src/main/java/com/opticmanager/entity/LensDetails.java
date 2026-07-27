package com.opticmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lens specification details for a prescription.
 * Modeled after the HL7 FHIR R4 VisionPrescription.lensSpecification component.
 */
@Entity
@Table(name = "lens_details")
@Getter
@Setter
@NoArgsConstructor
public class LensDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    /** Which eye this lens specification applies to (RIGHT or LEFT). */
    @Enumerated(EnumType.STRING)
    @Column(name = "eye", nullable = false)
    private Eye eye;

    /** Spherical power of the lens (in diopters). */
    @Column(name = "sphere", precision = 5, scale = 2)
    private BigDecimal sphere;

    /** Cylindrical power of the lens (in diopters). */
    @Column(name = "cylinder", precision = 5, scale = 2)
    private BigDecimal cylinder;

    /** Cylinder axis in degrees (0-180). */
    @Column(name = "axis")
    private Integer axis;

    /** Prismatic power of the lens (prism diopters). */
    @Column(name = "prism_amount", precision = 5, scale = 2)
    private BigDecimal prismAmount;

    /** The relative base direction for the prism (up, down, in, out). */
    @Column(name = "prism_base")
    private String prismBase;

    /** Power of the lens for near vision (add power, in diopters). */
    @Column(name = "add_power", precision = 5, scale = 2)
    private BigDecimal addPower;

    /** Contact lens power (in diopters). */
    @Column(name = "power", precision = 5, scale = 2)
    private BigDecimal power;

    /** Back curvature of the contact lens in millimeters. */
    @Column(name = "back_curve", precision = 5, scale = 2)
    private BigDecimal backCurve;

    /** Contact lens diameter in millimeters. */
    @Column(name = "diameter", precision = 5, scale = 2)
    private BigDecimal diameter;

    /** Lens wear duration (e.g., daily, monthly). */
    @Column(name = "duration")
    private String duration;

    /** Color of the contact lens. */
    @Column(name = "color")
    private String color;

    /** Brand of the contact lens. */
    @Column(name = "brand")
    private String brand;

    /** Additional notes for this lens. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
