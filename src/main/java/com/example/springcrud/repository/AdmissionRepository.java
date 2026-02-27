package com.example.springcrud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.springcrud.model.Admission;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionRepository extends MongoRepository<Admission, String> {

    // Find a specific admission by its ADM-XXX ID
    Optional<Admission> findFirstByAdmissionId(String admissionId);

    // Get all admission history for a specific patient
    List<Admission> findByPatientId(String patientId);

    // Get active/discharged patients
    List<Admission> findByStatusIgnoreCase(String status);

    // Find admissions within a specific date range
    List<Admission> findByAdmissionDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    // Find discharges within a specific date range
    List<Admission> findByDischargeDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}