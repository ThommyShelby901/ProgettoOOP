package gui;

import javax.swing.*;
import java.awt.*;

public class ToDoFrame {
    private JFrame frame;
    private DefaultListModel<String> todoListModel;
    private JList<String> todoList;
    private JButton btnCrea, btnModifica, btnElimina, btnTrasferisci, btnSposta, btnCerca, btnBack;
    private JPanel mainpanel;
    private JLabel label;
    private JScrollPane scrollPane;

    public ToDoFrame(String boardTitle) {
        frame = new JFrame("ToDo - " + boardTitle);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Pannello principale con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        // Zona NORTH: etichetta con il nome della bacheca corrente
        JLabel label = new JLabel("ToDo per la bacheca: " + boardTitle);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(label, BorderLayout.NORTH);

        // Zona CENTER: JList dei ToDo in uno ScrollPane
        todoListModel = new DefaultListModel<>();
        todoList = new JList<>(todoListModel);
        JScrollPane scrollPane = new JScrollPane(todoList);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Zona SOUTH: pannello dei pulsanti (layout a griglia 2x4)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        btnCrea = new JButton("Crea ToDo");
        btnModifica = new JButton("Modifica ToDo");
        btnElimina = new JButton("Elimina ToDo");
        btnTrasferisci = new JButton("Trasferisci");
        btnSposta = new JButton("Sposta");
        btnCerca = new JButton("Cerca ToDo");
        btnBack = new JButton("Back");

        buttonPanel.add(btnCrea);
        buttonPanel.add(btnModifica);
        buttonPanel.add(btnElimina);
        buttonPanel.add(btnTrasferisci);
        buttonPanel.add(btnSposta);
        buttonPanel.add(btnCerca);
        buttonPanel.add(btnBack);
        // Aggiungiamo una cella vuota per completare la griglia (2x4 = 8 celle)
        buttonPanel.add(new JLabel(""));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void showFrame() {
        frame.setVisible(true);
    }

    public void dispose() {
        frame.dispose();
    }

    // Getter per il modello e la JList
    public DefaultListModel<String> getTodoListModel() {
        return todoListModel;
    }

    public JList<String> getTodoList() {
        return todoList;
    }

    // Getter per i pulsanti
    public JButton getBtnCrea() {
        return btnCrea;
    }

    public JButton getBtnModifica() {
        return btnModifica;
    }

    public JButton getBtnElimina() {
        return btnElimina;
    }

    public JButton getBtnTrasferisci() {
        return btnTrasferisci;
    }

    public JButton getBtnSposta() {
        return btnSposta;
    }

    public JButton getBtnCerca() {
        return btnCerca;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public JFrame getFrame() {
        return frame;
    }
}
