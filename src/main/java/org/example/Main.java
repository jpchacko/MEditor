package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // macOS: put the menu bar in the system menu bar and set the app name
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "MEditor");

        // Swing components must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
