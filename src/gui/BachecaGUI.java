package gui;

import javax.swing.*;
import java.awt.*;

public class BachecaGUI {
    private JFrame frame;
    private DefaultListModel<String> todoListModel;
    private JList<String> todoList;

    // Lista utenti condivisi e relativo modello
    private DefaultListModel<String> condivisoListModel;
    private JList<String> condivisoList;

    private JButton btnCrea;
    private JButton btnModifica;
    private JButton btnElimina;
    private JButton btnTrasferisci;
    private JButton btnSposta;
    private JButton btnCerca;
    private JButton btnBack;

    // Bottoni per condivisione
    private JButton btnAggiungiCondivisione;
    private JButton btnRimuoviCondivisione;

    private JLabel labelTitoloBacheca;

    public BachecaGUI(String titoloBacheca) {
        frame = new JFrame("Bacheca: " + titoloBacheca);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        frame.setContentPane(mainPanel);

        labelTitoloBacheca = new JLabel("Bacheca: " + titoloBacheca, SwingConstants.CENTER);
        labelTitoloBacheca.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(labelTitoloBacheca, BorderLayout.NORTH);

        // Panel centrale diviso: ToDo a sinistra, Condivisioni a destra
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Lista ToDo
        todoListModel = new DefaultListModel<>();
        todoList = new JList<>(todoListModel);
        JScrollPane todoScroll = new JScrollPane(todoList);
        todoScroll.setBorder(BorderFactory.createTitledBorder("ToDo"));
        centerPanel.add(todoScroll);

        // Lista condivisioni
        condivisoListModel = new DefaultListModel<>();
        condivisoList = new JList<>(condivisoListModel);
        JScrollPane condivisoScroll = new JScrollPane(condivisoList);
        condivisoScroll.setBorder(BorderFactory.createTitledBorder("Condivisioni"));
        centerPanel.add(condivisoScroll);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Pannello bottoni in basso
        JPanel buttonPanel = new JPanel(new GridLayout(3, 4, 8, 8));

        btnCrea = new JButton("Crea");
        btnModifica = new JButton("Modifica");
        btnElimina = new JButton("Elimina");
        btnTrasferisci = new JButton("Trasferisci");
        btnSposta = new JButton("Sposta");
        btnCerca = new JButton("Cerca");
        btnBack = new JButton("Indietro");

        btnAggiungiCondivisione = new JButton("Aggiungi Condivisione");
        btnRimuoviCondivisione = new JButton("Rimuovi Condivisione");

        buttonPanel.add(btnCrea);
        buttonPanel.add(btnModifica);
        buttonPanel.add(btnElimina);
        buttonPanel.add(btnTrasferisci);
        buttonPanel.add(btnSposta);
        buttonPanel.add(btnCerca);
        buttonPanel.add(btnBack);

        // Spazi vuoti per allineare i nuovi bottoni
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));

        buttonPanel.add(btnAggiungiCondivisione);
        buttonPanel.add(btnRimuoviCondivisione);
        buttonPanel.add(new JLabel(""));

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getter bottoni principali
    public JButton getBtnCrea() { return btnCrea; }
    public JButton getBtnModifica() { return btnModifica; }
    public JButton getBtnElimina() { return btnElimina; }
    public JButton getBtnTrasferisci() { return btnTrasferisci; }
    public JButton getBtnSposta() { return btnSposta; }
    public JButton getBtnCerca() { return btnCerca; }
    public JButton getBtnBack() { return btnBack; }

    // Getter nuovi bottoni condivisioni
    public JButton getBtnAggiungiCondivisione() { return btnAggiungiCondivisione; }
    public JButton getBtnRimuoviCondivisione() { return btnRimuoviCondivisione; }

    // Getter lista ToDo e modello
    public JList<String> getTodoList() { return todoList; }
    public DefaultListModel<String> getTodoListModel() { return todoListModel; }

    // Getter lista condivisioni e modello
    public JList<String> getCondivisoList() { return condivisoList; }
    public DefaultListModel<String> getCondivisoListModel() { return condivisoListModel; }

    public JFrame getFrame() { return frame; }

    public void show() { frame.setVisible(true); }
    public void dispose() { frame.dispose(); }
}
