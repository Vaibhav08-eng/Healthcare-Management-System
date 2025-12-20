package com.healthcare.service.impl;

import com.healthcare.dao.AppointmentDao;
import com.healthcare.dao.AppointmentDaoImpl;
import com.healthcare.dao.DoctorDao;
import com.healthcare.dao.DoctorDaoImpl;
import com.healthcare.dao.PatientDao;
import com.healthcare.dao.PatientDaoImpl;
import com.healthcare.model.Appointment;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.ServiceException;
import com.healthcare.util.DataSourceProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service implementation for appointment-related business logic.
 * Handles transaction management for multi-step operations like booking and cancellation.
 */
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger LOGGER = Logger.getLogger(AppointmentServiceImpl.class.getName());
    private final AppointmentDao appointmentDao;
    private final DoctorDao doctorDao;
        private final PatientDao patientDao;

    public AppointmentServiceImpl() {
        this(new AppointmentDaoImpl(), new DoctorDaoImpl(), new PatientDaoImpl());
    }

        public AppointmentServiceImpl(AppointmentDao appointmentDao, DoctorDao doctorDao, PatientDao patientDao) {
        this.appointmentDao = appointmentDao;
        this.doctorDao = doctorDao;
            this.patientDao = patientDao;
    }

    @Override
    public boolean bookAppointment(int patientId, int doctorId, LocalDate date, String timeSlot, String reason) {
        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            // Get connection and start transaction
            connection = DataSourceProvider.getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // Set connection on DAOs for transaction participation
            appointmentDao.setConnection(connection);
            doctorDao.setConnection(connection);
            patientDao.setConnection(connection);

            // Step 1: Verify doctor exists
            if (!doctorDao.findById(doctorId).isPresent()) {
                connection.rollback();
                return false;
            }

            // Step 1b: Verify patient exists
            if (!patientDao.findById(patientId).isPresent()) {
                connection.rollback();
                return false;
            }

            // Step 1c: Validate and normalize time slot format (HH:mm-HH:mm, start < end)
            if (!isValidTimeSlot(timeSlot)) {
                connection.rollback();
                return false;
            }

            // Step 2: Check for conflicting appointments
            List<Appointment> existingAppointments = appointmentDao.findByDoctorAndRange(doctorId, date, date);
            boolean conflictExists = existingAppointments.stream()
                    .anyMatch(a -> a.getTimeSlot().equalsIgnoreCase(timeSlot)
                            && !"CANCELLED".equalsIgnoreCase(a.getStatus()));

            if (conflictExists) {
                connection.rollback();
                return false;
            }

            // Step 3: Create and save the appointment
            Appointment appointment = new Appointment();
            appointment.setPatientId(patientId);
            appointment.setDoctorId(doctorId);
            appointment.setAppointmentDate(date);
            appointment.setTimeSlot(timeSlot);
            appointment.setStatus("PENDING");
            appointment.setReason(reason);

            appointmentDao.save(appointment);

            // Commit transaction
            connection.commit();
            return true;

        } catch (Exception e) {
            rollbackQuietly(connection, e);
            LOGGER.log(Level.SEVERE, "Failed to book appointment", e);
            throw new ServiceException("Failed to book appointment", e);
        } finally {
            // Reset auto-commit and close connection
            try {
                if (connection != null) {
                    connection.setAutoCommit(originalAutoCommit);
                    connection.close();
                }
            } catch (SQLException e) {
                // Log but don't throw
            }
            // Clear connection from DAOs
            appointmentDao.setConnection(null);
            doctorDao.setConnection(null);
            patientDao.setConnection(null);
        }
    }

    @Override
    public boolean cancelAppointment(int appointmentId, int patientId) {
        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            // Get connection and start transaction
            connection = DataSourceProvider.getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // Set connection on DAO for transaction participation
            appointmentDao.setConnection(connection);

            // Step 1: Verify appointment exists and belongs to patient
            Appointment appointment = appointmentDao.findById(appointmentId)
                    .orElse(null);

            if (appointment == null || appointment.getPatientId() != patientId) {
                connection.rollback();
                return false;
            }

            // Step 2: Update status to CANCELLED
            appointmentDao.updateStatus(appointmentId, "CANCELLED");

            // Commit transaction
            connection.commit();
            return true;

        } catch (Exception e) {
            rollbackQuietly(connection, e);
            LOGGER.log(Level.SEVERE, "Failed to cancel appointment", e);
            throw new ServiceException("Failed to cancel appointment", e);
        } finally {
            // Reset auto-commit and close connection
            try {
                if (connection != null) {
                    connection.setAutoCommit(originalAutoCommit);
                    connection.close();
                }
            } catch (SQLException e) {
                // Log but don't throw
            }
            // Clear connection from DAO
            appointmentDao.setConnection(null);
        }
    }

    @Override
    public List<Appointment> getAppointmentsForDoctor(int doctorId, LocalDate date) {
        return appointmentDao.findByDoctorAndRange(doctorId, date, date);
    }

    @Override
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        return appointmentDao.findByPatient(patientId);
    }

    @Override
    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        return appointmentDao.findByDoctor(doctorId);
    }

        private boolean isValidTimeSlot(String timeSlot) {
            if (timeSlot == null) {
                return false;
            }
            String[] parts = timeSlot.trim().split("-");
            if (parts.length != 2) {
                return false;
            }
            try {
                LocalTime start = LocalTime.parse(parts[0].trim());
                LocalTime end = LocalTime.parse(parts[1].trim());
                return start.isBefore(end);
            } catch (Exception e) {
                return false;
            }
        }

    private void rollbackQuietly(Connection connection, Exception original) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException rollbackEx) {
            LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
            original.addSuppressed(rollbackEx);
        }
    }
}

