package com.opticmanager.repository;

import com.opticmanager.entity.Prescription;
import com.opticmanager.entity.PrescriptionStatus;
import com.opticmanager.entity.PrescriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(UUID patientId);

    List<Prescription> findByPatientIdAndStatus(UUID patientId, PrescriptionStatus status);

    List<Prescription> findByPatientIdAndPrescriptionType(UUID patientId, PrescriptionType type);

    List<Prescription> findByPrescriptionDateBetween(LocalDate start, LocalDate end);
}
