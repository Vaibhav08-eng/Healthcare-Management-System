package com.healthcare.ui.doctor;

import com.healthcare.model.Feedback;
import com.healthcare.model.User;
import com.healthcare.service.DoctorService;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Shows patient feedback for the doctor.
 */
public class FeedbackPanel extends JPanel {

    private final DoctorService doctorService = new DoctorService();
    private final User doctorUser;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Patient", "Rating", "Comment", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel averageLabel = new JLabel();

    public FeedbackPanel(User doctorUser) {
        this.doctorUser = doctorUser;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(averageLabel, BorderLayout.NORTH);
        refresh();
    }

    private void refresh() {
        List<Feedback> feedbackList = doctorService.getFeedback(doctorUser.getUserId());
        model.setRowCount(0);
        for (Feedback feedback : feedbackList) {
            model.addRow(new Object[]{
                    feedback.getPatientId(),
                    feedback.getRating(),
                    feedback.getComments(),
                    feedback.getCreatedAt()
            });
        }
        double avg = doctorService.calculateAverageRating(doctorUser.getUserId());
        averageLabel.setText("Average rating: " + String.format("%.2f", avg));
    }
}

