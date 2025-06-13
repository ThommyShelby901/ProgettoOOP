package org.example.implementazionepostgresdao;

import org.example.dao.DatabaseDAO;
import org.example.database.ConnessioneDatabase;
import org.example.model.Bacheca;
import org.example.model.StatoToDo;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImplementazionePostgresDAO implements DatabaseDAO {
    private final Connection connection;
    private static final String COL_ID_TODO = "id_todo"; // 🔥 Costante a livello di classe
    private static final String COL_DATA_SCADENZA = "data_scadenza";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String DEFAULT_COLOR_HEX = "#FFFFFF";

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


    // Mantieni solo operazioni CRUD sul database
    @Override
    public Bacheca aggiungiBacheca(String titolo, String descrizione, String username) throws SQLException {
        String query = "INSERT INTO bacheca (titolo, descrizione, username) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, descrizione);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
            return new Bacheca(titolo, descrizione);
        }
    }


    @Override
    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione, String username) {
        if (titoloCorrente == null || titoloCorrente.trim().isEmpty() ||
                nuovoTitolo == null || nuovoTitolo.trim().isEmpty() ||
                nuovaDescrizione == null || nuovaDescrizione.trim().isEmpty() ||
                username == null || username.trim().isEmpty()) {
            return;
        }

        String query = "UPDATE bacheca SET titolo = ?, descrizione = ? WHERE titolo = ? AND username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nuovoTitolo);
            pstmt.setString(2, nuovaDescrizione);
            pstmt.setString(3, titoloCorrente);
            pstmt.setString(4, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaBacheca(String titolo, String username) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty() || username == null || username.trim().isEmpty()) {
            return;
        }

        String query = "DELETE FROM bacheca WHERE titolo = ? AND username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Bacheca> getListaBachecheDalDB(String username) throws SQLException {
        String query = "SELECT titolo, descrizione FROM bacheca WHERE username = ?";
        return executeBachecaQuery(query, username);
    }


    @Override
    public void salvaBachechePredefinite(List<Bacheca> bacheche, String username) throws SQLException {
        String query = "INSERT INTO bacheca (titolo, descrizione, username) VALUES (?, ?, ?) " +
                "ON CONFLICT (titolo, username) DO NOTHING";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (Bacheca bacheca : bacheche) {
                pstmt.setString(1, bacheca.getTitoloBacheca());
                pstmt.setString(2, bacheca.getDescrizioneBacheca());
                pstmt.setString(3, username); // Questo associa la bacheca all'utente
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }


    @Override
    public boolean haBachechePredefinite(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM bacheca WHERE username = ? AND titolo IN ('Università', 'Lavoro', 'Tempo Libero')";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 3;  // Devono esserci tutte e 3 le bacheche predefinite
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


    @Override
    public List<Bacheca> getBachecheByUsername(String username) throws SQLException {
        String query = "SELECT titolo, descrizione FROM bacheca WHERE username = ?";
        List<Bacheca> bacheche = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Bacheca nuovaBacheca = new Bacheca(
                        rs.getString(COL_TITOLO),
                        rs.getString(COL_DESCRIZIONE)
                );
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
            pstmt.setString(2, nomeUtente);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Nessuna condivisione trovata da rimuovere");
            }
        }
    }

    @Override
    public void creaToDo(ToDo todo, String titoloBacheca) throws SQLException {
        String getMaxOrdineQuery = "SELECT COALESCE(MAX(ordine), 0) FROM todo WHERE titolo_bacheca = ? AND username = ?";
        String insertQuery = "INSERT INTO todo (titolo, descrizione, data_scadenza, url, stato, username, titolo_bacheca, ordine, colore_sfondo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_todo";

        int nuovoOrdine = 1;

        try (PreparedStatement getMaxStmt = connection.prepareStatement(getMaxOrdineQuery)) {
            getMaxStmt.setString(1, titoloBacheca);
            getMaxStmt.setString(2, todo.getAutore().getUsername());
            ResultSet rs = getMaxStmt.executeQuery();
            if (rs.next()) {
                nuovoOrdine = rs.getInt(1) + 1;
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, todo.getTitoloToDo());
            pstmt.setString(2, todo.getDescrizioneToDo());
            pstmt.setDate(3, todo.getDataScadenza() != null ? java.sql.Date.valueOf(todo.getDataScadenza()) : null);
            pstmt.setString(4, todo.getUrl());
            pstmt.setString(5, todo.getStatoToDo() != null ? todo.getStatoToDo().name() : StatoToDo.NONCOMPLETATO.name());

            pstmt.setString(6, todo.getAutore().getUsername());
            pstmt.setString(7, titoloBacheca);
            pstmt.setInt(8, nuovoOrdine);
            pstmt.setString(9, colorToHex(todo.getColoreSfondo()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                todo.setId(rs.getInt(COL_ID_TODO));
            }
        }
    }

    private String colorToHex(Color color) {
        if (color == null) {
            return DEFAULT_COLOR_HEX;
        }
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }


    @Override
    public ToDo getToDoByTitolo(String titolo) throws SQLException {
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza, colore_sfondo, percorso_immagine FROM todo WHERE titolo = ?";

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

                // Aggiungi il colore
                String coloreHex = rs.getString("colore_sfondo");
                todo.setColoreSfondo(coloreHex != null ? Color.decode(coloreHex) : Color.WHITE);

                // Aggiungi il percorso dell'immagine
                todo.setPercorsoImmagine(rs.getString("percorso_immagine"));

                return todo;
            }
        }
        return null;
    }

    @Override
    public List<ToDo> getTuttiToDo(String titoloBacheca, String username) throws SQLException {
        String query = "SELECT t.id_todo, t.titolo, t.descrizione, t.data_scadenza, t.url, t.stato, t.username, t.colore_sfondo, t.percorso_immagine " +
                "FROM todo t " +
                "WHERE t.titolo_bacheca = ? AND (t.username = ? OR EXISTS " +
                "(SELECT 1 FROM condivisione c WHERE c.id_todo = t.id_todo AND c.username = ?))";

        List<ToDo> listaToDo = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titoloBacheca);
            pstmt.setString(2, username);
            pstmt.setString(3, username);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ToDo todo = new ToDo();
                todo.setId(rs.getInt("id_todo"));
                todo.setTitoloToDo(rs.getString("titolo"));
                todo.setDescrizioneToDo(rs.getString("descrizione"));

                if (rs.getDate("data_scadenza") != null) {
                    todo.setDataScadenza(rs.getDate("data_scadenza").toLocalDate().toString());
                }

                todo.setUrl(rs.getString("url"));
                todo.setStatoToDo(StatoToDo.valueOf(rs.getString("stato")));
                todo.setPercorsoImmagine(rs.getString("percorso_immagine")); // Aggiungi questa linea

                // Aggiungi questa parte per il colore
                String coloreHex = rs.getString("colore_sfondo");
                todo.setColoreSfondo(coloreHex != null ? Color.decode(coloreHex) : Color.WHITE);

                String autoreUsername = rs.getString("username");
                if (autoreUsername != null) {
                    Utente autore = this.getUtenteByUsername(autoreUsername);
                    todo.setAutore(autore);
                }

                todo.setBacheca(titoloBacheca);
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
    public void aggiornaToDo(ToDo todo) throws SQLException {
        String query = "UPDATE todo SET titolo = ?, descrizione = ?, data_scadenza = ?, " +
                "colore_sfondo = ?, percorso_immagine = ? WHERE id_todo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, todo.getTitoloToDo());
            pstmt.setString(2, todo.getDescrizioneToDo());
            pstmt.setDate(3, todo.getDataScadenza() != null ?
                    java.sql.Date.valueOf(todo.getDataScadenza()) : null);
            pstmt.setString(4, colorToHex(todo.getColoreSfondo()));
            pstmt.setString(5, todo.getPercorsoImmagine());
            pstmt.setInt(6, todo.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ToDo> getToDoByUsername(String username) throws SQLException {
        List<ToDo> listaToDo = new ArrayList<>();
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza, url, stato, titolo_bacheca, username, colore_sfondo, percorso_immagine " +
                "FROM todo WHERE username = ? OR id_todo IN " +
                "(SELECT id_todo FROM condivisione WHERE username = ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ToDo todo = new ToDo();
                todo.setId(rs.getInt("id_todo"));
                todo.setTitoloToDo(rs.getString("titolo"));
                todo.setDescrizioneToDo(rs.getString("descrizione"));

                if (rs.getDate("data_scadenza") != null) {
                    todo.setDataScadenza(rs.getDate("data_scadenza").toLocalDate().toString());
                }

                todo.setUrl(rs.getString("url"));
                todo.setStatoToDo(StatoToDo.valueOf(rs.getString("stato")));
                todo.setBacheca(rs.getString("titolo_bacheca"));
                todo.setPercorsoImmagine(rs.getString("percorso_immagine")); // Aggiungi questa linea

                // Aggiunta del colore di sfondo
                String coloreHex = rs.getString("colore_sfondo");
                todo.setColoreSfondo(coloreHex != null ? Color.decode(coloreHex) : Color.WHITE);

                // Carica sempre l'utente completo come autore
                String autoreUsername = rs.getString("username");
                if (autoreUsername != null) {
                    Utente autore = getUtenteByUsername(autoreUsername);
                    todo.setAutore(autore);
                }

                listaToDo.add(todo);
            }
        }
        return listaToDo;
    }

    @Override
    public List<String> getCondivisioniPerToDo(int idToDo) throws SQLException {
        List<String> condivisioni = new ArrayList<>();
        String query = "SELECT username FROM condivisione WHERE id_todo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idToDo);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                condivisioni.add(rs.getString("username"));
            }
        }
        return condivisioni;
    }

    @Override
    public String getAutoreToDo(int idToDo) throws SQLException {
        String query = "SELECT username FROM todo WHERE id_todo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idToDo);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("username") : null;
        }
    }

    @Override
    public boolean isToDoSharedWithUser(int idToDo, String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM condivisione WHERE id_todo = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idToDo);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
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



    @Override
    public List<ToDo> getToDoInScadenzaEntro(String username, LocalDate dataLimite) throws SQLException {
        String query = "SELECT * FROM todo WHERE username = ? AND data_scadenza <= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(dataLimite));
            return executeToDoQuery(pstmt);
        }
    }

    @Override
    public List<ToDo> cercaToDoPerTitolo(String username, String titolo) throws SQLException {
        String query = "SELECT * FROM todo WHERE username = ? AND LOWER(titolo) LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            String likePattern = "%" + titolo.toLowerCase() + "%";
            pstmt.setString(2, likePattern);
            return executeToDoQuery(pstmt);
        }
    }

    @Override
    public List<ToDo> executeToDoQuery(PreparedStatement pstmt) throws SQLException {
        List<ToDo> result = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ToDo todo = new ToDo();
                todo.setId(rs.getInt("id_todo"));
                todo.setTitoloToDo(rs.getString("titolo"));
                todo.setDescrizioneToDo(rs.getString("descrizione"));

                if (rs.getDate("data_scadenza") != null) {
                    todo.setDataScadenza(rs.getDate("data_scadenza").toLocalDate().toString());
                }

                todo.setUrl(rs.getString("url"));
                todo.setStatoToDo(StatoToDo.valueOf(rs.getString("stato")));
                todo.setBacheca(rs.getString("titolo_bacheca"));
                todo.setPercorsoImmagine(rs.getString("percorso_immagine")); // Aggiungi questa linea

                // Carica il colore di sfondo se presente
                String coloreHex = rs.getString("colore_sfondo");
                todo.setColoreSfondo(coloreHex != null ? Color.decode(coloreHex) : Color.WHITE);

                String username = rs.getString("username");
                if (username != null) {
                    Utente autore = this.getUtenteByUsername(username);
                    todo.setAutore(autore);
                }

                result.add(todo);
            }
        }
        return result;
    }
}
