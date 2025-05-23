package controller;

import gui.LoginFrame;
import gui.HomeFrame;
import model.Bacheca;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    private LoginFrame loginFrame;
      // lista utenti globale

    public LoginController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.loginFrame.getLoginButton().addActionListener(e -> performLogin());

        Utente.inizializzaUtenti();




        // **Esempio: crea utenti di prova nella lista globale**

    }



    private void performLogin() {
        String username = loginFrame.getUsername();
        String password = loginFrame.getPassword();

        Utente utente = Utente.getListaUtentiGlobali().stream()
                .filter(u -> u.getNome().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (utente != null) {
            System.out.println("Accesso riuscito");

            if (utente.getListaBacheche().isEmpty()) {
                Bacheca uni = utente.creaBacheca("Università", "Bacheca per le attività universitarie");
                Bacheca lavoro = utente.creaBacheca("Lavoro", "Bacheca per le attività lavorative");
                Bacheca tempoLibero = utente.creaBacheca("Tempo Libero", "Bacheca per le attività di svago");

                ToDo todo1 = new ToDo();
                todo1.setTitoloToDo("Studiare Algoritmi");
                todo1.setDescrizioneToDo("Ripassare il capitolo 3");
                todo1.setAutore(utente);
                uni.aggiungiToDo(todo1);

                ToDo todo2 = new ToDo();
                todo2.setTitoloToDo("Preparare presentazione");
                todo2.setDescrizioneToDo("Per il meeting di lavoro di venerdì");
                todo2.setAutore(utente);
                lavoro.aggiungiToDo(todo2);
            }

            loginFrame.dispose();
            HomeFrame homeFrame = new HomeFrame(username);

            // Passa utente e lista utenti globali (se serve)
            new HomeController(homeFrame, utente);
            homeFrame.setVisible(true);
        } else {
            System.out.println("Accesso negato");
            loginFrame.showMessage("Credenziali non valide, riprova.");
        }
    }



}
