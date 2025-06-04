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

public class BachecaGUI {
        private JFrame frame;
        private JFrame frameChiamante;
        private AppController controller;
        private JPanel bachecaPanel;
        private JList<String> todoList;
        private JList<String> boardList;
        private DefaultListModel<String> boardListModel;
        private JComboBox<String> comboBoxUtenti;
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
            aggiornaComboBoxUtenti();

            frame = new JFrame("Gestione Bacheche");
            frame.setContentPane(bachecaPanel);
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            boardListModel = new DefaultListModel<>();
            boardList.setModel(boardListModel);
            aggiornaListaBacheche();
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

        private void aggiungiCondivisione() {
            String utenteSelezionato = (String) comboBoxUtenti.getSelectedItem();
            String titoloToDo = todoList.getSelectedValue();

            if (utenteSelezionato != null && titoloToDo != null) {
                ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, board.getTitoloBacheca());
                controller.aggiungiCondivisione(todo, utenteSelezionato);
                JOptionPane.showMessageDialog(frame, "To-Do condiviso con " + utenteSelezionato);
            } else {
                JOptionPane.showMessageDialog(frame, "Errore: Seleziona un To-Do e un utente!");
            }
        }

        private void rimuoviCondivisione() {
            String utenteSelezionato = (String) comboBoxUtenti.getSelectedItem();
            String titoloToDo = todoList.getSelectedValue();

            if (utenteSelezionato != null && titoloToDo != null) {
                ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, board.getTitoloBacheca());
                controller.rimuoviCondivisione(todo, utenteSelezionato);
                JOptionPane.showMessageDialog(frame, "Condivisione rimossa per " + utenteSelezionato);
            } else {
                JOptionPane.showMessageDialog(frame, "Errore: Seleziona un To-Do e un utente!");
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

    private void aggiornaListaToDo(String titoloBacheca) {
        if (titoloBacheca != null && !titoloBacheca.isEmpty()) {
            Utente utenteCorrente = controller.getUtenteCorrente();
            if (utenteCorrente != null) {
                List<ToDo> listaFiltrata = utenteCorrente.getToDoPerBacheca(titoloBacheca);
                String[] todoTitles = new String[listaFiltrata.size()];
                for (int i = 0; i < listaFiltrata.size(); i++) {
                    todoTitles[i] = listaFiltrata.get(i).getTitoloToDo();
                }
                todoList.setListData(todoTitles);
            }
        }
    }



        private void aggiornaComboBoxUtenti() {
            try {
                comboBoxUtenti.removeAllItems();
                for (String utente : controller.getListaUtenti()) {
                    comboBoxUtenti.addItem(utente);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
