package org.example.controller;

import org.example.dao.DatabaseDAO;
import org.example.gui.GuiHome;
import org.example.gui.GuiLogin;
import org.example.model.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * questa classe è il cervello del nostro programma, fa da intermediario tra la GUI e il model, e collega il DAO.
 * <p>
 * coordina le operazione di login, gestione delle bacheche e dei to-do.
 */

public class Controller {
    private final DatabaseDAO dao;
    private GuiLogin guiLogin;
    private GuiHome guiHome;
    private Utente utenteCorrente;

    /**
     * costruttore per creare un'istanza della classe.
     * collega il controller al dao e al login(finestra iniziale).
     * @param dao implementazione DatabaseDAO per le operazioni sul database.
     */
    public Controller(DatabaseDAO dao) {
        this.dao = dao;
        this.guiLogin =new GuiLogin(this);
        guiLogin.mostra();

    }

    /**
     * se le credenziali sono valide, inizializza le {@link Bacheca bachece} dell'{@link Utente utente} e apre la {@link GuiHome homeframe}.
     * @param username inserito dall'utente.
     * @param password inserita dall'utente.
     */
    public void login(String username, String password) {
        try {
            validaCredenziali(username, password);
            autenticaUtente(username, password);
            caricaDatiUtente();
            inizializzaBachechePredefinite();
            guiLogin.chiudiFinestra();
            guiHome = new GuiHome(this, guiLogin.getFrame());
        } catch (IllegalArgumentException | SQLException e) {
            guiLogin.showMessage(e.getMessage());
        } catch (Exception _) {
            guiLogin.showMessage("Credenziali non valide");
        }
    }

    /**
     * gestisce la registrazione, verifica che lo username non esista già, e poi salva nel database
     * @param username nuovo scelto
     * @param password nuova dell'utente.
     */
    public void registrazione(String username, String password) {
        try {
            validaCredenziali(username, password);
            if (dao.utenteEsiste(username)) {
                throw new IllegalArgumentException("Username già esistente");
            }
            dao.salvaUtente(new Utente(username, password));
            guiLogin.showMessage("Registrazione completata!");

            // Solo se tutto è andato bene:
            guiLogin.mostra();
        } catch (IllegalArgumentException e) {
            guiLogin.showMessage("Errore: " + e.getMessage());
        } catch (SQLException e) {
            guiLogin.showMessage("Errore database: " + e.getMessage());
        }
    }

    /**
     * gestisce il logout dell'utente attualmente loggato, resetta l'utente chiude la finestra principale
     * e apre una nuova finestra di login.
     */
    public void handleLogout() {
        utenteCorrente = null;
        if (guiHome != null) guiHome.getFrame().dispose();
        if (guiLogin != null) guiLogin.chiudiFinestra();
        guiLogin = new GuiLogin(this);
        guiLogin.mostra();
    }

    /**
     * restituisce la lista di {@link Bacheca bacheche} assoiate all'{@link #utenteCorrente utente attualmente loggato}.
     * @return lista di bacheche dell'utente corrente.
     * @throws SQLException se si verifica un errore durante il recupero delle bacheche.
     */
    public List<Bacheca> getListaBacheche() throws SQLException {
        return dao.getListaBachecheDalDB(utenteCorrente.getUsername());
    }

