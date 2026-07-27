package com.opticmanager.dto;

import com.opticmanager.entity.PrescriptionStatus;
import com.opticmanager.entity.PrescriptionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class PrescriptionDTO {

    private UUID id;

    @NotNull(message = "L'identificativo del paziente è obbligatorio")
    private UUID patientId;

    private String patientFullName;

    @NotNull(message = "La data della prescrizione è obbligatoria")
    private LocalDate prescriptionDate;

    private String prescriberName;

    @NotNull(message = "Il tipo di prescrizione è obbligatorio")
    private PrescriptionType prescriptionType;

    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;
    private String clinicalNotes;

    @Valid
    private List<LensDetailsDTO> lensDetails = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
