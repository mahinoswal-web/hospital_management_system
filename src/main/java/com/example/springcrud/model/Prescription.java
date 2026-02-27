package com.example.springcrud.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    private String prescriptionId; 
    private String patientId; // Links to the Patient
    private String doctorId;  // Links to the Doctor who wrote it
    private String doctorName; 
    
    private String diagnosis; // The doctor's notes
    private String medicineList; // Stores text for the printed pad
    
    // Stores strict database IDs for analytics (e.g., ["MED-001", "MED-005"])
    private List<String> medicineIds; 
    
    private String testList;     // Stores tests (e.g., "Blood Test\nX-Ray")
    
    // ONLY ONE date field, with the annotation applied!
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date; 

    // Constructors
    public Prescription() {
        this.date = LocalDate.now(); // Auto-set date to today
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getMedicineList() { return medicineList; }
    public void setMedicineList(String medicineList) { this.medicineList = medicineList; }

    public String getTestList() { return testList; }
    public void setTestList(String testList) { this.testList = testList; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<String> getMedicineIds() { return medicineIds; }
    public void setMedicineIds(List<String> medicineIds) { this.medicineIds = medicineIds; }
}