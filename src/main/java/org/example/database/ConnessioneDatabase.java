package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnessioneDatabase {

    // ATTRIBUTI
    private static ConnessioneDatabase instance;
    public Connection connection = null;
    private static final String nome = "postgres";
    private static final String password = "Lorenzo2004.";
    private static final String url = "jdbc:postgresql://localhost:5432/ToDo project";
    private static final String driver = "org.postgresql.Driver";

    // COSTRUTTORE
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

    public static ConnessioneDatabase getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnessioneDatabase();
        } else if (instance.connection.isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }
}