    /**
     * crea una nuova {@link Bacheca bacheca} la aggiunge al model e al DAO
     * @param titolo titolo della bacheca
     * @param descrizione della bacheca
     * @throws SQLException nel caso i campi siano vuoti
     */
    public void creaBacheca(String titolo, String descrizione) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty() || descrizione == null || descrizione.trim().isEmpty()) {
            throw new IllegalArgumentException("Titolo e descrizione obbligatori");
        }
        Bacheca bacheca = dao.aggiungiBacheca(titolo, descrizione, utenteCorrente.getUsername());
        utenteCorrente.aggiungiBacheca(bacheca);
        guiHome.aggiornaListaBacheche();
    }

    /**
     * modifica una {@link Bacheca bacheca} esistente per l'{@link #utenteCorrente utente corrente}. Valida l'input fornito, trova l'oggetto
     * Bacheca corrispondente, aggiorna i dettagli nel model, e salva le modifiche anche nel DAO.
     * @param titoloCorrente della bacheca che si vuole modificare.
     * @param nuovoTitolo da assegnare alla bacheca.
     * @param nuovaDescrizione da assegnare alla bacheca.
     */
    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        for (Bacheca b : utenteCorrente.getListaBacheche()) {
            if (b.getTitoloBacheca().equalsIgnoreCase(titoloCorrente)) {
                // Se i campi sono vuoti o nulli, mantieni i valori attuali
                if (nuovoTitolo == null || nuovoTitolo.trim().isEmpty()) {
                    nuovoTitolo = b.getTitoloBacheca();
                }
                if (nuovaDescrizione == null || nuovaDescrizione.trim().isEmpty()) {
                    nuovaDescrizione = b.getDescrizioneBacheca();
                }

                b.setTitoloBacheca(nuovoTitolo);
                b.setDescrizioneBacheca(nuovaDescrizione);

                dao.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione, utenteCorrente.getUsername());
                break;
            }
        }
    }

    /**
     * elimina una {@link Bacheca bacheca} dall'{@link #utenteCorrente utente corrente}, rimuove la bacheca dal model e dal DAO.
     * @param titolo titolo bacheca da eliminare
     * @throws SQLException titolo obbligatorio
     */
    public void eliminaBacheca(String titolo) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Titolo obbligatorio");
        }
        utenteCorrente.eliminaBacheca(titolo);
        dao.eliminaBacheca(titolo, utenteCorrente.getUsername());
    }

    /**
     * restituisce tutti i to-do associati a una {@link Bacheca bacheca} specifica dell'utente corrente
     * @param titoloBacheca dalla quale recuperare i to-do
     * @return una lista di {@link ToDo to-do} presenti nella bacheca indicata
     * @throws SQLException se si verifica un errore durante il recupero dei to-do nel database.
     */
    public List<ToDo> getToDoPerBacheca(String titoloBacheca) throws SQLException {
        if (titoloBacheca == null || titoloBacheca.isEmpty()) {
            return Collections.emptyList();
        }
        return dao.getTuttiToDo(titoloBacheca, utenteCorrente.getUsername());
    }

    /**
     * aggiunge un nuovo to-do, valida l'input lo aggiunge al model e lo salva nel database.
     * <p>
     * questo metodo si occupa di creare un nuovo oggetto To-Do {@link ToDo}.
     * @param titolo to-do
     * @param descrizione to-do
     * @param dataScadenza formato(anno-mese-gg) opzionale
     * @param url to-do opzionale
     * @param stato completato o non completato
     * @param titoloBacheca a cui associare il to-do
     * @param colore di sfondo. opzionale
     * @throws SQLException nel caso titolo o descrizione siano null.
     */
    public void creaToDo(String titolo, String descrizione, LocalDate dataScadenza,
                         String url, StatoToDo stato, String titoloBacheca, Color colore) throws SQLException {
        validaInputToDo(titolo, titoloBacheca);

        ToDo nuovoToDo = new ToDo();
        nuovoToDo.setTitoloToDo(titolo);
        nuovoToDo.setDescrizioneToDo(descrizione);
        nuovoToDo.setDataScadenza(dataScadenza);
        nuovoToDo.setUrl(url);
        nuovoToDo.setStatoToDo(stato);
        String coloreHex = colorToHex(colore);
        nuovoToDo.setColoreSfondo(coloreHex);

        nuovoToDo.setAutore(utenteCorrente);

        utenteCorrente.aggiungiToDo(nuovoToDo, titoloBacheca);
        dao.creaToDo(nuovoToDo, titoloBacheca); // salva nel DB
        caricaDatiUtente();
    }

    /**
     * modifica un oggetto {@link ToDo to-do}, aggiornando i suoi campi se i valori sono forniti. Tutti i parametri sono opzionali tranne il to-do stesso.
     * <p>
     * se un campo opzionale è null il parametro non verrà modificato.
     * questo metodo si avvale del {@link DatabaseDAO#aggiornaToDo(ToDo)} per persistere le modifiche
     * @param todo da modificare, obbligatorio non può essere null.
     * @param nuovoTitolo nuovo titolo opzionale
     * @param nuovaDescrizione nuova descrizione opzionale
     * @param nuovaDataScadenza nuova scadenza opzionale
     * @param nuovoUrl nuovo url opzionale
     * @param nuovoStato nuovo stato opzionale
     * @param nuovoColore nuovo colore opzionale
     * @throws SQLException se si verifica un errore nel salvataggio nel database.
     */
    public void modificaToDo(ToDo todo, String nuovoTitolo, String nuovaDescrizione,
                             LocalDate nuovaDataScadenza, String nuovoUrl,
                             StatoToDo nuovoStato, Color nuovoColore) throws SQLException {

        if (todo == null) throw new IllegalArgumentException("ToDo non può essere null");

        if (nuovoTitolo != null) todo.setTitoloToDo(nuovoTitolo);
        if (nuovaDescrizione != null) todo.setDescrizioneToDo(nuovaDescrizione);
        if (nuovaDataScadenza != null) todo.setDataScadenza(nuovaDataScadenza);
        if (nuovoUrl != null) todo.setUrl(nuovoUrl);
        if (nuovoStato != null) todo.setStatoToDo(nuovoStato);
        if (nuovoColore != null) {
            String coloreHex = colorToHex(nuovoColore);
            todo.setColoreSfondo(coloreHex);
        }


        // Mantieni ordine o altre proprietà se necessario
        todo.setOrdine(todo.getOrdine());
        dao.aggiornaToDo(todo);
    }

    /**
     * elimina un {@link ToDo to-do} specifico. Rimuove il to-do dal model e dal database.
     * @param titoloToDo da eliminare
     * @param titoloBacheca da eliminare
     * @throws SQLException oggetto TO-DO da eliminare.
     */
    public void eliminaToDo(String titoloToDo, String titoloBacheca) throws SQLException {
        if (titoloToDo == null || titoloBacheca == null) {
            throw new IllegalArgumentException("Titolo e bacheca obbligatori");
        }

        ToDo todo = utenteCorrente.cercaToDoPerTitoloEBoard(titoloToDo, titoloBacheca);
        if (todo != null) {
            utenteCorrente.eliminaToDo(todo);
            dao.eliminaToDo(titoloToDo, titoloBacheca);
        }
    }

    /**
     * Sposta un {@link ToDo to-do} all'interno di una {@link Bacheca bacheca}, cambiando l'ordine, si occupa di aggionare l'ordine in memoria e nel database.
     * @param titoloBacheca corrispondente
     * @param titoloToDo da spostare
     * @param nuovaPosizione all'interno della bacheca
     * @throws SQLException nel caso il numero inserito non fosse coerente.
     */
    public void spostaToDo(String titoloBacheca, String titoloToDo, int nuovaPosizione) throws SQLException {
        try {
            // Chiama il metodo del model
            utenteCorrente.spostaToDo(titoloBacheca, titoloToDo, nuovaPosizione);

            // Aggiorna l'ordine nel database per tutti i To-Do nella bacheca
            List<ToDo> todos = utenteCorrente.getToDoPerBacheca(titoloBacheca);
            for (ToDo todo : todos) {
                if (todo.getId() == 0) {
                    // Se l'ID non è stato settato, cerca il To-Do nel database
                    ToDo dbTodo = getToDoPerTitoloEBoard(todo.getTitoloToDo(), titoloBacheca);
                    if (dbTodo != null) {
                        todo.setId(dbTodo.getId());
                    }
                }
                dao.aggiornaOrdineToDo(todo.getId(), todo.getOrdine());
            }
        } catch (Exception e) {
            throw new SQLException("Errore durante lo spostamento: " + e.getMessage());
        }
    }

    /**
     * trasferisce un {@link ToDo to-do} da una {@link Bacheca bacheca} all'altra
     * @param todo da trasferire
     * @param nomeBachecaDestinazione titolo bacheca di destinazione.
     * @throws SQLException nel caso non venisse trovata la bacheca di destinazione.
     */
    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) throws SQLException {
        if (todo == null || nomeBachecaDestinazione == null || utenteCorrente == null) {
            throw new IllegalArgumentException("Parametri non validi per il trasferimento");
        }

        // Verifica che la bacheca destinazione esista
        Bacheca bachecaDest = utenteCorrente.getBachecaByTitolo(nomeBachecaDestinazione);
        if (bachecaDest == null) {
            throw new IllegalArgumentException("Bacheca destinazione non trovata");
        }
        utenteCorrente.trasferisciToDo(todo, nomeBachecaDestinazione);

        // Aggiorna il database
        dao.trasferisciToDo(todo, nomeBachecaDestinazione);
        caricaDatiUtente();
    }

    /**
     * aggiungi un immagine a un {@link ToDo to-do} impostando un percorso specifico.
     * @param todo a cui aggiungere l'immagine
     * @param percorsoImmagine da associare
     * @throws SQLException se si verifica un errore durante l'aggiornamento del database.
     */
    public void aggiungiImmagineAToDo(ToDo todo, String percorsoImmagine) throws SQLException {
        if (todo == null) {
            throw new IllegalArgumentException("ToDo non può essere null");
        }

        if (!todo.getAutore().getUsername().equals(utenteCorrente.getUsername()) &&
                !todo.getCondivisoCon().contains(utenteCorrente)) {
            throw new IllegalStateException("Non hai i permessi per modificare questo ToDo");
        }

        todo.setPercorsoImmagine(percorsoImmagine);
        dao.aggiornaToDo(todo);
    }

    /**
     * rimuove l'immagine associata a un {@link ToDo to-do}, impostando il percorso a null.
     * @param titoloToDo da cui rimuovere l'immagine
     * @param titoloBacheca a cui appartiene il to-do
     * @throws SQLException Se si verifica un errore durante l'accesso o l'aggiornamento del database
     */
    public void rimuoviImmagineDaToDo(String titoloToDo, String titoloBacheca) throws SQLException {
        ToDo todo = getToDoPerTitoloEBoard(titoloToDo, titoloBacheca);
        if (todo == null) {
            throw new IllegalArgumentException("ToDo non può essere null");
        }

        if (!todo.getAutore().getUsername().equals(utenteCorrente.getUsername()) &&
                !todo.getCondivisoCon().contains(utenteCorrente)) {
            throw new IllegalStateException("Non hai i permessi per modificare questo ToDo");
        }

        todo.setPercorsoImmagine(null);
        dao.aggiornaToDo(todo);
    }

    /**
     * cerca i {@link ToDo to-do} dell'utente corrente in base a una parte del titolo
     * @param titolo frammento di titolo da cercare
     * @return una lista di oggetti to-do i cui titoli contengono il frammento inserito
     */
    public List<ToDo> cercaToDoPerTitolo(String titolo) {
        return new ArrayList<>(
                utenteCorrente.getListaToDo().stream()
                        .filter(todo -> todo.getTitoloToDo().contains(titolo))
                        .toList()
        );
    }

    /**
     * recupera i {@link ToDo to-do} dell'utente che scadono entro una data specifica.
     * @param dataLimite la LocalDate massima per la scadenza
     * @return una lista di oggetti TO-DO in scadenza entro quella data.
     * @throws SQLException se c'è un problema nel recuperare i dati dal database.
     */
    public List<ToDo> getToDoInScadenzaEntro(LocalDate dataLimite) throws SQLException {
        return recuperaToDoInScadenzaEntro(dataLimite);
    }

    /**
     * recupera i {@link ToDo to-do} dell'utente corrente in scadenza oggi.
     * @return una lista di oggetti TO-DO in scadenza oggi
     * @throws SQLException se c'è un problema nel recuperare i dati dal database.
     */
    public List<ToDo> getToDoInScadenzaOggi() throws SQLException {
        List<ToDo> result = utenteCorrente.getToDoInScadenzaOggi();

        if (result.isEmpty()) {
            result = dao.getToDoInScadenzaOggi(utenteCorrente.getUsername());
            for (ToDo t : result) {
                utenteCorrente.aggiungiToDo(t, t.getBacheca());
            }
        }
        return result;
    }

     /**
     * recupera i {@link ToDo to-do} dell'utente in scadenza entro una certa data, il metodo verifica che i to-do in scadenza sono
     * già presenti nella lista dell'utente.
     * @param dataLimite entro cui devono scadere i to-do
     * @return una lista di to-do in scadenza entro quella data.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    private List<ToDo> recuperaToDoInScadenzaEntro(LocalDate dataLimite) throws SQLException {
        List<ToDo> result = utenteCorrente.getToDoInScadenzaEntro(dataLimite);

        if (result.isEmpty()) {
            result = dao.getToDoInScadenzaEntro(utenteCorrente.getUsername(), dataLimite);
            for (ToDo t : result) {
                utenteCorrente.aggiungiToDo(t, t.getBacheca());
            }
        }
        return result;
    }

    /**
     * gestisce le condivisioni di to-do tra utenti, verifica che il destinatario esista, che abbia le
     * bacheche predefinite e che possiede la bacheca specifica del to-do condiviso
     * @param todo oggetto che si desidera condividere
     * @param username dell'utente con la quale vogliamo condividere
     * @throws SQLException se si verifica un problema durante l'accesso o la modifica al database.
     */
    public void aggiungiCondivisione(ToDo todo, String username) throws SQLException {
        validaCondivisione(todo, username);

        // Verifica che l'utente esista
        Utente destinatario = dao.getUtenteByUsername(username);
        if (destinatario == null) {
            throw new IllegalArgumentException("Utente destinatario non trovato");
        }
        // Assicurati che l'utente abbia le bacheche predefinite
        if (dao.mancaBachechePredefinite(username)) {
            List<Bacheca> bacheche = Utente.inizializzaBacheche();
            dao.salvaBachechePredefinite(bacheche, username);
        }
        // Controlla se il destinatario ha la bacheca specifica
        boolean haBacheca = dao.utenteHaBacheca(username, todo.getBacheca());
        if (!haBacheca) {
            throw new IllegalStateException("L'utente destinatario non ha la bacheca '" + todo.getBacheca() + "'");
        }
        dao.aggiungiCondivisione(todo, username);
        todo.aggiungiUtenteCondiviso(destinatario);
        destinatario.aggiungiToDoCondiviso(todo);
    }

    /**
     * rimuove una condivisione esistente per un to-do specifico, valida l'input, rimuove la condivisione dal database
     * e aggiorna lo stato di condivisione nel model.
     * @param todo oggetto per la quale vogliamo rimuovere la condivisione.
     * @param username dell'utente da cui rimuovere la condivisione.
     * @throws SQLException se si verifica un problema durante l'accesso o la modifica nel database.
     */
    public void rimuoviCondivisione(ToDo todo, String username) throws SQLException {
        validaCondivisione(todo, username);
        Utente destinatario = dao.getUtenteByUsername(username);

        dao.rimuoviCondivisione(todo, username);
        todo.rimuoviUtenteCondiviso(destinatario);
        destinatario.rimuoviToDoCondiviso(todo);
    }

    /**
     * aggiorna i dettagli di un to-do esistente nel database, è un metodo ponte per salvare le modifiche del model nel database.
     * @param todo oggetto con i dati aggiornati da salvare.
     * @throws SQLException se si verifica un problema durante l'aggiornamento del database.
     */
    public void aggiornaToDo(ToDo todo) throws SQLException {
        if (todo == null) throw new IllegalArgumentException("ToDo non può essere null");
        dao.aggiornaToDo(todo);
    }

    /**
     * assicura che le credenziali non siano vuote, controllo base prima di autenticazione o registrazione
     * @param username da validare
     * @param password da validare
     */
    private void validaCredenziali(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username e password obbligatori");
        }
    }

    /**
     * autentica l'utente chiamando il DAO verificando che le credenziali corrispondono nel database
     * @param username da autenticare
     * @param password da autenticare
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    private void autenticaUtente(String username, String password) throws SQLException{
        utenteCorrente = dao.getUtenteByUsername(username);
        if (utenteCorrente == null || !utenteCorrente.getPassword().equals(password)) {
            throw new IllegalArgumentException("Credenziali non valide");
        }
    }

    /**
     * carica tutti i dati per l'utente attualmente loggato, to-do e bacheche, subito dopo il login per popolare il model.
     * <p>
     * recupera le {@link Bacheca bacheche} e i {@link ToDo to-do} dal database tramite il {@link DatabaseDAO},
     * e li assegna all'{@link #utenteCorrente utenteCorrente}, assicurando che il model sia sincronizzato con lo stato del database.
     * @throws SQLException se c'è un problema nel recuperare i dati dal database.
     */
    public void caricaDatiUtente() throws SQLException {
        if (utenteCorrente == null) {
            throw new IllegalStateException("Nessun utente autenticato");
        }

        List<Bacheca> bacheche = dao.getBachecheByUsername(utenteCorrente.getUsername());
        List<ToDo> todos = dao.getToDoByUsername(utenteCorrente.getUsername());

        utenteCorrente.setListaBacheche(bacheche);
        utenteCorrente.getListaToDo().clear();

        for (ToDo todo : todos) {
            caricaAutoreECondivisioni(todo);
            utenteCorrente.aggiungiToDo(todo, todo.getBacheca());
        }
    }

    /**
     * carica le informazioni sull'autore e sulle condivisioni, usato per popolare i campi autore e condivisoCon
     * con i dati del database. Se l'autore non è già impostato lo recupera.
     * poi svuota e ripopola la lista com utenti che hanno accesso a quel to-do tramite DAO.
     * @param todo oggetto di cui caricare autore e condivisioni.
     * @throws SQLException Se si verifica un errore durante l'interazione con il database.
     */
    private void caricaAutoreECondivisioni(@NotNull ToDo todo) throws SQLException {
        if (todo.getAutore() == null) {
            String autoreUsername = dao.getAutoreToDo(todo.getId());
            if (autoreUsername != null) {
                todo.setAutore(dao.getUtenteByUsername(autoreUsername));
            }
        }

        todo.getCondivisoCon().clear();
        for (String username : dao.getCondivisioniPerToDo(todo.getId())) {
            Utente utente = dao.getUtenteByUsername(username);
            if (utente != null) {
                todo.aggiungiUtenteCondiviso(utente);
            }
        }
    }

    /**
     * inizializza le 3 bacheche predefinite per ogni utente se non è stato ancora fatto, quindi se ha 0 bacheche.
     * @throws SQLException se c'è un problema nel salvataggio delle bacheche nel database.
     */
    private void inizializzaBachechePredefinite() throws SQLException {
        if (dao.mancaBachechePredefinite(utenteCorrente.getUsername())) {
            List<Bacheca> bacheche = Utente.inizializzaBacheche();
            dao.salvaBachechePredefinite(bacheche, utenteCorrente.getUsername());
        }
    }

    /**
     * valida i campi essenziali per la creazione o modifica di to-do
     * @param titolo not null

     * @param bacheca not null
     */
    private void validaInputToDo(String titolo, String bacheca) {
        if (titolo == null || titolo.trim().isEmpty()  ||
                bacheca == null || bacheca.trim().isEmpty()) {
            throw new IllegalArgumentException("Titolo, descrizione e bacheca obbligatori");
        }
    }

    /**
     * esegue una validazione per le operazioni di condivisione, è cruciale che solo l'autore originale del to-do possa gestirne le condivisioni.
     * @param todo oggetto coinvolto nell'operazione di condivisine
     * @param username dell'utente con cui si sta gestendo la condivisione
     */
    private void validaCondivisione(ToDo todo, String username) {
        if (todo == null || username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ToDo e username obbligatori");
        }
        if (!todo.getAutore().getUsername().equalsIgnoreCase(utenteCorrente.getUsername())) {
            throw new IllegalStateException("Solo l'autore può gestire le condivisioni");
        }
    }

    /**
     * restituisce l'oggetto utente attualmente loggato nel sistema
     * @return oggetto utente corrente, o null se è vuoto
     */
    public Utente getUtenteCorrente() { return utenteCorrente; }

    /**
     * cerca e restituisce un oggetto to-do specifico, identificato dal titolo e dalla bacheca a cui appartiene
     * @param titolo to-do da cercare
     * @param bacheca di appartenenza
     * @return oggetto to-do trovato o null se non esiste
     */
    public ToDo getToDoPerTitoloEBoard(String titolo, String bacheca) throws SQLException {
        return dao.getToDoPerTitoloEBacheca(titolo, bacheca);
    }


    /**
     * recupera gli username di tutti gli utenti registrati nel sistema, molto utile per selezionare un utente come nella condivisione di to-do
     * @return lista di utenti registrati
     * @throws SQLException Se si verifica un errore durante l'accesso al database per recuperare la lista.
     */
    public List<String> getListaUtenti() throws SQLException { return dao.getListaUtenti(); }

    /**
     * restituisce la lista aggiornata delle bacheche appartenenti all'utente attualmente loggato,
     * questo metodo si assicura di riflettere lo stato più recente delle bacheche da caricare
     * @return lista di oggetti bacheca che rappresentano le bacheche dell'utente corrente.
     * @throws SQLException Se si verifica un errore durante il recupero delle bacheche dal database.
     */
    public List<Bacheca> getListaBachecheAggiornate() throws SQLException {
        return getListaBacheche();
    }

    /**
     * Converte un oggetto {@link Color} nel suo equivalente codice esadecimale HTML.
     * Se il colore fornito è null, viene restituito il bianco ("#ffffff") come valore di default.
     * Il formato restituito è "#rrggbb", dove rr, gg, e bb rappresentano i valori esadecimali
     * a due cifre dei componenti rosso, verde e blu del colore.
     * @param color l'oggetto {@code Color} da convertire; può essere {@code null}
     * @return una stringa che rappresenta il colore in formato esadecimale HTML (es. "#1a2b3c")
     */
    private String colorToHex(Color color) {
        if (color == null) return "#ffffff";
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Recupera tutti i dettagli completi di un {@link ToDo to-do} specifico e li restituisce
     * sotto forma di una mappa di stringhe e oggetti. Serve per visualizzare tutte le informazioni di un to-do
     * in un'unica struttura. Per i campi che potrebbero essere null viene fornita una stringa "Nessuna" se il valore è assente.
     * @param titoloToDo di cui si vogliono recuperare i dettagli
     * @param titoloBacheca a cui appartiene il to-do
     * @return Una {@link java.util.Map Map} dove le chiavi sono stringhe che descrivono i dettagli
     * ("titolo", "descrizione", "dataScadenza", "coloreSfondo", "url", "percorsoImmagine")
     * e i valori sono gli oggetti corrispondenti ai dettagli del To-Do.
     * Se il To-Do non viene trovato, la mappa sarà vuota.
     * @throws SQLException Se si verifica un errore durante l'accesso o la manipolazione del database.
     */
    public Map<String, Object> getDettagliCompletiToDo(String titoloToDo, String titoloBacheca) throws SQLException {
        Map<String, Object> dettagli = new HashMap<>();

        ToDo todo = dao.getToDoPerTitoloEBacheca(titoloToDo, titoloBacheca); // ← carica i dati freschi!

        if (todo == null) return dettagli;

        dettagli.put("titolo", todo.getTitoloToDo());
        dettagli.put("descrizione", todo.getDescrizioneToDo() != null ? todo.getDescrizioneToDo() : "Nessuna");

        dettagli.put("coloreSfondo", todo.getColoreSfondo()); // String HEX


        dettagli.put("url", todo.getUrl());
        dettagli.put("percorsoImmagine", todo.getPercorsoImmagine());

        return dettagli;
    }

    /**
     * Formatta una lista di oggetti {@link ToDo} in una lista di stringhe leggibili.
     * Ogni {@link ToDo} viene convertito in una stringa che include il suo titolo, la data di scadenza (se presente) e lo stato.
     * Se la lista di input è vuota, restituisce una lista contenente un messaggio di "Nessun risultato trovato".
     * @param risultati lista di oggetti {@link ToDo} da formattare.
     * @return lista di stringhe, dove ogni stringa rappresenta un to-do formattato.
     * La lista sarà vuota se l'input è nullo o vuoto.
     */
    public List<String> formattaRisultati(List<ToDo> risultati) {
        List<String> listaFormattata = new ArrayList<>();
        if (risultati.isEmpty()) {
            listaFormattata.add("Nessun risultato trovato");
        } else {
            for (ToDo todo : risultati) {
                String riga = todo.getTitoloToDo() + " - Scadenza: " +
                        (todo.getDataScadenza() != null ? todo.getDataScadenza().toString() : "N.D.") +
                        " - Stato: " + todo.getStatoToDo();
                listaFormattata.add(riga);
            }
        }
        return listaFormattata;
    }
}