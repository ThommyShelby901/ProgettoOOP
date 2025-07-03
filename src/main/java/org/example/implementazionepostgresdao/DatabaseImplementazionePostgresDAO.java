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

/**
 * Questa classe implementa l'interfaccia {@link DatabaseDAO} per l'interazione con un database Postgre.
 * Gestisce tutte le operazioni relative a utenti, bacheche e to-do.
 */
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

    /**
     * Costruttore della classe che inizializza la connessione al database utilizzando il singleton {@link ConnessioneDatabase}
     * @throws SQLException se si verifica un errore quando si cerca di instaurare la connessione al database.
     */
    public DatabaseImplementazionePostgresDAO() throws SQLException {
        this.connection = ConnessioneDatabase.getInstance().connection;
    }

    /**
     * Verifica se quell'utente esiste già nel database.
     * @param username lo username dell'utente da controllare.
     * @return true se l'utente esiste, false se non esiste
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public boolean utenteEsiste(String username) throws SQLException {
        String queryCheck = "SELECT COUNT(*) FROM utente WHERE username = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(queryCheck)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0; //  Restituisce `true` se l'utente esiste già
        }
    }

    /**
     * salva un nuovo utente nel database
     * @param utente oggetto {@link Utente} da salvare
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public void salvaUtente(Utente utente) throws SQLException {
        String queryInsert = "INSERT INTO utente (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(queryInsert)) {
            pstmt.setString(1, utente.getUsername());
            pstmt.setString(2, utente.getPassword());
            pstmt.executeUpdate();
        }
    }

    /**
     * recupera un utente dal database tramite il suo username
     * @param username dell'utente da recuperare
     * @return oggetto {@link Utente} corrispondente, o null se non viene trovato
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public Utente getUtenteByUsername(String username) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT username, password FROM utente WHERE username = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? new Utente(rs.getString(USERNAME), rs.getString("password")) : null;
        }
    }

    /**
     * aggiunge una nuova bacheca al database
     * @param titolo della bacheca
     * @param descrizione della bacheca
     * @param username dell'utente proprietario della bacheca
     * @return oggetto {@link Bacheca} che rappresenta la bacheca appena aggiunta
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * metodo di utilità interno per controllare se una stringa è null o vuota dopo il trim
     * @param value stringa da controllare
     * @return true se la stringa è vuota, false altrimenti
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * modifica una bacheca esistente nel database, i campi del titolo e della descrizione verranno aggiornati
     * per la bacheca identificata da titoloCorrente e username
     * @param titoloCorrente titolo attuale della bacheca da modificare
     * @param nuovoTitolo nuovo titolo da assegnare alla bacheca
     * @param nuovaDescrizione nuova descrizione da assegnare alla bacheca
     * @param username username dell'utente proprietario della bacheca
     */
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

    /**
     * elimina una bacheca esistente dal database
     * @param titolo della bacheca da eliminare
     * @param username dell'utente proprietario della bacheca
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * recupera la lista di tutte le bacheche associate a un determinato utente dal database
     * @param username dell'utente di cui si vogliono recuperare le bacheche
     * @return lista di oggetti {@link Bacheca}
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public List<Bacheca> getListaBachecheDalDB(String username) throws SQLException {
        return getBachecheByUsername(username);
    }

    /**
     * salva una lista di bacheche predefinite nel database per un determinato utente.
     * Le bacheche verranno inserite solo se non esistono già
     * @param bacheche lista di oggetti {@link Bacheca} da salvare
     * @param username dell'utente a cui associare le bacheche
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * Verifica se all'utente mancano le bacheche predefinite ('Università', 'Lavoro', 'Tempo Libero').
     * @param username dell'utente da controllare
     * @return true se almeno una bacheca manca per l'utente, false altrimenti
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public boolean mancaBachechePredefinite(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM bacheca WHERE username = ? AND titolo IN ('Università', 'Lavoro', 'Tempo Libero')";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                // Ritorna true se l'utente NON ha bacheche predefinite (count == 0)
                return count == 0;
            }
            return true; // Se non trova niente, considera mancanti le bacheche
        }
    }

    /**
     * aggiorna la posizione(ordine) di un to-do nel database.
     * @param idToDo da aggiornare
     * @param nuovaPosizione da assegnare al to-do
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public void aggiornaOrdineToDo(int idToDo, int nuovaPosizione) throws SQLException {
        String query = "UPDATE todo SET ordine = ? WHERE id_todo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nuovaPosizione);
            stmt.setInt(2, idToDo);
            stmt.executeUpdate();
        }
    }

    /**
     * recupera una lista di bacheche associate a un determinato username
     * @param username dell'utente per cui recuperare le bacheche
     * @return lista di oggetti {@link Bacheca} associati all'utente
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * recupera una lista di tutti gli username degli utenti presenti nel database
     * @return lista di stringhe contenenti gli username degli utenti
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * aggiunge una condivisione per un to-do specifico con un determinato utente. Il metodo verifica prima
     * se la condivisione esista per evitare duplicati.
     * @param todo oggetto {@link ToDo} da condividere
     * @param nomeUtente username dell'utente con cui condividere il to-do
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * rimuove una condivisione per un to-do specifico o per un determinato utente
     * @param todo oggetto {@link ToDo} di cui rimuovere la condivisione
     * @param nomeUtente username dell'utente da cui rimuovere la condivisione
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * crea un nuovo to-do nel database e lo associa a una bacheca e un utente specifico.
     * L'ordine del to-do viene determinato calcolando il massimo ordine esistente per la bacheca e incrementandolo di uno
     * @param todo oggetto {@link ToDo} da creare
     * @param titoloBacheca a cui associare il to-do
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * recupera una lista di tutti i to-do associati a una specifica bacheca per un dato utente.
     * Include anche i to-do che sono stati condivisi con l'utente
     * @param titoloBacheca titolo della bacheca da cui recuperare i to-do
     * @param username dell'utente per cui recuperare i to-do
     * @return lista di oggetti {@link ToDo}
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
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

    /**
     * converte un oggetto color in una stringa esadecimale, se il colore è nullo restituisce un colore esadecimale predefinito
     * @param color oggetto color da convertire
     * @return stringa che rappresenta il colore in formato esadecimale
     */
    private String colorToHex(Color color) {
        if (color == null) {
            return DEFAULT_COLOR_HEX;
        }
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Aggiorna i dati di un to-do esistente nel database
     * @param todo oggetto {@link ToDo} con i dati aggiornati
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public void aggiornaToDo(ToDo todo) throws SQLException {
        String query = "UPDATE todo SET titolo = ?, descrizione = ?, data_scadenza = ?, " +
                "url = ?, stato = ?, colore_sfondo = ?, percorso_immagine = ?, ordine = ? " +
                "WHERE id_todo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, todo.getTitoloToDo());
            pstmt.setString(2, todo.getDescrizioneToDo());
            pstmt.setDate(3, todo.getDataScadenza() != null ?
                    java.sql.Date.valueOf(todo.getDataScadenza()) : null);
            pstmt.setString(4, todo.getUrl());
            pstmt.setString(5, todo.getStatoToDo().name());
            pstmt.setString(6, colorToHex(todo.getColoreSfondo()));
            pstmt.setString(7, todo.getPercorsoImmagine());
            pstmt.setInt(8, todo.getOrdine());
            pstmt.setInt(9, todo.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nessun ToDo trovato con ID: " + todo.getId());
            }
        }
    }

    /**
     * recupera una lista di tutti i to-do associati a un username. Include sia i to-do di cui l'utente è l'autore
     * sia quelli che gli sono stati condivisi
     * @param username username dell'utente per cui recuperare i to-do
     * @return una lista di oggetti {@link ToDo} associati all'utente
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
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

    /**
     * recupera una lista di username con cui un to-do specifico è stato condiviso
     * @param idToDo id del to-do da cui recuperare le condivisione
     * @return lista di stringhe contenente gli username degli utenti con cui il to-do è condiviso
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
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

    /**
     * Recupera lo username dell'autore di un ToDo specifico.
     * @param idToDo id del to-do di cui recuperare l'autore
     * @return lo username dell'autore del to-do
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
    @Override
    public String getAutoreToDo(int idToDo) throws SQLException {
        String query = "SELECT username FROM todo WHERE id_todo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idToDo);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString(USERNAME) : null;
        }
    }

    /**
     * elimina un to-do dal database tramite il suo titolo e il titolo della bacheca a cui appartiene
     * @param titolo del to-do da eliminaere
     * @param titoloBacheca in cui il to-do è contenuto
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
    @Override
    public void eliminaToDo(String titolo, String titoloBacheca) throws SQLException {
        String query = "DELETE FROM todo WHERE titolo = ? AND titolo_bacheca = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, titoloBacheca);
            pstmt.executeUpdate();
        }
    }

    /**
     * verifica se un utente è proprietario di una bacheca specifica
     * @param username lo username dell'utente da controllare
     * @param titoloBacheca titolo della bacheca da verificare
     * @return true se l'utente è proprietario della bacheca, false altrimenti
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
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

    /**
     * trasferisce un to-do da una bacheca all'altra, aggiornando il suo riferimento alla bacheca di destinazione
     * @param todo oggetto {@link ToDo} da trasferire
     * @param titoloBachecaDestinazione titolo della bacheca di destinazione
     */
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
            logger.log(Level.WARNING, "Errore trasferimento todo " + e.getMessage(), e);
        }
    }

    /**
     * Recupera una lista di ToDo in scadenza entro una data limite per un utente specifico.
     * @param username dell'utente per cui recuperare i to-do
     * @param dataLimite data limite entro cui i to-do devono scadere
     * @return lista di oggetti {@link ToDo} in scadenza
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
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

    /**
     * Recupera una lista di ToDo in scadenza oggi per un utente specifico.
     * @param username dell'utente per cui recuperare i to-do
     * @return lista di oggetti {@link ToDo} in scadenza oggi
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
    public List<ToDo> getToDoInScadenzaOggi(String username) throws SQLException {
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza, url, stato, colore_sfondo, percorso_immagine, ordine " +
                "FROM todo WHERE username = ? AND data_scadenza = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            return executeToDoQuery(pstmt);
        }
    }

    /**
     * Esegue una query PreparedStatement e restituisce una lista di oggetti ToDo.
     * Questo metodo è un helper per evitare la duplicazione del codice di estrazione dei risultati.
     * @param pstmt il PreparedStatement da eseguire
     * @return una lista di oggetti {@link ToDo} risultanti dalla query
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
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

    /**
     * estrae i dati di un to-do da un resultSet e li mappa a un oggetto {@link ToDo}.
     * Questo metodo gestisce la conversione dei tipi di dati e l'impostazione degli attributi to-do
     * @param rs il ResultSet da cui estrarre i dati del to-do
     * @return un oggetto {@link ToDo} popolato con i dati del ResultSet
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
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
