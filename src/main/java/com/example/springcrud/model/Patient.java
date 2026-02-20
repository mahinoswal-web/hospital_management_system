package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList; // Added Import
import java.util.List;

@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    private String patientId;
    private String condition;
    private String fullName;
    private LocalDateTime dateOfBirth;
    private String gender;
    private String phone;
    private String password;
    private String emailAddress;
    private String residentialAddress;
    private String emergencyContact;
    private String bloodGroup;
    
    private List<String> allergies;
    private List<String> chronicDiseases;
    private List<String> currentMedications;


    
    
    // NEW FIELD: To store tests booked by the doctor
    private List<String> assignedTests = new ArrayList<>();

    private Integer height;
    private Integer weight;
    private String doctorId; 

    // ---------- CONSTRUCTORS ----------
    public Patient() {}

    // ---------- HELPER METHODS (For Doctor Dashboard) ----------
    
    // 1. Add a single medicine easily
    public void addMedicine(String medicine) {
        if (this.currentMedications == null) {
            this.currentMedications = new ArrayList<>();
        }
        this.currentMedications.add(medicine);
    }

    // 2. Add a single test easily
    public void addTest(String test) {
        if (this.assignedTests == null) {
            this.assignedTests = new ArrayList<>();
        }
        this.assignedTests.add(test);
    }

    // ---------- GETTERS & SETTERS ----------

    public List<String> getAssignedTests() {
        return assignedTests;
    }

    public void setAssignedTests(List<String> assignedTests) {
        this.assignedTests = assignedTests;
    }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDateTime getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDateTime dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public List<String> getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(List<String> chronicDiseases) { this.chronicDiseases = chronicDiseases; }
    public List<String> getCurrentMedications() { return currentMedications; }
    public void setCurrentMedications(List<String> currentMedications) { this.currentMedications = currentMedications; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}