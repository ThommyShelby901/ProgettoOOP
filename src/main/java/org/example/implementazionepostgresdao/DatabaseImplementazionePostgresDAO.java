package org.example.implementazionepostgresdao;

import org.example.dao.DatabaseDAO;
import org.example.database.ConnessioneDatabase;
import org.example.model.Bacheca;
import org.example.model.StatoToDo;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.awt.*;
import java.sql.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseImplementazionePostgresDAO implements DatabaseDAO {
    private final Connection connection;
    private static final String COL_ID_TODO = "id_todo"; //  Costante a livello di classe
    private static final String COL_TITOLO = "titolo";
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String DEFAULT_COLOR_HEX = "#FFFFFF";
    private static final String COL_PERCORSO_IMMAGINE = "percorso_immagine";
    private static final String COL_COLORE_SFONDO_NOME = "colore_sfondo";
    private static final String STATO = "stato";
    private static final String USERNAME = "username";
    private static final String DATA_SCADENZA = "data_scadenza";
    private static final String COL_TITOLO_BACHECA = "titolo_bacheca";
    private static final Logger logger = Logger.getLogger(DatabaseImplementazionePostgresDAO.class.getName());



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
            return rs.next() ? new Utente(rs.getString(USERNAME), rs.getString("password")) : null;
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


    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione, String username) {
        if (isNullOrEmpty(titoloCorrente) || isNullOrEmpty(nuovoTitolo) || isNullOrEmpty(nuovaDescrizione) || isNullOrEmpty(username)) {
            return;
        }

        String query = "UPDATE bacheca SET titolo = ?, descrizione = ? WHERE titolo = ? AND username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nuovoTitolo);
            stmt.setString(2, nuovaDescrizione);
            stmt.setString(3, titoloCorrente);
            stmt.setString(4, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Errore aggiornamento bacheca: " + e.getMessage(), e);
        }



    }


    @Override
    public void eliminaBacheca(String titolo, String username) throws SQLException {
        if (isNullOrEmpty(titolo) || isNullOrEmpty(username)) {
            return;
        }

        String query = "DELETE FROM bacheca WHERE titolo = ? AND username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, titolo);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Bacheca> getListaBachecheDalDB(String username) throws SQLException {
        return getBachecheByUsername(username);
    }

    public void salvaBachechePredefinite(List<Bacheca> bacheche, String username) throws SQLException {
        String query = "INSERT INTO bacheca (titolo, descrizione, username) VALUES (?, ?, ?) " +
                "ON CONFLICT (titolo, username) DO NOTHING";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(3, username);
            for (Bacheca bacheca : bacheche) {
                stmt.setString(1, bacheca.getTitoloBacheca());
                stmt.setString(2, bacheca.getDescrizioneBacheca());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }


    @Override
    public boolean haBachechePredefinite(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM bacheca WHERE username = ? AND titolo IN ('Università', 'Lavoro', 'Tempo Libero')";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                // Considera che l'utente abbia le bacheche predefinite se ne ha almeno una
                return count > 0;
            }
            return false;
        }
    }

    @Override
    public void aggiornaOrdineToDo(int idToDo, int nuovaPosizione) throws SQLException {
        String query = "UPDATE todo SET ordine = ? WHERE id_todo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nuovaPosizione);
            stmt.setInt(2, idToDo);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Bacheca> getBachecheByUsername(String username) throws SQLException {
        List<Bacheca> bacheche = new ArrayList<>();
        String query = "SELECT titolo, descrizione FROM bacheca WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bacheche.add(new Bacheca(
                            rs.getString(COL_TITOLO),
                            rs.getString(COL_DESCRIZIONE)
                    ));
                }
            }
        }
        return bacheche;
    }

    @Override
    public List<String> getListaUtenti() throws SQLException {
        List<String> utenti = new ArrayList<>();
        String query = "SELECT username FROM utente";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                utenti.add(rs.getString(USERNAME));
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



    @Override
    public List<ToDo> getTuttiToDo(String titoloBacheca, String username) throws SQLException {
        String query = "SELECT t.id_todo, t.titolo, t.descrizione, t.data_scadenza, t.url, t.stato, t.username, t.colore_sfondo, t.percorso_immagine, t.titolo_bacheca, t.ordine " +
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
                ToDo todo = estraiToDoDaResultSet(rs);
                todo.setBacheca(titoloBacheca); // <-- qui lo imposti manualmente
                listaToDo.add(todo);
            }
        }
        return listaToDo;
    }

    private String colorToHex(Color color) {
        if (color == null) {
            return DEFAULT_COLOR_HEX;
        }
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
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
                "colore_sfondo = ?, percorso_immagine = ?, ordine = ? WHERE id_todo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, todo.getTitoloToDo());
            pstmt.setString(2, todo.getDescrizioneToDo());
            pstmt.setDate(3, todo.getDataScadenza() != null ?
                    java.sql.Date.valueOf(todo.getDataScadenza()) : null);
            pstmt.setString(4, colorToHex(todo.getColoreSfondo()));
            pstmt.setString(5, todo.getPercorsoImmagine());
            pstmt.setInt(6, todo.getOrdine());  // Aggiunto campo ordine
            pstmt.setInt(7, todo.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ToDo> getToDoByUsername(String username) throws SQLException {
        List<ToDo> listaToDo = new ArrayList<>();
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza, url, stato, titolo_bacheca, username, colore_sfondo, percorso_immagine, ordine " +  // Aggiunto spazio
                "FROM todo WHERE username = ? OR id_todo IN " +
                "(SELECT id_todo FROM condivisione WHERE username = ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listaToDo.add(estraiToDoDaResultSet(rs));
                }
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
                condivisioni.add(rs.getString(USERNAME));
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
            return rs.next() ? rs.getString(USERNAME) : null;
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
    public boolean utenteHaBacheca(String username, String titoloBacheca) throws SQLException {
        String query = "SELECT COUNT(*) FROM bacheca WHERE username = ? AND titolo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, titoloBacheca);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
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
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza, url, stato, colore_sfondo, percorso_immagine, ordine " +
                "FROM todo WHERE username = ? AND data_scadenza <= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(dataLimite));
            return executeToDoQuery(pstmt);
        }
    }



    @Override
    public List<ToDo> executeToDoQuery(PreparedStatement pstmt) throws SQLException {
        List<ToDo> result = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.add(estraiToDoDaResultSet(rs));
                }
        }
        return result;
    }

    private ToDo estraiToDoDaResultSet(ResultSet rs) throws SQLException {
        ToDo todo = new ToDo();
        todo.setId(rs.getInt(COL_ID_TODO));
        todo.setTitoloToDo(rs.getString(COL_TITOLO));
        todo.setDescrizioneToDo(rs.getString(COL_DESCRIZIONE));


        Date dataScadenza = rs.getDate(DATA_SCADENZA);
        if (dataScadenza != null) {
            todo.setDataScadenza(dataScadenza.toLocalDate());
        } else {
            todo.setDataScadenza(null);
        }

        todo.setUrl(rs.getString("url"));
        todo.setStatoToDo(StatoToDo.valueOf(rs.getString(STATO)));
        todo.setBacheca(rs.getString(COL_TITOLO_BACHECA ));
        todo.setOrdine(rs.getInt("ordine"));
        todo.setPercorsoImmagine(rs.getString(COL_PERCORSO_IMMAGINE));

        String coloreHex = rs.getString(COL_COLORE_SFONDO_NOME);
        todo.setColoreSfondo(coloreHex != null ? Color.decode(coloreHex) : Color.WHITE);

        String username = rs.getString(USERNAME);
        if (username != null) {
            Utente autore = getUtenteByUsername(username);
            todo.setAutore(autore);
        }

        return todo;
    }

}
