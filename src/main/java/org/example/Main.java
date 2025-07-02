package org.example;

import javax.swing.*;
import org.example.controller.Controller;
import org.example.dao.DatabaseDAO;
import org.example.implementazionepostgresdao.DatabaseImplementazionePostgresDAO;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();

            new Controller(dao);
        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Errore di connessione al database: " + e.getMessage());
            System.exit(1);
        }
    }
}