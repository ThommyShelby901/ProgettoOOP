package org.example.gui;


import org.example.controller.AppController;
import org.example.model.Bacheca;
import org.example.model.ToDo;

import javax.swing.*;

public class BachecaGUI {
    private JFrame frame;
    private AppController controller;
    private JFrame frameChiamante;

    // Componenti definiti nella form
    private JPanel bachecaPanel;
    private JList<String> boardList;
    private DefaultListModel<String> boardListModel;
    private JButton btnGestisciToDo;
    private JButton btnGestisciCondivisioni;
    private JButton btnRimuoviCondivisione;
    private JButton btnIndietro;

    public BachecaGUI(AppController controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frameChiamante = frameChiamante;
        this.frame = new JFrame("Gestione Bacheche");
        frame.setContentPane(bachecaPanel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Popola la lista delle bacheche dal Controller
        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);
        aggiornaListaBacheche();

        // Listener per aprire la gestione dei To-Do
        btnGestisciToDo.addActionListener(e -> {
            String selezionata = boardList.getSelectedValue();
            if (selezionata != null) {
                Bacheca board = controller.getBachecaByTitolo(selezionata);
                if (board != null) {
                    new ToDoGUI(controller, frame, board);
                    frame.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(frame, "Bacheca non trovata!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona una bacheca!");
            }
        });

        btnGestisciCondivisioni.addActionListener(e -> {
            String selezionata = boardList.getSelectedValue();
            if (selezionata != null) {
                Bacheca board = controller.getBachecaByTitolo(selezionata);
                if (board != null) {
                    String titoloToDo = JOptionPane.showInputDialog(frame, "Inserisci il titolo del To-Do da condividere:");
                    ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, board);

                    if (todo != null) {
                        String nomeUtente = JOptionPane.showInputDialog(frame, "Inserisci il nome dell'utente:");
                        if (nomeUtente != null && !nomeUtente.trim().isEmpty()) {
                            controller.aggiungiCondivisione(todo, nomeUtente.trim());
                            JOptionPane.showMessageDialog(frame, "To-Do condiviso con " + nomeUtente);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Nome utente non valido.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "To-Do non trovato!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Bacheca non trovata!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona una bacheca!");
            }
        });

        btnRimuoviCondivisione.addActionListener(e -> {
            String selezionata = boardList.getSelectedValue();
            if (selezionata != null) {
                Bacheca board = controller.getBachecaByTitolo(selezionata);
                if (board != null) {
                    String titoloToDo = JOptionPane.showInputDialog(frame, "Inserisci il titolo del To-Do:");
                    ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, board);

                    if (todo != null) {
                        String nomeUtente = JOptionPane.showInputDialog(frame, "Inserisci il nome dell'utente da rimuovere:");
                        if (nomeUtente != null && !nomeUtente.trim().isEmpty()) {
                            controller.rimuoviCondivisione(todo, nomeUtente.trim());
                            JOptionPane.showMessageDialog(frame, "Condivisione rimossa con " + nomeUtente);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Nome utente non valido.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "To-Do non trovato!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Bacheca non trovata!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona una bacheca!");
            }
        });

        // Listener per tornare alla schermata precedente
        btnIndietro.addActionListener(e -> {
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
}
