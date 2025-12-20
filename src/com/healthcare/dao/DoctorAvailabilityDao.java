package com.healthcare.dao;

import com.healthcare.model.DoctorAvailability;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityDao {
    /**
     * Sets a connection to be used for subsequent operations.
     * Used for transaction management when called from service layer.
     *
     * @param connection the connection to use
     */
    void setConnection(Connection connection);

    List<DoctorAvailability> findByDoctor(int doctorId);

    List<DoctorAvailability> findByDoctorAndDate(int doctorId, LocalDate date);

    void addSlot(DoctorAvailability availability);

    void updateSlot(DoctorAvailability availability);

    void deleteSlot(int availabilityId);
}

