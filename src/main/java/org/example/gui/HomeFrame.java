package org.example.gui;

import org.example.controller.AppController;
import org.example.model.Bacheca;

import javax.swing.*;

public class HomeFrame {
    private JFrame frame;
    private AppController controller;

    // Componenti definiti nel form
    private JPanel homePanel;
    private JList<String> boardList;
    private DefaultListModel<String> boardListModel;
    private JButton logoutButton;
    private JButton btnCreaBacheca;
    private JButton btnModificaBacheca;
    private JButton btnEliminaBacheca;
    private JButton visualizzaButton;

    public HomeFrame(AppController controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frame = frameChiamante;
        if (homePanel == null) {
            System.out.println("Errore: homePanel è null! Controlla il UI Designer.");
            homePanel = new JPanel(); // 🔥 Inizializzazione di emergenza
        }
        this.frame.setContentPane(homePanel); // Usa il pannello definito nel form
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Popola la lista di bacheche predefinite dal Controller
        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);
        aggiornaListaBacheche(); // Metodo per aggiornare la lista


        // Listener per la gestione delle azioni dei pulsanti
        logoutButton.addActionListener(e -> {
            System.out.println("Bottone Visualizza Bacheca premuto!"); // 🔥 Debug per verificare se il listener si attiva

            String selezionata = boardList.getSelectedValue(); // 🔥 Recupera la bacheca selezionata
            System.out.println("Bacheca selezionata: " + selezionata); // 🔥 Debug per verificare la selezione

            if (selezionata != null) {
                Bacheca board = controller.getBachecaByTitolo(selezionata);
                System.out.println("Board trovata? " + (board != null ? "Sì" : "No")); // 🔥 Debug per verificare se la bacheca esiste

                if (board != null) {
                    new ToDoGUI(controller, frame, board); // 🔥 Apri `ToDoGUI` con la bacheca corretta!
                    frame.setVisible(false); // 🔥 Nasconde `HomeFrame`
                } else {
                    JOptionPane.showMessageDialog(frame, "Bacheca non trovata!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona una bacheca!");
            }
        });




        btnCreaBacheca.addActionListener(e -> {
            String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo della nuova bacheca:");
            String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione della nuova bacheca:");

            if (titolo != null && !titolo.trim().isEmpty() && descrizione != null) {
                controller.creaBacheca(titolo.trim(), descrizione.trim());
                aggiornaListaBacheche();
            } else {
                JOptionPane.showMessageDialog(frame, "Titolo e descrizione non possono essere vuoti.");
            }
        });

        btnModificaBacheca.addActionListener(e -> {
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
        });

        btnEliminaBacheca.addActionListener(e -> {
            String selezionata = boardList.getSelectedValue();
            if (selezionata != null) {
                controller.eliminaBacheca(selezionata); // La logica è gestita nel Controller
                aggiornaListaBacheche();
                JOptionPane.showMessageDialog(frame, "Bacheca eliminata con successo!");
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da eliminare.");
            }
        });

        visualizzaButton.addActionListener(e -> {
            frameChiamante.setVisible(true);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void aggiornaListaBacheche() {
        boardListModel.clear();
        for (String bacheca : controller.getListaBacheche()) {
            boardListModel.addElement(bacheca);
        }
    }

    public DefaultListModel<String> getListaBachecheModel() {
        return boardListModel;  // Assicurati che `boardListModel` sia il modello della tua `JList`
    }

    public JFrame getFrame() {
        return frame;  // Ora `getFrame()` restituirà correttamente il riferimento al JFrame
    }

    private void createUIComponents() {
        homePanel = new JPanel();
    }
}
