package com.example.springcrud.controller;

import com.example.springcrud.model.Patient;
import com.example.springcrud.model.Prescription;
import com.example.springcrud.repository.DoctorRepository;
import com.example.springcrud.repository.PatientRepository;
import com.example.springcrud.repository.PrescriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller for managing Prescriptions.
 * All endpoints in this class return raw JSON data, 
 * making them perfect for Postman testing and frontend fetch/axios calls.
 */
@RestController // Replaces @Controller + @ResponseBody
@RequestMapping("/doctor/rx") 
public class PrescriptionController {

    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;

    // ==========================================================================================
    // CORE CRUD APIs
    // ==========================================================================================

    /**
     * PURPOSE: Creates and saves a new prescription to the database.
     * URL:     POST http://localhost:8080/doctor/rx/api/create
     * INPUT:   JSON body containing prescription details (patientId, doctorId, medicines, etc.)
     * OUTPUT:  The newly created Prescription object as JSON (including the auto-generated ID).
     */
    @PostMapping("/api/create")
    public Prescription saveViaPostman(@RequestBody Prescription prescription) {
        // Auto-generate a prescription ID if one isn't provided
        if (prescription.getPrescriptionId() == null || prescription.getPrescriptionId().isEmpty()) {
            long count = prescriptionRepository.count();
            prescription.setPrescriptionId(String.format("RX-%03d", count + 1));
        }
        return prescriptionRepository.save(prescription);
    }

    /**
     * PURPOSE: Retrieves the entire prescription history for a specific patient.
     * URL:     GET http://localhost:8080/doctor/rx/api/history/{patientId}
     * INPUT:   patientId as a Path Variable in the URL.
     * OUTPUT:  A JSON array (List) of all Prescription objects linked to that patient.
     */
    @GetMapping("/api/history/{patientId}")
    public List<Prescription> getPrescriptionsViaPostman(@PathVariable String patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    /**
     * PURPOSE: Fetches the details of one specific prescription.
     * URL:     GET http://localhost:8080/doctor/rx/api/view/{prescriptionId}
     * INPUT:   prescriptionId as a Path Variable in the URL.
     * OUTPUT:  A single Prescription JSON object, or a "Not found" message.
     */
    @GetMapping("/api/view/{prescriptionId}")
    public Object getPrescriptionViaPostman(@PathVariable String prescriptionId) {
        Optional<Prescription> rxOpt = prescriptionRepository.findByPrescriptionId(prescriptionId);
        if (rxOpt.isPresent()) {
            return rxOpt.get(); 
        } else {
            return "Prescription not found!"; 
        }
    }

    /**
     * PURPOSE: Fetches every single prescription stored in the database.
     * URL:     GET http://localhost:8080/doctor/rx/api/all
     * INPUT:   None.
     * OUTPUT:  A JSON array (List) containing all prescriptions.
     */
    @GetMapping("/api/all")
    public List<Prescription> getAllPrescriptionsViaPostman() {
        return prescriptionRepository.findAll();
    }

    /**
     * PURPOSE: Retrieves all prescriptions written by a specific doctor.
     * URL:     GET http://localhost:8080/doctor/rx/api/doctor/{doctorId}
     * INPUT:   doctorId as a Path Variable in the URL.
     * OUTPUT:  A JSON array (List) of Prescription objects issued by that doctor.
     */
    @GetMapping("/api/doctor/{doctorId}")
    public List<Prescription> getPrescriptionsByDoctorViaPostman(@PathVariable String doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    // ==========================================================================================
    // ANALYTICS & SEARCH APIS
    // ==========================================================================================

    /**
     * PURPOSE: Generates a list of all medicines a specific doctor frequently prescribes.
     * URL:     GET http://localhost:8080/doctor/rx/api/analytics/doctor-medicines/{doctorId}
     * INPUT:   doctorId as a Path Variable in the URL.
     * OUTPUT:  A JSON array of strings (medicine names/lists).
     */
    @GetMapping("/api/analytics/doctor-medicines/{doctorId}")
    public List<String> getMedicinesPrescribedByDoctor(@PathVariable String doctorId) {
        List<Prescription> doctorPrescriptions = prescriptionRepository.findByDoctorId(doctorId);
        
        return doctorPrescriptions.stream()
                .map(Prescription::getMedicineList)
                .filter(meds -> meds != null && !meds.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * PURPOSE: Finds all patients who have been prescribed a specific medicine (by its name).
     * URL:     GET http://localhost:8080/doctor/rx/api/analytics/patients-by-medicine-name?medicineName=Paracetamol
     * INPUT:   medicineName as a Query Parameter (e.g., ?medicineName=Aspirin).
     * OUTPUT:  A JSON array of Patient objects matching the criteria.
     */
    @GetMapping("/api/analytics/patients-by-medicine-name")
    public ResponseEntity<?> getPatientsByMedicineName(@RequestParam String medicineName) {
        try {
            List<Prescription> matchingRx = prescriptionRepository.findByMedicineListContainingIgnoreCase(medicineName);
            
            Set<String> uniquePatientIds = matchingRx.stream()
                    .filter(rx -> rx.getPatientId() != null && !rx.getPatientId().trim().isEmpty()) 
                    .map(Prescription::getPatientId)
                    .collect(Collectors.toSet());
            
            List<Patient> patients = new ArrayList<>();
            for (String patId : uniquePatientIds) {
                patientRepository.findByPatientId(patId).ifPresent(patients::add); 
            }
            
            return ResponseEntity.ok(patients);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * PURPOSE: Finds all patients who have been prescribed a specific medicine (by its internal ID).
     * URL:     GET http://localhost:8080/doctor/rx/api/analytics/patients-by-med-id?medId=MED-001
     * INPUT:   medId as a Query Parameter (e.g., ?medId=MED-005).
     * OUTPUT:  A JSON array of Patient objects matching the criteria.
     */
    @GetMapping("/api/analytics/patients-by-med-id")
    public ResponseEntity<?> getPatientsByMedicineId(@RequestParam String medId) {
        try {
            List<Prescription> matchingRx = prescriptionRepository.findByMedicineIdsContaining(medId);
            
            Set<String> uniquePatientIds = matchingRx.stream()
                    .filter(rx -> rx.getPatientId() != null && !rx.getPatientId().trim().isEmpty()) 
                    .map(Prescription::getPatientId)
                    .collect(Collectors.toSet());
            
            List<Patient> patients = new ArrayList<>();
            for (String patId : uniquePatientIds) {
                patientRepository.findByPatientId(patId).ifPresent(patients::add);
            }
            
            return ResponseEntity.ok(patients);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }
}