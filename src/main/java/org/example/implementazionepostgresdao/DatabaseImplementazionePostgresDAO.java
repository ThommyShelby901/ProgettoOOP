package org.example.implementazionepostgresdao;

import org.example.dao.DatabaseDAO;
import org.example.database.ConnessioneDatabase;
import org.example.model.Bacheca;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DatabaseImplementazionePostgresDAO implements DatabaseDAO {
    private final Connection connection;
    private static final String COL_ID_TODO = "id_todo"; // 🔥 Costante a livello di classe
    private static final String COL_DATA_SCADENZA = "data_scadenza";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";

    public DatabaseImplementazionePostgresDAO() throws SQLException {
        this.connection = ConnessioneDatabase.getInstance().connection;
    }

    @Override
    public boolean utenteEsiste(String username) throws SQLException {
        String queryCheck = "SELECT COUNT(*) FROM utente WHERE username = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(queryCheck)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0; //  Restituisce `true` se l'utente esiste già
        }
    }

    @Override
    public void salvaUtente(Utente utente) throws SQLException {
        String queryInsert = "INSERT INTO utente (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
            pstmt.setString(1, utente.getUsername());
            pstmt.setString(2, utente.getPassword());
            pstmt.executeUpdate();
        }
    }


    @Override
    public void aggiungiUtente(String username, String password) throws SQLException {
        if (utenteEsiste(username)) {
            return;
        }
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO utente (username, password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Utente getUtenteByUsername(String username) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT username, password FROM utente WHERE username = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? new Utente(rs.getString("username"), rs.getString("password")) : null;
        }
    }

    @Override
    public Bacheca aggiungiBacheca(String titolo, String descrizione, String username) {
        if (titolo == null || titolo.trim().isEmpty() || descrizione == null || descrizione.trim().isEmpty()) {
            return null;
        }

        String queryInsert = "INSERT INTO bacheca (titolo, descrizione, username) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, descrizione);
            pstmt.setString(3, username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return new Bacheca(titolo, descrizione); // 🔥 Restituisce direttamente l'oggetto senza variabile temporanea
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        if (titoloCorrente == null || titoloCorrente.trim().isEmpty() ||
                nuovoTitolo == null || nuovoTitolo.trim().isEmpty() ||
                nuovaDescrizione == null || nuovaDescrizione.trim().isEmpty()) {
            return;
        }

        String query = "UPDATE bacheca SET titolo = ?, descrizione = ? WHERE titolo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nuovoTitolo);
            pstmt.setString(2, nuovaDescrizione);
            pstmt.setString(3, titoloCorrente);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaBacheca(String titolo) throws SQLException {
        String query = "DELETE FROM bacheca WHERE titolo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Bacheca> getListaBachecheDalDB(String username) throws SQLException {
        String query = "SELECT titolo, descrizione FROM bacheca WHERE username = ? OR username IS NULL";
        return executeBachecaQuery(query, username);
    }



    @Override
    public void salvaBachechePredefinite(List<Bacheca> bacheche, String username) throws SQLException {
        String query = "INSERT INTO bacheca (titolo, descrizione, username) VALUES (?, ?, ?) " +
                "ON CONFLICT (titolo) DO UPDATE SET username = EXCLUDED.username";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(3, username); // 🔥 Imposta `username` una sola volta, fuori dal loop

            for (Bacheca bacheca : bacheche) {
                pstmt.setString(1, bacheca.getTitoloBacheca());
                pstmt.setString(2, bacheca.getDescrizioneBacheca());
                pstmt.addBatch(); // 🔥 Aggiunge al batch senza reimpostare `username`
            }
            pstmt.executeBatch(); // 🔥 Esegue tutto il batch processing
        }
    }



    public boolean haBachechePredefinite(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM bacheca WHERE username = ? AND titolo IN ('Bacheca1', 'Bacheca2', 'Bacheca3')"; // 🔥 Ora usa i titoli

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // 🔥 Se trova almeno una bacheca predefinita, restituisce TRUE
            }
        }
        return false;
    }

    @Override
    public void aggiornaOrdineToDo(int idToDo, int nuovaPosizione) throws SQLException {
        String query = "UPDATE todo SET ordine = ? WHERE id_todo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, nuovaPosizione);
            pstmt.setInt(2, idToDo);
            pstmt.executeUpdate();
        }
    }






    public List<Bacheca> getBachecheUtente(String username) throws SQLException {
        String query = "SELECT * FROM bacheca WHERE id_bacheca IN (1, 2, 3) " +
                "UNION " +
                "SELECT * FROM bacheca WHERE username = ?";
        return executeBachecaQuery(query, username);
    }




    @Override
    public List<Bacheca> getBachecheByUsername(String username) throws SQLException {
        String query = "SELECT DISTINCT titolo, descrizione FROM bacheca WHERE username = ?";
        List<Bacheca> bacheche = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Bacheca nuovaBacheca = new Bacheca(rs.getString(COL_TITOLO), rs.getString(COL_DESCRIZIONE));
                bacheche.add(nuovaBacheca);
            }
        }
        return bacheche;
    }


    @Override
    public List<String> getListaUtenti() throws SQLException {
        List<String> utenti = new ArrayList<>();
        String query = "SELECT username FROM utente";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                utenti.add(rs.getString("username")); // 🔥 Recupera tutti gli utenti
            }
        }
        return utenti;
    }

    @Override
    public void aggiungiCondivisione(ToDo todo, String nomeUtente) throws SQLException {
        String queryCheck = "SELECT COUNT(*) FROM condivisione WHERE id_todo = ? AND username = ?";
        String queryInsert = "INSERT INTO condivisione (id_todo, username) VALUES (?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(queryCheck)) {
            checkStmt.setInt(1, todo.getId());
            checkStmt.setString(2, nomeUtente);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return; // 🔥 Evita duplicati
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
            pstmt.setInt(1, todo.getId());
            pstmt.setString(2, nomeUtente);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void rimuoviCondivisione(ToDo todo, String nomeUtente) throws SQLException {
        String query = "DELETE FROM condivisione WHERE id_todo = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, todo.getId());
            pstmt.setString(2, nomeUtente); // 🔥 Usa lo `username` per rimuovere la condivisione
            pstmt.executeUpdate();
        }
    }

    @Override
    public void creaToDo(ToDo todo, String titoloBacheca) throws SQLException {
        String query = "INSERT INTO todo (titolo, descrizione, data_scadenza, url, stato, username, titolo_bacheca) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_todo";


        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, todo.getTitoloToDo());
            pstmt.setString(2, todo.getDescrizioneToDo());
            pstmt.setDate(3, todo.getDataScadenza() != null ? java.sql.Date.valueOf(todo.getDataScadenza()) : null);
            pstmt.setString(4, todo.getUrl());
            pstmt.setString(5, todo.getStatoToDo().name());

            String usernameAutore = (todo.getAutore() != null && todo.getAutore().getUsername() != null) ? todo.getAutore().getUsername() : "default_user";
            pstmt.setString(6, usernameAutore);
            pstmt.setString(7, titoloBacheca);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                todo.setId(rs.getInt(COL_ID_TODO)); // 🔥 Usa variabile locale
            }
        }
    }

    @Override
    public ToDo getToDoByTitolo(String titolo) throws SQLException {
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza FROM todo WHERE titolo = ?";
         // 🔥 Costante locale

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ToDo todo = new ToDo();
                todo.setId(rs.getInt(COL_ID_TODO));
                todo.setTitoloToDo(rs.getString(COL_TITOLO));
                todo.setDescrizioneToDo(rs.getString(COL_DESCRIZIONE));

                if (rs.getDate(COL_DATA_SCADENZA) != null) {
                    todo.setDataScadenza(rs.getDate(COL_DATA_SCADENZA).toLocalDate().toString());
                }
                return todo;
            }
        }
        return null;
    }

    @Override
    public List<ToDo> getTuttiToDo(String titoloBacheca) throws SQLException {
        List<ToDo> listaToDo = new ArrayList<>();
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza FROM todo WHERE titolo_bacheca = ?";
         // 🔥 Costante locale

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titoloBacheca);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ToDo todo = new ToDo();
                todo.setId(rs.getInt(COL_ID_TODO));
                todo.setTitoloToDo(rs.getString(COL_TITOLO));
                todo.setDescrizioneToDo(rs.getString(COL_DESCRIZIONE));

                if (rs.getDate(COL_DATA_SCADENZA) != null) {
                    todo.setDataScadenza(rs.getDate(COL_DATA_SCADENZA).toLocalDate().toString());
                }

                listaToDo.add(todo);
            }
        }
        return listaToDo;
    }

    @Override
    public List<Bacheca> executeBachecaQuery(String query, String username) throws SQLException {
        List<Bacheca> bacheche = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Validazione del parametro username
            if (username != null && username.matches("\\w+")) {
                pstmt.setString(1, username);
            } else {
                throw new IllegalArgumentException("Username non valido!");
            }

            try (ResultSet rs = pstmt.executeQuery()) { // Chiude automaticamente il ResultSet alla fine
                while (rs.next()) {
                    bacheche.add(new Bacheca(rs.getString(COL_TITOLO), rs.getString(COL_DESCRIZIONE)));
                }
            }
        }

        return bacheche;
    }

    @Override
    public void aggiornaToDo(ToDo todo) {
        String query = "UPDATE todo SET titolo = ?, descrizione = ?, data_scadenza = ? WHERE id_todo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, todo.getTitoloToDo());
            pstmt.setString(2, todo.getDescrizioneToDo());
            pstmt.setDate(3, java.sql.Date.valueOf(todo.getDataScadenza())); // 🔥 Converte LocalDate in SQL Date
            pstmt.setInt(4, todo.getId());
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaToDo(String titolo, String titoloBacheca) throws SQLException {
        String query = "DELETE FROM todo WHERE titolo = ? AND titolo_bacheca = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, titoloBacheca); // 🔥 Ora passiamo direttamente il titolo della bacheca!
            pstmt.executeUpdate();
        }
    }


    @Override
    public void trasferisciToDo(ToDo todo, String titoloBachecaDestinazione) {
        if (todo == null || titoloBachecaDestinazione == null || titoloBachecaDestinazione.trim().isEmpty()) {
            return;
        }

        String query = "UPDATE todo SET titolo_bacheca = ? WHERE id_todo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titoloBachecaDestinazione);
            pstmt.setInt(2, todo.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
