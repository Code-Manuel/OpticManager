package com.opticpatientmanager.repository;

import com.opticpatientmanager.model.OpticalPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OpticalPrescriptionRepository extends JpaRepository<OpticalPrescription, Long> {

    List<OpticalPrescription> findByPatientIdOrderByVisitDateDesc(Long patientId);

    @Query("SELECT p FROM OpticalPrescription p JOIN FETCH p.patient WHERE p.id = :id")
    Optional<OpticalPrescription> findByIdWithPatient(@Param("id") Long id);
}
