package com.healthcare.ui.patient;

import com.healthcare.model.MedicalRecord;
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
import java.util.List;

/**
 * Displays read-only medical history for the logged in patient.
 */
public class MedicalHistoryPanel extends JPanel {

    private final PatientService patientService = new PatientService();
    private final User patientUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Doctor", "Visit Date", "Diagnosis"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public MedicalHistoryPanel(User patientUser) {
        this.patientUser = patientUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refresh();
    }

    private void buildUi() {
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        JButton viewBtn = new JButton("View Details");
        viewBtn.addActionListener(e -> viewDetails());
        JPanel top = new JPanel();
        top.add(refreshBtn);
        top.add(viewBtn);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        List<MedicalRecord> records = patientService.getMedicalRecords(patientUser.getUserId());
        model.setRowCount(0);
        for (MedicalRecord record : records) {
            model.addRow(new Object[]{
                    record.getRecordId(),
                    record.getDoctorId(),
                    DateUtil.format(record.getVisitDate()),
                    record.getDiagnosis()
            });
        }
    }

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select a record.");
            return;
        }
        int recordId = (Integer) model.getValueAt(row, 0);
        MedicalRecord record = patientService.getMedicalRecords(patientUser.getUserId())
                .stream()
                .filter(r -> r.getRecordId() == recordId)
                .findFirst()
                .orElse(null);
        if (record != null) {
            String details = "Diagnosis: " + record.getDiagnosis() + "\n\nPrescription: " +
                    record.getPrescription() + "\n\nNotes: " + record.getNotes();
            UiUtil.showInfo(details);
        }
    }
}

