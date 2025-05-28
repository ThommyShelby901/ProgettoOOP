package org.example;

import javax.swing.*;
import org.example.controller.AppController;
import org.example.dao.DatabaseDAO;
import org.example.gui.LoginFrame;
import org.example.implementazionePostgresDAO.DatabaseImplementazionePostgresDAO;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();

            // Inizializziamo gli utenti
            dao.aggiungiUtente("mario", "1234");
            dao.aggiungiUtente("luigi", "5678");
            dao.aggiungiUtente("peach", "91011");

            // Inizializziamo le bacheche per TUTTI gli utenti
            dao.aggiungiBacheca("Tempo Libero", "Hobby e svago");
            dao.aggiungiBacheca("Università", "Studio e appunti");
            dao.aggiungiBacheca("Lavoro", "Gestione progetti e task");

            System.out.println("Utenti e bacheche inizializzati nel database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            AppController controller = new AppController(loginFrame);
            loginFrame.setController(controller);
        });
    }
}
