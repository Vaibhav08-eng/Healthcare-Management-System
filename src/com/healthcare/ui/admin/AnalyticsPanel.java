package com.healthcare.ui.admin;

import com.healthcare.service.ReportService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Renders lightweight textual analytics without external chart libraries.
 */
public class AnalyticsPanel extends JPanel {

    private final ReportService reportService = new ReportService();
    private final JTextArea textArea = new JTextArea();

    public AnalyticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textArea.setEditable(false);
        JButton refreshButton = new JButton("Refresh Analytics");
        refreshButton.addActionListener(e -> refresh());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
        refresh();
    }

    private void refresh() {
        StringBuilder builder = new StringBuilder();
        builder.append("Total Appointments: ").append(reportService.totalAppointments()).append("\n\n");

        builder.append("Users By Role:\n");
        for (Map.Entry<String, Long> entry : reportService.usersByRole().entrySet()) {
            builder.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        builder.append("\nAppointments By Doctor:\n");
        for (Map.Entry<Integer, Long> entry : reportService.appointmentsByDoctor().entrySet()) {
            builder.append("  Doctor #").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        builder.append("\nAppointments Last 7 Days:\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        reportService.appointmentsPerDay(7).forEach((date, count) ->
                builder.append("  ").append(formatter.format(date)).append(": ").append(count).append("\n"));
        textArea.setText(builder.toString());
    }
}

