package org.example;

import javax.swing.*;
import controller.AppController;
import gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(); // 🔥 Creiamo prima il LoginFrame
            AppController controller = new AppController(loginFrame); // 🔥 Ora passiamo il LoginFrame al Controller
            loginFrame.setController(controller); // 🔥 Colleghiamo il Controller al LoginFrame
        });
    }
}


