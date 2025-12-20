package com.healthcare.dao;

import com.healthcare.model.Appointment;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    /**
     * Sets a connection to be used for subsequent operations.
     * Used for transaction management when called from service layer.
     *
     * @param connection the connection to use
     */
    void setConnection(Connection connection);

    Optional<Appointment> findById(int appointmentId);

    List<Appointment> findAll();

    List<Appointment> findByDoctor(int doctorId);

    List<Appointment> findByDoctorAndRange(int doctorId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatient(int patientId);

    int save(Appointment appointment);

    void updateStatus(int appointmentId, String status);

    void reschedule(int appointmentId, LocalDateTime newStart, LocalDateTime newEnd);
}

