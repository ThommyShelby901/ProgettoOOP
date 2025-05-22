package gui;

import model.Bacheca;
import javax.swing.*;
import java.awt.*;

public class LoginFrame {
    // Variabili per il frame e i componenti della GUI
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // I due pannelli della tua interfaccia
    private JPanel panel; // ad esempio, per controlli relativi all'username
    private JPanel login; // ad esempio, per password e pulsante

    public LoginFrame() {
        // Crea il frame
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inizializza i pannelli
        panel = new JPanel();  // Layout di default: FlowLayout
        login = new JPanel();  // Layout di default: FlowLayout

        // Popola il primo pannello (panel) con lo username
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        panel.add(usernameField);

        // Popola il secondo pannello (login) con password e pulsante
        login.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        login.add(passwordField);

        // Il pulsante di login
        loginButton = new JButton("Login");
        login.add(loginButton);
        // Se necessario, aggiungi un componente vuoto come riempitivo
        // login.add(new JPanel());

        // Ora, invece di creare un mainPanel separato, usiamo direttamente il content pane del frame.
        Container contentPane = frame.getContentPane();
        // Imposta il layout del content pane in modo da disporre i due pannelli verticalmente.
        // In questo esempio usiamo GridLayout con 2 righe e 1 colonna con un po' di spaziatura
        contentPane.setLayout(new GridLayout(2, 1, 5, 5));

        // Aggiungi i due pannelli al content pane
        contentPane.add(panel);
        contentPane.add(login);

        // Ora usa pack() per fare in modo che il frame si adatti alle dimensioni dei componenti
        frame.pack();
        frame.setLocationRelativeTo(null);  // Centra il frame sullo schermo
        frame.setVisible(true);
    }

    // Getter per l'username
    public String getUsername() {
        return usernameField.getText();
    }

    // Getter per la password
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // Getter per il pulsante di login
    public JButton getLoginButton() {
        return loginButton;
    }

    // Metodo per mostrare messaggi all'utente
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    // Metodo wrapper per rendere visibile il frame
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    // Metodo wrapper per chiudere il frame
    public void dispose() {
        frame.dispose();
    }

    // Getter per il frame stesso
    public JFrame getFrame() {
        return frame;
    }

}