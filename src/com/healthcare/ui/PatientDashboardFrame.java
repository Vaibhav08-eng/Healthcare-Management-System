package com.healthcare.ui;

import com.healthcare.model.User;
import com.healthcare.ui.patient.AppointmentBookingPanel;
import com.healthcare.ui.patient.AppointmentHistoryPanel;
import com.healthcare.ui.patient.MedicalHistoryPanel;
import com.healthcare.ui.patient.ProfileManagementPanel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Patient dashboard with booking, history, medical records and profile tabs.
 */
public class PatientDashboardFrame extends JFrame {

    private final User patientUser;

    public PatientDashboardFrame(User patientUser) {
        this.patientUser = patientUser;
        setTitle("Patient Dashboard - " + patientUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildUi();
    }

    private void buildUi() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Appointment", new AppointmentBookingPanel(patientUser));
        tabs.addTab("Appointment History", new AppointmentHistoryPanel(patientUser));
        tabs.addTab("Medical History", new MedicalHistoryPanel(patientUser));
        tabs.addTab("Profile", new ProfileManagementPanel(patientUser));
        add(tabs, BorderLayout.CENTER);
    }
}

