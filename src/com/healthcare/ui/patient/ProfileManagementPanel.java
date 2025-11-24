package com.healthcare.ui.patient;

import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.service.PatientService;
import com.healthcare.util.DateUtil;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.time.LocalDate;

/**
 * Patient profile editor and password management.
 */
public class ProfileManagementPanel extends JPanel {

    private final PatientService patientService = new PatientService();
    private final User patientUser;
    private Patient patientProfile;

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField genderField = new JTextField();
    private final JTextField dobField = new JTextField();
    private final JTextField bloodGroupField = new JTextField();

    private final JTextField oldPasswordField = new JTextField();
    private final JTextField newPasswordField = new JTextField();
    private final JTextField confirmPasswordField = new JTextField();

    public ProfileManagementPanel(User patientUser) {
        this.patientUser = patientUser;
        setLayout(new GridLayout(0, 2, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        loadProfile();
    }

    private void buildUi() {
        add(new JLabel("Name"));
        add(nameField);
        add(new JLabel("Email"));
        add(emailField);
        add(new JLabel("Phone"));
        add(phoneField);
        add(new JLabel("Address"));
        add(addressField);
        add(new JLabel("Gender"));
        add(genderField);
        add(new JLabel("Date of Birth (yyyy-MM-dd)"));
        add(dobField);
        add(new JLabel("Blood Group"));
        add(bloodGroupField);

        JButton saveProfile = new JButton("Save Profile");
        saveProfile.addActionListener(e -> saveProfile());
        add(saveProfile);
        add(new JLabel());

        add(new JLabel("Old Password"));
        add(oldPasswordField);
        add(new JLabel("New Password"));
        add(newPasswordField);
        add(new JLabel("Confirm Password"));
        add(confirmPasswordField);
        JButton changePassword = new JButton("Change Password");
        changePassword.addActionListener(e -> changePassword());
        add(changePassword);
    }

    private void loadProfile() {
        this.patientProfile = patientService.getPatientProfile(patientUser.getUserId())
                .orElseGet(Patient::new);
        nameField.setText(patientUser.getName());
        emailField.setText(patientUser.getEmail());
        phoneField.setText(patientProfile.getPhone());
        addressField.setText(patientProfile.getAddress());
        genderField.setText(patientProfile.getGender());
        dobField.setText(DateUtil.format(patientProfile.getDob()));
        bloodGroupField.setText(patientProfile.getBloodGroup());
    }

    private void saveProfile() {
        patientUser.setName(nameField.getText().trim());
        patientUser.setEmail(emailField.getText().trim());
        patientProfile.setPatientId(patientUser.getUserId());
        patientProfile.setPhone(phoneField.getText().trim());
        patientProfile.setAddress(addressField.getText().trim());
        patientProfile.setGender(genderField.getText().trim());
        LocalDate dob = DateUtil.parse(dobField.getText().trim());
        patientProfile.setDob(dob);
        patientProfile.setBloodGroup(bloodGroupField.getText().trim());
        patientService.updateProfile(patientUser, patientProfile);
        UiUtil.showInfo("Profile updated.");
    }

    private void changePassword() {
        String oldPw = oldPasswordField.getText();
        String newPw = newPasswordField.getText();
        String confirmPw = confirmPasswordField.getText();
        if (!newPw.equals(confirmPw)) {
            UiUtil.showInfo("New passwords do not match.");
            return;
        }
        try {
            patientService.changePassword(patientUser.getUserId(), oldPw, newPw);
            UiUtil.showInfo("Password updated.");
        } catch (Exception ex) {
            UiUtil.showError("Unable to change password.", ex);
        }
    }
}

