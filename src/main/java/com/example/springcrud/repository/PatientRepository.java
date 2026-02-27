package com.example.springcrud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.springcrud.model.Patient;
import java.util.List; 
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    // Used for login
    Optional<Patient> findByPhone(String phone);

    // Used to filter patients on the Doctor Dashboard
    List<Patient> findByDoctorId(String doctorId); 

    // Custom search method: Finds patients for a specific doctor by Name OR Patient ID
    List<Patient> findByDoctorIdAndFullNameContainingIgnoreCaseOrDoctorIdAndPatientIdContainingIgnoreCase(
        String doctorId1, String name, 
        String doctorId2, String patientId
    );

    // Bypasses the MongoDB _id and searches safely by your custom PAT-XXX ID
    Optional<Patient> findFirstByPatientId(String patientId);

    // ==========================================
    // ADDED TO FIX PRESCRIPTION CONTROLLER!
    // ==========================================
    Optional<Patient> findByPatientId(String patientId);

}