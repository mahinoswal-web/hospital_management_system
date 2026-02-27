package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctor_medicines")
public class DoctorMedicine {

    @Id
    private String id;
    private String doctorId;   
    private String medicineId; 
    private String medicineName; 
    
    // NEW FIELD ADDED HERE
    private String companyName; 
    
    private String customDosage; 

    public DoctorMedicine() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
    
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    
    // NEW GETTER & SETTER
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCustomDosage() { return customDosage; }
    public void setCustomDosage(String customDosage) { this.customDosage = customDosage; }
}