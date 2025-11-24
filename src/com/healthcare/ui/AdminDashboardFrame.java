package com.healthcare.ui;

import com.healthcare.model.User;
import com.healthcare.ui.admin.AnalyticsPanel;
import com.healthcare.ui.admin.AppointmentManagementPanel;
import com.healthcare.ui.admin.SystemSettingsPanel;
import com.healthcare.ui.admin.UserManagementPanel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Admin home screen consisting of the four management tabs.
 */
public class AdminDashboardFrame extends JFrame {

    private final User currentUser;

    public AdminDashboardFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Admin Dashboard - " + currentUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildUi();
    }

    private void buildUi() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", new UserManagementPanel());
        tabs.addTab("Appointments", new AppointmentManagementPanel());
        tabs.addTab("Settings", new SystemSettingsPanel());
        tabs.addTab("Analytics", new AnalyticsPanel());
        add(tabs, BorderLayout.CENTER);
    }
}

