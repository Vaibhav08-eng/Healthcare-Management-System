package com.healthcare.dao;

import com.healthcare.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for doctor specific data.
 */
public class DoctorDaoImpl implements DoctorDao {

    private static final String BASE_SELECT = "SELECT doctor_id, specialization, experience_years, phone, consultation_fee FROM doctors";

    @Override
    public Optional<Doctor> findById(int doctorId) {
        String sql = BASE_SELECT + " WHERE doctor_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load doctor", e);
        }
        return Optional.empty();
    }

    @Override
       public List<Doctor> findAll() {
        return executeQuery(BASE_SELECT);
    }

    @Override
    public List<Doctor> findBySpecialization(String specialization) {
        String sql = BASE_SELECT + " WHERE specialization LIKE ? ORDER BY specialization";
        List<Doctor> doctors = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + specialization + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                doctors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find doctors by specialization", e);
        }
        return doctors;
    }

    @Override
    public void save(Doctor doctor) {
        String sql = "INSERT INTO doctors (doctor_id, specialization, experience_years, phone, consultation_fee) VALUES (?,?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctor.getDoctorId());
            ps.setString(2, doctor.getSpecialization());
            ps.setInt(3, doctor.getExperienceYears());
            ps.setString(4, doctor.getPhone());
            ps.setDouble(5, doctor.getConsultationFee());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert doctor", e);
        }
    }

    @Override
    public void update(Doctor doctor) {
        String sql = "UPDATE doctors SET specialization=?, experience_years=?, phone=?, consultation_fee=? WHERE doctor_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getSpecialization());
            ps.setInt(2, doctor.getExperienceYears());
            ps.setString(3, doctor.getPhone());
            ps.setDouble(4, doctor.getConsultationFee());
            ps.setInt(5, doctor.getDoctorId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update doctor", e);
        }
    }

    private List<Doctor> executeQuery(String sql) {
        List<Doctor> doctors = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doctors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch doctors", e);
        }
        return doctors;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getInt("doctor_id"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setExperienceYears(rs.getInt("experience_years"));
        doctor.setPhone(rs.getString("phone"));
        doctor.setConsultationFee(rs.getDouble("consultation_fee"));
        return doctor;
    }
}

