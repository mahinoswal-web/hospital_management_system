package com.example.springcrud.controller;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.springcrud.model.Patient;
import com.example.springcrud.model.Prescription;
import com.example.springcrud.model.Doctor;
import com.example.springcrud.model.TestResult;

import com.example.springcrud.repository.PatientRepository;
import com.example.springcrud.repository.DoctorRepository; // FIXED TYPO HERE
import com.example.springcrud.repository.PrescriptionRepository; // ADD THIS LINE!
import com.example.springcrud.repository.DoctorMedicineRepository;
import com.example.springcrud.repository.LabTestRepository; // ADDED MISSING IMPORT
import com.example.springcrud.repository.TestResultRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private DoctorMedicineRepository doctorMedicineRepository; 
    @Autowired private LabTestRepository labTestRepository; // Cleaned up the variable name
    @Autowired private TestResultRepository testResultRepository;


    @Autowired private PrescriptionRepository prescriptionRepository;

   // ==========================================
    // 1. DASHBOARD WITH SEARCH & ANALYTICS
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
                myPatients = patientRepository.findByDoctorIdsContainingAndFullNameContainingIgnoreCaseOrDoctorIdsContainingAndPatientIdContainingIgnoreCase(
                    doc.getDoctorId(), keyword, 
                    doc.getDoctorId(), keyword
                );
                model.addAttribute("keyword", keyword); 
            } else {
                myPatients = patientRepository.findByDoctorIdsContaining(doc.getDoctorId());
            }

            model.addAttribute("doctorName", doc.getName()); 
            model.addAttribute("displayId", doc.getDoctorId()); 
            model.addAttribute("patients", myPatients);
            model.addAttribute("totalPatients", myPatients.size());

            // --- NEW ANALYTICS LOGIC ---
            double fee = doc.getConsultationFee() != null ? doc.getConsultationFee() : 500.0; // Default to 500 if null
            double totalRevenue = myPatients.size() * fee;
            model.addAttribute("totalRevenue", totalRevenue);

            // Generate a simulated 7-day trend for the charts
            List<String> chartLabels = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
            List<Integer> visitData = List.of(3, 7, 4, 9, 5, 8, 2); // Sample weekly visits
            
            // Calculate daily revenue automatically based on the doctor's fee!
            List<Double> revenueData = visitData.stream().map(v -> v * fee).toList();

            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("visitData", visitData);
            model.addAttribute("revenueData", revenueData);
        }
        
        return "doctor/docdashboard";
    }

   // ==========================================
    // 2. VIEW PATIENT DETAILS
    // ==========================================
    @GetMapping("/patient/{patientId}")
    public String viewPatient(@PathVariable String patientId, Model model, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";
        
        Optional<Patient> patientOpt = patientRepository.findFirstByPatientId(patientId);
        
        if (patientOpt.isPresent()) {
            model.addAttribute("patient", patientOpt.get());
            
            doctorRepository.findById(internalId).ifPresent(doc -> {
                model.addAttribute("inventory", doctorMedicineRepository.findByDoctorId(doc.getDoctorId()));
            });

            model.addAttribute("hospitalTests", labTestRepository.findAll());

            // ---> NEW CODE: THIS IS THE LINE I ADDED FOR YOU! <---
            model.addAttribute("testRecords", testResultRepository.findByPatientId(patientOpt.get().getPatientId()));
            // -----------------------------------------------------

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
    // 4. EDIT PATIENT
    // ==========================================
    @GetMapping("/edit-patient/{patientId}")
    public String showEditForm(@PathVariable String patientId, Model model, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";
        
        Optional<Patient> patient = patientRepository.findFirstByPatientId(patientId);
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

        // --- THE FIX: PREVENT DOCTOR ERASURE ON UPDATE ---
        if (patient.getId() != null && !patient.getId().isEmpty()) {
            Optional<Patient> existingPat = patientRepository.findById(patient.getId());
            if (existingPat.isPresent()) {
                // Rescue the existing list of doctors before we save over it!
                patient.setDoctorIds(existingPat.get().getDoctorIds()); 
            }
        }

        // Add the current logged-in doctor
        doctorRepository.findById(internalId).ifPresent(doc -> {
            patient.addDoctorId(doc.getDoctorId()); 
        });

        if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
            long count = patientRepository.count();
            patient.setPatientId(String.format("PAT-%03d", count + 1));
        }

        if (patient.getCondition() == null || patient.getCondition().isEmpty()) {
            patient.setCondition("Stable");
        }

        patientRepository.save(patient);
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 6. DELETE PATIENT
    // ==========================================
    @GetMapping("/delete-patient/{patientId}")
    public String deletePatient(@PathVariable String patientId, HttpSession session) {
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";
        
        Optional<Patient> patient = patientRepository.findFirstByPatientId(patientId);
        patient.ifPresent(p -> patientRepository.delete(p));
        
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 7. VIEW DOCTOR PROFILE
    // ==========================================
   @GetMapping("/profile")
public String showDoctorProfile(Model model, HttpSession session) {
    String internalId = (String) session.getAttribute("loggedInDoctorId"); // Make sure this matches your login logic!
    if (internalId == null) return "redirect:/login";

    Optional<Doctor> docOpt = doctorRepository.findById(internalId);
    if (docOpt.isPresent()) {
        model.addAttribute("doctor", docOpt.get());
        return "doctor/profile"; 
    }
    return "redirect:/doctor/docdashboard";
}

   // ==========================================
    // 8. PRINT & SAVE PRESCRIPTION
    // ==========================================
    @GetMapping("/print-prescription/{patientId}")
    public String printPrescription(@PathVariable String patientId, Model model, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Patient> patientOpt = patientRepository.findFirstByPatientId(patientId);
        Optional<Doctor> docOpt = doctorRepository.findById(internalId);

        if (patientOpt.isPresent() && docOpt.isPresent()) {
            Patient p = patientOpt.get();
            Doctor d = docOpt.get();

            // --- DATABASE AUTO-SAVE LOGIC ---
            Prescription rx = new Prescription();
            rx.setPatientId(p.getPatientId()); // Saves as PAT-XXX
            rx.setDoctorId(d.getDoctorId());   // Saves as DOC-XXX
            rx.setDoctorName(d.getName());
            rx.setDoctorSpecialization(d.getSpecialization()); // New field!
            rx.setDiagnosis("Standard Consultation"); 
            
            // Format the medicine list
            if (p.getCurrentMedications() != null && !p.getCurrentMedications().isEmpty()) {
                rx.setMedicineList(String.join(", ", p.getCurrentMedications()));
            } else {
                rx.setMedicineList("No active medications prescribed.");
            }

            // Save the permanent record!
            // Note: Make sure @Autowired private PrescriptionRepository prescriptionRepository; is at the top of this file!
            prescriptionRepository.save(rx);
            // ---------------------------------

            model.addAttribute("patient", p);
            model.addAttribute("doctor", d);
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
            
            // FIXED: Using your smart helper method instead of manual null checks!
            p.addMedicine(medicineName); 
            patientRepository.save(p);
            
            return "redirect:/doctor/patient/" + p.getPatientId();
        }
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 10. BOOK TEST (Upgraded for TestResult Tracking)
    // ==========================================
    @PostMapping("/book-test")
    public String bookTest(@RequestParam String patientId, @RequestParam String testName, HttpSession session) {
        String internalDocId = (String) session.getAttribute("loggedInDoctorId");
        if (internalDocId == null) return "redirect:/login";

        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        
        if (patientOpt.isPresent()) {
            Patient p = patientOpt.get();
            
            // 1. Keep adding it to the patient's simple list so your current UI doesn't break!
            p.addTest(testName);
            patientRepository.save(p);
            
            // 2. Create the robust TestResult document
            TestResult newTestRecord = new TestResult();
            newTestRecord.setPatientId(p.getPatientId()); // Stores PAT-XXX
            newTestRecord.setTestName(testName);
            
            // Safely fetch the doctor to store their DOC-XXX ID
            doctorRepository.findById(internalDocId).ifPresent(doc -> {
                newTestRecord.setDoctorId(doc.getDoctorId());
            });
            
            // Note: The status ("Pending") and prescribedDate are set automatically by your TestResult constructor!
            testResultRepository.save(newTestRecord);
            
            return "redirect:/doctor/patient/" + p.getPatientId();
        }
        return "redirect:/doctor/docdashboard";
    }


    // ==========================================
    // 11. MEDICINE INVENTORY
    // ==========================================
    @GetMapping("/inventory")
    public String viewDoctorInventory(Model model, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) {
            return "redirect:/login";
        }
        
        doctorRepository.findById(internalId).ifPresent(doc -> {
            model.addAttribute("doctorId", doc.getDoctorId());
            model.addAttribute("displayId", doc.getDoctorId()); 
        });
        
        return "doctor/doctor-inventory"; 
    }

    // ==========================================
    // 12. PUBLISH TEST RESULTS
    // ==========================================
    @PostMapping("/publish-test-result")
    public String publishTestResult(@RequestParam String recordId, 
                                    @RequestParam String patientId, 
                                    @RequestParam String resultDetails, 
                                    HttpSession session) {
        
        if (session.getAttribute("loggedInDoctorId") == null) return "redirect:/login";

        Optional<TestResult> recordOpt = testResultRepository.findById(recordId);
        
        if (recordOpt.isPresent()) {
            TestResult record = recordOpt.get();
            record.setResultDetails(resultDetails);
            record.setStatus("Completed");
            record.setCompletedDate(LocalDate.now()); // Automatically stamps today's date
            
            testResultRepository.save(record);
        }
        
        // Redirect back to the patient's profile using the original patientId (e.g., PAT-001)
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            return "redirect:/doctor/patient/" + patientOpt.get().getPatientId();
        }
        
        return "redirect:/doctor/docdashboard";
    }

    // ==========================================
    // 13. UPDATE DOCTOR PROFILE
    // ==========================================
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Doctor updatedDoctor, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Doctor> existingDocOpt = doctorRepository.findById(internalId);
        if (existingDocOpt.isPresent()) {
            Doctor existingDoc = existingDocOpt.get();
            
            // Simple, clean String assignments
            existingDoc.setName(updatedDoctor.getName());
            existingDoc.setEmail(updatedDoctor.getEmail());
            existingDoc.setPhone(updatedDoctor.getPhone());
            existingDoc.setSpecialization(updatedDoctor.getSpecialization());
            existingDoc.setHospitalName(updatedDoctor.getHospitalName());
            existingDoc.setQualification(updatedDoctor.getQualification());
            
            doctorRepository.save(existingDoc);
        }
        return "redirect:/doctor/profile?success=true";
    }

    // ==========================================
    // 14. CONSULT EXISTING PATIENT (Multiple Doctors)
    // ==========================================
    @PostMapping("/consult-existing")
    public String consultExistingPatient(@RequestParam String patientId, HttpSession session) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Doctor> docOpt = doctorRepository.findById(internalId);
        Optional<Patient> patOpt = patientRepository.findFirstByPatientId(patientId);

        if (docOpt.isPresent() && patOpt.isPresent()) {
            Patient p = patOpt.get();
            String docDisplayId = docOpt.get().getDoctorId();

            // If this doctor is not already assigned to this patient, add them to the team!
            if (p.getDoctorIds() == null || !p.getDoctorIds().contains(docDisplayId)) {
                p.addDoctorId(docDisplayId);
                patientRepository.save(p);
            }
            
            // Jump straight into the patient's medical file!
            return "redirect:/doctor/patient/" + p.getPatientId();
        }

        // If the patient ID doesn't exist, redirect back to dashboard with an error
        return "redirect:/doctor/docdashboard?error=NotFound";
    }

    // ==========================================
    // 15. DEDICATED MY PATIENTS DIRECTORY
    // ==========================================
    @GetMapping("/mypatients")
    public String showMyPatientsDirectory(Model model, HttpSession session, @RequestParam(required = false) String keyword) {
        String internalId = (String) session.getAttribute("loggedInDoctorId");
        if (internalId == null) return "redirect:/login";

        Optional<Doctor> currentDoc = doctorRepository.findById(internalId);
        if (currentDoc.isPresent()) {
            Doctor doc = currentDoc.get();
            List<Patient> myPatients;

            // Reuse your smart search logic
            if (keyword != null && !keyword.isEmpty()) {
                myPatients = patientRepository.findByDoctorIdsContainingAndFullNameContainingIgnoreCaseOrDoctorIdsContainingAndPatientIdContainingIgnoreCase(
                    doc.getDoctorId(), keyword, 
                    doc.getDoctorId(), keyword
                );
                model.addAttribute("keyword", keyword); 
            } else {
                myPatients = patientRepository.findByDoctorIdsContaining(doc.getDoctorId());
            }

            model.addAttribute("displayId", doc.getDoctorId()); 
            model.addAttribute("patients", myPatients);
        }
        
        return "doctor/doctor-patients"; // Points to our new file!
    }

}