package com.healthcare;

import com.healthcare.ui.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Online Healthcare Management System.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

