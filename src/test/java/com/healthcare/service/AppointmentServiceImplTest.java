package com.healthcare.service;

import com.healthcare.dao.AppointmentDaoImpl;
import com.healthcare.dao.DoctorDaoImpl;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.service.impl.AppointmentServiceImpl;
import com.healthcare.util.DataSourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceImplTest {

    private final AppointmentDaoImpl appointmentDao = new AppointmentDaoImpl();
    private final DoctorDaoImpl doctorDao = new DoctorDaoImpl();
    private final AppointmentServiceImpl service = new AppointmentServiceImpl(appointmentDao, doctorDao);

    @BeforeEach
    void setUpSchema() throws Exception {
        try (Connection conn = DataSourceProvider.getConnection(); Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS appointments");
            st.execute("DROP TABLE IF EXISTS doctors");
            st.execute("CREATE TABLE doctors (doctor_id INT PRIMARY KEY, specialization VARCHAR(50), experience_years INT, phone VARCHAR(50), consultation_fee DOUBLE)");
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
        // seed doctor
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1);
        doctor.setSpecialization("CARDIO");
        doctor.setExperienceYears(5);
        doctor.setPhone("123");
        doctor.setConsultationFee(100);
        doctorDao.save(doctor);
    }

    @Test
    void shouldBookWhenSlotFree() {
        boolean booked = service.bookAppointment(10, 1, LocalDate.now(), "09:00-10:00", "Checkup");
        assertTrue(booked);
        List<Appointment> doctorAppointments = service.getAppointmentsForDoctor(1, LocalDate.now());
        assertEquals(1, doctorAppointments.size());
        assertEquals("PENDING", doctorAppointments.get(0).getStatus());
    }

    @Test
    void shouldRollbackOnConflict() {
        boolean first = service.bookAppointment(10, 1, LocalDate.now(), "09:00-10:00", "Checkup");
        assertTrue(first);
        ServiceException ex = assertThrows(ServiceException.class, () ->
                service.bookAppointment(11, 1, LocalDate.now(), "09:00-10:00", "Conflict"));
        assertNotNull(ex.getCause());
        List<Appointment> doctorAppointments = service.getAppointmentsForDoctor(1, LocalDate.now());
        assertEquals(1, doctorAppointments.size(), "Second booking should not be persisted");
    }
}

