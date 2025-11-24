package com.healthcare.ui.patient;

import com.healthcare.model.Appointment;
import com.healthcare.model.User;
import com.healthcare.service.PatientService;
import com.healthcare.util.DateUtil;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

/**
 * Shows upcoming and past appointments for the patient.
 */
public class AppointmentHistoryPanel extends JPanel {

    private final PatientService patientService = new PatientService();
    private final User patientUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Doctor", "Date", "Slot", "Status", "Reason"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public AppointmentHistoryPanel(User patientUser) {
        this.patientUser = patientUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refresh();
    }

    private void buildUi() {
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        JButton cancelBtn = new JButton("Cancel Selected");
        cancelBtn.addActionListener(e -> cancelSelected());
        JPanel actions = new JPanel();
        actions.add(refreshBtn);
        actions.add(cancelBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void refresh() {
        List<Appointment> appointments = patientService.getAppointmentHistory(patientUser.getUserId());
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[]{
                    appointment.getAppointmentId(),
                    appointment.getDoctorId(),
                    DateUtil.format(appointment.getAppointmentDate()),
                    appointment.getTimeSlot(),
                    appointment.getStatus(),
                    appointment.getReason()
            });
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select an appointment first.");
            return;
        }
        LocalDate date = DateUtil.parse((String) model.getValueAt(row, 2));
        if (date != null && date.isBefore(LocalDate.now())) {
            UiUtil.showInfo("Cannot cancel past appointments.");
            return;
        }
        int appointmentId = (Integer) model.getValueAt(row, 0);
        patientService.cancelAppointment(appointmentId);
        refresh();
    }
}

