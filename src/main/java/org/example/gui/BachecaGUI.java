package org.example.gui;


import org.example.controller.AppController;
import org.example.model.Bacheca;
import org.example.model.ToDo;
import org.example.model.Utente;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BachecaGUI {
    private JScrollPane scrollPaneUtenti;

    private JFrame frame;
        private JFrame frameChiamante;
        private AppController controller;
        private JPanel bachecaPanel;
        protected JList<String> todoList;
        protected JList<String> boardList;
        private DefaultListModel<String> boardListModel;
        private JList<String> listUtenti;
        private DefaultListModel<String> listUtentiModel;
        private JButton btnIndietro;
        private JButton btnAggiungiCondivisione;
        private JButton btnRimuoviCondivisione;
        private Bacheca board;

        public BachecaGUI(AppController controller, JFrame frameChiamante) throws SQLException {
            this.controller = controller;
            this.frameChiamante = frameChiamante;

            inizializzaUI(); // 🔥 Separiamo l'inizializzazione della UI
            configuraEventi(); // 🔥 Separiamo la configurazione degli eventi
            frame.setVisible(true);
        }

    private void inizializzaUI() throws SQLException {
        frame = new JFrame("Gestione Bacheche");
        frame.setContentPane(bachecaPanel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Associa i modelli alle JList già presenti nella form
        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);

        listUtentiModel = new DefaultListModel<>();
        listUtenti.setModel(listUtentiModel);

        listUtenti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        // Aggiornamento dei dati
        aggiornaListaUtenti();
        aggiornaListaBacheche();
    }




    private void aggiornaListaUtenti() {
        try {
            System.out.println("Utenti recuperati: " + controller.getListaUtenti());
            listUtentiModel.clear();
            for (String utente : controller.getListaUtenti()) {
                if (!utente.equals(controller.getUtenteCorrente().getUsername())) {
                    System.out.println("Aggiungo utente: " + utente);
                    listUtentiModel.addElement(utente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



        private void configuraEventi() {
            todoList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selezionaTodo();
                }
            });
            boardList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selezionaBacheca();
                }
            });
            btnIndietro.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tornaIndietro();
                }
            });
            btnAggiungiCondivisione.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    aggiungiCondivisione();
                }
            });
            btnRimuoviCondivisione.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rimuoviCondivisione();
                }
            });
        }

        private void selezionaTodo() {
            if (!todoList.getValueIsAdjusting()) {
                String titoloToDo = todoList.getSelectedValue();
                if (board != null && titoloToDo != null) {
                    controller.getToDoPerTitoloEBoard(titoloToDo, board.getTitoloBacheca());
                }
            }
        }

    private void selezionaBacheca() {
        if (!boardList.getValueIsAdjusting()) {
            String titoloSelezionato = boardList.getSelectedValue();
            board = controller.getBachecaByTitolo(titoloSelezionato);
            if (board != null) {
                aggiornaListaToDo(board.getTitoloBacheca());
            }
        }
    }

        private void tornaIndietro() {
            frameChiamante.setVisible(true);
            frame.dispose();
        }

    public JFrame getFrame() {
        return frame;
    }

    private void aggiungiCondivisione() {
        List<String> utentiSelezionati = listUtenti.getSelectedValuesList();
        String titoloToDo = todoList.getSelectedValue();

        if (utentiSelezionati.isEmpty() || titoloToDo == null || board == null) {
            JOptionPane.showMessageDialog(frame, "Errore: Seleziona un To-Do e almeno un utente!");
            return;
        }

        try {
            // Estrai il titolo pulito (senza la parte delle condivisioni se presente)
            String titoloPulito = titoloToDo.split(" \\[Condiviso con:")[0].trim();
            ToDo todo = controller.getToDoPerTitoloEBoard(titoloPulito, board.getTitoloBacheca());

            if (todo != null) {
                for (String utente : utentiSelezionati) {
                    controller.aggiungiCondivisione(todo, utente);
                }
                JOptionPane.showMessageDialog(frame, "To-Do condiviso con " + String.join(", ", utentiSelezionati));

                // Ricarica i dati dell'utente corrente
                controller.caricaDatiUtente(controller.getUtenteCorrente().getUsername());
                aggiornaListaToDo(board.getTitoloBacheca());

                // Mantieni la selezione del To-Do dopo l'aggiornamento
                for (int i = 0; i < todoList.getModel().getSize(); i++) {
                    String current = todoList.getModel().getElementAt(i);
                    if (current.startsWith(titoloPulito)) {
                        todoList.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rimuoviCondivisione() {
        List<String> utentiSelezionati = listUtenti.getSelectedValuesList();
        String titoloToDo = todoList.getSelectedValue();

        if (utentiSelezionati.isEmpty() || titoloToDo == null || board == null) {
            JOptionPane.showMessageDialog(frame, "Errore: Seleziona un To-Do e almeno un utente!");
            return;
        }

        try {
            String titoloPulito = titoloToDo.split(" \\[Condiviso con:")[0].trim();
            ToDo todo = controller.getToDoPerTitoloEBoard(titoloPulito, board.getTitoloBacheca());

            if (todo != null) {
                for (String utente : utentiSelezionati) {
                    controller.rimuoviCondivisione(todo, utente);
                }
                JOptionPane.showMessageDialog(frame, "Condivisioni rimosse per " + String.join(", ", utentiSelezionati));

                controller.caricaDatiUtente(controller.getUtenteCorrente().getUsername());
                aggiornaListaToDo(board.getTitoloBacheca());

                for (int i = 0; i < todoList.getModel().getSize(); i++) {
                    String current = todoList.getModel().getElementAt(i);
                    if (current.startsWith(titoloPulito)) {
                        todoList.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

        private void aggiornaListaBacheche() throws SQLException {
            boardListModel.clear();
            for (Bacheca board : controller.getListaBachecheAggiornate()) {
                String condivisioniText = board.getListaCondivisioni().isEmpty()
                        ? ""
                        : " [Condivisa con: " + String.join(", ", board.getListaCondivisioni()) + "]";
                boardListModel.addElement(board.getTitoloBacheca() + condivisioniText);
            }
        }

    public void aggiornaListaToDo(String titoloBacheca) {
        if (titoloBacheca != null && !titoloBacheca.isEmpty()) {
            Utente utenteCorrente = controller.getUtenteCorrente();
            if (utenteCorrente != null) {
                List<ToDo> listaFiltrata = utenteCorrente.getToDoPerBacheca(titoloBacheca);
                String[] todoTitles = new String[listaFiltrata.size()];
                for (int i = 0; i < listaFiltrata.size(); i++) {
                    ToDo todo = listaFiltrata.get(i);
                    // Mostra "Condiviso con:" solo se ci sono effettivamente condivisioni
                    if (todo.getCondivisoCon() != null && !todo.getCondivisoCon().isEmpty()) {
                        String condivisioniText = " [Condiviso con: " +
                                todo.getCondivisoCon().stream()
                                        .map(Utente::getUsername)
                                        .collect(Collectors.joining(", ")) + "]";
                        todoTitles[i] = todo.getTitoloToDo() + condivisioniText;
                    } else {
                        todoTitles[i] = todo.getTitoloToDo();
                    }
                }
                todoList.setListData(todoTitles);
            }
        }
    }




    }
