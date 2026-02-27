package com.example.springcrud.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.Admission;
import com.example.springcrud.repository.AdmissionRepository;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionRepository admissionRepository;

    // ==========================================
    // 1. ADMIT A PATIENT (Check-in)
    // ==========================================
    @PostMapping
    public ResponseEntity<Admission> createAdmission(@RequestBody Admission admission) {
        // Auto-generate ADM-XXX ID just like we did for Prescriptions!
        if (admission.getAdmissionId() == null || admission.getAdmissionId().isEmpty()) {
            long count = admissionRepository.count();
            admission.setAdmissionId(String.format("ADM-%03d", count + 1));
        }
        
        // Note: admissionDate and status ("ADMITTED") are automatically set by your model's constructor
        Admission savedAdmission = admissionRepository.save(admission);
        return new ResponseEntity<>(savedAdmission, HttpStatus.CREATED);
    }

    // ==========================================
    // 2. DISCHARGE A PATIENT (Checkout)
    // ==========================================
    @PutMapping("/discharge/{admissionId}")
    public ResponseEntity<Admission> dischargePatient(@PathVariable String admissionId) {
        Optional<Admission> admissionOpt = admissionRepository.findFirstByAdmissionId(admissionId);
        
        if (admissionOpt.isPresent()) {
            Admission admission = admissionOpt.get();
            
            // If they are already discharged, don't overwrite the original discharge date!
            if ("DISCHARGED".equalsIgnoreCase(admission.getStatus())) {
                return new ResponseEntity<>(admission, HttpStatus.OK);
            }

            admission.setStatus("DISCHARGED");
            admission.setDischargeDate(LocalDateTime.now()); // Stamps exact checkout time
            
            Admission updatedAdmission = admissionRepository.save(admission);
            return new ResponseEntity<>(updatedAdmission, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ==========================================
    // 3. GET ACTIVE ADMISSIONS (Hospital Capacity)
    // ==========================================
    @GetMapping("/active")
    public ResponseEntity<List<Admission>> getActiveAdmissions() {
        List<Admission> activeAdmissions = admissionRepository.findByStatusIgnoreCase("ADMITTED");
        
        if (activeAdmissions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(activeAdmissions, HttpStatus.OK);
    }

    // ==========================================
    // 4. PATIENT'S ADMISSION HISTORY 
    // ==========================================
    @GetMapping("/history/{patientId}")
    public ResponseEntity<List<Admission>> getPatientHistory(@PathVariable String patientId) {
        // This lets a doctor see every time a specific patient has visited the hospital
        List<Admission> history = admissionRepository.findByPatientId(patientId);
        
        if (history.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    // ==========================================
    // 5. GET ADMISSIONS BY DATE
    // ==========================================
    @GetMapping("/admitted/{date}") // Format: dd-MM-yyyy
    public ResponseEntity<List<Admission>> getAdmissionsByDate(@PathVariable String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            
            LocalDateTime startOfDay = parsedDate.atStartOfDay();
            LocalDateTime endOfDay = parsedDate.atTime(23, 59, 59);

            List<Admission> admittedOnDate = admissionRepository.findByAdmissionDateBetween(startOfDay, endOfDay);

            if (admittedOnDate.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(admittedOnDate, HttpStatus.OK);

        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // ==========================================
    // 6. GET DISCHARGES BY DATE
    // ==========================================
    @GetMapping("/discharged/{date}") // Format: dd-MM-yyyy
    public ResponseEntity<List<Admission>> getDischargesByDate(@PathVariable String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            
            LocalDateTime startOfDay = parsedDate.atStartOfDay();
            LocalDateTime endOfDay = parsedDate.atTime(23, 59, 59);

            List<Admission> dischargedOnDate = admissionRepository.findByDischargeDateBetween(startOfDay, endOfDay);

            if (dischargedOnDate.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(dischargedOnDate, HttpStatus.OK);

        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}