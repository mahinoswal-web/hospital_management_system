package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.Patient;
import com.example.springcrud.repository.PatientRepository;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientRepository.save(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    // ================= READ ALL (With Filters) =================
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String emailAddress) {

        List<Patient> patients = patientRepository.findAll();

        List<Patient> filteredPatients = patients.stream()
                .filter(p -> patientId == null || (p.getPatientId() != null && patientId.equalsIgnoreCase(p.getPatientId())))
                .filter(p -> fullName == null || (p.getFullName() != null && p.getFullName().toLowerCase().contains(fullName.toLowerCase())))
                .filter(p -> gender == null || (p.getGender() != null && gender.equalsIgnoreCase(p.getGender())))
                .filter(p -> {
                    if (bloodGroup == null) return true;
                    if (p.getBloodGroup() == null) return false;
                    String decodedBloodGroup = bloodGroup.replace(" ", "+");
                    return decodedBloodGroup.equalsIgnoreCase(p.getBloodGroup());
                })
                .filter(p -> phoneNumber == null || (p.getPhone() != null && p.getPhone().contains(phoneNumber)))
                .filter(p -> emailAddress == null || (p.getEmailAddress() != null && p.getEmailAddress().equalsIgnoreCase(emailAddress)))
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredPatients, HttpStatus.OK);
    }

    // ================= READ BY PATIENT ID =================
    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String patientId) {
        Optional<Patient> patient = patientRepository.findFirstByPatientId(patientId);
        return patient.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ================= READ BY DOCTOR ID =================
    @GetMapping("/byDoctor/{doctorId}")
    public ResponseEntity<List<Patient>> getPatientsByDoctorId(@PathVariable String doctorId) {
        List<Patient> patients = patientRepository.findByDoctorId(doctorId);
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    // ================= UPDATE =================
    @PutMapping("/{patientId}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable String patientId,
            @RequestBody Patient patient) {

        return patientRepository.findFirstByPatientId(patientId).map(patientToUpdate -> {
            
            patientToUpdate.setFullName(patient.getFullName());
            patientToUpdate.setDateOfBirth(patient.getDateOfBirth());
            patientToUpdate.setGender(patient.getGender());
            patientToUpdate.setPhone(patient.getPhone());
            patientToUpdate.setEmailAddress(patient.getEmailAddress());
            patientToUpdate.setResidentialAddress(patient.getResidentialAddress());
            patientToUpdate.setEmergencyContact(patient.getEmergencyContact());
            patientToUpdate.setBloodGroup(patient.getBloodGroup());
            patientToUpdate.setAllergies(patient.getAllergies());
            patientToUpdate.setChronicDiseases(patient.getChronicDiseases());
            patientToUpdate.setCurrentMedications(patient.getCurrentMedications());
            patientToUpdate.setHeight(patient.getHeight());
            patientToUpdate.setWeight(patient.getWeight());
            patientToUpdate.setDoctorId(patient.getDoctorId());
            patientToUpdate.setCondition(patient.getCondition());

            Patient updatedPatient = patientRepository.save(patientToUpdate);
            return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
            
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ================= DELETE =================
    @DeleteMapping("/{patientId}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable String patientId) {
        try {
            Optional<Patient> patient = patientRepository.findFirstByPatientId(patientId);
            if (patient.isPresent()) {
                patientRepository.delete(patient.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}