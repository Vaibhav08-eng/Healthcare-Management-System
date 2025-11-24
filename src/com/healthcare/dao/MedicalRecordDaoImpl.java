package com.healthcare.dao;

import com.healthcare.model.MedicalRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for doctor created medical records.
 */
public class MedicalRecordDaoImpl implements MedicalRecordDao {

    private static final String BASE_SELECT = "SELECT record_id, patient_id, doctor_id, visit_date, diagnosis, prescription, notes FROM medical_records";

    @Override
    public List<MedicalRecord> findByPatient(int patientId) {
        String sql = BASE_SELECT + " WHERE patient_id=? ORDER BY visit_date DESC";
        return execute(sql, patientId);
    }

    @Override
    public List<MedicalRecord> findByDoctor(int doctorId) {
        String sql = BASE_SELECT + " WHERE doctor_id=? ORDER BY visit_date DESC";
        return execute(sql, doctorId);
    }

    @Override
    public int save(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (patient_id, doctor_id, visit_date, diagnosis, prescription, notes) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, record.getPatientId());
            ps.setInt(2, record.getDoctorId());
            ps.setObject(3, record.getVisitDate());
            ps.setString(4, record.getDiagnosis());
            ps.setString(5, record.getPrescription());
            ps.setString(6, record.getNotes());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("Unable to get generated key for medical record");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save medical record", e);
        }
    }

    @Override
    public void update(MedicalRecord record) {
        String sql = "UPDATE medical_records SET visit_date=?, diagnosis=?, prescription=?, notes=? WHERE record_id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, record.getVisitDate());
            ps.setString(2, record.getDiagnosis());
            ps.setString(3, record.getPrescription());
            ps.setString(4, record.getNotes());
            ps.setInt(5, record.getRecordId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update medical record", e);
        }
    }

    private List<MedicalRecord> execute(String sql, int id) {
        List<MedicalRecord> records = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                records.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load medical records", e);
        }
        return records;
    }

    private MedicalRecord mapRow(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setPatientId(rs.getInt("patient_id"));
        record.setDoctorId(rs.getInt("doctor_id"));
        record.setVisitDate(rs.getDate("visit_date").toLocalDate());
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setPrescription(rs.getString("prescription"));
        record.setNotes(rs.getString("notes"));
        return record;
    }
}

