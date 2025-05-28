package org.example.gui;

import org.example.controller.AppController;
import org.example.model.Bacheca;
import org.example.model.ToDo;

import javax.swing.*;
import java.time.LocalDate;

public class ToDoGUI {
    private JFrame frame;
    private AppController controller;
    private Bacheca board;
    private JFrame frameChiamante;

    // Componenti definiti nella form
    private JPanel todoPanel;
    private JList<String> todoList;
    private DefaultListModel<String> todoListModel;
    private JButton btnAggiungiToDo;
    private JButton btnModificaToDo;
    private JButton btnEliminaToDo;
    private JButton btnIndietro;
    private JButton btnTrasferisciToDo;
    private JButton btnSpostaToDo;

    public ToDoGUI(AppController controller, JFrame frameChiamante, Bacheca board) {
        this.controller = controller;
        this.frameChiamante = frameChiamante;
        this.board = board;
        this.frame = new JFrame("Gestione To-Do - " + board.getTitoloBacheca());
        if (todoPanel == null) {
            System.out.println("Errore: `todoPanel` è null! Creando nuovo JPanel.");
            todoPanel = new JPanel();
        }
        frame.setContentPane(todoPanel); // Usa il pannello definito nella form
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Popola la lista dei To-Do dal Controller
        todoListModel = new DefaultListModel<>();
        todoList.setModel(todoListModel);
        aggiornaListaToDo();

        // Listener per creare un nuovo To-Do
        btnAggiungiToDo.addActionListener(e -> {
            String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo del To-Do:");
            String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione del To-Do:");
            String dataScadenza = JOptionPane.showInputDialog(frame, "Inserisci la data di scadenza (AAAA-MM-GG):");

            if (titolo != null && !titolo.trim().isEmpty() && descrizione != null && !descrizione.trim().isEmpty()) {
                controller.aggiungiToDo(board, titolo.trim(), descrizione.trim(), dataScadenza != null ? dataScadenza.trim() : null);
                aggiornaListaToDo();
            } else {
                JOptionPane.showMessageDialog(frame, "Titolo e descrizione non possono essere vuoti.");
            }
        });



        // Listener per modificare un To-Do esistente
        btnModificaToDo.addActionListener(e -> {
            String selezionato = todoList.getSelectedValue();
            if (selezionato != null) {
                ToDo todo = controller.getToDoPerTitoloEBoard(selezionato, board);
                if (todo != null) {
                    String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo:", todo.getTitoloToDo());
                    String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione:", todo.getDescrizioneToDo());
                    String nuovaDataScadenza = JOptionPane.showInputDialog(frame, "Modifica la data di scadenza (AAAA-MM-GG):");



                    if (nuovoTitolo != null && !nuovoTitolo.trim().isEmpty() && nuovaDescrizione != null && nuovaDataScadenza != null) {
                        controller.modificaToDo(todo, nuovoTitolo.trim(), nuovaDescrizione.trim(), nuovaDataScadenza.trim());
                        aggiornaListaToDo();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Titolo e descrizione non possono essere vuoti.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "To-Do non trovato!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona un To-Do da modificare.");
            }
        });


        // Listener per eliminare un To-Do
        btnEliminaToDo.addActionListener(e -> {
            String selezionato = todoList.getSelectedValue();
            if (selezionato != null) {
                controller.eliminaToDo(board, selezionato);
                aggiornaListaToDo();
                JOptionPane.showMessageDialog(frame, "To-Do eliminato con successo!");
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona un To-Do da eliminare.");
            }
        });

        // Listener per tornare alla schermata delle bacheche
        btnIndietro.addActionListener(e -> {
            frameChiamante.setVisible(true);
            frame.dispose();
        });


        btnTrasferisciToDo.addActionListener(e -> {
            String selezionato = todoList.getSelectedValue();
            if (selezionato != null) {
                ToDo todo = controller.getToDoPerTitoloEBoard(selezionato, board);
                if (todo != null) {
                    String nuovaBachecaNome = JOptionPane.showInputDialog(frame, "Inserisci il nome della nuova bacheca:");
                    Bacheca nuovaBacheca = controller.getBachecaByTitolo(nuovaBachecaNome);

                    if (nuovaBacheca != null) {
                        controller.trasferisciToDo(todo, nuovaBacheca);
                        aggiornaListaToDo();
                        JOptionPane.showMessageDialog(frame, "To-Do trasferito con successo!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Bacheca non trovata!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "To-Do non trovato!");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona un To-Do da trasferire.");
            }
        });

        btnSpostaToDo.addActionListener(e -> {
            String selezionato = todoList.getSelectedValue();
            int posizioneAttuale = todoList.getSelectedIndex();

            if (selezionato != null && posizioneAttuale >= 0) {
                String direzione = JOptionPane.showInputDialog(frame, "Sposta 'su' o 'giù'?");
                boolean spostaSu = "su".equalsIgnoreCase(direzione);

                int nuovaPosizione = spostaSu ? posizioneAttuale - 1 : posizioneAttuale + 1;

                // Verifica che la nuova posizione sia valida
                if (nuovaPosizione >= 0 && nuovaPosizione < todoListModel.getSize()) {
                    String titoloPosizione = todoListModel.getElementAt(nuovaPosizione);
                    controller.spostaToDo(board, selezionato, titoloPosizione); // Passiamo i parametri giusti!

                    aggiornaListaToDo();
                    JOptionPane.showMessageDialog(frame, "To-Do spostato con successo!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Impossibile spostare oltre i limiti della lista.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Seleziona un To-Do da spostare.");
            }
        });



        frame.setVisible(true);
        System.out.println("ToDoGUI è visibile? " + frame.isVisible());
    }

    private void aggiornaListaToDo() {
        if (board == null) {
            System.out.println("Errore: Nessuna bacheca selezionata.");
            return;
        }

        DefaultListModel<String> todoListModel = new DefaultListModel<>();

        for (ToDo todo : board.getListaToDo()) {
            String scadenzaText;

            if (todo.getDataScadenza() == null) {
                scadenzaText = "(Nessuna scadenza)";
            } else if (todo.verificaScadenzaOggi()) {
                scadenzaText = "\u001B[33m(SCADE OGGI: " + todo.getDataScadenza() + ")\u001B[0m";
            } else if (todo.getDataScadenza().isBefore(LocalDate.now())) {
                scadenzaText = "\u001B[31m(SCADUTO: " + todo.getDataScadenza() + ")\u001B[0m";
            } else {
                scadenzaText = "(Scadenza: " + todo.getDataScadenza() + ")";
            }

            todoListModel.addElement(todo.getTitoloToDo() + " " + scadenzaText);
        }

        todoList.setModel(todoListModel);
    }


}
