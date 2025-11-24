package com.healthcare.ui.doctor;

import com.healthcare.model.DoctorAvailability;
import com.healthcare.model.User;
import com.healthcare.service.DoctorService;
import com.healthcare.util.DateUtil;
import com.healthcare.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
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
 * Doctor availability management UI.
 */
public class ScheduleManagementPanel extends JPanel {

    private final DoctorService doctorService = new DoctorService();
    private final User doctorUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Date", "Time Slot", "Available"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextField dateField = new JTextField();
    private final JTextField slotField = new JTextField();

    public ScheduleManagementPanel(User doctorUser) {
        this.doctorUser = doctorUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refresh();
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.add(new JLabel("Date (yyyy-MM-dd)"));
        form.add(dateField);
        form.add(new JLabel("Time Slot e.g. 10:00-10:30"));
        form.add(slotField);

        JButton addBtn = new JButton("Add Slot");
        addBtn.addActionListener(e -> addSlot());
        JButton removeBtn = new JButton("Remove Slot");
        removeBtn.addActionListener(e -> removeSlot());
        JButton toggleBtn = new JButton("Toggle Availability");
        toggleBtn.addActionListener(e -> toggleAvailability());

        JPanel actions = new JPanel();
        actions.add(addBtn);
        actions.add(removeBtn);
        actions.add(toggleBtn);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void refresh() {
        List<DoctorAvailability> slots = doctorService.getAvailability(doctorUser.getUserId());
        model.setRowCount(0);
        for (DoctorAvailability slot : slots) {
            model.addRow(new Object[]{
                    slot.getAvailabilityId(),
                    DateUtil.format(slot.getAvailableDate()),
                    slot.getTimeSlot(),
                    slot.isAvailable()
            });
        }
    }

    private void addSlot() {
        LocalDate date = DateUtil.parse(dateField.getText().trim());
        if (date == null || slotField.getText().trim().isEmpty()) {
            UiUtil.showInfo("Provide both date and time slot.");
            return;
        }
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctorId(doctorUser.getUserId());
        availability.setAvailableDate(date);
        availability.setTimeSlot(slotField.getText().trim());
        availability.setAvailable(true);
        doctorService.addAvailabilitySlot(availability);
        refresh();
    }

    private void removeSlot() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select a slot first.");
            return;
        }
        int availabilityId = (Integer) model.getValueAt(row, 0);
        doctorService.deleteAvailabilitySlot(availabilityId);
        refresh();
    }

    private void toggleAvailability() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UiUtil.showInfo("Select a slot first.");
            return;
        }
        DoctorAvailability availability = new DoctorAvailability();
        availability.setAvailabilityId((Integer) model.getValueAt(row, 0));
        availability.setDoctorId(doctorUser.getUserId());
        availability.setAvailableDate(DateUtil.parse((String) model.getValueAt(row, 1)));
        availability.setTimeSlot((String) model.getValueAt(row, 2));
        availability.setAvailable(!((Boolean) model.getValueAt(row, 3)));
        doctorService.updateAvailabilitySlot(availability);
        refresh();
    }
}

