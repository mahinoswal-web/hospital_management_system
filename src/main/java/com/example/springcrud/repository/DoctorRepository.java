package com.example.springcrud.repository;

import com.example.springcrud.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    // Ensure this matches the variable name 'phone' in Doctor.java
    Optional<Doctor> findByPhone(String phone);
}