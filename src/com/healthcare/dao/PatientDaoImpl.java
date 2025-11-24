package com.healthcare.dao;

import com.healthcare.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Patient DAO implementation.
 */
public class PatientDaoImpl implements PatientDao {

    private static final String BASE_SELECT = "SELECT patient_id, dob, gender, phone, address, blood_group FROM patients";

    @Override
    public Optional<Patient> findById(int patientId) {
        String sql = BASE_SELECT + " WHERE patient_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch patient", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch patients", e);
        }
        return patients;
    }

    @Override
    public void save(Patient patient) {
        String sql = "INSERT INTO patients (patient_id, dob, gender, phone, address, blood_group) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patient.getPatientId());
            ps.setObject(2, patient.getDob());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getPhone());
            ps.setString(5, patient.getAddress());
            ps.setString(6, patient.getBloodGroup());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert patient profile", e);
        }
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE patients SET dob=?, gender=?, phone=?, address=?, blood_group=? WHERE patient_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, patient.getDob());
            ps.setString(2, patient.getGender());
            ps.setString(3, patient.getPhone());
            ps.setString(4, patient.getAddress());
            ps.setString(5, patient.getBloodGroup());
            ps.setInt(6, patient.getPatientId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update patient profile", e);
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        java.sql.Date dob = rs.getDate("dob");
        if (dob != null) {
            patient.setDob(dob.toLocalDate());
        }
        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setAddress(rs.getString("address"));
        patient.setBloodGroup(rs.getString("blood_group"));
        return patient;
    }
}

