package gui;

import javax.swing.*;
import java.awt.*;

public class HomeFrame {
    // Istanza di JFrame che rappresenta la finestra principale
    private JFrame frame;
    private JPanel HomeFrame;
    private JPanel HomeFra;
    private JPanel HomeFram;

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
    private JButton btnVisualizzaBacheca;
    // Pulsante per il logout
    private JButton btnLogout;

    public HomeFrame(String username) {
        // Creazione della finestra principale
        frame = new JFrame("Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Inizializzazione dei pannelli
        HomeFrame = new JPanel();
        HomeFram = new JPanel();
        HomeFra = new JPanel();

        // Impostazione layout (puoi cambiarlo in base alle necessità)
        HomeFrame.setLayout(new BorderLayout());
        HomeFram.setLayout(new GridLayout(0, 1));
        HomeFra.setLayout(new FlowLayout());

        // Aggiunta di componenti ai pannelli
        welcomeLabel = new JLabel("Benvenuto, " + username + "!");
        HomeFrame.add(welcomeLabel, BorderLayout.NORTH);

        boardLabel = new JLabel("Bacheche");
        HomeFram.add(boardLabel);

        boardListModel = new DefaultListModel<>();
        boardList = new JList<>(boardListModel);
        JScrollPane scrollPane = new JScrollPane(boardList);
        HomeFram.add(scrollPane);

        // Creazione dei pulsanti e aggiunta a HomeFra
        btnCreaBacheca = new JButton("Crea Bacheca");
        btnModificaBacheca = new JButton("Modifica Bacheca");
        btnEliminaBacheca = new JButton("Elimina Bacheca");
        btnVisualizzaBacheca = new JButton("Visualizza Bacheca");

        HomeFra.add(btnCreaBacheca);
        HomeFra.add(btnModificaBacheca);
        HomeFra.add(btnEliminaBacheca);
        HomeFra.add(btnVisualizzaBacheca);

        // Pulsante logout
        btnLogout = new JButton("Logout");
        HomeFra.add(btnLogout);

        // Aggiunta dei pannelli alla finestra principale
        frame.add(HomeFrame, BorderLayout.NORTH);
        frame.add(HomeFram, BorderLayout.CENTER);
        frame.add(HomeFra, BorderLayout.SOUTH);

        // Rendere visibile la finestra
        frame.setVisible(true);


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
