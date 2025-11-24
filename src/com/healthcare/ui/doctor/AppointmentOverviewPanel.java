package com.healthcare.ui.doctor;

import com.healthcare.model.Appointment;
import com.healthcare.model.User;
import com.healthcare.service.DoctorService;
import com.healthcare.util.DateUtil;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Displays doctor's appointments in a grid with quick actions.
 */
public class AppointmentOverviewPanel extends JPanel {

    private final DoctorService doctorService = new DoctorService();
    private final User doctorUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Date", "Slot", "Status", "Reason"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public AppointmentOverviewPanel(User doctorUser) {
        this.doctorUser = doctorUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refresh();
    }

    private void buildUi() {
        JPanel actions = new JPanel();
        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(e -> updateStatus("CONFIRMED"));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> updateStatus("CANCELLED"));
        JButton completeBtn = new JButton("Mark Completed");
        completeBtn.addActionListener(e -> updateStatus("COMPLETED"));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        actions.add(confirmBtn);
        actions.add(cancelBtn);
        actions.add(completeBtn);
        actions.add(refreshBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void refresh() {
        List<Appointment> appointments = doctorService.getAppointments(doctorUser.getUserId());
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[]{
                    appointment.getAppointmentId(),
                    appointment.getPatientId(),
                    DateUtil.format(appointment.getAppointmentDate()),
                    appointment.getTimeSlot(),
                    appointment.getStatus(),
                    appointment.getReason()
            });
        }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select an appointment.");
            return;
        }
        int appointmentId = (Integer) model.getValueAt(row, 0);
        doctorService.updateAppointmentStatus(appointmentId, status);
        refresh();
    }
}

