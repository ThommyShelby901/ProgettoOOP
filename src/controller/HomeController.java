package controller;

import gui.BachecaGUI;
import gui.HomeFrame;

import model.Bacheca;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HomeController {
    private HomeFrame homeFrame;
    private Utente utente;



    public HomeController(HomeFrame homeFrame, Utente utente) {
        this.homeFrame = homeFrame;
        this.utente = utente;


        // Imposta i listener sui pulsanti della HomeFrame
        homeFrame.getBtnCreaBacheca().addActionListener(e -> creaBacheca());
        homeFrame.getBtnModificaBacheca().addActionListener(e -> modificaBacheca());
        homeFrame.getBtnEliminaBacheca().addActionListener(e -> eliminaBacheca());
        homeFrame.getBtnLogout().addActionListener(e -> logout());
        homeFrame.getBtnVisualizzaBacheca().addActionListener(e -> visualizzaBacheca());

        // Carica e visualizza le bacheche predefinite
        refreshBoardList();
    }

    private void creaBacheca() {
        String title = JOptionPane.showInputDialog(homeFrame.getFrame(), "Inserisci il titolo della bacheca:");
        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(homeFrame.getFrame(), "Titolo non valido!");
            return;
        }
        String description = JOptionPane.showInputDialog(homeFrame.getFrame(), "Inserisci la descrizione della bacheca:");
        if (description == null) {
            description = "";
        }
        // Chiamata al metodo del model per creare la bacheca
        utente.creaBacheca(title, description);
        refreshBoardList();
    }

    private void modificaBacheca() {
        int selectedIndex = homeFrame.getBoardList().getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(homeFrame.getFrame(), "Seleziona una bacheca da modificare!");
            return;
        }
        String currentTitle = homeFrame.getBoardListModel().getElementAt(selectedIndex);
        String newTitle = JOptionPane.showInputDialog(homeFrame.getFrame(), "Inserisci il nuovo titolo:", currentTitle);
        if (newTitle == null || newTitle.trim().isEmpty()) {
            JOptionPane.showMessageDialog(homeFrame.getFrame(), "Titolo non valido!");
            return;
        }
        String newDescription = JOptionPane.showInputDialog(homeFrame.getFrame(), "Inserisci la nuova descrizione:");
        if (newDescription == null) {
            newDescription = "";
        }

        // Qui 'utente' è l'utente loggato, da cui prendo la lista delle bacheche
        utente.modificaBacheca( currentTitle, newTitle, newDescription);

        refreshBoardList();
    }


    private void eliminaBacheca() {
        int selectedIndex = homeFrame.getBoardList().getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(homeFrame.getFrame(), "Seleziona una bacheca da eliminare!");
            return;
        }
        String title = homeFrame.getBoardListModel().getElementAt(selectedIndex);
        int conferma = JOptionPane.showConfirmDialog(homeFrame.getFrame(), "Sei sicuro di voler eliminare la bacheca?", "Elimina Bacheca", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            // Chiamata al metodo del model per eliminare la bacheca
            utente.eliminaBacheca(title);
            refreshBoardList();
        }
    }

    // Aggiorna il contenuto della JList in base alle bacheche del model
    private void refreshBoardList() {
        homeFrame.getBoardListModel().clear();
        List<Bacheca> boards = utente.getListaBacheche();
        if (boards.isEmpty()) {
            homeFrame.showMessage("Nessuna bacheca presente.");
            return;
        }
        for (Bacheca b : boards) {
            homeFrame.getBoardListModel().addElement(b.getTitoloBacheca());
        }
    }


    // Esegui il logout: chiude la HomeFrame e, se necessario, riapre la schermata di login
    private void logout() {
        int conferma = JOptionPane.showConfirmDialog(
                homeFrame.getFrame(),  // Usa il JFrame interno come "parent"
                "Sei sicuro di voler fare logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            homeFrame.dispose();
            // Qui puoi eventualmente riaprire la LoginFrame
        }
    }


    private void visualizzaBacheca() {
        int index = homeFrame.getBoardList().getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(homeFrame.getFrame(), "Seleziona una bacheca da visualizzare!");
            return;
        }

        String boardTitle = homeFrame.getBoardListModel().getElementAt(index);

        // Trova la bacheca corrispondente nella lista dell'utente
        Bacheca board = utente.getListaBacheche().stream()
                .filter(b -> b.getTitoloBacheca().equalsIgnoreCase(boardTitle))
                .findFirst()
                .orElse(null);

        if (board == null) {
            JOptionPane.showMessageDialog(homeFrame.getFrame(), "Bacheca non trovata.");
            return;
        }

        // Mostra la lista dei ToDo associati alla bacheca
        board.mostraToDo();

        // Crea e mostra la GUI della bacheca
        BachecaGUI bachecaGUI = new BachecaGUI(board.getTitoloBacheca());
        ToDoController toDoController = new ToDoController(utente, board, bachecaGUI);

        bachecaGUI.show();
    }


    // TODO: Implementa questo metodo in base alla tua gestione utenti (ad esempio cerca in una lista utenti globali)


}
