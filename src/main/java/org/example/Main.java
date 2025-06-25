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

            // Crea il LoginFrame
            LoginFrame loginFrame = new LoginFrame();

            // Crea il controller
            AppController controller = new AppController(dao, loginFrame);

            // Imposta il controller nel LoginFrame
            loginFrame.setController(controller);

        } catch (SQLException e) {
            // Mostra un messaggio di errore se la connessione fallisce
            JOptionPane.showMessageDialog(null, "Errore di connessione al database: " + e.getMessage());
            System.exit(1);
        }
    }
}