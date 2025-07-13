package org.example.dao;

import org.example.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * interfaccia DAO per l'accesso ai dati del database
 */
public interface DatabaseDAO {

    /**
     * Recupera un to-do tramite il suo ID.
     * @param idToDo ID del to-do
     * @return to-do trovato o null
     * @throws SQLException se si verifica un errore nel database
     */
    ToDo getToDoById(int idToDo) throws SQLException;

    /**
     * Ottiene tutte le voci della checklist di un to-do specificato.
     * @param idToDo ID del to-do
     * @return Lista delle voci della checklist
     * @throws SQLException se si verifica un errore nel database
     */
    List<CheckList> getChecklistByToDoId(int idToDo) throws SQLException;

    /**
     * Aggiunge una voce alla checklist di un to-do.
     *
     * @param idToDo ID del to-do
     * @param descrizione testo della voce
     * @param stato stato iniziale della voce
     * @throws SQLException se si verifica un errore nel database
     */
    void aggiungiVoceChecklist(int idToDo, String descrizione, StatoCheck stato) throws SQLException;

    /**
     * Modifica una voce della checklist.
     * @param idChecklist ID della voce
     * @param nuovaDescrizione nuovo testo della voce
     * @param nuovoStato nuovo stato della voce
     * @throws SQLException se si verifica un errore nel database
     */
    void modificaVoceChecklist(int idChecklist, String nuovaDescrizione, StatoCheck nuovoStato) throws SQLException;

    /**
     * Rimuove una voce dalla checklist.
     * @param idChecklist ID della voce da rimuovere
     * @throws SQLException se si verifica un errore nel database
     */
    void eliminaVoceChecklist(int idChecklist) throws SQLException;

    /**
     * Aggiorna lo stato di un to-do.
     * @param idToDo ID del to-do
     * @param nuovoStato nuovo stato da impostare
     * @throws SQLException se si verifica un errore nel database
     */
    void impostaStatoToDo(int idToDo, StatoToDo nuovoStato) throws SQLException;

    /**
     * Controlla se tutte le voci della checklist sono completate.
     * @param idToDo ID del to-do
     * @return true se tutte completate, false altrimenti
     * @throws SQLException se si verifica un errore nel database
     */
    boolean tutteChecklistCompletate(int idToDo) throws SQLException;

