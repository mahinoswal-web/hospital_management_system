package com.example.springcrud.controller;

import com.example.springcrud.model.Doctor;
import com.example.springcrud.model.Patient;
import com.example.springcrud.model.Prescription;
import com.example.springcrud.repository.DoctorRepository;
import com.example.springcrud.repository.PatientRepository;
import com.example.springcrud.repository.PrescriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor/rx") 
public class PrescriptionController {

    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;

    // 1. Show the "Create Prescription" Form
    @GetMapping("/createprescription/{patientId}")
    public String createPrescriptionForm(@PathVariable String patientId, Model model, HttpSession session) {
        String docId = (String) session.getAttribute("loggedInDoctorId");
        if (docId == null) return "redirect:/login";

        Optional<Patient> patient = patientRepository.findById(patientId);
        Optional<Doctor> doctor = doctorRepository.findById(docId);

        if (patient.isPresent() && doctor.isPresent()) {
            Prescription rx = new Prescription();
            rx.setPatientId(patient.get().getId()); 
            rx.setDoctorId(doctor.get().getDoctorId());
            rx.setDoctorName(doctor.get().getName());

            model.addAttribute("prescription", rx);
            model.addAttribute("patient", patient.get());
            
            return "doctor/prescription_form"; 
        }
        return "redirect:/doctor/docdashboard";
    }

    // 2. Save the Prescription to Database
    @PostMapping("/save")
    public String savePrescription(@ModelAttribute Prescription prescription, HttpSession session) {
        
        // --- UPDATE 1: Security Check ---
        if (session.getAttribute("loggedInDoctorId") == null) {
            return "redirect:/login";
        }

        // Generate custom RX-XXX ID 
        if (prescription.getPrescriptionId() == null || prescription.getPrescriptionId().isEmpty()) {
            long count = prescriptionRepository.count();
            prescription.setPrescriptionId(String.format("RX-%03d", count + 1));
        }
        
        // Save to MongoDB
        prescriptionRepository.save(prescription);
        
        // --- UPDATE 2: Redirect back to the Patient details page to prevent template errors ---
        return "redirect:/doctor/view/" + prescription.getPatientId();
    }

    // 3. View Prescription History for a Patient
    @GetMapping("/history/{patientId}")
    public String viewHistory(@PathVariable String patientId, Model model, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";

        Optional<Patient> patient = patientRepository.findById(patientId);
        List<Prescription> history = prescriptionRepository.findByPatientId(patientId);

        if (patient.isPresent()) {
            model.addAttribute("patient", patient.get());
            model.addAttribute("prescriptions", history);
            
            // NOTE: You will need to create prescription_history.html to use this specific page!
            return "doctor/prescription_history"; 
        }
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // FOR POSTMAN JSON TESTING ONLY
    // ==========================================
    @PostMapping("/api/save")
    @ResponseBody // Returns raw JSON instead of trying to load an HTML page
    public Prescription saveViaPostman(@RequestBody Prescription prescription) {
        
        // Generate custom RX-XXX ID
        if (prescription.getPrescriptionId() == null || prescription.getPrescriptionId().isEmpty()) {
            long count = prescriptionRepository.count();
            prescription.setPrescriptionId(String.format("RX-%03d", count + 1));
        }
        
        // Save and return the saved object directly to Postman
        return prescriptionRepository.save(prescription);
    }

    // GET endpoint to view prescriptions in Postman
    @GetMapping("/api/history/{patientId}")
    @ResponseBody 
    public List<Prescription> getPrescriptionsViaPostman(@PathVariable String patientId) {
        // Fetch the list of prescriptions for this specific patient from the database
        return prescriptionRepository.findByPatientId(patientId);
    }

    @GetMapping("/api/details/{prescriptionId}")
    @ResponseBody // This forces Spring to return raw JSON instead of HTML
    public Object getPrescriptionViaPostman(@PathVariable String prescriptionId) {
        
        Optional<Prescription> rxOpt = prescriptionRepository.findByPrescriptionId(prescriptionId);
        
        if (rxOpt.isPresent()) {
            return rxOpt.get(); // Returns the JSON data
        } else {
            return "Prescription not found!"; 
        }
    }

    // ==========================================
    // VIEW SINGLE PRESCRIPTION BY RX-ID
    // ==========================================
    @GetMapping("/details/{prescriptionId}")
    public String viewPrescriptionDetails(@PathVariable String prescriptionId, Model model, HttpSession session) {
        // Security check: Make sure someone is logged in (Doctor or Patient)
        if (session.getAttribute("loggedInDoctorId") == null && session.getAttribute("loggedInPatientId") == null) {
            return "redirect:/api/auth/login";
        }

        // Search for the prescription by its RX-000 ID
        Optional<Prescription> rxOpt = prescriptionRepository.findByPrescriptionId(prescriptionId);
        
        if (rxOpt.isPresent()) {
            Prescription rx = rxOpt.get();
            model.addAttribute("prescription", rx);
            
            // Fetch the patient details to show on the prescription pad
            Optional<Patient> patientOpt = patientRepository.findById(rx.getPatientId());
            if (patientOpt.isPresent()) {
                model.addAttribute("patient", patientOpt.get());
            }
            
            return "doctor/prescription_detail"; 
        }
        
        // If the ID is wrong or doesn't exist, send them back
        return "redirect:/login"; 
    }


    // ==========================================
    // (GET ALL)
    // ==========================================
    @GetMapping("/api/all")
    @ResponseBody 
    public List<Prescription> getAllPrescriptionsViaPostman() {
        // Fetches every single prescription from the MongoDB collection
        return prescriptionRepository.findAll();
    }
}