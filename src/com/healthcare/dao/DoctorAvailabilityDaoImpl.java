package com.healthcare.dao;

import com.healthcare.model.DoctorAvailability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for doctor availability slots.
 */
public class DoctorAvailabilityDaoImpl implements DoctorAvailabilityDao {

    private static final String BASE_SELECT = "SELECT availability_id, doctor_id, available_date, time_slot, is_available FROM doctor_availability";

    @Override
    public List<DoctorAvailability> findByDoctor(int doctorId) {
        String sql = BASE_SELECT + " WHERE doctor_id=? ORDER BY available_date, time_slot";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            return mapRows(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load doctor availability", e);
        }
    }

    @Override
    public List<DoctorAvailability> findByDoctorAndDate(int doctorId, LocalDate date) {
        String sql = BASE_SELECT + " WHERE doctor_id=? AND available_date=? ORDER BY time_slot";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setObject(2, date);
            ResultSet rs = ps.executeQuery();
            return mapRows(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load doctor availability for date", e);
        }
    }

    @Override
    public void addSlot(DoctorAvailability availability) {
        String sql = "INSERT INTO doctor_availability (doctor_id, available_date, time_slot, is_available) VALUES (?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availability.getDoctorId());
            ps.setObject(2, availability.getAvailableDate());
            ps.setString(3, availability.getTimeSlot());
            ps.setBoolean(4, availability.isAvailable());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert availability slot", e);
        }
    }

    @Override
    public void updateSlot(DoctorAvailability availability) {
        String sql = "UPDATE doctor_availability SET available_date=?, time_slot=?, is_available=? WHERE availability_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, availability.getAvailableDate());
            ps.setString(2, availability.getTimeSlot());
            ps.setBoolean(3, availability.isAvailable());
            ps.setInt(4, availability.getAvailabilityId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update availability slot", e);
        }
    }

    @Override
    public void deleteSlot(int availabilityId) {
        String sql = "DELETE FROM doctor_availability WHERE availability_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availabilityId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete availability slot", e);
        }
    }

    private List<DoctorAvailability> mapRows(ResultSet rs) throws SQLException {
        List<DoctorAvailability> list = new ArrayList<>();
        while (rs.next()) {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setAvailabilityId(rs.getInt("availability_id"));
            availability.setDoctorId(rs.getInt("doctor_id"));
            availability.setAvailableDate(rs.getDate("available_date").toLocalDate());
            availability.setTimeSlot(rs.getString("time_slot"));
            availability.setAvailable(rs.getBoolean("is_available"));
            list.add(availability);
        }
        return list;
    }
}

