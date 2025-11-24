package com.healthcare.ui;

import com.healthcare.model.User;
import com.healthcare.service.AuthService;
import com.healthcare.util.UiUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Simple login screen that routes users to the correct dashboard.
 */
public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Online Healthcare Management System - Login");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildUi();
    }

    private void buildUi() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void authenticate() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            UiUtil.showInfo("Please enter both email and password.");
            return;
        }
        User user = authService.login(email, password);
        if (user == null) {
            UiUtil.showInfo("Invalid credentials or inactive user.");
            return;
        }
        dispose();
        switch (user.getRole()) {
            case "ADMIN":
                SwingUtilities.invokeLater(() -> new AdminDashboardFrame(user).setVisible(true));
                break;
            case "DOCTOR":
                SwingUtilities.invokeLater(() -> new DoctorDashboardFrame(user).setVisible(true));
                break;
            case "PATIENT":
                SwingUtilities.invokeLater(() -> new PatientDashboardFrame(user).setVisible(true));
                break;
            default:
                UiUtil.showInfo("Unsupported role: " + user.getRole());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

