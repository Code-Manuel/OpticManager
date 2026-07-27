package com.opticpatientmanager.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {

    @NotNull(message = "La data della visita è obbligatoria")
    @PastOrPresent(message = "La data della visita non può essere futura")
    private LocalDate visitDate;

    @DecimalMin(value = "-20.00", message = "Sfera OD fuori range")
    @DecimalMax(value = "20.00",  message = "Sfera OD fuori range")
    private BigDecimal sphereOD;

    @DecimalMin(value = "-10.00", message = "Cilindro OD fuori range")
    @DecimalMax(value = "10.00",  message = "Cilindro OD fuori range")
    private BigDecimal cylinderOD;

    @Min(value = 0,   message = "Asse OD deve essere >= 0")
    @Max(value = 180, message = "Asse OD deve essere <= 180")
    private Integer axisOD;

    @DecimalMin(value = "-20.00", message = "Sfera OS fuori range")
    @DecimalMax(value = "20.00",  message = "Sfera OS fuori range")
    private BigDecimal sphereOS;

    @DecimalMin(value = "-10.00", message = "Cilindro OS fuori range")
    @DecimalMax(value = "10.00",  message = "Cilindro OS fuori range")
    private BigDecimal cylinderOS;

    @Min(value = 0,   message = "Asse OS deve essere >= 0")
    @Max(value = 180, message = "Asse OS deve essere <= 180")
    private Integer axisOS;

    @DecimalMin(value = "50.0", message = "PD fuori range fisiologico")
    @DecimalMax(value = "80.0", message = "PD fuori range fisiologico")
    private BigDecimal pupillaryDistance;

    @Size(max = 500)
    private String notes;
}
