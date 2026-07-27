package com.opticpatientmanager.service;

import com.opticpatientmanager.dto.PrescriptionRequest;
import com.opticpatientmanager.dto.PrescriptionResponse;
import com.opticpatientmanager.exception.ResourceNotFoundException;
import com.opticpatientmanager.model.OpticalPrescription;
import com.opticpatientmanager.model.Patient;
import com.opticpatientmanager.repository.OpticalPrescriptionRepository;
import com.opticpatientmanager.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrescriptionService {

    private final OpticalPrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;

    public List<PrescriptionResponse> findByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByVisitDateDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PrescriptionResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public PrescriptionResponse create(Long patientId, PrescriptionRequest req) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paziente non trovato con id: " + patientId));
        OpticalPrescription p = OpticalPrescription.builder()
                .patient(patient)
                .visitDate(req.getVisitDate())
                .sphereOD(req.getSphereOD())
                .cylinderOD(req.getCylinderOD())
                .axisOD(req.getAxisOD())
                .sphereOS(req.getSphereOS())
                .cylinderOS(req.getCylinderOS())
                .axisOS(req.getAxisOS())
                .pupillaryDistance(req.getPupillaryDistance())
                .notes(req.getNotes())
                .build();
        return toResponse(prescriptionRepository.save(p));
    }

    @Transactional
    public PrescriptionResponse update(Long id, PrescriptionRequest req) {
        OpticalPrescription p = getOrThrow(id);
        p.setVisitDate(req.getVisitDate());
        p.setSphereOD(req.getSphereOD());
        p.setCylinderOD(req.getCylinderOD());
        p.setAxisOD(req.getAxisOD());
        p.setSphereOS(req.getSphereOS());
        p.setCylinderOS(req.getCylinderOS());
        p.setAxisOS(req.getAxisOS());
        p.setPupillaryDistance(req.getPupillaryDistance());
        p.setNotes(req.getNotes());
        return toResponse(prescriptionRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        prescriptionRepository.delete(getOrThrow(id));
    }

    private OpticalPrescription getOrThrow(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrizione non trovata con id: " + id));
    }

    public OpticalPrescription findEntityWithPatient(Long id) {
        return prescriptionRepository.findByIdWithPatient(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrizione non trovata con id: " + id));
    }

    private PrescriptionResponse toResponse(OpticalPrescription p) {
        return PrescriptionResponse.builder()
                .id(p.getId())
                .patientId(p.getPatient().getId())
                .visitDate(p.getVisitDate())
                .sphereOD(p.getSphereOD())
                .cylinderOD(p.getCylinderOD())
                .axisOD(p.getAxisOD())
                .sphereOS(p.getSphereOS())
                .cylinderOS(p.getCylinderOS())
                .axisOS(p.getAxisOS())
                .pupillaryDistance(p.getPupillaryDistance())
                .notes(p.getNotes())
                .build();
    }
}
