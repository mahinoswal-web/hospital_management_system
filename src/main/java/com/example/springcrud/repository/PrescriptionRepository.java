package com.example.springcrud.repository; // This must be here!

import com.example.springcrud.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    
    Optional<Prescription> findByPrescriptionId(String prescriptionId);
    List<Prescription> findByPatientId(String patientId);
    List<Prescription> findByDoctorId(String doctorId);
}