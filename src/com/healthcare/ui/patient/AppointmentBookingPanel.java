package com.healthcare.ui.patient;

import com.healthcare.model.Appointment;
import com.healthcare.model.Doctor;
import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.User;
import com.healthcare.service.PatientService;
import com.healthcare.util.DateUtil;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Patient workflow for booking appointments.
 */
public class AppointmentBookingPanel extends JPanel {

    private final PatientService patientService = new PatientService();
    private final User patientUser;
    private final JComboBox<String> specializationBox = new JComboBox<>();
    private final JComboBox<Doctor> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField();
    private final DefaultListModel<String> slotsModel = new DefaultListModel<>();
    private List<Doctor> allDoctors;

    public AppointmentBookingPanel(User patientUser) {
        this.patientUser = patientUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        loadDoctors();
    }

    private void buildUi() {
        JPanel top = new JPanel(new GridLayout(0, 2, 5, 5));
        top.add(new JLabel("Specialization"));
        top.add(specializationBox);
        top.add(new JLabel("Doctor"));
        top.add(doctorBox);
        top.add(new JLabel("Date (yyyy-MM-dd)"));
        top.add(dateField);

        JButton loadSlots = new JButton("Load Slots");
        loadSlots.addActionListener(e -> loadSlots());
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> book());

        JPanel buttons = new JPanel();
        buttons.add(loadSlots);
        buttons.add(bookBtn);

        JList<String> slotList = new JList<>(slotsModel);
        slotList.setBorder(BorderFactory.createTitledBorder("Available Slots"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(slotList), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void loadDoctors() {
        allDoctors = patientService.getAllDoctors();
        specializationBox.removeAllItems();
        Set<String> specializations = allDoctors.stream()
                .map(Doctor::getSpecialization)
                .collect(Collectors.toSet());
        specializationBox.addItem("All");
        for (String specialization : specializations) {
            specializationBox.addItem(specialization);
        }
        specializationBox.addActionListener(e -> filterDoctors());
        filterDoctors();
    }

    private void filterDoctors() {
        String selectedSpec = (String) specializationBox.getSelectedItem();
        doctorBox.removeAllItems();
        List<Doctor> filtered = "All".equals(selectedSpec) || selectedSpec == null
                ? allDoctors
                : allDoctors.stream().filter(d -> selectedSpec.equals(d.getSpecialization())).collect(Collectors.toList());
        for (Doctor doctor : filtered) {
            doctorBox.addItem(doctor);
        }
    }

    private void loadSlots() {
        Doctor selectedDoctor = (Doctor) doctorBox.getSelectedItem();
        LocalDate date = DateUtil.parse(dateField.getText().trim());
        if (selectedDoctor == null || date == null) {
            UiUtil.showInfo("Select doctor and date first.");
            return;
        }
        List<DoctorAvailability> slots = patientService.getAvailableSlots(selectedDoctor.getDoctorId(), date);
        slotsModel.clear();
        for (DoctorAvailability availability : slots) {
            if (availability.isAvailable()) {
                slotsModel.addElement(availability.getTimeSlot());
            }
        }
    }

    private void book() {
        Doctor selectedDoctor = (Doctor) doctorBox.getSelectedItem();
        LocalDate date = DateUtil.parse(dateField.getText().trim());
        if (selectedDoctor == null || date == null) {
            UiUtil.showInfo("Select doctor and date first.");
            return;
        }
        if (slotsModel.isEmpty()) {
            UiUtil.showInfo("Load slots first.");
            return;
        }
        String selectedSlot = JOptionPane.showInputDialog(this, "Enter slot exactly as shown:");
        if (selectedSlot == null || selectedSlot.isEmpty()) {
            return;
        }
        String reason = JOptionPane.showInputDialog(this, "Reason for visit:");
        try {
            Appointment appointment = patientService.bookAppointment(
                    patientUser.getUserId(),
                    selectedDoctor.getDoctorId(),
                    date,
                    selectedSlot.trim(),
                    reason
            );
            UiUtil.showInfo("Appointment booked with ID " + appointment.getAppointmentId());
        } catch (Exception ex) {
            UiUtil.showError("Unable to book appointment.", ex);
        }
    }
}

