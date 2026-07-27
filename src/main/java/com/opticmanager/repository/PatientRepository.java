package com.opticmanager.repository;

import com.opticmanager.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.fiscalCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Patient> search(@Param("query") String query);

    Optional<Patient> findByFiscalCode(String fiscalCode);

    boolean existsByFiscalCode(String fiscalCode);
}
