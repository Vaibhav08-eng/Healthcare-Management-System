package com.healthcare.dao;

import com.healthcare.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDao {
    Optional<Patient> findById(int patientId);

    List<Patient> findAll();

    void save(Patient patient);

    void update(Patient patient);
}

