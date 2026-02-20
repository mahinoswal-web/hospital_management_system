package com.example.springcrud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.springcrud.model.Patient;
import com.example.springcrud.model.Doctor;
import com.example.springcrud.repository.PatientRepository;
import com.example.springcrud.repository.DoctorRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;

    // ==========================================
    // 1. DASHBOARD WITH SEARCH
    // ==========================================
    @GetMapping("/docdashboard")
    public String showDashboard(Model model, HttpSession session, @RequestParam(required = false) String keyword) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Doctor> currentDoc = doctorRepository.findById(internalId);
        if (currentDoc.isPresent()) {
            Doctor doc = currentDoc.get();
            List<Patient> myPatients;

            if (keyword != null && !keyword.isEmpty()) {
                // Search Logic: Filter by Name OR ID (Must belong to this doctor)
                myPatients = patientRepository.findByDoctorIdAndFullNameContainingIgnoreCaseOrDoctorIdAndPatientIdContainingIgnoreCase(
                    doc.getDoctorId(), keyword, 
                    doc.getDoctorId(), keyword
                );
                model.addAttribute("keyword", keyword); // Send back to view to keep input filled
            } else {
                // Default: Show all patients
                myPatients = patientRepository.findByDoctorId(doc.getDoctorId());
            }

            model.addAttribute("doctorName", doc.getName()); 
            model.addAttribute("displayId", doc.getDoctorId()); 
            model.addAttribute("patients", myPatients);
            model.addAttribute("totalPatients", myPatients.size());
        }
        
        return "doctor/docdashboard";
    }

    // ==========================================
    // 2. DIAGNOSE / VIEW PATIENT DETAILS (UPDATED)
    // ==========================================
   @GetMapping("/view/{id}")
    public String viewPatient(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";
        
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isPresent()) {
            model.addAttribute("patient", patientOpt.get());
            
            // CHANGE THIS BACK TO:
            return "doctor/patient-details"; 
        }
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 3. ADD PATIENT FORM
    // ==========================================
    @GetMapping("/add-patient")
    public String showAddPatientForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";
        model.addAttribute("patient", new Patient());
        return "doctor/add-patient";
    }

    // ==========================================
    // 4. EDIT PATIENT (Reuses Add Form)
    // ==========================================
    @GetMapping("/edit-patient/{id}")
    public String showEditForm(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";
        
        Optional<Patient> patient = patientRepository.findById(id);
        
        if (patient.isPresent()) {
            model.addAttribute("patient", patient.get());
            return "doctor/add-patient"; 
        }
        
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 5. SAVE PATIENT (Handles Create & Update)
    // ==========================================
    @PostMapping("/save-patient")
    public String savePatient(@ModelAttribute Patient patient, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        // If ID is empty string, set null so MongoDB generates a new one
        if (patient.getId() != null && patient.getId().isEmpty()) {
            patient.setId(null);
        }

        // Link to the current doctor
        doctorRepository.findById(internalId).ifPresent(doc -> {
            patient.setDoctorId(doc.getDoctorId()); 
        });

        // Generate PAT-XXX ID only if it's a NEW patient
        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            long count = patientRepository.count();
            patient.setPatientId(String.format("PAT-%03d", count + 1));
        }

        // Default condition
        if (patient.getCondition() == null || patient.getCondition().isEmpty()) {
            patient.setCondition("Stable");
        }

        patientRepository.save(patient);
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 6. DELETE PATIENT
    // ==========================================
    @GetMapping("/delete-patient/{id}")
    public String deletePatient(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";
        
        patientRepository.deleteById(id);
        return "redirect:/doctor/docdashboard";
    }


    // ==========================================
    // 7. VIEW DOCTOR PROFILE
    // ==========================================
    @GetMapping("/profile")
    public String showDoctorProfile(Model model, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Doctor> docOpt = doctorRepository.findById(internalId);
        if (docOpt.isPresent()) {
            model.addAttribute("doctor", docOpt.get());
            return "doctor/profile"; 
        }
        
        return "redirect:/doctor/docdashboard";
    }


    // ==========================================
    // 8. PRINT PRESCRIPTION
    // ==========================================
    @GetMapping("/print-prescription/{id}")
    public String printPrescription(@PathVariable String id, Model model, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Patient> patientOpt = patientRepository.findById(id);
        Optional<Doctor> docOpt = doctorRepository.findById(internalId);

        if (patientOpt.isPresent() && docOpt.isPresent()) {
            model.addAttribute("patient", patientOpt.get());
            model.addAttribute("doctor", docOpt.get());
            return "doctor/prescription";
        }
        
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 9. ADD MEDICINE
    // ==========================================
    @PostMapping("/add-medicine")
    public String addMedicine(@RequestParam String patientId, @RequestParam String medicineName) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient p = patientOpt.get();
            // Assumes currentMedications is initialized in Patient.java
            if (p.getCurrentMedications() != null) {
                p.getCurrentMedications().add(medicineName); 
                patientRepository.save(p);
            }
            return "redirect:/doctor/view/" + patientId;
        }
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 10. BOOK TEST
    // ==========================================
    @PostMapping("/book-test")
    public String bookTest(@RequestParam String patientId, @RequestParam String testName) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient p = patientOpt.get();
            // Assumes assignedTests is initialized in Patient.java
            if (p.getAssignedTests() != null) {
                p.getAssignedTests().add(testName);
                patientRepository.save(p);
            }
            return "redirect:/doctor/view/" + patientId;
        }
        return "redirect:/doctor/docdashboard";
    }
}