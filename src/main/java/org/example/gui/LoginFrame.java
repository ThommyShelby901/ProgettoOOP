package org.example.gui;

import org.example.controller.AppController;

import javax.swing.*;

public class LoginFrame {
    private JFrame frame;
    private AppController controller;
    private JPanel panel;
    private JTextField usernameField;  // Ora è un campo di classe!
    private JPasswordField passwordField;
    private JButton loginButton; // Ora è un campo di classe!

    public LoginFrame() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);  // Rimosso `JTextField` locale, ora è un campo della classe
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);  // Stesso discorso per il campo password
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(loginButton);


        frame.add(panel);
        frame.setVisible(true);
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

    public void setController(AppController controller) { // 🔥 Metodo per collegare il Controller dopo la creazione
        this.controller = controller;
        loginButton.addActionListener(e -> {
            System.out.println("Bottone login premuto!"); // 🔥 Verifica se il bottone risponde
            controller.performLogin();
        });

    }
}
