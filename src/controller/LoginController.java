package controller;

import gui.LoginFrame;
import gui.HomeFrame;
import model.Bacheca;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginFrame loginFrame;

    public LoginController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        this.loginFrame.getLoginButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = loginFrame.getUsername();
        String password = loginFrame.getPassword();

        // Creo un oggetto Utente con i dati inseriti
        Utente utente = new Utente(username, password);

        // Chiamo il metodo esitoAccesso() dalla classe Utente
        if (utente.esitoAccesso()) {
            System.out.println("Accesso riuscito");

            // Se non ha bacheche, le creo di default
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
            new HomeController(homeFrame, utente);
            homeFrame.setVisible(true);
        } else {
            System.out.println("Accesso negato");
            loginFrame.showMessage("Credenziali non valide, riprova.");
        }
    }
}
