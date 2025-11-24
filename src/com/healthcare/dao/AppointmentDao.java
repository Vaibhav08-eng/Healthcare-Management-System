package com.healthcare.dao;

import com.healthcare.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
    Optional<Appointment> findById(int appointmentId);

    List<Appointment> findAll();

    List<Appointment> findByDoctor(int doctorId);

    List<Appointment> findByDoctorAndRange(int doctorId, LocalDate start, LocalDate end);

    List<Appointment> findByPatient(int patientId);

    int save(Appointment appointment);

    void updateStatus(int appointmentId, String status);

    void reschedule(int appointmentId, LocalDate newDate, String newSlot);
}

