package com.healthcare.dao;

import com.healthcare.model.Feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores patient feedback for doctors.
 */
public class FeedbackDaoImpl implements FeedbackDao {

    private static final String BASE_SELECT = "SELECT feedback_id, patient_id, doctor_id, rating, comments, created_at FROM feedback";

    @Override
    public List<Feedback> findByDoctor(int doctorId) {
        String sql = BASE_SELECT + " WHERE doctor_id=? ORDER BY created_at DESC";
        List<Feedback> feedbackList = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                feedbackList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load feedback", e);
        }
        return feedbackList;
    }

    @Override
    public void save(Feedback feedback) {
        String sql = "INSERT INTO feedback (patient_id, doctor_id, rating, comments) VALUES (?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedback.getPatientId());
            ps.setInt(2, feedback.getDoctorId());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComments());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save feedback", e);
        }
    }

    private Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(rs.getInt("feedback_id"));
        feedback.setPatientId(rs.getInt("patient_id"));
        feedback.setDoctorId(rs.getInt("doctor_id"));
        feedback.setRating(rs.getInt("rating"));
        feedback.setComments(rs.getString("comments"));
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            feedback.setCreatedAt(ts.toLocalDateTime());
        } else {
            feedback.setCreatedAt(LocalDateTime.now());
        }
        return feedback;
    }
}

