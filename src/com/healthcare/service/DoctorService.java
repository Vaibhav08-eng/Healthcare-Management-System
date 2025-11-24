package com.healthcare.service;

import com.healthcare.dao.AppointmentDao;
import com.healthcare.dao.AppointmentDaoImpl;
import com.healthcare.dao.DoctorAvailabilityDao;
import com.healthcare.dao.DoctorAvailabilityDaoImpl;
import com.healthcare.dao.FeedbackDao;
import com.healthcare.dao.FeedbackDaoImpl;
import com.healthcare.dao.MedicalRecordDao;
import com.healthcare.dao.MedicalRecordDaoImpl;
import com.healthcare.model.Appointment;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.Feedback;
import com.healthcare.model.MedicalRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * Encapsulates functionality available to doctors.
 */
public class DoctorService {

    private final AppointmentDao appointmentDao;
    private final DoctorAvailabilityDao availabilityDao;
    private final MedicalRecordDao medicalRecordDao;
    private final FeedbackDao feedbackDao;

    public DoctorService() {
        this(new AppointmentDaoImpl(), new DoctorAvailabilityDaoImpl(),
                new MedicalRecordDaoImpl(), new FeedbackDaoImpl());
    }

    public DoctorService(AppointmentDao appointmentDao,
                         DoctorAvailabilityDao availabilityDao,
                         MedicalRecordDao medicalRecordDao,
                         FeedbackDao feedbackDao) {
        this.appointmentDao = appointmentDao;
        this.availabilityDao = availabilityDao;
        this.medicalRecordDao = medicalRecordDao;
        this.feedbackDao = feedbackDao;
    }

    public List<Appointment> getAppointments(int doctorId) {
        return appointmentDao.findByDoctor(doctorId);
    }

    public List<Appointment> getAppointments(int doctorId, LocalDate start, LocalDate end) {
        return appointmentDao.findByDoctorAndRange(doctorId, start, end);
    }

    public void updateAppointmentStatus(int appointmentId, String status) {
        appointmentDao.updateStatus(appointmentId, status);
    }

    public List<DoctorAvailability> getAvailability(int doctorId) {
        return availabilityDao.findByDoctor(doctorId);
    }

    public List<DoctorAvailability> getAvailability(int doctorId, LocalDate date) {
        return availabilityDao.findByDoctorAndDate(doctorId, date);
    }

    public void addAvailabilitySlot(DoctorAvailability availability) {
        availabilityDao.addSlot(availability);
    }

    public void updateAvailabilitySlot(DoctorAvailability availability) {
        availabilityDao.updateSlot(availability);
    }

    public void deleteAvailabilitySlot(int availabilityId) {
        availabilityDao.deleteSlot(availabilityId);
    }

    public List<MedicalRecord> getMedicalRecordsForPatient(int patientId) {
        return medicalRecordDao.findByPatient(patientId);
    }

    public List<MedicalRecord> getMedicalRecordsCreatedByDoctor(int doctorId) {
        return medicalRecordDao.findByDoctor(doctorId);
    }

    public int addMedicalRecord(MedicalRecord record) {
        return medicalRecordDao.save(record);
    }

    public void updateMedicalRecord(MedicalRecord record) {
        medicalRecordDao.update(record);
    }

    public List<Feedback> getFeedback(int doctorId) {
        return feedbackDao.findByDoctor(doctorId);
    }

    public double calculateAverageRating(int doctorId) {
        List<Feedback> feedback = getFeedback(doctorId);
        return feedback.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
    }
}

