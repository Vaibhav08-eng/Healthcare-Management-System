package com.healthcare.dao;

import com.healthcare.model.MedicalRecord;

import java.util.List;

public interface MedicalRecordDao {
    List<MedicalRecord> findByPatient(int patientId);

    List<MedicalRecord> findByDoctor(int doctorId);

    int save(MedicalRecord record);

    void update(MedicalRecord record);
}

