package org.example.controller;

import org.example.dao.DatabaseDAO;
import org.example.gui.HomeFrame;
import org.example.gui.LoginFrame;
import org.example.model.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AppController {
    // Componenti BCE
    private final DatabaseDAO dao;
    private LoginFrame loginFrame;
    private HomeFrame homeFrame;
    private Utente utenteCorrente;

    public AppController(DatabaseDAO dao, LoginFrame loginFrame) {
        this.dao = dao;
        this.loginFrame = loginFrame;
    }

    // ==================== GESTIONE AUTENTICAZIONE ====================
    public void login(String username, String password) {
        try {
            validaCredenziali(username, password);
            autenticaUtente(username, password);
            caricaDatiUtente();
            inizializzaBachechePredefinite();
            avviaHomeFrame();
        } catch (IllegalArgumentException | SQLException e) {
            loginFrame.showMessage(e.getMessage());
        } catch (Exception _) {
            loginFrame.showMessage("Credenziali non valide");
        }
    }

    public void registrazione(String username, String password) {
        try {
            validaCredenziali(username, password);
            if (dao.utenteEsiste(username)) {
                throw new IllegalArgumentException("Username già esistente");
            }
            dao.salvaUtente(new Utente(username, password));
            loginFrame.showMessage("Registrazione completata!");
        } catch (SQLException e) {
            loginFrame.showMessage("Errore database: " + e.getMessage());
        }
    }

    public void handleLogout() {
        utenteCorrente = null;
        if (homeFrame != null) homeFrame.getFrame().dispose();
        if (loginFrame != null) loginFrame.chiudiFinestra();
        loginFrame = new LoginFrame();
        loginFrame.setController(this);
    }

    // ==================== GESTIONE BACHECHE ====================
    public List<Bacheca> getListaBacheche() throws SQLException {
        return dao.getListaBachecheDalDB(utenteCorrente.getUsername());
    }

    public void creaBacheca(String titolo, String descrizione) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty() || descrizione == null || descrizione.trim().isEmpty()) {
            throw new IllegalArgumentException("Titolo e descrizione obbligatori");
        }
        Bacheca bacheca = dao.aggiungiBacheca(titolo, descrizione, utenteCorrente.getUsername());
        utenteCorrente.aggiungiBacheca(bacheca);
        homeFrame.aggiornaListaBacheche();
    }

    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        validaInputBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione);
        utenteCorrente.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione);
        dao.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione, utenteCorrente.getUsername());
    }

    public void eliminaBacheca(String titolo) throws SQLException {
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Titolo obbligatorio");
        }
        utenteCorrente.eliminaBacheca(titolo);
        dao.eliminaBacheca(titolo, utenteCorrente.getUsername());
    }

    // ==================== GESTIONE TO-DO ====================
    public List<ToDo> getToDoPerBacheca(String titoloBacheca) throws SQLException {
        if (titoloBacheca == null || titoloBacheca.isEmpty()) {
            return Collections.emptyList();
        }
        return dao.getTuttiToDo(titoloBacheca, utenteCorrente.getUsername());
    }

    public void creaToDo(String titolo, String descrizione, String dataScadenza,
                         String url, StatoToDo stato, String titoloBacheca, Color colore) throws SQLException {
        validaInputToDo(titolo, descrizione, titoloBacheca);
        ToDo todo = utenteCorrente.creaToDo(titolo, descrizione, dataScadenza, url, stato, titoloBacheca, colore);
        if (todo != null) {
            todo.setAutore(utenteCorrente);
            dao.creaToDo(todo, titoloBacheca);
        }
    }

    // Modifica nel metodo modificaToDo
    public void modificaToDo(ToDo todo, String nuovoTitolo, String nuovaDescrizione,
                             String nuovaDataScadenza, String nuovoUrl, StatoToDo nuovoStato) throws SQLException {
        if (todo == null) throw new IllegalArgumentException("ToDo non può essere null");

        // Rimossa la richiesta dell'utente come parametro
        utenteCorrente.modificaToDo(todo, nuovoTitolo, nuovaDescrizione, nuovaDataScadenza, nuovoUrl, nuovoStato);
        dao.aggiornaToDo(todo);
        caricaDatiUtente();
    }

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

    public void spostaToDo(String titoloBacheca, String titoloToDo, int nuovaPosizione) throws SQLException {
        utenteCorrente.spostaToDo(titoloBacheca, titoloToDo, nuovaPosizione);
        List<ToDo> todos = utenteCorrente.getToDoPerBacheca(titoloBacheca);
        for (int i = 0; i < todos.size(); i++) {
            dao.aggiornaOrdineToDo(todos.get(i).getId(), i);
        }
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

        //  Chiama il metodo nel model SENZA controllo dell'autore
        utenteCorrente.trasferisciToDo(todo, nomeBachecaDestinazione);

        // Aggiorna il database
        dao.trasferisciToDo(todo, nomeBachecaDestinazione);
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

    public List<ToDo> cercaToDoPerTitolo(String titolo) {
        return utenteCorrente.getListaToDo().stream()
                .filter(todo -> todo.getTitoloToDo().contains(titolo))
                .collect(Collectors.toList());
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


    // ==================== GESTIONE CONDIVISIONI ====================
    public void aggiungiCondivisione(ToDo todo, String username) throws SQLException {
        validaCondivisione(todo, username);
        Utente destinatario = dao.getUtenteByUsername(username);

        dao.aggiungiCondivisione(todo, username);
        todo.aggiungiUtenteCondiviso(destinatario);
        destinatario.aggiungiToDoCondiviso(todo);
    }

    public void rimuoviCondivisione(ToDo todo, String username) throws SQLException {
        validaCondivisione(todo, username);
        Utente destinatario = dao.getUtenteByUsername(username);

        dao.rimuoviCondivisione(todo, username);
        todo.rimuoviUtenteCondiviso(destinatario);
        destinatario.rimuoviToDoCondiviso(todo);
    }

    // ==================== METODI DI SUPPORTO ====================
    private void validaCredenziali(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username e password obbligatori");
        }
    }

    private void autenticaUtente(String username, String password) throws Exception {
        utenteCorrente = dao.getUtenteByUsername(username);
        if (utenteCorrente == null || !utenteCorrente.getPassword().equals(password)) {
            throw new Exception("Credenziali non valide");
        }
    }

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

    private void caricaAutoreECondivisioni(ToDo todo) throws SQLException {
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

    private void inizializzaBachechePredefinite() throws SQLException {
        if (!dao.haBachechePredefinite(utenteCorrente.getUsername())) {
            List<Bacheca> bacheche = Utente.inizializzaBacheche();
            dao.salvaBachechePredefinite(bacheche, utenteCorrente.getUsername());
        }
    }

    private void avviaHomeFrame() {
        loginFrame.chiudiFinestra();
        homeFrame = new HomeFrame(this, loginFrame.getFrame());
    }

    private void validaInputBacheca(String... campi) {
        for (String campo : campi) {
            if (campo == null || campo.trim().isEmpty()) {
                throw new IllegalArgumentException("Tutti i campi sono obbligatori");
            }
        }
    }

    private void validaInputToDo(String titolo, String descrizione, String bacheca) {
        if (titolo == null || titolo.trim().isEmpty() ||
                descrizione == null || descrizione.trim().isEmpty() ||
                bacheca == null || bacheca.trim().isEmpty()) {
            throw new IllegalArgumentException("Titolo, descrizione e bacheca obbligatori");
        }
    }

    private void validaCondivisione(ToDo todo, String username) {
        if (todo == null || username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ToDo e username obbligatori");
        }
        if (!todo.getAutore().equals(utenteCorrente)) {
            throw new IllegalStateException("Solo l'autore può gestire le condivisioni");
        }
    }

    // ==================== GETTER ====================
    public Utente getUtenteCorrente() { return utenteCorrente; }
    public Bacheca getBachecaByTitolo(String titolo) { return utenteCorrente.getBachecaByTitolo(titolo); }
    public ToDo getToDoPerTitoloEBoard(String titolo, String bacheca) {
        return utenteCorrente.cercaToDoPerTitoloEBoard(titolo, bacheca);
    }
    public List<String> getListaUtenti() throws SQLException { return dao.getListaUtenti(); }
    public List<Bacheca> getListaBachecheAggiornate() throws SQLException {
        return getListaBacheche();
    }



}