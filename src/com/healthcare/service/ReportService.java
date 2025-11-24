package com.healthcare.service;

import com.healthcare.dao.AppointmentDao;
import com.healthcare.dao.AppointmentDaoImpl;
import com.healthcare.dao.UserDao;
import com.healthcare.dao.UserDaoImpl;
import com.healthcare.model.Appointment;
import com.healthcare.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Simple analytics aggregation for the admin dashboard.
 */
public class ReportService {

    private final UserDao userDao;
    private final AppointmentDao appointmentDao;

    public ReportService() {
        this(new UserDaoImpl(), new AppointmentDaoImpl());
    }

    public ReportService(UserDao userDao, AppointmentDao appointmentDao) {
        this.userDao = userDao;
        this.appointmentDao = appointmentDao;
    }

    public Map<String, Long> usersByRole() {
        List<User> users = userDao.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
    }

    public Map<Integer, Long> appointmentsByDoctor() {
        return appointmentDao.findAll().stream()
                .collect(Collectors.groupingBy(Appointment::getDoctorId, Collectors.counting()));
    }

    public long totalAppointments() {
        return appointmentDao.findAll().size();
    }

    public Map<LocalDate, Long> appointmentsPerDay(int daysBack) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(daysBack);
        return appointmentDao.findAll().stream()
                .filter(a -> !a.getAppointmentDate().isBefore(start))
                .collect(Collectors.groupingBy(Appointment::getAppointmentDate, Collectors.counting()));
    }
}

