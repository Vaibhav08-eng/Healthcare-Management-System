package com.healthcare.service;

import com.healthcare.dao.AppointmentDao;
import com.healthcare.dao.AppointmentDaoImpl;
import com.healthcare.dao.DoctorAvailabilityDao;
import com.healthcare.dao.DoctorAvailabilityDaoImpl;
import com.healthcare.dao.DoctorDao;
import com.healthcare.dao.DoctorDaoImpl;
import com.healthcare.dao.MedicalRecordDao;
import com.healthcare.dao.MedicalRecordDaoImpl;
import com.healthcare.dao.PatientDao;
import com.healthcare.dao.PatientDaoImpl;
import com.healthcare.dao.UserDao;
import com.healthcare.dao.UserDaoImpl;
import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.MedicalRecord;
import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Patient facing features such as booking appointments and maintaining profile.
 */
public class PatientService {

    private final DoctorDao doctorDao;
    private final DoctorAvailabilityDao availabilityDao;
    private final AppointmentDao appointmentDao;
    private final MedicalRecordDao medicalRecordDao;
    private final PatientDao patientDao;
    private final UserDao userDao;

    public PatientService() {
        this(new DoctorDaoImpl(), new DoctorAvailabilityDaoImpl(), new AppointmentDaoImpl(),
                new MedicalRecordDaoImpl(), new PatientDaoImpl(), new UserDaoImpl());
    }

    public PatientService(DoctorDao doctorDao,
                          DoctorAvailabilityDao availabilityDao,
                          AppointmentDao appointmentDao,
                          MedicalRecordDao medicalRecordDao,
                          PatientDao patientDao,
                          UserDao userDao) {
        this.doctorDao = doctorDao;
        this.availabilityDao = availabilityDao;
        this.appointmentDao = appointmentDao;
        this.medicalRecordDao = medicalRecordDao;
        this.patientDao = patientDao;
        this.userDao = userDao;
    }

    public List<Doctor> getAllDoctors() {
        return doctorDao.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorDao.findBySpecialization(specialization);
    }

    public List<DoctorAvailability> getAvailableSlots(int doctorId, LocalDate date) {
        return availabilityDao.findByDoctorAndDate(doctorId, date);
    }

    public Appointment bookAppointment(int patientId, int doctorId, LocalDate date, String timeSlot, String reason) {
        boolean alreadyBooked = appointmentDao.findByDoctorAndRange(doctorId, date, date)
                .stream()
                .anyMatch(a -> a.getTimeSlot().equalsIgnoreCase(timeSlot)
                        && !"CANCELLED".equalsIgnoreCase(a.getStatus()));
        if (alreadyBooked) {
            throw new IllegalStateException("Selected slot has already been booked.");
        }
        Appointment appointment = new Appointment();
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setAppointmentDate(date);
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus("PENDING");
        appointment.setReason(reason);
        int id = appointmentDao.save(appointment);
        appointment.setAppointmentId(id);
        return appointment;
    }

    public List<Appointment> getAppointmentHistory(int patientId) {
        return appointmentDao.findByPatient(patientId);
    }

    public void cancelAppointment(int appointmentId) {
        appointmentDao.updateStatus(appointmentId, "CANCELLED");
    }

    public List<MedicalRecord> getMedicalRecords(int patientId) {
        return medicalRecordDao.findByPatient(patientId);
    }

    public Optional<Patient> getPatientProfile(int patientId) {
        return patientDao.findById(patientId);
    }

    public void updateProfile(User user, Patient patient) {
        userDao.update(user);
        if (patientDao.findById(patient.getPatientId()).isPresent()) {
            patientDao.update(patient);
        } else {
            patientDao.save(patient);
        }
    }

    public void changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDao.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        userDao.update(user);
    }
}

