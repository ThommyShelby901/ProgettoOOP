package org.example.gui;

import org.example.controller.AppController;
import org.example.model.StatoToDo;
import org.example.model.ToDo;


import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class ToDoGUI {
    private JFrame frame;
    private AppController controller;
    private String titoloBacheca;
    private JFrame frameChiamante;

    // Componenti definiti nella form
    private JPanel todoPanel;
    private JList<String> todoList;
    private DefaultListModel<String> todoListModel;
    private JButton btnAggiungiToDo, btnModificaToDo, btnEliminaToDo, btnIndietro;
    private JButton TRasferiscxiButton, btnSpostaToDo, btnVediCondivisioni;

    public ToDoGUI(AppController controller, JFrame frameChiamante, String titoloBacheca) {
    this.controller = controller;
    this.frameChiamante = frameChiamante;
    this.titoloBacheca = titoloBacheca;
    this.frame = new JFrame("Gestione To-Do - " + titoloBacheca);

    if (todoPanel == null) {
        System.out.println("❌ Errore: `todoPanel` è null! Creazione nuovo JPanel.");
        todoPanel = new JPanel();
    }

    frame.setContentPane(todoPanel);
    frame.setSize(600, 400);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocationRelativeTo(null);

    todoListModel = new DefaultListModel<>();
    todoList.setModel(todoListModel);
    aggiornaListaToDo(titoloBacheca);


    btnAggiungiToDo.addActionListener(e -> {
        String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo del To-Do:");
        String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione del To-Do:");
        String dataScadenza = JOptionPane.showInputDialog(frame, "Inserisci la data di scadenza (AAAA-MM-GG) (lascia vuoto se non applicabile):");
        String url = JOptionPane.showInputDialog(frame, "Inserisci un URL per il To-Do:");
        String statoString = JOptionPane.showInputDialog(frame, "Inserisci lo stato del To-Do (IN_CORSO, COMPLETATO, etc.):");

        titolo = (titolo != null) ? titolo.trim() : "";
        descrizione = (descrizione != null) ? descrizione.trim() : "";
        dataScadenza = (dataScadenza != null && !dataScadenza.trim().isEmpty()) ? dataScadenza.trim() : null;
        url = (url != null && !url.trim().isEmpty()) ? url.trim() : null;

        StatoToDo stato = null;
        if (statoString != null && !statoString.trim().isEmpty()) {
            try {
                stato = StatoToDo.valueOf(statoString.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, "❌ Stato non valido. Usa uno dei seguenti: IN_CORSO, COMPLETATO, etc.");
                return;
            }
        }

        if (!titolo.isEmpty() && !descrizione.isEmpty()) {
            try {
                controller.creaToDo(titolo, descrizione, dataScadenza, url, stato, titoloBacheca);
                aggiornaListaToDo(titoloBacheca);
                JOptionPane.showMessageDialog(frame, "✅ To-Do creato con successo!");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Titolo e descrizione non possono essere vuoti.");
        }
    });

    btnModificaToDo.addActionListener(e -> {
        String selezionato = todoList.getSelectedValue();
        if (selezionato != null) {
            ToDo todo = controller.getToDoPerTitoloEBoard(selezionato, titoloBacheca);
            if (todo != null) {
                String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo:", todo.getTitoloToDo());
                String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione:", todo.getDescrizioneToDo());
                String nuovaDataScadenza = JOptionPane.showInputDialog(frame, "Modifica la data di scadenza (AAAA-MM-GG):");

                if (nuovoTitolo != null && !nuovoTitolo.trim().isEmpty() &&
                        nuovaDescrizione != null && nuovaDataScadenza != null) {
                    controller.modificaToDo(todo, controller.getUtenteCorrente(), nuovoTitolo.trim(),
                            nuovaDescrizione.trim(), nuovaDataScadenza.trim(), null, null);
                    aggiornaListaToDo(titoloBacheca);
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

    btnEliminaToDo.addActionListener(e -> {
        String selezionato = todoList.getSelectedValue();
        if (selezionato != null) {
            try {
                controller.eliminaToDo(selezionato, titoloBacheca);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            aggiornaListaToDo(titoloBacheca);
            JOptionPane.showMessageDialog(frame, "✅ To-Do eliminato con successo!");
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Seleziona un To-Do da eliminare.");
        }
    });

    btnIndietro.addActionListener(e -> {
        frameChiamante.setVisible(true);
        frame.dispose();
    });

    TRasferiscxiButton.addActionListener(e -> {
        String selezionato = todoList.getSelectedValue();
        if (selezionato != null) {
            ToDo todo = controller.getToDoPerTitoloEBoard(selezionato, titoloBacheca);
            if (todo != null) {
                String nuovaBachecaNome = JOptionPane.showInputDialog(frame, "Inserisci il nome della nuova bacheca:");
                if (nuovaBachecaNome != null && !nuovaBachecaNome.trim().isEmpty()) {
                    try {
                        controller.trasferisciToDo(todo, nuovaBachecaNome);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    aggiornaListaToDo(titoloBacheca);
                    JOptionPane.showMessageDialog(frame, "✅ To-Do trasferito con successo!");
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Nome della bacheca non valido.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "❌ Errore: To-Do non trovato.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Seleziona un To-Do da trasferire.");
        }
    });

    btnSpostaToDo.addActionListener(e -> {
        String selezionato = todoList.getSelectedValue();
        int posizioneAttuale = todoList.getSelectedIndex();
        if (selezionato != null && posizioneAttuale >= 0) {
            String direzione = JOptionPane.showInputDialog(frame, "Sposta 'su' o 'giù'?");
            boolean spostaSu = "su".equalsIgnoreCase(direzione);
            int nuovaPosizione = spostaSu ? posizioneAttuale - 1 : posizioneAttuale + 1;

            System.out.println("➡️ Posizione attuale: " + posizioneAttuale + " | Nuova Posizione: " + nuovaPosizione);

            if (nuovaPosizione >= 0 && nuovaPosizione < todoListModel.getSize()) {
                try {
                    controller.spostaToDo(titoloBacheca, selezionato, nuovaPosizione);
                    aggiornaListaToDo(titoloBacheca);
                    JOptionPane.showMessageDialog(frame, "✅ To-Do spostato con successo!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "❌ Errore nello spostamento: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "❌ Posizione fuori dai limiti. Lista contiene: " + todoListModel.getSize() + " elementi.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Seleziona un To-Do da spostare.");
        }
    });

    btnVediCondivisioni.addActionListener(e -> {
        try {
            new BachecaGUI(controller, frame); // 🔥 Apriamo la GUI delle bacheche
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        frame.setVisible(false); // 🔥 Nascondiamo `ToDoGUI`
    });

    frame.setVisible(true);
    System.out.println("ToDoGUI è visibile? " + frame.isVisible());
}

public void aggiornaListaToDo(String titoloBacheca) {
    if (titoloBacheca == null || titoloBacheca.isEmpty()) {
        System.out.println("❌ Errore: Titolo bacheca nullo o vuoto.");
        return;
    }

    DefaultListModel<String> todoListModel = new DefaultListModel<>();
    List<ToDo> listaToDo = null;

    // 🔥 Recupera i dati dal database!
    try {
        listaToDo = controller.getTuttiToDo(titoloBacheca);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    System.out.println("✅ Numero di To-Do trovati: " + listaToDo.size());

    // 🔥 Controlla se ci sono dati
    for (ToDo todo : listaToDo) {
        System.out.println("📌 To-Do caricato: " + todo.getTitoloToDo()); // 🔥 Stampa i titoli dei `ToDo`
        String scadenzaText = todo.getDataScadenza() == null ? "(Nessuna scadenza)" : "(Scadenza: " + todo.getDataScadenza() + ")";
        todoListModel.addElement(todo.getTitoloToDo() + " " + scadenzaText);
    }

    todoList.setModel(todoListModel); // 🔥 Aggiorna la GUI con i dati dal database!
}




}


