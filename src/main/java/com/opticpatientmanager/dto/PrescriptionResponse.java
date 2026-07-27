package com.opticpatientmanager.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponse {

    private Long id;
    private Long patientId;
    private LocalDate visitDate;
    private BigDecimal sphereOD;
    private BigDecimal cylinderOD;
    private Integer axisOD;
    private BigDecimal sphereOS;
    private BigDecimal cylinderOS;
    private Integer axisOS;
    private BigDecimal pupillaryDistance;
    private String notes;
}
