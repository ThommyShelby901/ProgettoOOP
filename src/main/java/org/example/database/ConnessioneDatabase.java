package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * questa classe assicura una connessione al database sicura, fornendo un buon punto d'accesso.
 * Utilizzando credenziali e url definite come costanti private. Classe singleton per la connessione al database Postgre
 */
public class ConnessioneDatabase {

    // ATTRIBUTI
    private static ConnessioneDatabase instance;
    public Connection connection = null;
    private static final String nome = "postgres";
    private static final String password = "Lorenzo2004.";
    private static final String url = "jdbc:postgresql://localhost:5432/ToDo project";
    private static final String driver = "org.postgresql.Driver";

    // COSTRUTTORE
    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza la connessione al database caricando il driver JDBC
     * e stabilendo la connessione utilizzando l'URL, il nome utente e la password definiti.
     * Stampa un messaggio di successo o un errore in caso di fallimento della connessione
     * o del caricamento del driver.
     * @throws SQLException Se si verifica un errore durante l'ottenimento della connessione al database.
     */
    private ConnessioneDatabase() throws SQLException {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, nome, password);
            System.out.println("Connessione riuscita!");

        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Restituisce l'unica istanza della classe.
     * Se l'istanza non esiste ancora o se la connessione esistente è chiusa, ne crea una nuova.
     * Questo assicura che ci sia sempre una connessione valida e singola.
     * @return L'istanza di {@link ConnessioneDatabase} contenente la connessione al database.
     * @throws SQLException Se si verifica un errore durante la creazione di una nuova connessione
     * (ad esempio, problemi con le credenziali o l'URL del database).
     */
    public static ConnessioneDatabase getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnessioneDatabase();
        } else if (instance.connection.isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }
}