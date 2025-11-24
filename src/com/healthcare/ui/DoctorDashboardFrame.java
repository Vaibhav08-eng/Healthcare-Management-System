package com.healthcare.ui;

import com.healthcare.model.User;
import com.healthcare.ui.doctor.AppointmentOverviewPanel;
import com.healthcare.ui.doctor.FeedbackPanel;
import com.healthcare.ui.doctor.PatientRecordsPanel;
import com.healthcare.ui.doctor.ScheduleManagementPanel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Doctor's dashboard with schedule, appointments, records and feedback.
 */
public class DoctorDashboardFrame extends JFrame {

    private final User doctorUser;

    public DoctorDashboardFrame(User doctorUser) {
        this.doctorUser = doctorUser;
        setTitle("Doctor Dashboard - " + doctorUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildUi();
    }

    private void buildUi() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Schedule", new ScheduleManagementPanel(doctorUser));
        tabs.addTab("Patient Records", new PatientRecordsPanel(doctorUser));
        tabs.addTab("Appointments", new AppointmentOverviewPanel(doctorUser));
        tabs.addTab("Feedback", new FeedbackPanel(doctorUser));
        add(tabs, BorderLayout.CENTER);
    }
}

