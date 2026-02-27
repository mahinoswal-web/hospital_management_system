package com.example.springcrud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.springcrud.model.DocAppointment;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface DocAppointmentRepository extends MongoRepository<DocAppointment, String> {

    // Safely look up a specific appointment
    Optional<DocAppointment> findFirstByAppointmentId(String appointmentId);

    // Get all appointments for a specific doctor
    List<DocAppointment> findByDoctorId(String doctorId);

    // Get all appointments for a specific patient
    List<DocAppointment> findByPatientId(String patientId);

    // ============================================================
    // FIXED: Added this method to resolve your compilation error!
    // ============================================================
    List<DocAppointment> findByDoctorIdAndStatusIgnoreCase(String doctorId, String status);

    // ============================================================
    // SLOT VALIDATION: Check if a doctor is already booked
    // ============================================================
    boolean existsByDoctorIdAndAppointmentDateTime(String doctorId, LocalDateTime dateTime);
}