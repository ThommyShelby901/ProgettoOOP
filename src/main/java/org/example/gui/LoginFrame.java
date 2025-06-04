package org.example.gui;

import org.example.controller.AppController;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame {
    private JFrame frame;
    private AppController controller;
    private JPanel principale;
    private JPanel loginPanel;
    private JPanel registrazionePanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registratiButton;
    private JTextField usernameR;
    private JPasswordField passwordR;
    private JButton confermaRegistrazioneButton;
    private JButton indietroButton;


    public LoginFrame() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(principale);
        frame.pack();
        frame.setSize(800, 400);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) {
                    controller.handleLogin(usernameField.getText(), new String(passwordField.getPassword()));
                }
            }
        });

        registratiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout)principale.getLayout()).show(principale, "registrazionePanel");
            }
        });

        confermaRegistrazioneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameR.getText();
                String password = new String(passwordR.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    showMessage("Errore: Compila tutti i campi!");
                    return;
                }
                if (controller != null) {
                    controller.handleRegistration(username, password);
                }

                // Torna al login DOPO la registrazione
                ((CardLayout)principale.getLayout()).show(principale, "loginPanel");

                // Pulisce i campi
                usernameR.setText("");
                passwordR.setText("");
            }
        });


        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout)principale.getLayout()).show(principale, "loginPanel");
            }
        });
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
    public void setController(AppController controller) {
        this.controller = controller;
    }
}

