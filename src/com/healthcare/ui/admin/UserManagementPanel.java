package com.healthcare.ui.admin;

import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.service.AdminService;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Allows the admin to CRUD users.
 */
public class UserManagementPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Email", "Role", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = new JTextField();

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refreshTable();
    }

    private void buildUi() {
        JPanel top = new JPanel(new BorderLayout(5, 5));
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchUsers());
        top.add(new JLabel("Search:"), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);
        top.add(searchBtn, BorderLayout.EAST);

        JPanel actions = new JPanel();
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> openUserDialog(null));
        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(e -> editSelectedUser());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedUser());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        List<User> users = adminService.getAllUsers();
        tableModel.setRowCount(0);
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getUserId(), user.getName(), user.getEmail(), user.getRole(),
                    user.getStatus()});
        }
    }

    private void searchUsers() {
        String keyword = searchField.getText().trim();
        List<User> users = keyword.isEmpty() ? adminService.getAllUsers() : adminService.searchUsers(keyword);
        tableModel.setRowCount(0);
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getUserId(), user.getName(), user.getEmail(), user.getRole(),
                    user.getStatus()});
        }
    }

    private void openUserDialog(User existingUser) {
        JTextField nameField = new JTextField(existingUser != null ? existingUser.getName() : "");
        JTextField emailField = new JTextField(existingUser != null ? existingUser.getEmail() : "");
        JTextField passwordField = new JTextField();
        String[] roles = {"ADMIN", "DOCTOR", "PATIENT"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        if (existingUser != null) {
            roleBox.setSelectedItem(existingUser.getRole());
        }
        String[] statusValues = {"ACTIVE", "INACTIVE"};
        JComboBox<String> statusBox = new JComboBox<>(statusValues);
        if (existingUser != null) {
            statusBox.setSelectedItem(existingUser.getStatus());
        }
        JTextField extraField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Email"));
        panel.add(emailField);
        panel.add(new JLabel(existingUser == null ? "Password" : "New Password (optional)"));
        panel.add(passwordField);
        panel.add(new JLabel("Role"));
        panel.add(roleBox);
        panel.add(new JLabel("Status"));
        panel.add(statusBox);
        panel.add(new JLabel("Specialization / Blood Group"));
        panel.add(extraField);
        panel.add(new JLabel("Phone"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                (existingUser == null ? "Add User" : "Edit User"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            User user = existingUser != null ? existingUser : new User();
            user.setName(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setRole(roleBox.getSelectedItem().toString());
            user.setStatus(statusBox.getSelectedItem().toString());
            String rawPassword = passwordField.getText().trim();
            if (existingUser == null && rawPassword.isEmpty()) {
                UiUtil.showInfo("Password is required for new users.");
                return;
            }
            if (existingUser != null && !rawPassword.isEmpty()) {
                user.setPasswordHash(rawPassword);
            }
            if (existingUser == null) {
                if ("DOCTOR".equals(user.getRole())) {
                    Doctor doctor = new Doctor();
                    doctor.setSpecialization(extraField.getText().trim());
                    doctor.setExperienceYears(5);
                    doctor.setPhone(phoneField.getText().trim());
                    doctor.setConsultationFee(500);
                    adminService.createUser(user, rawPassword, doctor, null);
                } else if ("PATIENT".equals(user.getRole())) {
                    Patient patient = new Patient();
                    patient.setBloodGroup(extraField.getText().trim());
                    patient.setPhone(phoneField.getText().trim());
                    adminService.createUser(user, rawPassword, null, patient);
                } else {
                    adminService.createUser(user, rawPassword, null, null);
                }
            } else {
                if ("DOCTOR".equals(user.getRole())) {
                    Doctor doctor = new Doctor();
                    doctor.setSpecialization(extraField.getText().trim());
                    doctor.setPhone(phoneField.getText().trim());
                    adminService.updateUser(user, doctor, null);
                } else if ("PATIENT".equals(user.getRole())) {
                    Patient patient = new Patient();
                    patient.setBloodGroup(extraField.getText().trim());
                    patient.setPhone(phoneField.getText().trim());
                    adminService.updateUser(user, null, patient);
                } else {
                    adminService.updateUser(user, null, null);
                }
            }
            refreshTable();
        }
    }

    private void editSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UiUtil.showInfo("Select a user first.");
            return;
        }
        User user = new User();
        user.setUserId((Integer) tableModel.getValueAt(selectedRow, 0));
        user.setName((String) tableModel.getValueAt(selectedRow, 1));
        user.setEmail((String) tableModel.getValueAt(selectedRow, 2));
        user.setRole((String) tableModel.getValueAt(selectedRow, 3));
        user.setStatus((String) tableModel.getValueAt(selectedRow, 4));
        openUserDialog(user);
    }

    private void deleteSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UiUtil.showInfo("Select a user first.");
            return;
        }
        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected user?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            adminService.deleteUser(userId);
            refreshTable();
        }
    }
}

