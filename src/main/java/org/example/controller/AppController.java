package org.example.controller;

import org.example.dao.DatabaseDAO;
import org.example.gui.HomeFrame;
import org.example.gui.LoginFrame;
import org.example.implementazionepostgresdao.DatabaseImplementazionePostgresDAO;
import org.example.model.Bacheca;
import org.example.model.StatoToDo;
import org.example.model.ToDo;
import org.example.model.Utente;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppController {
    private final LoginFrame loginFrame;
    private HomeFrame homeFrame;
    private Utente utenteCorrente;
    private DatabaseDAO dao;

    public AppController(DatabaseDAO dao, LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.dao = dao;

        // 🔥 Assicura che le bacheche vengano inserite
        loginFrame.getLoginButton().addActionListener(e -> performLogin());
    }

    public void inizializzaBachechePerUtente(String username) throws SQLException {
        if (!dao.haBachechePredefinite(username)) { // 🔥 Controlla se l'utente ha già le bacheche
            List<Bacheca> bacheche = Utente.inizializzaBacheche();
            dao.salvaBachechePredefinite(bacheche, username);
        }

    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public void registraUtente(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            loginFrame.showMessage("Errore: Username e password non possono essere vuoti.");
            return;
        }

        try {
            if (dao.utenteEsiste(username)) {
                loginFrame.showMessage("Errore: lo username è già in uso.");
                return;
            }

            Utente nuovoUtente = new Utente(username, password);
            dao.salvaUtente(nuovoUtente);
            loginFrame.showMessage("Registrazione completata!");
        } catch (SQLException e) {
            e.printStackTrace();
            loginFrame.showMessage("Errore di connessione al database.");
        }
    }

    public void performLogin() {
        String username = loginFrame.getUsername();
        String password = loginFrame.getPassword();

        try {
            dao = new DatabaseImplementazionePostgresDAO();
            utenteCorrente = dao.getUtenteByUsername(username);

            if (utenteCorrente != null && utenteCorrente.getPassword().equals(password)) {
                List<Bacheca> bachecheUtente = dao.getBachecheByUsername(username); // 🔥 Recupera le bacheche dal DB
                utenteCorrente.setListaBacheche(bachecheUtente); // 🔥 Assegna le bacheche all'utente

                loginFrame.chiudiFinestra();
                homeFrame = new HomeFrame(this, loginFrame.getFrame());
            } else {
                loginFrame.showMessage("Credenziali non valide, riprova.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            loginFrame.showMessage("Errore di connessione al database.");
        }
    }

    // ---------------------- GESTIONE BACHECHE ----------------------

    public List<Bacheca> getListaBachecheAggiornate() throws SQLException {
        return dao.getListaBachecheDalDB(utenteCorrente.getUsername()); // 🔥 Ottieni le bacheche aggiornate!
    }

    public Bacheca getBachecaByTitolo(String titolo) {
        return utenteCorrente.getBachecaByTitolo(titolo);
    }

    public void creaBacheca(String titolo, String descrizione){
        if (titolo == null || titolo.trim().isEmpty() || descrizione == null || descrizione.trim().isEmpty()) {
            return;
        }

        // 🔥 Salva la bacheca nel database
        Bacheca bachecaSalvata = dao.aggiungiBacheca(titolo, descrizione, utenteCorrente.getUsername());

        if (bachecaSalvata != null) {
            // 🔥 Sincronizza il Model aggiornando la lista di bacheche dell'utente
            utenteCorrente.creaBacheca(bachecaSalvata.getTitoloBacheca(), bachecaSalvata.getDescrizioneBacheca());
        }
    }

    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        if (titoloCorrente == null || titoloCorrente.trim().isEmpty() ||
                nuovoTitolo == null || nuovoTitolo.trim().isEmpty() ||
                nuovaDescrizione == null || nuovaDescrizione.trim().isEmpty()) {
            return;
        }

        // 🔥 Modifichiamo la bacheca nel `Model` tramite `Utente`
        utenteCorrente.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione);

        // 🔥 Ora aggiorniamo la Bacheca nel database tramite `DAO`
        dao.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione);

    }


    public void eliminaBacheca(String titolo) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty()) {
            return;
        }

        // Rimuoviamo la bacheca nel `Model` tramite `Utente`
        utenteCorrente.eliminaBacheca(titolo);

        // Ora rimuoviamo la bacheca nel database tramite `DAO`
        dao.eliminaBacheca(titolo);

    }

    // ---------------------- GESTIONE TO-DO ----------------------

    public List<ToDo> getTuttiToDo(String titoloBacheca) throws SQLException {
        if (titoloBacheca == null || titoloBacheca.isEmpty()) {
            return new ArrayList<>();
        }

        List<ToDo> toDoList = dao.getTuttiToDo(titoloBacheca); // 🔥 Recupera i To-Do in base al titolo

        if (toDoList != null && !toDoList.isEmpty()) {
            return toDoList;
        }

        return new ArrayList<>();
    }

    public ToDo getToDoPerTitoloEBoard(String titolo, String titoloB) {
        if (titolo == null || titoloB == null || utenteCorrente == null) {
            return null;
        }

        ToDo todo = utenteCorrente.cercaToDoPerTitoloEBoard(titolo, titoloB);
        if (todo != null) {
            return todo;
        }

        try {
            return dao.getToDoByTitolo(titolo);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void creaToDo(String titolo, String descrizione,
                         String dataScadenza, String url, StatoToDo stato, String titoloBacheca) throws SQLException {
        ToDo nuovoToDo = utenteCorrente.creaToDo(titolo, descrizione, dataScadenza, url, stato, titoloBacheca);

        if (nuovoToDo != null) {
            dao.creaToDo(nuovoToDo, titoloBacheca); // Ora il DAO riceve il titolo
        }
    }


    public void modificaToDo(ToDo todo, Utente utenteRichiedente,
                             String nuovoTitolo, String nuovaDescrizione,

                             String nuovaDataScadenza, String nuovoUrl,
                             StatoToDo nuovoStato)  {
        if (todo == null || utenteRichiedente == null) {
            return;
        }

        // Modifica il `Model` tramite `Utente`
        utenteRichiedente.modificaToDo(todo, utenteRichiedente, nuovoTitolo, nuovaDescrizione, nuovaDataScadenza, nuovoUrl, nuovoStato);

        // Ora salva la modifica nel database tramite `DAO`
        dao.aggiornaToDo(todo);
    }


    public void eliminaToDo(String titoloToDo, String titoloBacheca) throws SQLException {
        if (titoloToDo == null || titoloBacheca == null) {
            return;
        }

        // 🔹 Troviamo la bacheca giusta nel Model
        Bacheca bacheca = getBachecaByTitolo(titoloBacheca);
        if (bacheca == null) {
            return;
        }

        // 🔥 Troviamo il To-Do manualmente scorrendo la lista della bacheca
        ToDo todoDaEliminare = null;
        for (ToDo todo : bacheca.getListaToDo()) {
            if (todo.getTitoloToDo().equalsIgnoreCase(titoloToDo)) {
                todoDaEliminare = todo;
                break;
            }
        }

        if (todoDaEliminare == null) {
            return;
        }

        // 🔥 Rimuove il To-Do dal `Model`
        utenteCorrente.eliminaToDo(todoDaEliminare);

        // 🔹 Ora rimuove il To-Do dal database tramite `DAO`
        dao.eliminaToDo(titoloToDo, titoloBacheca);

    }

    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) throws SQLException {
        if (todo == null || nomeBachecaDestinazione == null || utenteCorrente == null) {
            return;
        }

        // 🔥 Trasferisce il To-Do nel `Model`
        utenteCorrente.trasferisciToDo(todo, nomeBachecaDestinazione);

        // 🔹 Recupera la nuova bacheca direttamente
        Bacheca nuovaBacheca = getBachecaByTitolo(nomeBachecaDestinazione);

        if (nuovaBacheca != null) {
            // ✅ Ora passiamo solo il titolo della bacheca al DAO!
            dao.trasferisciToDo(todo, nuovaBacheca.getTitoloBacheca());
        }
    }


    public void spostaToDo(String titoloBacheca, String titoloDaSpostare, int nuovaPosizione) throws SQLException {
        ToDo toDoDaSpostare = getToDoPerTitoloEBoard(titoloDaSpostare, titoloBacheca);
        if (toDoDaSpostare == null) {
            System.out.println("❌ To-Do non trovato.");
            return;
        }

        // 🔥 Ora il controller delega lo spostamento al modello `Utente`
        utenteCorrente.spostaToDo(toDoDaSpostare, titoloBacheca, nuovaPosizione);

        // 🔹 Dopo aver modificato l'ordine in memoria, aggiorna il database tramite il DAO
        dao.aggiornaOrdineToDo(toDoDaSpostare.getId(), nuovaPosizione);
    }



    // ---------------------- GESTIONE CONDIVISIONI ----------------------

    public void aggiungiCondivisione(ToDo todo, String nomeUtente) {
        if (todo == null || nomeUtente == null || nomeUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("To-Do o nome utente non valido!");
        }
        try {
            Utente utenteDaAggiungere = dao.getUtenteByUsername(nomeUtente); // Recupera dal database

            if (utenteDaAggiungere != null) {
                dao.aggiungiCondivisione(todo, nomeUtente); // Passa la richiesta al DAO
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void rimuoviCondivisione(ToDo todo, String nomeUtente) {
        if (todo == null || nomeUtente == null || nomeUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("To-Do o nome utente non valido!");
        }

        try {
            Utente utenteDaRimuovere = dao.getUtenteByUsername(nomeUtente); //  Recupera dal database

            if (utenteDaRimuovere != null) {
                dao.rimuoviCondivisione(todo, nomeUtente); // Passa la richiesta al DAO
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<String> getListaUtenti() throws SQLException {
        return dao.getListaUtenti(); // 🔥 Il Controller prende i dati dal DAO
    }
}
