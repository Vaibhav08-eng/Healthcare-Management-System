package com.healthcare.dao;

import com.healthcare.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC DAO for appointments.
 */
public class AppointmentDaoImpl implements AppointmentDao {

    private static final String BASE_SELECT = "SELECT appointment_id, patient_id, doctor_id, appointment_date, time_slot, status, reason, notes, created_at FROM appointments";

    @Override
    public Optional<Appointment> findById(int appointmentId) {
        String sql = BASE_SELECT + " WHERE appointment_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load appointment", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() {
        return executeQuery(BASE_SELECT + " ORDER BY appointment_date DESC, time_slot DESC");
    }

    @Override
    public List<Appointment> findByDoctor(int doctorId) {
        String sql = BASE_SELECT + " WHERE doctor_id=? ORDER BY appointment_date DESC";
        return executeQuery(sql, doctorId);
    }

    @Override
    public List<Appointment> findByDoctorAndRange(int doctorId, LocalDate start, LocalDate end) {
        String sql = BASE_SELECT + " WHERE doctor_id=? AND appointment_date BETWEEN ? AND ? ORDER BY appointment_date";
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setObject(2, start);
            ps.setObject(3, end);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load appointment range", e);
        }
        return list;
    }

    @Override
    public List<Appointment> findByPatient(int patientId) {
        String sql = BASE_SELECT + " WHERE patient_id=? ORDER BY appointment_date DESC";
        return executeQuery(sql, patientId);
    }

    @Override
    public int save(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, time_slot, status, reason, notes) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, appointment.getPatientId());
            ps.setInt(2, appointment.getDoctorId());
            ps.setObject(3, appointment.getAppointmentDate());
            ps.setString(4, appointment.getTimeSlot());
            ps.setString(5, appointment.getStatus());
            ps.setString(6, appointment.getReason());
            ps.setString(7, appointment.getNotes());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("Unable to get generated id for appointment");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create appointment", e);
        }
    }

    @Override
    public void updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status=? WHERE appointment_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update appointment status", e);
        }
    }

    @Override
    public void reschedule(int appointmentId, LocalDate newDate, String newSlot) {
        String sql = "UPDATE appointments SET appointment_date=?, time_slot=?, status='CONFIRMED' WHERE appointment_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newDate);
            ps.setString(2, newSlot);
            ps.setInt(3, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reschedule appointment", e);
        }
    }

    private List<Appointment> executeQuery(String sql) {
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute appointment query", e);
        }
        return list;
    }

    private List<Appointment> executeQuery(String sql, int id) {
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute appointment query", e);
        }
        return list;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setTimeSlot(rs.getString("time_slot"));
        appointment.setStatus(rs.getString("status"));
        appointment.setReason(rs.getString("reason"));
        appointment.setNotes(rs.getString("notes"));
        java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            appointment.setCreatedAt(createdAt.toLocalDateTime());
        }
        return appointment;
    }
}

