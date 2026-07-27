package com.opticpatientmanager.service;

import com.opticpatientmanager.dto.PatientRequest;
import com.opticpatientmanager.dto.PatientResponse;
import com.opticpatientmanager.exception.ResourceNotFoundException;
import com.opticpatientmanager.model.Patient;
import com.opticpatientmanager.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;

    public Page<PatientResponse> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<PatientResponse> search(String query, Pageable pageable) {
        return patientRepository
                .findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query, query, pageable)
                .map(this::toResponse);
    }

    public PatientResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public PatientResponse create(PatientRequest req) {
        Patient patient = Patient.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .fiscalCode(req.getFiscalCode())
                .birthDate(req.getBirthDate())
                .phone(req.getPhone())
                .email(req.getEmail())
                .build();
        return toResponse(patientRepository.save(patient));
    }

    @Transactional
    public PatientResponse update(Long id, PatientRequest req) {
        Patient patient = getOrThrow(id);
        patient.setFirstName(req.getFirstName());
        patient.setLastName(req.getLastName());
        patient.setFiscalCode(req.getFiscalCode());
        patient.setBirthDate(req.getBirthDate());
        patient.setPhone(req.getPhone());
        patient.setEmail(req.getEmail());
        return toResponse(patientRepository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        patientRepository.delete(getOrThrow(id));
    }

    private Patient getOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paziente non trovato con id: " + id));
    }

    private PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .fiscalCode(p.getFiscalCode())
                .birthDate(p.getBirthDate())
                .phone(p.getPhone())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
