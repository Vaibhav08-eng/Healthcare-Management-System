package com.healthcare.util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Small helper methods for Swing dialogs.
 */
public final class UiUtil {

    private UiUtil() {
    }

    public static void showInfo(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE));
    }

    public static void showError(String message, Throwable throwable) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, message + "\n" + throwable.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
    }
}

