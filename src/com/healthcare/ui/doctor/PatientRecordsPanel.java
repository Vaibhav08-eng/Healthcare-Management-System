package com.healthcare.ui.doctor;

import com.healthcare.model.MedicalRecord;
import com.healthcare.model.User;
import com.healthcare.service.DoctorService;
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

/**
 * Doctor panel for viewing and editing patient medical records.
 */
public class PatientRecordsPanel extends JPanel {

    private final DoctorService doctorService = new DoctorService();
    private final User doctorUser;
    private final JTextField patientIdField = new JTextField();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Visit Date", "Diagnosis"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private List<MedicalRecord> currentRecords;

    public PatientRecordsPanel(User doctorUser) {
        this.doctorUser = doctorUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
    }

    private void buildUi() {
        JPanel top = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton searchBtn = new JButton("Search Patient Records");
        searchBtn.addActionListener(e -> searchRecords());
        JButton viewBtn = new JButton("View");
        viewBtn.addActionListener(e -> viewRecord());
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addRecord());
        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(e -> editRecord());

        top.add(new JLabel("Patient ID"));
        top.add(patientIdField);
        top.add(searchBtn);
        top.add(addBtn);

        JPanel bottom = new JPanel();
        bottom.add(viewBtn);
        bottom.add(editBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void searchRecords() {
        try {
            int patientId = Integer.parseInt(patientIdField.getText().trim());
            currentRecords = doctorService.getMedicalRecordsForPatient(patientId);
            model.setRowCount(0);
            for (MedicalRecord record : currentRecords) {
                model.addRow(new Object[]{
                        record.getRecordId(),
                        record.getPatientId(),
                        DateUtil.format(record.getVisitDate()),
                        snippet(record.getDiagnosis())
                });
            }
        } catch (NumberFormatException ex) {
            UiUtil.showInfo("Enter a valid patient id.");
        }
    }

    private void viewRecord() {
        MedicalRecord record = getSelectedRecord();
        if (record == null) {
            return;
        }
        String details = "Diagnosis: " + record.getDiagnosis() + "\n\nPrescription: " + record.getPrescription()
                + "\n\nNotes: " + record.getNotes();
        JOptionPane.showMessageDialog(this, details, "Medical Record", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addRecord() {
        MedicalRecord record = showRecordDialog(null);
        if (record != null) {
            record.setDoctorId(doctorUser.getUserId());
            doctorService.addMedicalRecord(record);
            searchRecords();
        }
    }

    private void editRecord() {
        MedicalRecord selected = getSelectedRecord();
        if (selected == null) {
            return;
        }
        MedicalRecord updated = showRecordDialog(selected);
        if (updated != null) {
            updated.setRecordId(selected.getRecordId());
            updated.setDoctorId(doctorUser.getUserId());
            doctorService.updateMedicalRecord(updated);
            searchRecords();
        }
    }

    private MedicalRecord getSelectedRecord() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select a record first.");
            return null;
        }
        int recordId = (Integer) model.getValueAt(row, 0);
        return currentRecords.stream().filter(r -> r.getRecordId() == recordId).findFirst().orElse(null);
    }

    private MedicalRecord showRecordDialog(MedicalRecord record) {
        JTextField patientField = new JTextField(record != null ? String.valueOf(record.getPatientId()) : "");
        JTextField visitDateField = new JTextField(record != null ? DateUtil.format(record.getVisitDate()) : "");
        JTextField diagnosisField = new JTextField(record != null ? record.getDiagnosis() : "");
        JTextField prescriptionField = new JTextField(record != null ? record.getPrescription() : "");
        JTextField notesField = new JTextField(record != null ? record.getNotes() : "");

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Patient ID"));
        panel.add(patientField);
        panel.add(new JLabel("Visit Date (yyyy-MM-dd)"));
        panel.add(visitDateField);
        panel.add(new JLabel("Diagnosis"));
        panel.add(diagnosisField);
        panel.add(new JLabel("Prescription"));
        panel.add(prescriptionField);
        panel.add(new JLabel("Notes"));
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                record == null ? "Add Record" : "Edit Record",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                MedicalRecord newRecord = new MedicalRecord();
                newRecord.setPatientId(Integer.parseInt(patientField.getText().trim()));
                LocalDate visitDate = DateUtil.parse(visitDateField.getText().trim());
                newRecord.setVisitDate(visitDate != null ? visitDate : LocalDate.now());
                newRecord.setDiagnosis(diagnosisField.getText().trim());
                newRecord.setPrescription(prescriptionField.getText().trim());
                newRecord.setNotes(notesField.getText().trim());
                return newRecord;
            } catch (NumberFormatException ex) {
                UiUtil.showInfo("Invalid patient id.");
            }
        }
        return null;
    }

    private String snippet(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > 30 ? text.substring(0, 30) + "..." : text;
    }
}

