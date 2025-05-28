package org.example.controller;

import org.example.dao.DatabaseDAO;
import org.example.gui.HomeFrame;
import org.example.gui.LoginFrame;
import org.example.implementazionePostgresDAO.DatabaseImplementazionePostgresDAO;
import org.example.model.Bacheca;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.sql.SQLException;
import java.util.List;

public class AppController {
    private LoginFrame loginFrame;
    private HomeFrame homeFrame;
    private Utente utenteCorrente;

    public AppController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        loginFrame.getLoginButton().addActionListener(e -> performLogin()); // 🔥 Collega il pulsante al login!
    }


    public void performLogin() {
        String username = loginFrame.getUsername();
        String password = loginFrame.getPassword();

        try {
            DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();
            utenteCorrente = dao.getUtenteByUsername(username);

            if (utenteCorrente != null && utenteCorrente.getPassword().equals(password)) {
                List<Bacheca> bachecheUtente = dao.getBachecheByUsername(); // 🔥 Recupera le bacheche dal DB
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






    public void setLoginFrame(LoginFrame loginFrame) { // 🔥 Metodo per collegare il LoginFrame dopo la creazione
        this.loginFrame = loginFrame;
        loginFrame.getLoginButton().addActionListener(e -> performLogin());
    }


    // ---------------------- GESTIONE BACHECHE ----------------------

    public List<String> getListaBacheche() {
        return utenteCorrente.getListaBacheche().stream()
                .map(Bacheca::getTitoloBacheca)
                .toList();
    }

    public Bacheca getBachecaByTitolo(String titolo) {
        return utenteCorrente.getBachecaByTitolo(titolo);
    }

    public void creaBacheca(String titolo, String descrizione) {
        utenteCorrente.creaBacheca(titolo, descrizione);
    }

    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        Bacheca board = getBachecaByTitolo(titoloCorrente);
        if (board != null) {
            utenteCorrente.modificaBacheca(titoloCorrente, nuovoTitolo, nuovaDescrizione);
        }
    }

    public void eliminaBacheca(String titolo) {
        utenteCorrente.eliminaBacheca(titolo);
    }

    // ---------------------- GESTIONE TO-DO ----------------------

    public List<String> getListaToDo(Bacheca board) {
        return board.getListaToDo().stream()
                .map(ToDo::getTitoloToDo)
                .toList();
    }

    public ToDo getToDoPerTitoloEBoard(String titolo, Bacheca board) {
        return utenteCorrente.cercaToDoPerTitoloEBoard(titolo, board);
    }

    public void aggiungiToDo(Bacheca board, String titolo, String descrizione, String dataScadenza) {
        utenteCorrente.creaToDo(titolo, descrizione, null, null, dataScadenza, null, null, board);
    }

    public void modificaToDo(ToDo todo, String nuovoTitolo, String nuovaDescrizione, String nuovaDataScadenza) {
        if (todo != null) {
            utenteCorrente.modificaToDo(todo, utenteCorrente, nuovoTitolo, nuovaDescrizione, null, null, nuovaDataScadenza, null, null);
        }
    }



    public void eliminaToDo(Bacheca board, String titolo) {
        ToDo todo = getToDoPerTitoloEBoard(titolo, board);
        if (todo != null) {
            utenteCorrente.eliminaToDo(todo);
        }
    }

    public void trasferisciToDo(ToDo todo, Bacheca nuovaBacheca) {
        if (todo != null && nuovaBacheca != null) {
            utenteCorrente.trasferisciToDo(todo, nuovaBacheca.getTitoloBacheca());
        }
    }

    public void spostaToDo(Bacheca board, String titoloDaSpostare, String titoloPosizione) {
        ToDo toDoDaSpostare = getToDoPerTitoloEBoard(titoloDaSpostare, board);
        ToDo toDoPosizione = getToDoPerTitoloEBoard(titoloPosizione, board);

        if (toDoDaSpostare == null || toDoPosizione == null) {
            System.out.println("Errore: Uno dei To-Do non esiste.");
            return;
        }

        board.getListaToDo().remove(toDoDaSpostare);
        int index = board.getListaToDo().indexOf(toDoPosizione);
        board.getListaToDo().add(index, toDoDaSpostare);
    }



    // ---------------------- GESTIONE CONDIVISIONI ----------------------

    public void aggiungiCondivisione(ToDo todo, String nomeUtente) {
        Utente utenteDaAggiungere = Utente.getListaUtentiGlobali().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nomeUtente))
                .findFirst()
                .orElse(null);

        if (utenteDaAggiungere != null) {
            todo.aggiungiCondivisione(utenteCorrente, utenteDaAggiungere);
        }
    }

    public void rimuoviCondivisione(ToDo todo, String nomeUtente) {
        Utente utenteDaRimuovere = Utente.getListaUtentiGlobali().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nomeUtente))
                .findFirst()
                .orElse(null);

        if (utenteDaRimuovere != null) {
            todo.eliminaCondivisione(utenteCorrente, utenteDaRimuovere);
        }
    }



}
