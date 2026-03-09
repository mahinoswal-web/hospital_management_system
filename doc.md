# Clinic Management API Documentation

Base URL: `http://localhost:8080`

This documentation provides detailed information about the Clinic Management API.

---

## Doctor Availability API

### 1. Create Doctor Availability

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/doctor-availability`
- **Request Body:**
  ```json
  {
    "doctorId": "DOC-101",
    "day": "MONDAY",
    "startTime": "09:00",
    "endTime": "17:00",
    "available": true
  }
  ```

### 2. Get Doctor Availability

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/doctor-availability`
- **Query Parameters:**
  - `doctorId` (e.g., `DOC-101`)
  - `day` (e.g., `MONDAY`)
  - `available` (e.g., `true`)

### 3. Get Doctor Availability By ID

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/doctor-availability/{availabilityId}`

### 4. Update Doctor Availability

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/doctor-availability/{availabilityId}`
- **Request Body:**
  ```json
  {
    "doctorId": "DOC-101",
    "day": "TUESDAY",
    "startTime": "10:00",
    "endTime": "16:00",
    "available": false
  }
  ```

### 5. Delete Doctor Availability

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/doctor-availability/{availabilityId}`

---

## Details API

### 1. Create Details

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/details`
- **Request Body:**
  ```json
  {
    "recordStatus": "ACTIVE",
    "doctorChangeAllowed": true,
    "patient": {
      "patientId": "PAT-1001",
      "fullName": "John Doe",
      "age": 40,
      "gender": "MALE"
    },
    "currentDoctor": {
      "doctorId": "DOC-2001",
      "fullName": "Dr. House",
      "specialization": "Diagnostics"
    },
    "diagnosis": {
      "icdCode": "I10",
      "severity": "HIGH"
    },
    "treatmentTimeline": {
      "treatmentStatus": "ONGOING",
      "treatmentStartDate": "2024-08-01"
    },
    "followUp": {
      "followUpRequired": true
    }
  }
  ```

### 2. Get Details

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/details`
- **Query Parameters:**
  - `recordStatus`
  - `doctorChangeAllowed`
  - `patientId`
  - `currentDoctorId`
  - `icdCode`
  - `severity`
  - `treatmentStatus`
  - `followUpRequired`
  - `treatmentStartFrom`
  - `treatmentStartTo`

### 3. Get Details By ID

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/details/{detailsId}`

### 4. Update Details

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/details/{detailsId}`
- **Request Body:**
  ```json
  {
    "recordStatus": "INACTIVE",
    "doctorChangeAllowed": false,
    "patient": {
      "patientId": "PAT-1001",
      "fullName": "John Doe",
      "age": 41,
      "gender": "MALE"
    }
  }
  ```

### 5. Delete Details

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/details/{detailsId}`

---

## Patient API

### 1. Create Patient

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/patients`
- **Request Body:**
  ```json
  {
    "patientId": "PAT-1001",
    "fullName": "John Doe",
    "dateOfBirth": "1995-05-20",
    "gender": "MALE",
    "phoneNumber": "9876543210",
    "emailAddress": "john.doe@email.com",
    "residentialAddress": "123 Main Street",
    "emergencyContact": "9123456789",
    "bloodGroup": "O+",
    "allergies": "Peanuts",
    "chronicDiseases": "None",
    "currentMedications": "None",
    "height": 175,
    "weight": 70
  }
  ```

### 2. Get All Patients

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/patients`
- **Query Parameters:**
  - `patientId`
  - `fullName`
  - `gender`
  - `bloodGroup`
  - `phoneNumber`
  - `emailAddress`

### 3. Get Patient By ID

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/patients/{patientId}`

### 4. Update Patient

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/patients/{patientId}`
- **Request Body:**
  ```json
  {
    "patientId": "PAT-1001",
    "fullName": "John Doe Updated",
    "dateOfBirth": "1995-05-20",
    "gender": "MALE",
    "phoneNumber": "9999999999",
    "emailAddress": "john.updated@email.com",
    "residentialAddress": "456 New Street",
    "emergencyContact": "9000000000",
    "bloodGroup": "O+",
    "allergies": "None",
    "chronicDiseases": "Asthma",
    "currentMedications": "Inhaler",
    "height": 176,
    "weight": 72
  }
  ```

