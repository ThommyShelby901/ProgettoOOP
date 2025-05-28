package org.example.implementazionePostgresDAO;

import org.example.dao.DatabaseDAO;
import org.example.database.ConnessioneDatabase;
import org.example.model.Bacheca;
import org.example.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DatabaseImplementazionePostgresDAO implements DatabaseDAO {
    private final Connection connection;

    public DatabaseImplementazionePostgresDAO() throws SQLException {
        this.connection = ConnessioneDatabase.getInstance().connection;
    }

    @Override
    public void aggiungiUtente(String username, String password) throws SQLException {
        String queryCheck = "SELECT COUNT(*) FROM utente WHERE username = ?";
        String queryInsert = "INSERT INTO utente (username, password) VALUES (?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(queryCheck)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("L'utente " + username + " esiste già nel database.");
                return; // 🔥 Evita di inserire un utente duplicato
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }


    @Override
    public Utente getUtenteByUsername(String username) throws SQLException {
        String query = "SELECT username, password FROM utente WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? new Utente(rs.getString("username"), rs.getString("password")) : null;
        }
    }

    @Override
    public void aggiungiBacheca(String titolo, String descrizione) throws SQLException {
        String queryCheck = "SELECT COUNT(*) FROM bacheca WHERE titolo = ?";
        String queryInsert = "INSERT INTO bacheca (titolo, descrizione) VALUES (?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(queryCheck)) {
            checkStmt.setString(1, titolo);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("La bacheca " + titolo + " esiste già nel database.");
                return; // 🔥 Evita duplicati
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, descrizione);
            pstmt.executeUpdate();
        }
    }




    @Override
    public List<Bacheca> getBachecheByUsername() throws SQLException {
        String query = "SELECT DISTINCT titolo, descrizione FROM bacheca";
        List<Bacheca> bacheche = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                bacheche.add(new Bacheca(rs.getString("titolo"), rs.getString("descrizione")));
            }
        }
        return bacheche;
    }


}
