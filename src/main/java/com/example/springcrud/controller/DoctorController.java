package com.example.springcrud.controller;

import com.example.springcrud.model.Doctor;
import com.example.springcrud.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

        @Autowired
        private DoctorRepository doctorRepository;

        // ================= CREATE =================
        // POST
        // http://localhost:8080/api/doctors
        @PostMapping
        public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
                Doctor savedDoctor = doctorRepository.save(doctor);
                return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
        }

        // ================= READ ALL =================
        // get all doctors
        // GET
        // http://localhost:8080/api/doctors
        @GetMapping
        public ResponseEntity<List<Doctor>> getAllDoctors() {
                List<Doctor> doctors = doctorRepository.findAll();
                return new ResponseEntity<>(doctors, HttpStatus.OK);
        }

        // ================= READ BY ID =================
        // get by selected id
        // GET
        // http://localhost:8080/api/doctors/DOC-102
        
        @GetMapping("/{doctorId}") // <-- TYPO FIXED HERE! Changed } to )
        public ResponseEntity<Doctor> getDoctorById(@PathVariable String doctorId) {
                // Using findFirstByDoctorId to bypass duplicate errors
                Optional<Doctor> doctor = doctorRepository.findFirstByDoctorId(doctorId);
                return doctor.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // ================= UPDATE =================
        // update doctor
        // PUT
        // http://localhost:8080/api/doctors/DOC-101
        @PutMapping("/{doctorId}")
        public ResponseEntity<Doctor> updateDoctor(
                        @PathVariable String doctorId,
                        @RequestBody Doctor doctor) {

                return doctorRepository.findFirstByDoctorId(doctorId)
                                .map(existing -> {
                                        existing.setName(doctor.getName());
                                        existing.setSpecialization(doctor.getSpecialization());
                                        existing.setExperience(doctor.getExperience());
                                        existing.setQualification(doctor.getQualification());
                                        existing.setGender(doctor.getGender());
                                        existing.setPhone(doctor.getPhone());
                                        existing.setEmail(doctor.getEmail());
                                        existing.setConsultationFee(doctor.getConsultationFee());
                                        existing.setAvailability(doctor.getAvailability());
                                        existing.setHospitalName(doctor.getHospitalName());
                                        existing.setRating(doctor.getRating());
                                        existing.setAddress(doctor.getAddress());

                                        Doctor updated = doctorRepository.save(existing);
                                        return new ResponseEntity<>(updated, HttpStatus.OK);
                                })
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        // ================= DELETE =================
        // DEL
        // http://localhost:8080/api/doctors/DOC-102
        @DeleteMapping("/{doctorId}")
        public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable String doctorId) {
                try {
                        Optional<Doctor> doctor = doctorRepository.findFirstByDoctorId(doctorId);
                        if (doctor.isPresent()) {
                                doctorRepository.delete(doctor.get());
                                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                        }
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }
}