package com.healthcare.dao;

import com.healthcare.model.Doctor;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DoctorDao {
    /**
     * Sets a connection to be used for subsequent operations.
     * Used for transaction management when called from service layer.
     *
     * @param connection the connection to use
     */
    void setConnection(Connection connection);

    Optional<Doctor> findById(int doctorId);

    List<Doctor> findAll();

    List<Doctor> findBySpecialization(String specialization);

    void save(Doctor doctor);

    void update(Doctor doctor);
}

