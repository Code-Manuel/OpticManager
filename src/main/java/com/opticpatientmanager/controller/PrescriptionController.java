package com.opticpatientmanager.controller;

import com.opticpatientmanager.dto.PrescriptionRequest;
import com.opticpatientmanager.dto.PrescriptionResponse;
import com.opticpatientmanager.model.OpticalPrescription;
import com.opticpatientmanager.service.PdfService;
import com.opticpatientmanager.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PdfService pdfService;

    @GetMapping
    public List<PrescriptionResponse> list(@PathVariable Long patientId) {
        return prescriptionService.findByPatient(patientId);
    }

    @GetMapping("/{id}")
    public PrescriptionResponse get(@PathVariable Long patientId, @PathVariable Long id) {
        return prescriptionService.findById(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long patientId, @PathVariable Long id) {
        OpticalPrescription rx = prescriptionService.findEntityWithPatient(id);
        try {
            byte[] pdf = pdfService.generatePrescriptionPdf(rx);
            String filename = "prescrizione_" + rx.getVisitDate() + ".pdf";
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(pdf);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la generazione del PDF", e);
        }
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponse> create(
            @PathVariable Long patientId,
            @Valid @RequestBody PrescriptionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.create(patientId, req));
    }

    @PutMapping("/{id}")
    public PrescriptionResponse update(
            @PathVariable Long patientId,
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequest req) {
        return prescriptionService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long patientId, @PathVariable Long id) {
        prescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
