package com.healthcare.ui.admin;

import com.healthcare.model.Appointment;
import com.healthcare.service.AdminService;
import com.healthcare.util.DateUtil;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin appointment control center.
 */
public class AppointmentManagementPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Doctor", "Date", "Slot", "Status", "Reason"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private List<Appointment> allAppointments;

    private final JTextField doctorFilter = new JTextField();
    private final JTextField statusFilter = new JTextField();
    private final JTextField startDateFilter = new JTextField();
    private final JTextField endDateFilter = new JTextField();

    public AppointmentManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refreshTable();
    }

    private void buildUi() {
        JPanel filters = new JPanel(new GridLayout(2, 4, 5, 5));
        filters.add(new JLabel("Doctor ID"));
        filters.add(new JLabel("Status"));
        filters.add(new JLabel("Start Date (yyyy-MM-dd)"));
        filters.add(new JLabel("End Date (yyyy-MM-dd)"));
        filters.add(doctorFilter);
        filters.add(statusFilter);
        filters.add(startDateFilter);
        filters.add(endDateFilter);

        JButton applyFilter = new JButton("Apply Filter");
        applyFilter.addActionListener(e -> filterTable());
        JButton resetFilter = new JButton("Reset");
        resetFilter.addActionListener(e -> {
            doctorFilter.setText("");
            statusFilter.setText("");
            startDateFilter.setText("");
            endDateFilter.setText("");
            populateTable(allAppointments);
        });
        JPanel filterPanel = new JPanel();
        filterPanel.add(applyFilter);
        filterPanel.add(resetFilter);

        JPanel top = new JPanel(new BorderLayout());
        top.add(filters, BorderLayout.CENTER);
        top.add(filterPanel, BorderLayout.SOUTH);

        JPanel actions = new JPanel();
        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(e -> updateStatus("CONFIRMED"));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> updateStatus("CANCELLED"));
        JButton completeBtn = new JButton("Mark Completed");
        completeBtn.addActionListener(e -> updateStatus("COMPLETED"));
        JButton rescheduleBtn = new JButton("Reschedule");
        rescheduleBtn.addActionListener(e -> reschedule());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        actions.add(confirmBtn);
        actions.add(cancelBtn);
        actions.add(completeBtn);
        actions.add(rescheduleBtn);
        actions.add(refreshBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        allAppointments = adminService.getAllAppointments();
        populateTable(allAppointments);
    }

    private void populateTable(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                    appointment.getAppointmentId(),
                    appointment.getPatientId(),
                    appointment.getDoctorId(),
                    DateUtil.format(appointment.getAppointmentDate()),
                    appointment.getTimeSlot(),
                    appointment.getStatus(),
                    appointment.getReason()
            });
        }
    }

    private void filterTable() {
        List<Appointment> filtered = allAppointments;
        if (!doctorFilter.getText().trim().isEmpty()) {
            int doctorId = Integer.parseInt(doctorFilter.getText().trim());
            filtered = filtered.stream().filter(a -> a.getDoctorId() == doctorId).collect(Collectors.toList());
        }
        if (!statusFilter.getText().trim().isEmpty()) {
            String status = statusFilter.getText().trim().toUpperCase();
            filtered = filtered.stream().filter(a -> a.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
        }
        if (!startDateFilter.getText().trim().isEmpty()) {
            LocalDate start = DateUtil.parse(startDateFilter.getText().trim());
            filtered = filtered.stream().filter(a -> !a.getAppointmentDate().isBefore(start)).collect(Collectors.toList());
        }
        if (!endDateFilter.getText().trim().isEmpty()) {
            LocalDate end = DateUtil.parse(endDateFilter.getText().trim());
            filtered = filtered.stream().filter(a -> !a.getAppointmentDate().isAfter(end)).collect(Collectors.toList());
        }
        populateTable(filtered);
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select an appointment.");
            return;
        }
        int appointmentId = (Integer) tableModel.getValueAt(row, 0);
        adminService.changeAppointmentStatus(appointmentId, status);
        refreshTable();
    }

    private void reschedule() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select an appointment.");
            return;
        }
        String date = JOptionPane.showInputDialog(this, "Enter new date (yyyy-MM-dd):");
        String slot = JOptionPane.showInputDialog(this, "Enter new time slot (e.g. 10:00-10:30):");
        if (date == null || slot == null) {
            return;
        }
        LocalDate newDate = DateUtil.parse(date.trim());
        int appointmentId = (Integer) tableModel.getValueAt(row, 0);
        adminService.rescheduleAppointment(appointmentId, newDate, slot.trim());
        refreshTable();
    }
}

