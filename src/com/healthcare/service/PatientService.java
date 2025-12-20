package com.healthcare.service;

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
import com.healthcare.service.impl.AppointmentServiceImpl;
import com.healthcare.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Patient facing features such as booking appointments and maintaining profile.
 * Delegates appointment booking/cancellation to AppointmentService for proper transaction management.
 */
public class PatientService {

    private final DoctorDao doctorDao;
    private final DoctorAvailabilityDao availabilityDao;
    private final AppointmentService appointmentService;
    private final MedicalRecordDao medicalRecordDao;
    private final PatientDao patientDao;
    private final UserDao userDao;

    public PatientService() {
        this(new DoctorDaoImpl(), new DoctorAvailabilityDaoImpl(), new AppointmentServiceImpl(),
                new MedicalRecordDaoImpl(), new PatientDaoImpl(), new UserDaoImpl());
    }

    public PatientService(DoctorDao doctorDao,
                          DoctorAvailabilityDao availabilityDao,
                          AppointmentService appointmentService,
                          MedicalRecordDao medicalRecordDao,
                          PatientDao patientDao,
                          UserDao userDao) {
        this.doctorDao = doctorDao;
        this.availabilityDao = availabilityDao;
        this.appointmentService = appointmentService;
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

    /**
     * Books an appointment using AppointmentService for proper transaction management.
     *
     * @param patientId the patient ID
     * @param doctorId  the doctor ID
     * @param date      the appointment date
     * @param timeSlot  the time slot
     * @param reason    the reason for the appointment
     * @return the created appointment if successful, null otherwise
     * @throws IllegalStateException if booking fails (e.g., slot already booked)
     */
    public Appointment bookAppointment(int patientId, int doctorId, LocalDate date, String timeSlot, String reason) {
        boolean success = appointmentService.bookAppointment(patientId, doctorId, date, timeSlot, reason);
        if (!success) {
            throw new IllegalStateException("Failed to book appointment. The slot may already be booked or the doctor may not exist.");
        }
        // Retrieve the created appointment
        List<Appointment> appointments = appointmentService.getAppointmentsForDoctor(doctorId, date);
        return appointments.stream()
                .filter(a -> a.getPatientId() == patientId
                        && a.getTimeSlot().equalsIgnoreCase(timeSlot)
                        && "PENDING".equalsIgnoreCase(a.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Appointment was created but could not be retrieved."));
    }

    public List<Appointment> getAppointmentHistory(int patientId) {
        return appointmentService.getAppointmentsForPatient(patientId);
    }

    /**
     * Cancels an appointment using AppointmentService for proper transaction management.
     *
     * @param appointmentId the appointment ID
     * @param patientId     the patient ID (for verification)
     * @throws IllegalStateException if cancellation fails
     */
    public void cancelAppointment(int appointmentId, int patientId) {
        boolean success = appointmentService.cancelAppointment(appointmentId, patientId);
        if (!success) {
            throw new IllegalStateException("Failed to cancel appointment. It may not exist or may not belong to you.");
        }
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

