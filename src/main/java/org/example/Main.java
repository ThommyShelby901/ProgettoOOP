package org.example;

import javax.swing.*;
import org.example.controller.Controller;
import org.example.dao.DatabaseDAO;
import org.example.implementazionepostgresdao.DatabaseImplementazionePostgresDAO;
import java.sql.SQLException;

/**
 * classe main dell'applicazione, punto di ingresso del programma, responsabile dell'inizializzazione del DAO
 * e del controller
 */
public class Main {
    /**
     * Inizializza un'istanza di {@link DatabaseImplementazionePostgresDAO} per interagire con il database
     * e un'istanza di {@link Controller} per gestire la logica e l'interazione utente.
     * In caso di {@link SQLException} durante l'inizializzazione del database (es. problemi di connessione),
     * viene mostrato un messaggio di errore all'utente.
     * @param args Argomenti della riga di comando
     */
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