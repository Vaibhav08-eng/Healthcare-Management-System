package com.healthcare.ui.admin;

import com.healthcare.model.SystemSetting;
import com.healthcare.service.AdminService;
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
 * Simple editable table for system settings.
 */
public class SystemSettingsPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Key", "Value"}, 0);
    private final JTable table = new JTable(tableModel);

    public SystemSettingsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        refresh();
    }

    private void buildUi() {
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.addActionListener(e -> saveChanges());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        JPanel bottom = new JPanel();
        bottom.add(saveBtn);
        bottom.add(refreshBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refresh() {
        List<SystemSetting> settings = adminService.getSystemSettings();
        tableModel.setRowCount(0);
        for (SystemSetting setting : settings) {
            tableModel.addRow(new Object[]{setting.getKey(), setting.getValue()});
        }
    }

    private void saveChanges() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String key = (String) tableModel.getValueAt(i, 0);
            String value = (String) tableModel.getValueAt(i, 1);
            adminService.updateSystemSetting(key, value);
        }
        UiUtil.showInfo("Settings updated.");
    }
}

