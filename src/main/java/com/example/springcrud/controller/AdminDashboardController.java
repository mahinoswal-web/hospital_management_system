package com.example.springcrud.controller;

import com.example.springcrud.model.Doctor;
import com.example.springcrud.model.Patient;
import com.example.springcrud.repository.DoctorRepository;
import com.example.springcrud.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminDashboardController {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;

    // ==========================================
    // 1. ADMIN MAIN DASHBOARD
    // ==========================================
    @GetMapping("/admin/dashboard") 
    public String showAdminDashboard(Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";

        List<Doctor> allDoctors = doctorRepository.findAll();
        List<Patient> allPatients = patientRepository.findAll();

        long totalDoctors = allDoctors.size();
        long totalPatients = allPatients.size();
        
        double totalRevenue = 0;
        for (Doctor doc : allDoctors) {
            double fee = doc.getConsultationFee() != null ? doc.getConsultationFee() : 500.0;
            long patientsForDoc = allPatients.stream()
                .filter(p -> p.getDoctorIds() != null && p.getDoctorIds().contains(doc.getDoctorId()))
                .count();
            totalRevenue += (fee * patientsForDoc);
        }

        model.addAttribute("totalDoctors", totalDoctors);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("doctors", allDoctors);
        model.addAttribute("patients", allPatients);
        
        model.addAttribute("activePage", "dashboard"); 
        return "admin/dashboard"; 
    }

    // ==========================================
    // 2. MANAGE DOCTORS PAGE -> Now loads adm-doctors.html
    // ==========================================
    @GetMapping("/admin/doctors")
    public String manageDoctors(Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("activePage", "manage-doctors");
        
        // FIXED: Pointing to the newly renamed file
        return "admin/adm-doctors"; 
    }

    // ==========================================
    // 3. DELETE DOCTOR
    // ==========================================
    @GetMapping("/admin/delete-doctor/{id}")
    public String deleteDoctor(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        doctorRepository.deleteById(id);
        return "redirect:/admin/doctors?deleted=true";
    }

    // ==========================================
    // 4. SHOW ADD DOCTOR FORM
    // ==========================================
    @GetMapping("/admin/add-doctor")
    public String showAddDoctorForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("activePage", "manage-doctors");
        return "admin/add-doctor";
    }

    // ==========================================
    // 5. REGISTER NEW DOCTOR (POST)
    // ==========================================
    @PostMapping("/admin/register-doctor")
    public String registerDoctor(@ModelAttribute Doctor doctor, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        try {
            if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) {
                doctor.setPassword("Welcome123");
            }
            doctorRepository.save(doctor);
            return "redirect:/admin/doctors?success=true"; 
        } catch (Exception e) {
            return "redirect:/admin/add-doctor?error=DatabaseError";
        }
    }

    // ==========================================
    // 6. SHOW EDIT DOCTOR FORM (GET)
    // ==========================================
    @GetMapping("/admin/edit-doctor/{id}")
    public String showEditDoctorForm(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        java.util.Optional<Doctor> docOpt = doctorRepository.findById(id);
        
        if (docOpt.isPresent()) {
            model.addAttribute("doctor", docOpt.get()); 
            model.addAttribute("activePage", "manage-doctors");
            return "admin/edit-doctor";
        }
        return "redirect:/admin/doctors?error=DoctorNotFound";
    }

    // ==========================================
    // 7. SAVE UPDATED DOCTOR (POST)
    // ==========================================
    @PostMapping("/admin/update-doctor")
    public String updateDoctor(@ModelAttribute Doctor doctor, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        try {
            doctorRepository.save(doctor);
            return "redirect:/admin/doctors?updated=true"; 
        } catch (Exception e) {
            return "redirect:/admin/doctors?error=UpdateFailed";
        }
    }

    // ==========================================
    // 8. MANAGE PATIENTS PAGE -> Now loads adm-patients.html
    // ==========================================
    @GetMapping("/admin/patients")
    public String managePatients(Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("activePage", "manage-patients");
        
        // FIXED: Pointing to the newly renamed file
        return "admin/adm-patients"; 
    }

    // ==========================================
    // 9. DELETE PATIENT
    // ==========================================
    @GetMapping("/admin/delete-patient/{id}")
    public String deletePatient(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        patientRepository.deleteById(id);
        return "redirect:/admin/patients?deleted=true";
    }

    // ==========================================
    // 10. SHOW EDIT PATIENT FORM (GET)
    // ==========================================
    @GetMapping("/admin/edit-patient/{id}")
    public String showEditPatientForm(@PathVariable String id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";

        java.util.Optional<Patient> patOpt = patientRepository.findById(id);
        
        if (patOpt.isPresent()) {
            model.addAttribute("patient", patOpt.get()); 
            model.addAttribute("activePage", "manage-patients");
            return "admin/adm-edit-patient";
        }
        return "redirect:/admin/patients?error=PatientNotFound";
    }

    // ==========================================
    // 11. SAVE UPDATED PATIENT (POST)
    // ==========================================
    @PostMapping("/admin/update-patient")
    public String updatePatient(@ModelAttribute Patient patient, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";

        try {
            // Fetch the existing patient to safely update fields without erasing medical history arrays
            Patient existingPatient = patientRepository.findById(patient.getId()).orElse(null);
            
            if (existingPatient != null) {
                existingPatient.setFullName(patient.getFullName());
                existingPatient.setPhone(patient.getPhone());
                existingPatient.setEmailAddress(patient.getEmailAddress());
                existingPatient.setDateOfBirth(patient.getDateOfBirth());
                existingPatient.setGender(patient.getGender());
                existingPatient.setCondition(patient.getCondition());
                existingPatient.setResidentialAddress(patient.getResidentialAddress());
                existingPatient.setEmergencyContact(patient.getEmergencyContact());
                existingPatient.setBloodGroup(patient.getBloodGroup());
                existingPatient.setHeight(patient.getHeight());
                existingPatient.setWeight(patient.getWeight());
                
                patientRepository.save(existingPatient);
            }
            return "redirect:/admin/patients?updated=true"; 

        } catch (Exception e) {
            return "redirect:/admin/patients?error=UpdateFailed";
        }
    }

    // ==========================================
    // 12. SHOW ADD PATIENT FORM (GET)
    // ==========================================
    @GetMapping("/admin/add-patient")
    public String showAddPatientForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";
        
        model.addAttribute("patient", new Patient());
        model.addAttribute("activePage", "manage-patients");
        return "admin/adm-add-patient";
    }

    // ==========================================
    // 13. REGISTER NEW PATIENT (POST)
    // ==========================================
    @PostMapping("/admin/register-patient")
    public String registerPatient(@ModelAttribute Patient patient, HttpSession session) {
        if (session.getAttribute("loggedInAdminId") == null) return "redirect:/login";

        try {
            // Auto-generate PAT-XXX ID
            if (patient.getPatientId() == null || patient.getPatientId().isEmpty()) {
                long count = patientRepository.count();
                patient.setPatientId(String.format("PAT-%03d", count + 1));
            }
            
            // Set some smart defaults
            if (patient.getCondition() == null || patient.getCondition().isEmpty()) {
                patient.setCondition("Stable");
            }
            if (patient.getPassword() == null || patient.getPassword().isEmpty()) {
                patient.setPassword("Patient123"); 
            }
            
            patientRepository.save(patient);
            return "redirect:/admin/patients?success=true"; 

        } catch (Exception e) {
            return "redirect:/admin/add-patient?error=DatabaseError";
        }
    }
}