### 5. Delete Patient

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/patients/{patientId}`

---

## Medicines Test API

### 1. Create Medicines Test

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/medicines-tests`
- **Request Body:**
  ```json
  {
    "patientId": "PAT-001",
    "doctorId": "DOC-101",
    "testName": "Blood Sugar",
    "category": "PATHOLOGY",
    "price": 450.0,
    "description": "Fasting blood sugar test",
    "resultStatus": "PENDING",
    "history": "No previous abnormal results"
  }
  ```

### 2. Get All Medicines Tests

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/medicines-tests`
- **Query Parameters:**
  - `patientId`
  - `doctorId`
  - `testName`
  - `category`
  - `resultStatus`
  - `maxPrice`

### 3. Get Medicines Test By ID

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/medicines-tests/{testId}`

### 4. Update Medicines Test

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/medicines-tests/{testId}`
- **Request Body:**
  ```json
  {
    "patientId": "PAT-001",
    "doctorId": "DOC-101",
    "testName": "Blood Sugar (Fasting)",
    "category": "PATHOLOGY",
    "price": 500.0,
    "description": "Updated description",
    "resultStatus": "COMPLETED",
    "history": "Patient stable"
  }
  ```

### 5. Delete Medicines Test

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/medicines-tests/{testId}`

---

## Medicines API

### 1. Create Medicine

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/medicines`
- **Request Body:**
  ```json
  {
    "medId": "MED-001",
    "name": "Paracetamol",
    "companyName": "ABC Pharma",
    "recordStatus": "ACTIVE",
    "doctorChangeAllowed": true,
    "dosage": "500mg",
    "route": "ORAL",
    "frequency": "Twice a day",
    "duration": "5 days",
    "expiryDate": "2027-12-31",
    "price": 50.0,
    "startDate": "2026-01-01",
    "endDate": "2026-01-05",
    "specialInstructions": "After food"
  }
  ```

### 2. Get All Medicines

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/medicines`
- **Query Parameters:**
  - `medId`
  - `name`
  - `companyName`
  - `recordStatus`
  - `doctorChangeAllowed`
  - `maxPrice`

### 3. Get Medicine By ID

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/medicines/{medicineId}`

### 4. Update Medicine

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/medicines/{medicineId}`
- **Request Body:**
  ```json
  {
    "medId": "MED-001",
    "name": "Paracetamol Plus",
    "companyName": "ABC Pharma",
    "recordStatus": "ACTIVE",
    "doctorChangeAllowed": false,
    "dosage": "650mg",
    "route": "ORAL",
    "frequency": "Once a day",
    "duration": "3 days",
    "expiryDate": "2028-12-31",
    "price": 75.0,
    "startDate": "2026-02-01",
    "endDate": "2026-02-03",
    "specialInstructions": "Before sleep"
  }
  ```

### 5. Delete Medicine

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/medicines/{medicineId}`

---

## Doctor API

### 1. Create Doctor

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/doctors`
- **Request Body:**
  ```json
  {
    "doctorId": "DOC-101",
    "name": "Dr. Stephen Strange",
    "specialization": "Neurology",
    "experience": 10,
    "qualification": ["MBBS", "MD"],
    "gender": "MALE",
    "phone": "9876543210",
    "email": "strange@hospital.com",
    "consultationFee": 800,
    "availability": "AVAILABLE",
    "hospitalName": "City Hospital",
    "rating": 4.7,
    "address": "New York"
  }
  ```

### 2. Get All Doctors

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/doctors`
- **Query Parameters:**
  - `name`
  - `specialization`
  - `minExperience`
  - `maxExperience`
  - `qualification`
  - `gender`
  - `hospitalName`
  - `minFee`
  - `maxFee`
  - `minRating`
  - `availability`
  - `address`

### 3. Get Doctor By ID

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/doctors/{doctorId}`

### 4. Update Doctor

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/doctors/{doctorId}`
- **Request Body:**
  ```json
  {
    "name": "Dr. Stephen Strange",
    "specialization": "Neurosurgery",
    "experience": 12,
    "qualification": ["MBBS", "MD", "DM"],
    "gender": "MALE",
    "phone": "9999999999",
    "email": "neuro@hospital.com",
    "consultationFee": 1200,
    "availability": "AVAILABLE",
    "hospitalName": "Metro Hospital",
    "rating": 4.9,
    "address": "New York Downtown"
  }
  ```

### 5. Delete Doctor

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/doctors/{doctorId}`
