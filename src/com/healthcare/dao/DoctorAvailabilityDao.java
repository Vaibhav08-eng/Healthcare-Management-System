package com.healthcare.dao;

import com.healthcare.model.DoctorAvailability;

import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityDao {
    List<DoctorAvailability> findByDoctor(int doctorId);

    List<DoctorAvailability> findByDoctorAndDate(int doctorId, LocalDate date);

    void addSlot(DoctorAvailability availability);

    void updateSlot(DoctorAvailability availability);

    void deleteSlot(int availabilityId);
}

