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
    JScrollPane scrollPaneUtenti;
    private final JFrame frame;
    final JFrame frameChiamante;
    private final AppController controller;
    private JPanel bachecaPanel;
    private JList<String> todoList;
    private JList<String> boardList;
    private final DefaultListModel<String> boardListModel;
    private JList<String> listUtenti;
    private final DefaultListModel<String> listUtentiModel;
    private JButton btnIndietro;
    private JButton btnAggiungiCondivisione;
    private JButton btnRimuoviCondivisione;
    private Bacheca b;

    public BachecaGUI(AppController controller, JFrame frameChiamante) throws SQLException {
        this.controller = controller;
        this.frameChiamante = frameChiamante;
        frame = new JFrame("Gestione Bacheche");
        frame.setContentPane(bachecaPanel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);

        listUtentiModel = new DefaultListModel<>();
        listUtenti.setModel(listUtentiModel);
        listUtenti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        aggiornaListaUtenti();
        aggiornaListaBacheche();

        // 🔥 Gestione eventi direttamente nel costruttore
        todoList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!todoList.getValueIsAdjusting()) {
                    String titoloToDo = todoList.getSelectedValue();
                    if (b != null && titoloToDo != null) {
                        controller.getToDoPerTitoloEBoard(titoloToDo, b.getTitoloBacheca());
                    }
                }
            }
        });

        boardList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!boardList.getValueIsAdjusting()) {
                    String titoloSelezionato = boardList.getSelectedValue();
                    b = controller.getBachecaByTitolo(titoloSelezionato);
                    if (b != null) {
                        aggiornaListaToDo(b.getTitoloBacheca());
                    }
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

        btnAggiungiCondivisione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestisciCondivisione(true);
            }
        });

        btnRimuoviCondivisione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestisciCondivisione(false);
            }
        });


        frame.setVisible(true);
    }

    private void gestisciCondivisione(boolean aggiungi) {
        List<String> utentiSelezionati = listUtenti.getSelectedValuesList();
        String titoloToDo = todoList.getSelectedValue();

        if (utentiSelezionati.isEmpty() || titoloToDo == null || b == null) {
            JOptionPane.showMessageDialog(frame, "Errore: Seleziona un To-Do e almeno un utente!");
            return;
        }

        try {
            String titoloPulito = titoloToDo.split(" \\[Condiviso con:")[0].trim();
            ToDo todo = controller.getToDoPerTitoloEBoard(titoloPulito, b.getTitoloBacheca());

            if (todo != null) {
                for (String utente : utentiSelezionati) {
                    if (aggiungi) {
                        controller.aggiungiCondivisione(todo, utente);
                    } else {
                        controller.rimuoviCondivisione(todo, utente);
                    }
                }
                JOptionPane.showMessageDialog(frame, (aggiungi ? "To-Do condiviso con " : "Condivisioni rimosse per ") + String.join(", ", utentiSelezionati));
                controller.caricaDatiUtente();
                aggiornaListaToDo(b.getTitoloBacheca());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void aggiornaListaUtenti() {
        try {
            listUtentiModel.clear();
            for (String utente : controller.getListaUtenti()) {
                if (!utente.equals(controller.getUtenteCorrente().getUsername())) {
                    listUtentiModel.addElement(utente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void aggiornaListaBacheche() throws SQLException {
        boardListModel.clear();
        for (Bacheca bacheca : controller.getListaBachecheAggiornate()) {
            String condivisioniText = bacheca.getListaCondivisioni().isEmpty()
                    ? ""
                    : " [Condivisa con: " + String.join(", ", bacheca.getListaCondivisioni()) + "]";
            boardListModel.addElement(bacheca.getTitoloBacheca() + condivisioniText);
        }
    }


    public void aggiornaListaToDo(String titoloBacheca) {
        if (titoloBacheca != null && !titoloBacheca.isEmpty()) {
            Utente utenteCorrente = controller.getUtenteCorrente();
            if (utenteCorrente != null) {
                List<ToDo> listaFiltrata = utenteCorrente.getToDoPerBacheca(titoloBacheca);
                String[] todoTitles = listaFiltrata.stream()
                        .map(todo -> todo.getCondivisoCon().isEmpty() ? todo.getTitoloToDo() :
                                todo.getTitoloToDo() + " [Condiviso con: " +
                                        todo.getCondivisoCon().stream().map(Utente::getUsername).collect(Collectors.joining(", ")) + "]")
                        .toArray(String[]::new);
                todoList.setListData(todoTitles);
            }
        }
    }
    public JFrame getFrame() {
        return frame;
    }
    public JList<String> getBoardList() {
        return boardList;
    }
}
