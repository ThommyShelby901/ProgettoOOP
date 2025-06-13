package org.example.gui;

import org.example.controller.AppController;
import org.example.model.StatoToDo;
import org.example.model.ToDo;
import org.example.model.Utente;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class ToDoGUI {
    private JFrame frame;
    private AppController controller;
    private String titoloBacheca;
    private JFrame frameChiamante;


    // Componenti definiti nella form
    private JButton btnVisualizzaToDo;
    private JPanel todoPanel;
    private JScrollPane scroll;
    private JList<String> todoList;
    private DefaultListModel<String> todoListModel;
    private JButton btnAggiungiToDo, btnModificaToDo, btnEliminaToDo, btnIndietro;
    private JButton TrasferisciButton, btnSpostaToDo, btnVediCondivisioni;
    private JButton btnAggiungiImmagine;

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


        btnAggiungiToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo del To-Do:");
                String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione del To-Do:");
                String dataScadenza = JOptionPane.showInputDialog(frame, "Inserisci la data di scadenza (AAAA-MM-GG) :");
                String url = JOptionPane.showInputDialog(frame, "Inserisci un URL per il To-Do:");
                String statoString = JOptionPane.showInputDialog(frame, "Inserisci lo stato del To-Do (lascia vuoto per NON COMPLETATO, oppure scrivi c per COMPLETATO):");

                titolo = (titolo != null) ? titolo.trim() : "";
                descrizione = (descrizione != null) ? descrizione.trim() : "";
                dataScadenza = (dataScadenza != null && !dataScadenza.trim().isEmpty()) ? dataScadenza.trim() : null;
                url = (url != null && !url.trim().isEmpty()) ? url.trim() : null;

                StatoToDo stato = StatoToDo.NONCOMPLETATO; // Valore di default
                if (statoString != null && !statoString.trim().isEmpty()) {
                    String statoInput = statoString.trim().toLowerCase(); // Converti in minuscolo per gestire tutti i casi
                    if (statoInput.equals("c") || statoInput.equals("completato")) {
                        stato = StatoToDo.COMPLETATO;
                    }
                }

                // ✅ Semplice selezione del colore con un `JComboBox`
                String[] colori = {"Rosso", "Verde", "Blu", "Giallo", "Grigio"};
                Color[] valoriColori = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.GRAY, new Color(128, 0, 128)};
                JComboBox<String> colorBox = new JComboBox<>(colori);
                JOptionPane.showMessageDialog(frame, colorBox, "Scegli un colore", JOptionPane.QUESTION_MESSAGE);

                Color coloreSfondo = valoriColori[colorBox.getSelectedIndex()]; // ✅ Assegna il colore selezionato

                if (!titolo.isEmpty() && !descrizione.isEmpty()) {
                    try {
                        controller.creaToDo(titolo, descrizione, dataScadenza, url, stato, titoloBacheca, coloreSfondo);
                        aggiornaListaToDo(titoloBacheca);
                        JOptionPane.showMessageDialog(frame, "✅ To-Do creato con successo!");
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Titolo e descrizione non possono essere vuoti.");
                }
            }
        });


        btnModificaToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionato = todoList.getSelectedValue();
                if (selezionato != null) {
                    String titoloToDo = selezionato.replaceAll("^\\d+\\.\\s*([^(]*).*$", "$1").trim();
                    ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, titoloBacheca);

                    if (todo != null) {
                        String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo:", todo.getTitoloToDo());
                        String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione:", todo.getDescrizioneToDo());
                        String nuovaDataScadenza = JOptionPane.showInputDialog(frame, "Modifica la data di scadenza (AAAA-MM-GG):",
                                todo.getDataScadenza() != null ? todo.getDataScadenza() : "");

                        if (nuovoTitolo != null && !nuovoTitolo.trim().isEmpty() &&
                                nuovaDescrizione != null && !nuovaDescrizione.trim().isEmpty()) {
                            try {
                                // Aggiungi questa riga per forzare l'aggiornamento dello stato
                                StatoToDo nuovoStato = (JOptionPane.showConfirmDialog(frame, "Il To-Do è completato?",
                                        "Stato", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                        ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;

                                controller.modificaToDo(todo, controller.getUtenteCorrente(), nuovoTitolo.trim(),
                                        nuovaDescrizione.trim(),
                                        nuovaDataScadenza != null && !nuovaDataScadenza.trim().isEmpty() ? nuovaDataScadenza.trim() : null,
                                        null, nuovoStato);

                                // Forza un refresh completo della lista
                                aggiornaListaToDo(titoloBacheca);
                                JOptionPane.showMessageDialog(frame, "✅ To-Do modificato con successo!");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(frame, "❌ Errore durante la modifica: " + ex.getMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Titolo e descrizione non possono essere vuoti.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "To-Do non trovato!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Seleziona un To-Do da modificare.");
                }
            }
        });

        btnEliminaToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionato = todoList.getSelectedValue();
                if (selezionato != null) {
                    // Estrai il titolo esattamente come fatto negli altri metodi
                    String titoloToDo = selezionato.replaceAll("^\\d+\\.\\s*([^(]*).*$", "$1").trim();

                    try {
                        controller.eliminaToDo(titoloToDo, titoloBacheca);
                        aggiornaListaToDo(titoloBacheca); // Aggiorna la lista dopo l'eliminazione
                        JOptionPane.showMessageDialog(frame, "✅ To-Do eliminato con successo!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "❌ Errore durante l'eliminazione: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Seleziona un To-Do da eliminare.");
                }
            }
        });

    btnIndietro.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            frameChiamante.setVisible(true);
            frame.dispose();
        }
    });

        TrasferisciButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionato = todoList.getSelectedValue();
                if (selezionato != null) {
                    String titoloToDo = selezionato.replaceAll("^\\d+\\.\\s*([^(]*).*$", "$1").trim();
                    ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, titoloBacheca);

                    if (todo != null) {
                        // 🔥 Ora rimuoviamo il controllo sull'autore perché non è necessario
                        String nuovaBachecaNome = JOptionPane.showInputDialog(frame, "Inserisci il nome della nuova bacheca:");
                        if (nuovaBachecaNome != null && !nuovaBachecaNome.trim().isEmpty()) {
                            try {
                                controller.trasferisciToDo(todo, nuovaBachecaNome.trim());
                                aggiornaListaToDo(titoloBacheca);
                                JOptionPane.showMessageDialog(frame, "✅ To-Do trasferito con successo!");
                            } catch (IllegalArgumentException | IllegalStateException ex) {
                                JOptionPane.showMessageDialog(frame, "❌ Errore: " + ex.getMessage());
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "❌ Errore durante il trasferimento: " + ex.getMessage());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "❌ Errore: To-Do non trovato nella bacheca corrente");
                    }
                }
            }
        });

        btnSpostaToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String selezionato = todoList.getSelectedValue();
                if (selezionato != null) {
                    // Rimuovo il numero e il punto (esempio "1. Titolo (Scadenza: 2025-06-02)")
                    selezionato = selezionato.substring(selezionato.indexOf(".") + 2);

                    // Rimuovo la parte della scadenza tra parentesi (esempio "(Scadenza: 2025-06-02)")
                    int parentesi = selezionato.indexOf(" (");
                    if (parentesi != -1) {
                        selezionato = selezionato.substring(0, parentesi);
                    }

                    selezionato = selezionato.trim(); // Tolgo eventuali spazi

                    String input = JOptionPane.showInputDialog(frame, "Inserisci la nuova posizione (1 - " + todoListModel.getSize() + "):");
                    try {
                        int posizioneUtente = Integer.parseInt(input);

                        if (posizioneUtente >= 1 && posizioneUtente <= todoListModel.getSize()) {
                            int nuovaPosizione = posizioneUtente - 1;  // Indice 0-based

                            try {
                                controller.spostaToDo(titoloBacheca, selezionato, nuovaPosizione);
                                aggiornaListaToDo(titoloBacheca); // 🔄 aggiorna graficamente
                                JOptionPane.showMessageDialog(frame, "✅ To-Do spostato con successo!");
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "❌ Errore nello spostamento: " + ex.getMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "❌ Posizione fuori dai limiti.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "❌ Inserisci un numero valido.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Seleziona un To-Do da spostare.");
                }
            }
        });

        btnVediCondivisioni.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apriGestioneCondivisioniBacheca();
            }
        });

        btnVisualizzaToDo.addActionListener(e -> {
            String selezionato = todoList.getSelectedValue();
            if (selezionato != null) {
                String titoloToDo = selezionato.replaceAll("^\\d+\\.\\s*([^(]*).*$", "$1").trim();
                ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, titoloBacheca);
                if (todo != null) {
                    new VisualizzaToDoDialog(frame, todo, controller);
                }
            }
        });



        btnAggiungiImmagine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionato = todoList.getSelectedValue();
                if (selezionato != null) {
                    String titoloToDo = selezionato.replaceAll("^\\d+\\.\\s*([^(]*).*$", "$1").trim();
                    ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, titoloBacheca);

                    if (todo != null) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Seleziona un'immagine");
                        fileChooser.setFileFilter(new FileNameExtensionFilter("Immagini", "jpg", "jpeg", "png", "gif"));

                        int result = fileChooser.showOpenDialog(frame);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            try {
                                String percorsoImmagine = fileChooser.getSelectedFile().getAbsolutePath();
                                controller.aggiungiImmagineAToDo(todo, percorsoImmagine);
                                JOptionPane.showMessageDialog(frame, "Immagine aggiunta con successo!");
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "Errore durante il salvataggio: " + ex.getMessage());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "To-Do non trovato!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Seleziona un To-Do a cui aggiungere l'immagine.");
                }
            }
        });




        frame.setVisible(true);
    System.out.println("ToDoGUI è visibile? " + frame.isVisible());
}

    private void apriGestioneCondivisioniBacheca() {
        try {
            // Chiudi la finestra corrente temporaneamente
            frame.setVisible(false);

            // Apri la BachecaGUI
            BachecaGUI bachecaGUI = new BachecaGUI(controller, frame);

            // Seleziona automaticamente la bacheca corrente
            bachecaGUI.boardList.setSelectedValue(titoloBacheca, true);
            bachecaGUI.aggiornaListaToDo(titoloBacheca);

            // Quando la BachecaGUI si chiude
            bachecaGUI.getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    frame.setVisible(true);
                    aggiornaListaToDo(titoloBacheca);
                }
            });

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Errore: " + ex.getMessage());
            frame.setVisible(true);
        }
    }

    public void aggiornaListaToDo(String titoloBacheca) {
        todoListModel.clear();

        List<ToDo> listaToDo = null;
        try {
            listaToDo = controller.getTuttiToDo(titoloBacheca);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < listaToDo.size(); i++) {
            ToDo todo = listaToDo.get(i);
            String scadenzaText = todo.getDataScadenza() == null ? "(Nessuna scadenza)" : "(Scadenza: " + todo.getDataScadenza() + ")";
            todoListModel.addElement((i+1) + ". " + todo.getTitoloToDo() + " " + scadenzaText);

            frame.revalidate();
            frame.repaint();
        }
    }




}


