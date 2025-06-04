package org.example;

import javax.swing.*;
import org.example.controller.AppController;
import org.example.dao.DatabaseDAO;
import org.example.gui.LoginFrame;
import org.example.implementazionepostgresdao.DatabaseImplementazionePostgresDAO;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Inizializzazione utenti demo (solo per sviluppo)
        initializeDemoUsers();

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();
                // Prima crea il LoginFrame senza controller
                LoginFrame loginFrame = new LoginFrame();
                // Poi crea il controller passando il loginFrame
                AppController controller = new AppController(dao, loginFrame);
                // Infine imposta il controller nel loginFrame
                loginFrame.setController(controller);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Errore di connessione al database: " + e.getMessage());
                System.exit(1);
            }
        });
    }

    private static void initializeDemoUsers() {
        try {
            DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();
            if (!dao.utenteEsiste("mario")) dao.aggiungiUtente("mario", "1234");
            if (!dao.utenteEsiste("luigi")) dao.aggiungiUtente("luigi", "5678");
            if (!dao.utenteEsiste("peach")) dao.aggiungiUtente("peach", "91011");
        } catch (SQLException e) {
            System.err.println("Errore nell'inizializzazione degli utenti demo: " + e.getMessage());
        }
    }
}