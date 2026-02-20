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
    // This must match the variable name 'doctorId' in your Patient model
    List<Patient> findByDoctorId(String doctorId); 

    // Custom search method: Finds patients for a specific doctor by Name OR Patient ID
List<Patient> findByDoctorIdAndFullNameContainingIgnoreCaseOrDoctorIdAndPatientIdContainingIgnoreCase(
    String doctorId1, String name, 
    String doctorId2, String patientId
);


}