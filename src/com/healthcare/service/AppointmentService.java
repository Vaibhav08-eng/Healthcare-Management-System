package com.healthcare.service;

import com.healthcare.model.Appointment;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for appointment-related business logic.
 * Handles appointment booking, cancellation, and retrieval with proper transaction management.
 */
public interface AppointmentService {

    /**
     * Books an appointment for a patient with a doctor at a specific date and time slot.
     * Performs conflict checking and transaction management.
     *
     * @param patientId the patient ID
     * @param doctorId  the doctor ID
     * @param date      the appointment date
     * @param timeSlot  the time slot (e.g., "09:00-10:00")
     * @param reason    the reason for the appointment
     * @return true if booking was successful, false otherwise
     */
    boolean bookAppointment(int patientId, int doctorId, LocalDate date, String timeSlot, String reason);

    /**
     * Cancels an appointment. Verifies that the appointment belongs to the given patient.
     *
     * @param appointmentId the appointment ID
     * @param patientId     the patient ID (for verification)
     * @return true if cancellation was successful, false otherwise
     */
    boolean cancelAppointment(int appointmentId, int patientId);

    /**
     * Gets all appointments for a doctor on a specific date.
     *
     * @param doctorId the doctor ID
     * @param date     the date
     * @return list of appointments
     */
    List<Appointment> getAppointmentsForDoctor(int doctorId, LocalDate date);

    /**
     * Gets all appointments for a patient.
     *
     * @param patientId the patient ID
     * @return list of appointments
     */
    List<Appointment> getAppointmentsForPatient(int patientId);

    /**
     * Gets all appointments for a doctor (all dates).
     *
     * @param doctorId the doctor ID
     * @return list of appointments
     */
    List<Appointment> getAppointmentsForDoctor(int doctorId);
}

