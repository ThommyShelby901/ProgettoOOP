package org.example.controller;

import org.example.dao.DatabaseDAO;
import org.example.gui.HomeFrame;
import org.example.gui.LoginFrame;
import org.example.model.Bacheca;
import org.example.model.StatoToDo;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppController {
    private final LoginFrame loginFrame;
    private HomeFrame homeFrame;
    private Utente utenteCorrente;
    private DatabaseDAO dao;

    public AppController(DatabaseDAO dao, LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.dao = dao;
    }

    public void handleLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            loginFrame.showMessage("Errore: Username e password obbligatori");
            return;
        }

        try {
            autenticaUtente(username, password);
            caricaDatiUtente(username);
            inizializzaBachecheUtente(username);
            apriHomeFrame();
        } catch (SQLException e) {
            loginFrame.showMessage("Errore database: " + e.getMessage());
        } catch (Exception e) {
            loginFrame.showMessage(e.getMessage());
        }
    }

    public void handleRegistration(String username, String password) {
        try {
            if (dao.utenteEsiste(username)) {
                loginFrame.showMessage("Username già esistente");
                return;
            }

            Utente nuovoUtente = new Utente(username, password);
            dao.salvaUtente(nuovoUtente);
            loginFrame.showMessage("Registrazione completata!");

        } catch (SQLException e) {
            loginFrame.showMessage("Errore database: " + e.getMessage());
        }
    }

    private void autenticaUtente(String username, String password)
            throws Exception {
        utenteCorrente = dao.getUtenteByUsername(username);

        if (utenteCorrente == null || !utenteCorrente.getPassword().equals(password)) {
            throw new Exception("Credenziali non valide");
        }
    }

    public void caricaDatiUtente(String username) throws SQLException {
        List<Bacheca> bacheche = dao.getBachecheByUsername(username);
        List<ToDo> todos = dao.getToDoByUsername(username);

        utenteCorrente.setListaBacheche(bacheche);
        utenteCorrente.getListaToDo().clear();

        for (ToDo todo : todos) {
            // Se l'autore non è impostato o è incompleto, lo carichiamo dal database
            if (todo.getAutore() == null || todo.getAutore().getUsername() == null) {
                String autoreUsername = dao.getAutoreToDo(todo.getId());
                if (autoreUsername != null) {
                    Utente autore = dao.getUtenteByUsername(autoreUsername);
                    todo.setAutore(autore);
                }
            }

            // Pulisci le condivisioni esistenti prima di ricaricarle
            todo.getCondivisoCon().clear();

            // Carica le condivisioni per ogni ToDo
            List<String> condivisioni = dao.getCondivisioniPerToDo(todo.getId());
            for (String usernameCondiviso : condivisioni) {
                Utente utenteCondiviso = dao.getUtenteByUsername(usernameCondiviso);
                if (utenteCondiviso != null) {
                    todo.aggiungiUtenteCondiviso(utenteCondiviso);
                    utenteCondiviso.aggiungiToDoCondiviso(todo);
                }
            }

            utenteCorrente.aggiungiToDo(todo, todo.getBacheca());
        }
    }

    public void inizializzaBachecheUtente(String username) throws SQLException {
        if (!dao.haBachechePredefinite(username)) {
            List<Bacheca> bacheche = Utente.inizializzaBacheche();
            dao.salvaBachechePredefinite(bacheche, username);
        }
    }

    private void apriHomeFrame() {
        loginFrame.chiudiFinestra();
        homeFrame = new HomeFrame(this, loginFrame.getFrame());
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }



    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public Utente getUtenteByUsername(String username) throws SQLException {
        return dao.getUtenteByUsername(username);
    }


    // ---------------------- GESTIONE BACHECHE ----------------------

    public List<Bacheca> getListaBachecheAggiornate() throws SQLException {
        return dao.getListaBachecheDalDB(utenteCorrente.getUsername()); // 🔥 Ottieni le bacheche aggiornate!
    }

    public Bacheca getBachecaByTitolo(String titolo) {
        return utenteCorrente.getBachecaByTitolo(titolo);
    }

    public void creaBacheca(String titolo, String descrizione) {
        // Validazione input
        if (titolo == null || titolo.trim().isEmpty() ||
                descrizione == null || descrizione.trim().isEmpty()) {
            return;
        }

        try {
            // Delegare al DAO
            Bacheca bachecaSalvata = dao.aggiungiBacheca(titolo, descrizione, utenteCorrente.getUsername());

            // Aggiornare il Model
            if (bachecaSalvata != null) {
                utenteCorrente.aggiungiBacheca(bachecaSalvata);
                homeFrame.aggiornaListaBacheche(); // Notifica la View
            }
        } catch (SQLException e) {
            // Gestione errori
        }
    }

    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        if (titoloCorrente == null || titoloCorrente.trim().isEmpty() ||
                nuovoTitolo == null || nuovoTitolo.trim().isEmpty() ||
                nuovaDescrizione == null || nuovaDescrizione.trim().isEmpty()) {
            return;
        }

        utenteCorrente.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione);
        dao.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione, utenteCorrente.getUsername());
    }

    public void eliminaBacheca(String titolo) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty()) {
            return;
        }

        utenteCorrente.eliminaBacheca(titolo);
        dao.eliminaBacheca(titolo, utenteCorrente.getUsername());
    }

    // ---------------------- GESTIONE TO-DO ----------------------

    public List<ToDo> getTuttiToDo(String titoloBacheca) throws SQLException {
        if (titoloBacheca == null || titoloBacheca.isEmpty() || utenteCorrente == null) {
            return new ArrayList<>();
        }
        return dao.getTuttiToDo(titoloBacheca, utenteCorrente.getUsername());
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
                         String dataScadenza, String url, StatoToDo stato, String titoloBacheca, Color coloreSfondo) throws SQLException {
        ToDo nuovoToDo = utenteCorrente.creaToDo(titolo, descrizione, dataScadenza, url, stato, titoloBacheca, coloreSfondo);

        // Assicurati che l'autore sia impostato
        if (nuovoToDo != null) {
            nuovoToDo.setAutore(utenteCorrente);// Imposta esplicitamente l'autore
            nuovoToDo.setColoreSfondo(coloreSfondo);
            dao.creaToDo(nuovoToDo, titoloBacheca);
        }
    }


    public void modificaToDo(ToDo todo, Utente utenteRichiedente,
                             String nuovoTitolo, String nuovaDescrizione,
                             String nuovaDataScadenza, String nuovoUrl,
                             StatoToDo nuovoStato) throws SQLException {
        if (todo == null || utenteRichiedente == null) {
            return;
        }

        // Modifica il modello
        utenteRichiedente.modificaToDo(todo, utenteRichiedente, nuovoTitolo, nuovaDescrizione,
                nuovaDataScadenza, nuovoUrl, nuovoStato);

        // Aggiorna il database
        dao.aggiornaToDo(todo);

        // Ricarica i dati per assicurarsi la sincronizzazione
        caricaDatiUtente(utenteCorrente.getUsername());
    }


    public void eliminaToDo(String titoloToDo, String titoloBacheca) throws SQLException {
        if (titoloToDo == null || titoloBacheca == null) {
            return;
        }

        // Trova il ToDo nella listaToDo dell'utente filtrando per titolo e bacheca
        ToDo todoDaEliminare = null;
        for (ToDo todo : utenteCorrente.getListaToDo()) {
            if (todo.getTitoloToDo().equalsIgnoreCase(titoloToDo) &&
                    todo.getBacheca() != null &&
                    todo.getBacheca().equalsIgnoreCase(titoloBacheca)) {
                todoDaEliminare = todo;
                break;
            }
        }

        if (todoDaEliminare == null) {
            return;
        }

        // Rimuove il ToDo dall'utente (e quindi dalla sua lista globale)
        utenteCorrente.eliminaToDo(todoDaEliminare);

        // Rimuove dal database tramite DAO
        dao.eliminaToDo(titoloToDo, titoloBacheca);
    }


    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) throws SQLException {
        if (todo == null || nomeBachecaDestinazione == null || utenteCorrente == null) {
            throw new IllegalArgumentException("Parametri non validi per il trasferimento");
        }

        // Verifica che la bacheca destinazione esista
        Bacheca bachecaDest = utenteCorrente.getBachecaByTitolo(nomeBachecaDestinazione);
        if (bachecaDest == null) {
            throw new IllegalArgumentException("Bacheca destinazione non trovata");
        }

        // 🔥 Chiama il metodo nel model SENZA controllo dell'autore
        utenteCorrente.trasferisciToDo(todo, nomeBachecaDestinazione);

        // Aggiorna il database
        dao.trasferisciToDo(todo, nomeBachecaDestinazione);
    }



    public void spostaToDo(String titoloBacheca, String titoloDaSpostare, int nuovaPosizione) throws SQLException {
        final String titoloBachecaPulito = titoloBacheca.trim();
        final String titoloDaSpostarePulito = titoloDaSpostare.trim();

        System.out.println(" ToDo nella bacheca '" + titoloBachecaPulito + "':");
        for (ToDo t : utenteCorrente.getListaToDo()) {
            if (t.getBacheca().trim().equalsIgnoreCase(titoloBachecaPulito)) {
                System.out.println("   - '" + t.getTitoloToDo() + "'");
            }
        }

        // Sposto il ToDo nella lista globale dell'utente (in memoria)
        utenteCorrente.spostaToDo(titoloBachecaPulito, titoloDaSpostarePulito, nuovaPosizione);

        // Recupero la lista aggiornata dei ToDo filtrata per la bacheca
        List<ToDo> listaFiltrata = new ArrayList<>();
        for (ToDo t : utenteCorrente.getListaToDo()) {
            if (t.getBacheca().trim().equalsIgnoreCase(titoloBachecaPulito)) {
                listaFiltrata.add(t);
            }
        }

        // Ordina la lista per ordine
        Collections.sort(listaFiltrata, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo t1, ToDo t2) {
                return Integer.compare(t1.getOrdine(), t2.getOrdine());
            }
        });

        for (int i = 0; i < listaFiltrata.size(); i++) {
            ToDo todo = listaFiltrata.get(i);
            todo.setOrdine(i);
            dao.aggiornaOrdineToDo(todo.getId(), i);
        }
    }

    public void aggiungiImmagineAToDo(ToDo todo, String percorsoImmagine) throws SQLException {
        if (todo == null) {
            throw new IllegalArgumentException("ToDo non può essere null");
        }

        // Verifica che l'utente corrente abbia i permessi sul ToDo
        if (!todo.getAutore().getUsername().equals(utenteCorrente.getUsername()) &&
                !todo.getCondivisoCon().contains(utenteCorrente)) {
            throw new IllegalStateException("Non hai i permessi per modificare questo ToDo");
        }

        todo.setPercorsoImmagine(percorsoImmagine);
        dao.aggiornaToDo(todo);
    }

    public void rimuoviImmagineDaToDo(ToDo todo) throws SQLException {
        if (todo == null) {
            throw new IllegalArgumentException("ToDo non può essere null");
        }

        // Verifica che l'utente corrente abbia i permessi sul ToDo
        if (!todo.getAutore().getUsername().equals(utenteCorrente.getUsername()) &&
                !todo.getCondivisoCon().contains(utenteCorrente)) {
            throw new IllegalStateException("Non hai i permessi per modificare questo ToDo");
        }

        todo.setPercorsoImmagine(null);
        dao.aggiornaToDo(todo);
    }



    public List<ToDo> getToDoInScadenzaEntro(LocalDate dataLimite) throws SQLException {
        List<ToDo> result = utenteCorrente.getToDoInScadenzaEntro(dataLimite);
        if (result.isEmpty()) {
            result = dao.getToDoInScadenzaEntro(utenteCorrente.getUsername(), dataLimite);
            for (ToDo t : result) {
                utenteCorrente.aggiungiToDo(t, t.getBacheca());
            }
        }
        return result;
    }

    // Aggiungi questo metodo alla classe AppController
    public List<ToDo> getToDoInScadenzaOggi() throws SQLException {
        LocalDate oggi = LocalDate.now();
        List<ToDo> result = utenteCorrente.getToDoInScadenza(oggi);
        if (result.isEmpty()) {
            result = dao.getToDoInScadenzaEntro(utenteCorrente.getUsername(), oggi);
            for (ToDo t : result) {
                utenteCorrente.aggiungiToDo(t, t.getBacheca());
            }
        }
        return result;
    }



    public List<ToDo> cercaToDoPerTitolo(String titolo) throws SQLException {
        List<ToDo> result = utenteCorrente.cercaToDoPerTitolo(titolo);
        if (result.isEmpty()) {
            result = dao.cercaToDoPerTitolo(utenteCorrente.getUsername(), titolo);
            for (ToDo t : result) {
                utenteCorrente.aggiungiToDo(t, t.getBacheca());
            }
        }
        return result;
    }

    public void aggiornaToDo(ToDo todo) throws SQLException {
        if (todo == null) {
            throw new IllegalArgumentException("ToDo non può essere null");
        }
        dao.aggiornaToDo(todo);
    }


    // ---------------------- GESTIONE CONDIVISIONI ----------------------

    public void aggiungiCondivisione(ToDo todo, String nomeUtente) {
        if (todo == null || nomeUtente == null || nomeUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("To-Do o nome utente non valido!");
        }

        try {
            // Verifica che l'utente esista
            Utente utenteDaAggiungere = dao.getUtenteByUsername(nomeUtente);
            if (utenteDaAggiungere == null) {
                throw new IllegalArgumentException("Utente non trovato");
            }

            // Verifica che l'utente corrente sia l'autore
            if (todo.getAutore() == null || !todo.getAutore().getUsername().equals(utenteCorrente.getUsername())) {
                throw new IllegalStateException("Solo l'autore può condividere il To-Do");
            }

            // Verifica che l'utente abbia la stessa bacheca
            boolean haBacheca = false;
            for (Bacheca b : utenteDaAggiungere.getListaBacheche()) {
                if (b.getTitoloBacheca().equalsIgnoreCase(todo.getBacheca())) {
                    haBacheca = true;
                    break;
                }
            }

            if (!haBacheca) {
                throw new IllegalStateException("L'utente con cui vuoi condividere non ha la bacheca '" +
                        todo.getBacheca() + "'");
            }

            // Aggiungi la condivisione al database
            dao.aggiungiCondivisione(todo, nomeUtente);

            // Aggiorna il model
            todo.aggiungiUtenteCondiviso(utenteDaAggiungere);
            utenteDaAggiungere.aggiungiToDoCondiviso(todo);

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiunta della condivisione", e);
        }
    }

    public void rimuoviCondivisione(ToDo todo, String nomeUtente) {
        if (todo == null || nomeUtente == null || nomeUtente.trim().isEmpty()) {
            throw new IllegalArgumentException("To-Do o nome utente non valido!");
        }

        try {
            // Verifica che l'utente esista
            Utente utenteDaRimuovere = dao.getUtenteByUsername(nomeUtente);
            if (utenteDaRimuovere == null) {
                throw new IllegalArgumentException("Utente non trovato");
            }

            // Verifica che l'utente corrente sia l'autore OPPURE che stia rimuovendo la propria condivisione
            boolean isAutore = todo.getAutore() != null && todo.getAutore().getUsername().equals(utenteCorrente.getUsername());
            boolean isSelfRemoval = nomeUtente.equals(utenteCorrente.getUsername());
            boolean isCondiviso = todo.getCondivisoCon().stream()
                    .anyMatch(u -> u.getUsername().equals(nomeUtente));

            if (!isCondiviso) {
                throw new IllegalStateException("L'utente selezionato non ha accesso a questo To-Do");
            }

            if (!isAutore && !isSelfRemoval) {
                throw new IllegalStateException("Non hai i permessi per rimuovere questa condivisione");
            }

            // Rimuovi la condivisione dal database
            dao.rimuoviCondivisione(todo, nomeUtente);

            // Aggiorna il model
            todo.rimuoviUtenteCondiviso(utenteDaRimuovere);
            utenteDaRimuovere.rimuoviToDoCondiviso(todo);

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la rimozione della condivisione", e);
        }
    }

    public List<String> getListaUtenti() throws SQLException {
        return dao.getListaUtenti(); // 🔥 Il Controller prende i dati dal DAO
    }
}
