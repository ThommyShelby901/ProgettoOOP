package org.example;

import javax.swing.*;
import org.example.controller.AppController;
import org.example.dao.DatabaseDAO;
import org.example.gui.LoginFrame;
import org.example.implementazionepostgresdao.DatabaseImplementazionePostgresDAO;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();

            // Inizializziamo gli utenti
            dao.aggiungiUtente("mario", "1234");
            dao.aggiungiUtente("luigi", "5678");
            dao.aggiungiUtente("peach", "91011");


        } catch (SQLException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseDAO dao = new DatabaseImplementazionePostgresDAO(); // 🔥 Una sola istanza per tutto il programma
                LoginFrame loginFrame = new LoginFrame();
                AppController controller = new AppController(dao, loginFrame);
                loginFrame.setController(controller);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
