package com.healthcare.service;

import com.healthcare.dao.AppointmentDao;
import com.healthcare.dao.AppointmentDaoImpl;
import com.healthcare.dao.DoctorDao;
import com.healthcare.dao.DoctorDaoImpl;
import com.healthcare.dao.PatientDao;
import com.healthcare.dao.PatientDaoImpl;
import com.healthcare.dao.SystemSettingDao;
import com.healthcare.dao.SystemSettingDaoImpl;
import com.healthcare.dao.UserDao;
import com.healthcare.dao.UserDaoImpl;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.SystemSetting;
import com.healthcare.model.User;
import com.healthcare.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Contains admin specific actions such as user, appointment and settings management.
 */
public class AdminService {

    private final UserDao userDao;
    private final PatientDao patientDao;
    private final DoctorDao doctorDao;
    private final AppointmentDao appointmentDao;
    private final SystemSettingDao settingDao;

    public AdminService() {
        this(new UserDaoImpl(), new PatientDaoImpl(), new DoctorDaoImpl(),
                new AppointmentDaoImpl(), new SystemSettingDaoImpl());
    }

    public AdminService(UserDao userDao,
                        PatientDao patientDao,
                        DoctorDao doctorDao,
                        AppointmentDao appointmentDao,
                        SystemSettingDao settingDao) {
        this.userDao = userDao;
        this.patientDao = patientDao;
        this.doctorDao = doctorDao;
        this.appointmentDao = appointmentDao;
        this.settingDao = settingDao;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public List<User> searchUsers(String keyword) {
        return userDao.search(keyword);
    }

    public int createUser(User user, String rawPassword, Doctor doctor, Patient patient) {
        user.setPasswordHash(PasswordUtil.hashPassword(rawPassword));
        int id = userDao.save(user);
        if ("DOCTOR".equalsIgnoreCase(user.getRole()) && doctor != null) {
            doctor.setDoctorId(id);
            doctorDao.save(doctor);
        }
        if ("PATIENT".equalsIgnoreCase(user.getRole()) && patient != null) {
            patient.setPatientId(id);
            patientDao.save(patient);
        }
        return id;
    }

    public void updateUser(User user, Doctor doctor, Patient patient) {
        if (user.getPasswordHash() != null && user.getPasswordHash().length() < 64) {
            // Assume plain password provided if length short
            user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
        }
        userDao.update(user);
        if ("DOCTOR".equalsIgnoreCase(user.getRole()) && doctor != null) {
            doctor.setDoctorId(user.getUserId());
            doctorDao.update(doctor);
        }
        if ("PATIENT".equalsIgnoreCase(user.getRole()) && patient != null) {
            patient.setPatientId(user.getUserId());
            patientDao.update(patient);
        }
    }

    public void deleteUser(int userId) {
        userDao.delete(userId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDao.findAll();
    }

    public void changeAppointmentStatus(int appointmentId, String status) {
        appointmentDao.updateStatus(appointmentId, status);
    }

    public void rescheduleAppointment(int appointmentId, LocalDate date, String timeSlot) {
        appointmentDao.reschedule(appointmentId, date, timeSlot);
    }

    public List<SystemSetting> getSystemSettings() {
        return settingDao.findAll();
    }

    public void updateSystemSetting(String key, String value) {
        settingDao.saveOrUpdate(new SystemSetting(key, value));
    }
}

