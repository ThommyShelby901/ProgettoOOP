package org.example.implementazionepostgresdao;

import org.example.dao.DatabaseDAO;
import org.example.database.ConnessioneDatabase;
import org.example.model.*;

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
     * Verifica se quell'utente esiste già nel database eseguendo una query COUNT sulla tabella utente
     * @param username lo username dell'utente da controllare.
     * @return true se l'utente esiste ovvero se il conteggio è maggiore di 0, false se non esiste
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
     * salva un nuovo utente nel database eseguendo una query insert sulla tabella utente
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
     * aggiunge una nuova bacheca al database. Dopo l'inserimento, restituisce un nuovo oggetto {@link Bacheca} creato
     * con il titolo e la descrizione specificati.
     * @param titolo della bacheca
     * @param descrizione della bacheca
     * @param username dell'utente proprietario della bacheca
     * @return oggetto {@link Bacheca} che rappresenta la bacheca appena aggiunta
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query INSERT
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
     * elimina una bacheca esistente dal database, esegue una query delete ulla tabella bacheca
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
     * Le bacheche verranno inserite solo se non esistono già combinazioni di titolo e username.
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
     * Il metodo restituisce true se il conteggio delle bacheche predefinite trovate è zero,
     * indicando che l'utente non ne possiede ancora nessuna.
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
     * aggiorna la posizione(ordine) di un to-do nel database. Esegue una query update sulla tabella to-do per modificare
     * il campo ordine del to-do.
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
     * recupera una lista di bacheche associate a un determinato username, esegue una select sulla tabella bacheca
     * per recuperare tutte le bacheche associate all'username fornito
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
     * recupera una lista di tutti gli username degli utenti presenti nel database.
     * Questa implementazione esegue una query SELECT su tutta la tabella utente
     * per ottenere tutti gli username.
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
            pstmt.setString(9,(todo.getColoreSfondo()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                todo.setId(rs.getInt(COL_ID_TODO));
            }
        }
    }

    /**
     * recupera una lista di tutti i to-do associati a una specifica bacheca per un dato utente.
     * Esegue una select che include anche i to-do che sono stati condivisi con l'utente
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
     * Recupera un oggetto to-do dal database in base al titolo del task e al titolo della bacheca.
     * <p>
     * Utilizza una query parametrizzata per prevenire SQL injection.
     * @param titolo         il titolo del task da cercare
     * @param titoloBacheca  il titolo della bacheca associata al task
     * @return un oggetto {@code ToDo} se è stato trovato un risultato, altrimenti {@code null}
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    @Override
    public ToDo getToDoPerTitoloEBacheca(String titolo, String titoloBacheca) throws SQLException {
        String query = "SELECT id_todo, titolo, descrizione, data_scadenza, url, stato, username, colore_sfondo, percorso_immagine, titolo_bacheca, ordine" +
                " FROM todo WHERE titolo = ? AND titolo_bacheca = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.setString(2, titoloBacheca);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return estraiToDoDaResultSet(rs); // usa il tuo metodo esistente per costruire il ToDo
            }
        }
        return null;
    }


    /**
     * Esegue una query update sulla tabella to-do, aggiornando
     * tutti i campi principali del to-do (titolo, descrizione, data di scadenza, URL, stato,
     * colore di sfondo, percorso immagine e ordine) basandosi sull'ID del to-do.
     * Il colore di sfondo dell'oggetto {@link ToDo} viene convertito in formato esadecimale
     * prima di essere salvato.
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
            pstmt.setString(6, (todo.getColoreSfondo()));
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
     * recupera una lista di tutti i to-do associati a un username tramite una select. Include sia i to-do di cui l'utente è l'autore
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
     * esegue una select sulla tabella condivisione, per recuperare tutti gli username associati all'id del to-do
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
     * Esegue una select sulla tabella todo per recuperare lo username associato all'id del to-do fornito
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
     * elimina un to-do dal database , con una delete sulla tabella todo
     * tramite il suo titolo e il titolo della bacheca a cui appartiene
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
     * verifica se un utente è proprietario di una bacheca specifica, esegue una query select count(*)
     * sulla tabella bacheca filtrando per username e titolo, restituisce true se esiste almeno una tupla
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
     * Recupera una lista di to-do in scadenza entro una data limite per un utente specifico,
     * tramite una select e una condizione dove datalimite sia minore o uguale alla data inserita
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
     * Recupera una lista di to-do in scadenza oggi per un utente specifico.
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
        todo.setColoreSfondo(coloreHex != null ? coloreHex : "#ffffff");


        String username = rs.getString(USERNAME);
        if (username != null) {
            Utente autore = getUtenteByUsername(username);
            todo.setAutore(autore);
        }
        return todo;
    }

    public List<CheckList> getChecklistByToDoId(int idToDo) throws SQLException {
        List<CheckList> lista = new ArrayList<>();
        String sql = "SELECT idChecklist, id_todo, descrizione, statocheck FROM CHECKLIST WHERE id_todo = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idToDo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CheckList item = new CheckList();
                    item.setIdCheckList(rs.getInt("idChecklist"));
                    item.setIdToDo(rs.getInt(COL_ID_TODO));
                    item.setDescrizione(rs.getString("descrizione"));

                    String statoStr = rs.getString("statocheck");
                    StatoCheck stato = StatoCheck.valueOf(statoStr);
                    item.setStato(stato);

                    lista.add(item);
                }
            }
        }
        return lista;
    }


    public void aggiungiVoceChecklist(int idToDo, String descrizione, StatoCheck stato) throws SQLException {
        String sql = "INSERT INTO CHECKLIST (id_todo, descrizione, statocheck) VALUES (?, ?, ?::stato_check)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idToDo);
            ps.setString(2, descrizione);
            ps.setString(3, stato.name()); // scrivi il valore enum come stringa
            ps.executeUpdate();
        }
    }


    public void modificaVoceChecklist(int idChecklist, String nuovaDescrizione, StatoCheck nuovoStato) throws SQLException {
        String sql = "UPDATE CHECKLIST SET descrizione = ?, statocheck = ?::stato_check WHERE idChecklist = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nuovaDescrizione);
            ps.setString(2, nuovoStato.name());
            ps.setInt(3, idChecklist);
            ps.executeUpdate();
        }
    }

    public void eliminaVoceChecklist(int idChecklist) throws SQLException {
        String sql = "DELETE FROM CHECKLIST WHERE idChecklist = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idChecklist);
            ps.executeUpdate();
        }
    }
}
