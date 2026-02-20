package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "doctors")
public class Doctor {

    @Id
    private String id;
    
    // Core Fields
    private String doctorId;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private String password;
    private String hospitalName;

    // CHANGED: Using Wrapper classes (Integer, Double) so they can be null checked
    private Integer experience;       
    private List<String> qualification;
    private String gender;
    private Double consultationFee;   
    private Double rating;            
    private String availability;
    private String address;

    // --- CONSTRUCTORS ---
    public Doctor() {}

    public Doctor(String name, String specialization, String phone, String email, String password, String hospitalName) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.hospitalName = hospitalName;
    }

    // --- GETTERS AND SETTERS ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    // --- WRAPPER GETTERS/SETTERS ---

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public List<String> getQualification() { return qualification; }
    public void setQualification(List<String> qualification) { this.qualification = qualification; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}