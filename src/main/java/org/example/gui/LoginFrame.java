package org.example.gui;

import org.example.controller.AppController;

import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class LoginFrame {
    private JFrame frame;
    private AppController controller;
    private JPanel loginPanel;
    private JPanel registrazionePanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registraButton;
    private JTextField usernameR;
    private JPasswordField passwordR;
    private JButton confermaRegistrazioneButton;
    private JButton indietroButton;


    public LoginFrame() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));


        loginPanel = new JPanel();
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        registraButton = new JButton("Registrati");
        loginPanel.add(registraButton);


        registrazionePanel = new JPanel();
        registrazionePanel.add(new JLabel("Username per registrarti:"));
        usernameR = new JTextField(15);
        registrazionePanel.add(usernameR);

        registrazionePanel.add(new JLabel("Password per registrarti:"));
        passwordR = new JPasswordField(15);
        registrazionePanel.add(passwordR);

        indietroButton = new JButton("Indietro");
        registrazionePanel.add(indietroButton);

        confermaRegistrazioneButton = new JButton("Conferma Registrazione");
        registrazionePanel.add(confermaRegistrazioneButton);

        registrazionePanel.setVisible(false);
        frame.add(loginPanel);
        frame.add(registrazionePanel);
        frame.setVisible(true);
    }


    public void setController(AppController controller) {
        this.controller = controller;

        loginButton.addActionListener(e -> controller.performLogin());

        registraButton.addActionListener(e -> {

            loginPanel.setVisible(false);
            registrazionePanel.setVisible(true);
        });

        confermaRegistrazioneButton.addActionListener(e -> {
            String username = usernameR.getText();
            String password = new String(passwordR.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Errore: Compila tutti i campi!");
                return;
            }

            controller.registraUtente(username, password);
        });

        indietroButton.addActionListener(e -> {

            registrazionePanel.setVisible(false); //  Nasconde la registrazione
            loginPanel.setVisible(true); //  Mostra il login
        });

    }



        // **Metodi per ottenere i dati**
    public String getUsername() {
        return usernameField.getText();  // Recupera il valore del campo username
    }

    public String getPassword() {
        return new String(passwordField.getPassword());  // Recupera il valore del campo password
    }

    public void chiudiFinestra() {
        frame.dispose();  // Chiude la finestra di Login
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);  // Mostra un avviso all'utente
    }

    public JButton getLoginButton() {
        return loginButton;  // **Ora il Controller può accedere al pulsante!**
    }

    public JFrame getFrame() {
        return frame;
    }
}

