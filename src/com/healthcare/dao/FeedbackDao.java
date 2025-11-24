package com.healthcare.dao;

import com.healthcare.model.Feedback;

import java.util.List;

public interface FeedbackDao {
    List<Feedback> findByDoctor(int doctorId);

    void save(Feedback feedback);
}

