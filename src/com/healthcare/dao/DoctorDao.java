package com.healthcare.dao;

import com.healthcare.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorDao {
    Optional<Doctor> findById(int doctorId);

    List<Doctor> findAll();

    List<Doctor> findBySpecialization(String specialization);

    void save(Doctor doctor);

    void update(Doctor doctor);
}

