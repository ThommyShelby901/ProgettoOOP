package org.example.gui;

import org.example.controller.AppController;
import org.example.model.Bacheca;

import javax.swing.*;
import java.sql.SQLException;


public class HomeFrame {
    private JFrame frame;
    private AppController controller;

    // Componenti già disegnati nella form
    private JPanel homePanel;
    private JList<String> boardList;
    private DefaultListModel<String> boardListModel;
    private JButton logoutButton;
    private JButton btnCreaBacheca;
    private JButton btnModificaBacheca;
    private JButton btnEliminaBacheca;
    private JButton visualizzaButton;
    private JScrollPane scrollPaneBoardList;

    public HomeFrame(AppController controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frame = frameChiamante;

        frame.setContentPane(homePanel); // 🔥 Usa il pannello disegnato nella form
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);
        aggiornaListaBacheche(); // 🔹 Aggiorna la lista delle bacheche

        configuraEventi(); // 🔥 Collegamento degli eventi ai pulsanti

        frame.setVisible(true);
    }

    private void configuraEventi() {
        logoutButton.addActionListener(e -> logout());
        btnCreaBacheca.addActionListener(e -> creaBacheca());
        btnModificaBacheca.addActionListener(e -> modificaBacheca());
        btnEliminaBacheca.addActionListener(e -> eliminaBacheca());
        visualizzaButton.addActionListener(e -> visualizzaBacheca());
    }

    private void logout() {
        int conferma = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler uscire?", "Logout", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            frame.dispose();
            LoginFrame login = new LoginFrame();
            login.setController(controller);
            login.getFrame().setVisible(true);
        }
    }

    private void creaBacheca() {
        String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo della nuova bacheca:");
        String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione della nuova bacheca:");

        if (titolo != null && !titolo.trim().isEmpty() && descrizione != null && !descrizione.trim().isEmpty()) {
            try {
                controller.creaBacheca(titolo.trim(), descrizione.trim());
                aggiornaListaBacheche();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Errore nella creazione della bacheca: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Titolo e descrizione non possono essere vuoti.");
        }
    }

    private void modificaBacheca() {
        String selezionata = boardList.getSelectedValue();
        if (selezionata != null) {
            String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo della bacheca:");
            String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione della bacheca:");

            if (nuovoTitolo != null && nuovaDescrizione != null && !nuovoTitolo.trim().isEmpty()) {
                controller.modificaBacheca(selezionata, nuovoTitolo.trim(), nuovaDescrizione.trim());
                aggiornaListaBacheche();
            } else {
                JOptionPane.showMessageDialog(frame, "Titolo e descrizione non possono essere vuoti.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da modificare.");
        }
    }

    private void eliminaBacheca() {
        String selezionata = boardList.getSelectedValue();
        if (selezionata != null) {
            try {
                controller.eliminaBacheca(selezionata);
                aggiornaListaBacheche();
                JOptionPane.showMessageDialog(frame, "Bacheca eliminata con successo!");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da eliminare.");
        }
    }

    private void visualizzaBacheca() {
        String selezionata = boardList.getSelectedValue();
        if (selezionata != null) {
            Bacheca bachecaSelezionata = controller.getBachecaByTitolo(selezionata);
            if (bachecaSelezionata != null) {
                new ToDoGUI(controller, frame, bachecaSelezionata.getTitoloBacheca());
                frame.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(frame, "❌ Errore: La bacheca selezionata non esiste!");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Seleziona una bacheca prima di visualizzarla!");
        }
    }

    private void aggiornaListaBacheche() {
        boardListModel.clear();
        try {
            controller.inizializzaBachechePerUtente(controller.getUtenteCorrente().getUsername());
            for (Bacheca board : controller.getListaBachecheAggiornate()) {
                boardListModel.addElement(board.getTitoloBacheca());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "❌ Errore nel recupero delle bacheche dal database!");
        }
    }


}
