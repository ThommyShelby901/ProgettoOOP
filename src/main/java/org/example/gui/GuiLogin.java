package org.example.gui;

import org.example.controller.Controller;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Questa classe {@code GuiLogin} gestisce l'interfaccia grafica per le operazioni di login e registrazione.
 * <p>
 * utilizza un {@link CardLayout} per alternare tra il pannello di login e quello di registrazione.
 */

public class GuiLogin {
    /** frame principale della finestra di login/registrazione*/
    private final JFrame frame;
    /** pannello principale che contiene gli altri due pannelli e che li mostra in base all'azione dell'utente*/
    private JPanel principale;
    /** pannello dedicato all'interfaccia di login, che viene mostrato sempre quando il programma viene eseguito*/
    JPanel loginPanel;
    /** pannello dedicato all'interfaccia di registrazione che viene mostrato quando viene cliccato {@link JButton registratiButton}*/
    JPanel registrazionePanel;
    /** Campo di testo per l'inserimento dello username nella schermata di login.*/
    private JTextField usernameField;
    /** Campo di testo per l'inserimento della password nella schermata di login.*/
    private JPasswordField passwordField;
    /** Bottone per avviare il processo di login*/
    private JButton loginButton;
    /** Bottone per passare alla schermata di registrazione*/
    private JButton registratiButton;
    /** campo di testo per l'inserimento dello username nella schermata di registrazione*/
    private JTextField usernameR;
    /** campo di testo per l'inserimento della password nella schermata di registrazione*/
    private JPasswordField passwordR;
    /** bottone per confermare la registrazione del nuovo utente*/
    private JButton confermaRegistrazioneButton;
    /** Bottone per passare dalla schermata di registrazione nuovamente a quella di login*/
    private JButton indietroButton;
    /** label associato a {@link #usernameField}*/
    JLabel user;
    /** label associato a {@link #passwordField}*/
    JLabel pass;
    /** label associayo a {@link #usernameR}*/
    JLabel user2;
    /** label associato a {@link #passwordR}*/
    JLabel pass2;

    /**
     * costruttore che imposta i listener per i bottoni di login, registrazione, conferma registrazione e indietro.
     * <p>
     * connette la gui al {@link Controller} per gestire le operazioni.
     * La particolarià di questa gui è che gestisce un CardLayout e che quindi mostra un unico JPanel contiene
     * due JPanel a sua volta che saranno mostrati in base alle richieste dell'utente in questo caso in base al login e alla registrazione
     * @param controller istanza del {@link Controller} che gestirà le azioni dell'utente
     */
    public GuiLogin(Controller controller) {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(principale);
        frame.pack();
        frame.setSize(800, 400);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (controller != null) {
                    controller.login(username, password);
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
                    controller.registrazione(username, password);
                }

                // Torna al login dopo la registrazione
                ((CardLayout)principale.getLayout()).show(principale, "loginPanel");

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

    /**
     * Rende visibile la finestra di login, usato dopo l'inizializzazione di questa gui, quando viene azionato il listener che ci
     * permette dalla registrazione di tronare indietro nel panel del login e quando facciamo logout per mostrare nuovamente la schermata
     */
    public void mostra() {
        frame.setVisible(true);
    }

    /**
     * chiude la finestra di login, usato quando il login ha successo
     */
    public void chiudiFinestra() {
        frame.dispose();
    }

    /**
     * mostra un messaggio all'utente, essendoci tante parti che richiedevano un avviso, optiamo per un metodo che chiamiamo
     * quando è necessario dare un feedback, errore o avviso.
     * @param message testo della stringa da visualizzare nel messaggio
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);  // Mostra un avviso all'utente
    }

    /**
     * resistuisce il frame principale di quest'interfaccia, passiamo il frame della schermata di login al costruttore di
     * {@link GuiHome}, invece di creare una nuova finestra riutilizza lo stesso frame
     * @return istanza associata a questa gui.
     */
    public JFrame getFrame() {
        return frame;
    }
}

