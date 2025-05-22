package gui;

import javax.swing.*;
import java.awt.*;

public class HomeFrame {
    // Istanza di JFrame che rappresenta la finestra principale
    private JFrame frame;
    private JPanel HomeFrame;
    private JPanel HomeFram;
    private JPanel HomeFra;
    private JButton btnVisualizzaBacheca;

    // Componenti per l'interfaccia
    private JLabel welcomeLabel;

    // Componenti per la visualizzazione delle bacheche
    private JLabel boardLabel;
    private JList<String> boardList;
    private DefaultListModel<String> boardListModel;

    // Pulsanti per le operazioni sulle bacheche
    private JButton btnCreaBacheca;
    private JButton btnModificaBacheca;
    private JButton btnEliminaBacheca;


    // Pulsante per il logout
    private JButton btnLogout;

    // Costruttore: riceve il nome dell'utente per personalizzare il messaggio di benvenuto
    public HomeFrame(String username) {
        // Creazione del JFrame interno
        frame = new JFrame("Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);// Dimensione impostata sul frame, ma non sui componenti interni
        frame.setLocationRelativeTo(null);

        // Pannello principale con GridLayout (disposizione verticale: una colonna, tante righe quante necessarie)
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        // Messaggio di benvenuto
        welcomeLabel = new JLabel("Benvenuto, " + username + "!");
        mainPanel.add(welcomeLabel);

        // Etichetta per la sezione delle bacheche
        boardLabel = new JLabel("Bacheche");
        mainPanel.add(boardLabel);

        // Lista per visualizzare i nomi delle bacheche
        boardListModel = new DefaultListModel<>();
        boardList = new JList<>(boardListModel);
        JScrollPane scrollPane = new JScrollPane(boardList);
        // Non impostiamo una dimensione preferita sullo scrollPane
        mainPanel.add(scrollPane);

        // Pannello dei pulsanti per le operazioni sulle bacheche
        JPanel buttonPanel = new JPanel(); // Layout di default (FlowLayout)
        btnCreaBacheca = new JButton("Crea Bacheca");
        btnModificaBacheca = new JButton("Modifica Bacheca");
        btnEliminaBacheca = new JButton("Elimina Bacheca");
        btnVisualizzaBacheca = new JButton("Visualizza Bacheca");
        buttonPanel.add(btnCreaBacheca);
        buttonPanel.add(btnModificaBacheca);
        buttonPanel.add(btnEliminaBacheca);
        buttonPanel.add(btnVisualizzaBacheca);
        mainPanel.add(buttonPanel);

        // Pulsante per il logout
        btnLogout = new JButton("Logout");
        mainPanel.add(btnLogout);

        // Aggiungiamo il pannello principale al frame
        frame.add(mainPanel);


    }

    public JButton getBtnVisualizzaBacheca() {
        return btnVisualizzaBacheca;
    }

    // Metodo wrapper per rendere visibile la finestra
    public void showFrame() {
        frame.setVisible(true);
    }

    // Metodo wrapper per chiudere il frame
    public void dispose() {
        frame.dispose();
    }

    // Metodo wrapper per impostare la visibilità
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    // Getter per il JFrame interno (utile per ancorare eventuali dialog)
    public JFrame getFrame() {
        return frame;
    }

    // Getters per i pulsanti e per il modello della lista
    public JButton getBtnCreaBacheca() {
        return btnCreaBacheca;
    }

    public JButton getBtnModificaBacheca() {
        return btnModificaBacheca;
    }

    public JButton getBtnEliminaBacheca() {
        return btnEliminaBacheca;
    }

    public JButton getBtnLogout() {
        return btnLogout;
    }

    public DefaultListModel<String> getBoardListModel() {
        return boardListModel;
    }
    public JList<String> getBoardList() {
        return boardList;
    }

    // Metodo per mostrare messaggi all'utente
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}
