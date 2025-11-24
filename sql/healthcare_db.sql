-- Online Healthcare Management System Database Setup
DROP DATABASE IF EXISTS healthcare_db;
CREATE DATABASE healthcare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE healthcare_db;

-- Users table holds all login information
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patients table extends users
CREATE TABLE patients (
    patient_id INT PRIMARY KEY,
    dob DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    address VARCHAR(255),
    blood_group VARCHAR(5),
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Doctors table extends users
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY,
    specialization VARCHAR(100),
    experience_years INT,
    phone VARCHAR(20),
    consultation_fee DECIMAL(10,2),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Doctor availability slots
CREATE TABLE doctor_availability (
    availability_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    available_date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    UNIQUE KEY uniq_slot (doctor_id, available_date, time_slot)
);

-- Appointments between patients and doctors
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    status ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
    reason VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    UNIQUE KEY uniq_appointment (doctor_id, appointment_date, time_slot)
);

-- Medical records added by doctors
CREATE TABLE medical_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    visit_date DATE NOT NULL,
    diagnosis TEXT,
    prescription TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Feedback table for patients rating doctors
CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- System settings key/value store
CREATE TABLE system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed data
INSERT INTO users (name, email, password_hash, role) VALUES
('System Admin', 'admin@health.com', SHA2('admin123', 256), 'ADMIN'),
('Dr. Alice Heart', 'alice.heart@health.com', SHA2('doctor1', 256), 'DOCTOR'),
('Dr. Brian Ortho', 'brian.ortho@health.com', SHA2('doctor2', 256), 'DOCTOR'),
('John Patient', 'john.patient@health.com', SHA2('patient1', 256), 'PATIENT'),
('Emily Patient', 'emily.patient@health.com', SHA2('patient2', 256), 'PATIENT');

INSERT INTO doctors (doctor_id, specialization, experience_years, phone, consultation_fee) VALUES
((SELECT user_id FROM users WHERE email='alice.heart@health.com'), 'Cardiology', 10, '555-1001', 500.00),
((SELECT user_id FROM users WHERE email='brian.ortho@health.com'), 'Orthopedics', 8, '555-1002', 450.00);

INSERT INTO patients (patient_id, dob, gender, phone, address, blood_group) VALUES
((SELECT user_id FROM users WHERE email='john.patient@health.com'), '1995-03-12', 'Male', '555-2001', '123 Wellness St', 'O+'),
((SELECT user_id FROM users WHERE email='emily.patient@health.com'), '1998-07-24', 'Female', '555-2002', '456 Care Ave', 'A+');

INSERT INTO doctor_availability (doctor_id, available_date, time_slot, is_available) VALUES
((SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='alice.heart@health.com'), CURDATE(), '10:00-10:30', TRUE),
((SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='alice.heart@health.com'), CURDATE(), '10:30-11:00', TRUE),
((SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='brian.ortho@health.com'), CURDATE() + INTERVAL 1 DAY, '11:00-11:30', TRUE),
((SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='brian.ortho@health.com'), CURDATE() + INTERVAL 1 DAY, '11:30-12:00', TRUE);

INSERT INTO appointments (patient_id, doctor_id, appointment_date, time_slot, status, reason, notes) VALUES
((SELECT patient_id FROM patients p JOIN users u ON p.patient_id = u.user_id WHERE u.email='john.patient@health.com'),
 (SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='alice.heart@health.com'),
 CURDATE(), '10:00-10:30', 'CONFIRMED', 'Routine checkup', 'Bring past reports'),
((SELECT patient_id FROM patients p JOIN users u ON p.patient_id = u.user_id WHERE u.email='emily.patient@health.com'),
 (SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='brian.ortho@health.com'),
 CURDATE() + INTERVAL 1 DAY, '11:00-11:30', 'PENDING', 'Knee pain', NULL);

INSERT INTO medical_records (patient_id, doctor_id, visit_date, diagnosis, prescription, notes) VALUES
((SELECT patient_id FROM patients p JOIN users u ON p.patient_id = u.user_id WHERE u.email='john.patient@health.com'),
 (SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='alice.heart@health.com'),
 CURDATE() - INTERVAL 30 DAY, 'Mild hypertension', 'ACE inhibitors', 'Monitor BP weekly'),
((SELECT patient_id FROM patients p JOIN users u ON p.patient_id = u.user_id WHERE u.email='emily.patient@health.com'),
 (SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='brian.ortho@health.com'),
 CURDATE() - INTERVAL 10 DAY, 'Ligament sprain', 'Rest, ice, NSAIDs', 'Physiotherapy recommended');

INSERT INTO feedback (patient_id, doctor_id, rating, comments) VALUES
((SELECT patient_id FROM patients p JOIN users u ON p.patient_id = u.user_id WHERE u.email='john.patient@health.com'),
 (SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='alice.heart@health.com'),
 5, 'Very helpful and polite.'),
((SELECT patient_id FROM patients p JOIN users u ON p.patient_id = u.user_id WHERE u.email='emily.patient@health.com'),
 (SELECT doctor_id FROM doctors d JOIN users u ON d.doctor_id = u.user_id WHERE u.email='brian.ortho@health.com'),
 4, 'Explained exercises clearly.');

INSERT INTO system_settings (setting_key, setting_value) VALUES
('system_name', 'Online Healthcare Management System'),
('max_daily_appointments', '20'),
('default_slot_length', '30');

