package com.healthcare.dao;

import com.healthcare.model.Appointment;
import com.healthcare.util.DataSourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentDaoImplTest {

    private final AppointmentDaoImpl dao = new AppointmentDaoImpl();

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = DataSourceProvider.getConnection(); Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS appointments");
            st.execute("CREATE TABLE appointments (" +
                    "appointment_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "patient_id INT," +
                    "doctor_id INT," +
                    "appointment_date DATE," +
                    "time_slot VARCHAR(50)," +
                    "status VARCHAR(20)," +
                    "reason VARCHAR(255)," +
                    "notes VARCHAR(255)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE(doctor_id, appointment_date, time_slot)" +
                    ")");
        }
    }

    @Test
    void shouldNotCloseProvidedConnection() throws Exception {
        try (Connection shared = DataSourceProvider.getConnection()) {
            dao.setConnection(shared);
            Appointment a = new Appointment();
            a.setPatientId(1);
            a.setDoctorId(2);
            a.setAppointmentDate(LocalDate.now());
            a.setTimeSlot("10:00-11:00");
            a.setStatus("PENDING");
            a.setReason("Test");
            dao.save(a);

            List<Appointment> list = dao.findByDoctorAndRange(2, LocalDate.now(), LocalDate.now());
            assertFalse(list.isEmpty());
            assertFalse(shared.isClosed(), "DAO should not close externally provided connection");
        } finally {
            dao.setConnection(null);
        }
    }
}

