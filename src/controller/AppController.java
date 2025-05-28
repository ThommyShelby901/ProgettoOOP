package controller;

import gui.HomeFrame;
import gui.LoginFrame;
import model.Bacheca;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class AppController {
    private LoginFrame loginFrame;
    private HomeFrame homeFrame;
    private Utente utenteCorrente;

    public AppController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        Utente.inizializzaUtenti();
        loginFrame.getLoginButton().addActionListener(e -> performLogin()); // 🔥 Collega il pulsante al login!
    }


    public void performLogin() {

            System.out.println("Esecuzione di performLogin()..."); // 🔥 Debug


        if (loginFrame == null) {
            System.out.println("Errore: loginFrame è null!");
            return;
        }

        String username = loginFrame.getUsername();
        String password = loginFrame.getPassword();

        utenteCorrente = Utente.getListaUtentiGlobali().stream()
                .filter(u -> u.getNome().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (utenteCorrente != null) {
            utenteCorrente.inizializzaBacheche();
            loginFrame.chiudiFinestra();
            homeFrame = new HomeFrame(this, loginFrame.getFrame()); // 🔥 Apriamo la Home SOLO dopo il login riuscito
        } else {
            loginFrame.showMessage("Credenziali non valide, riprova.");
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