    /**
     * Verifica se un utente esiste nel database basandosi sul suo username.
     * @param username Lo username dell'utente da controllare.
     * @return true se l'utente esiste, false altrimenti.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    boolean utenteEsiste(String username) throws SQLException;

    /**
     * Salva un nuovo utente nel database.
     *
     * @param utente L'oggetto {@link Utente} da salvare.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void salvaUtente(Utente utente) throws SQLException;
    /**
     * Recupera un utente dal database basandosi sul suo username.
     *
     * @param username Lo username dell'utente da recuperare.
     * @return L'oggetto {@link Utente} corrispondente allo username, o null se non trovato.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    Utente getUtenteByUsername(String username) throws SQLException;

    /**
     * Recupera una lista di tutti gli username degli utenti presenti nel database.
     * @return Una listadi stringhe rappresentanti gli username.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<String> getListaUtenti() throws SQLException;

    /**
     * Restituisce un {@link ToDo} filtrato per titolo e bacheca.
     * @param titolo titolo del task
     * @param titoloBacheca titolo della bacheca
     * @return il {@code ToDo} corrispondente, oppure {@code null}
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    ToDo getToDoPerTitoloEBacheca(String titolo, String titoloBacheca) throws SQLException;

    /**
     * Salva una lista di bacheche predefinite associate a un utente.
     * @param bacheche La lista di oggetti {@link Bacheca} da salvare.
     * @param username Lo username dell'utente a cui associare le bacheche.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void salvaBachechePredefinite(List<Bacheca> bacheche, String username) throws SQLException;

    /**
     * Verifica se a un utente mancano le bacheche predefinite (cioè non sono ancora state create).
     * @param username Lo username dell'utente da controllare.
     * @return true se mancano le bacheche predefinite, false altrimenti.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    boolean mancaBachechePredefinite(String username) throws SQLException;

    /**
     * Verifica se un utente è il proprietario di una specifica bacheca.
     * @param username Lo username dell'utente.
     * @param titoloBacheca Il titolo della bacheca da controllare.
     * @return true se l'utente è proprietario della bacheca, false altrimenti.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    boolean utenteHaBacheca(String username, String titoloBacheca) throws SQLException;

    /**
     * Recupera una lista di to-do in scadenza entro una data limite per un dato utente.
     * @param username Lo username dell'utente.
     * @param dataLimite La data limite entro cui i to-do devono scadere.
     * @return Una lista di oggetti {@link ToDo} in scadenza.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<ToDo> getToDoInScadenzaEntro(String username, LocalDate dataLimite) throws SQLException;

    /**
     * Recupera una lista di to-do in scadenza oggi per un dato utente.
     * @param username Lo username dell'utente.
     * @return Una lista di oggetti {@link ToDo} in scadenza oggi.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<ToDo> getToDoInScadenzaOggi(String username) throws SQLException;

    /**
     * Esegue una query sui to-do utilizzando un PreparedStatement fornito.
     * Questo metodo è inteso per un uso interno o per query più complesse che richiedono un controllo diretto del PreparedStatement.
     * @param pstmt Il {@link PreparedStatement} pre-configurato da eseguire.
     * @return Una {@link List} di oggetti {@link ToDo} risultanti dalla query.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    List<ToDo> executeToDoQuery(PreparedStatement pstmt) throws SQLException;

    /**
     * Recupera lo username dell'autore di un to-do dato il suo ID
     * @param idToDo L'ID del to-do.
     * @return Lo username dell'autore del to-do.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    String getAutoreToDo(int idToDo) throws SQLException;

    /**
     * Recupera una lista degli username con cui un to-do è condiviso.
     * @param idToDo L'ID del to-do.
     * @return Una lista di stringhe rappresentanti gli username con cui il to-do è condiviso.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<String> getCondivisioniPerToDo(int idToDo) throws SQLException;

    /**
     * Aggiunge una nuova bacheca al database.
     * @param titolo Il titolo della nuova bacheca.
     * @param descrizione La descrizione della nuova bacheca.
     * @param username Lo username dell'utente che crea la bacheca.
     * @return L'oggetto {@link Bacheca} appena creato e aggiunto al database.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    Bacheca aggiungiBacheca(String titolo, String descrizione, String username)throws SQLException;

    /**
     * Modifica una bacheca esistente.
     * @param titoloCorrente Il titolo attuale della bacheca da modificare.
     * @param nuovoTitolo Il nuovo titolo per la bacheca.
     * @param nuovaDescrizione La nuova descrizione per la bacheca.
     * @param username Lo username dell'utente proprietario della bacheca.
     */
    void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione, String username);

    /**
     * Elimina una bacheca dal database.
     * @param titolo Il titolo della bacheca da eliminare.
     * @param username Lo username dell'utente proprietario della bacheca.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void eliminaBacheca(String titolo, String username) throws SQLException;

    /**
     * Recupera una lista di tutte le bacheche di proprietà di un dato utente
     * @param username Lo username dell'utente.
     * @return Una lista di oggetti {@link Bacheca} di proprietà dell'utente.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<Bacheca> getBachecheByUsername(String username) throws SQLException;

    /**
     * Recupera una lista di tutti i to-do associati a un utente, includendo quelli creati dall'utente e quelli a lui condivisi.
     * @param username Lo username dell'utente.
     * @return Una lista di oggetti {@link ToDo} associati all'utente.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<ToDo> getToDoByUsername(String username) throws SQLException;

    /**
     * Aggiunge una condivisione per un to-do, rendendolo visibile a un altro utente.
     * @param todo L'oggetto {@link ToDo} da condividere.
     * @param username Lo username dell'utente con cui condividere il to-do.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void aggiungiCondivisione(ToDo todo, String username) throws SQLException;

    /**
     * Rimuove una condivisione per un to-do.
     * @param todo L'oggetto {@link ToDo} da cui rimuovere la condivisione.
     * @param username Lo username dell'utente da cui rimuovere la condivisione.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void rimuoviCondivisione(ToDo todo, String username) throws SQLException;

    /**
     * Crea un nuovo to-do e lo associa a una bacheca specifica.
     * @param todo L'oggetto {@link ToDo} da creare.
     * @param titoloBacheca Il titolo della bacheca a cui associare il to-do.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void creaToDo(ToDo todo, String titoloBacheca) throws SQLException;

    /**
     * Recupera tutti i to-do di una specifica bacheca, considerando sia i to-do di proprietà dell'utente che quelli condivisi con lui per quella bacheca.
     * @param titoloBacheca Il titolo della bacheca.
     * @param username Lo username dell'utente che visualizza i to-do.
     * @return Una lista di oggetti {@link ToDo} presenti nella bacheca.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<ToDo> getTuttiToDo(String titoloBacheca, String username) throws SQLException;

    /**
     * Aggiorna le informazioni di un to-do esistente nel database.
     * @param todo L'oggetto {@link ToDo} con le informazioni aggiornate.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void aggiornaToDo(ToDo todo) throws SQLException;

    /**
     * Elimina un to-do dal database.
     * @param titolo Il titolo del to-do da eliminare.
     * @param titoloBacheca Il titolo della bacheca a cui appartiene il to-do.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void eliminaToDo(String titolo, String titoloBacheca) throws SQLException;

    /**
     * Trasferisce un to-do da una bacheca all'altra.
     * @param todo L'oggetto {@link ToDo} da trasferire.
     * @param titoloBachecaDestinazione Il titolo della bacheca di destinazione.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void trasferisciToDo(ToDo todo, String titoloBachecaDestinazione) throws SQLException;

    /**
     * Recupera una lista di tutte le bacheche presenti nel database per un dato utente.
     * @param username Lo username dell'utente.
     * @return Una {@link List} di oggetti {@link Bacheca}.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    List<Bacheca> getListaBachecheDalDB(String username) throws SQLException;

    /**
     * Aggiorna la posizione (ordine) di un to-do all'interno della sua bacheca.
     * @param idToDo L'ID del to-do da riordinare.
     * @param nuovaPosizione La nuova posizione desiderata per il to-do.
     * @throws SQLException Se si verifica un errore durante l'accesso al database.
     */
    void aggiornaOrdineToDo(int idToDo, int nuovaPosizione) throws SQLException;
}



