package org.example.gui;

import org.example.controller.AppController;
import org.example.dao.DatabaseDAO;
import org.example.implementazionepostgresdao.DatabaseImplementazionePostgresDAO;
import org.example.model.Bacheca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;


public class HomeFrame {
    private JFrame frame;
    private AppController controller;

    // Componenti già disegnati nella form
    private JPanel homePanel;
    private JList<String> boardList;
    private DefaultListModel<String> boardListModel;
    private JButton logoutButton;
    private JButton btnCreaBacheca;
    private JButton btnModificaBacheca;
    private JButton btnEliminaBacheca;
    private JButton visualizzaButton;
    private JScrollPane scrollPaneBoardList;
    private JButton btnCercaToDo;

    public HomeFrame(AppController controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frame = frameChiamante;


        frame.setContentPane(homePanel); // 🔥 Usa il pannello disegnato nella form
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);
        aggiornaListaBacheche();


        btnCercaToDo.setToolTipText("Cerca ToDo per testo o scadenza");// 🔹 Aggiorna la lista delle bacheche

        configuraEventi(); // 🔥 Collegamento degli eventi ai pulsanti

        frame.setVisible(true);
    }

    private void configuraEventi() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int conferma = JOptionPane.showConfirmDialog(
                        frame,
                        "Sei sicuro di voler uscire?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

                if (conferma == JOptionPane.YES_OPTION) {
                    // 🔥 Resetta l'utente corrente nel controller
                    controller.setUtenteCorrente(null);

                    // 🔹 Chiudi la HomeFrame
                    frame.dispose();

                    // 🔥 Riapri il LoginFrame con un nuovo Controller
                    try {
                        DatabaseDAO dao = new DatabaseImplementazionePostgresDAO();
                        LoginFrame loginFrame = new LoginFrame();
                        AppController newController = new AppController(dao, loginFrame);
                        loginFrame.setController(newController);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        btnCreaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo della nuova bacheca:");
                String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione della nuova bacheca:");

                if (titolo != null && !titolo.trim().isEmpty() && descrizione != null && !descrizione.trim().isEmpty()) {
                    controller.creaBacheca(titolo.trim(), descrizione.trim()); // 🔥 Chiamata diretta al Controller
                    aggiornaListaBacheche(); // 🔹 La View si aggiorna dopo la modifica
                }
            }
        });

        btnModificaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionata = boardList.getSelectedValue();
                if (selezionata != null) {
                    String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo della bacheca:");
                    String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione della bacheca:");

                    if (nuovoTitolo != null && nuovaDescrizione != null && !nuovoTitolo.trim().isEmpty()) {
                        controller.modificaBacheca(selezionata, nuovoTitolo.trim(), nuovaDescrizione.trim()); // 🔥 Chiamata al Controller
                        aggiornaListaBacheche();
                    }
                }
            }
        });

        btnEliminaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionata = boardList.getSelectedValue();
                if (selezionata != null) {
                    try {
                        controller.eliminaBacheca(selezionata); // 🔥 Il Controller gestisce la logica
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    aggiornaListaBacheche();
                }
            }
        });

        visualizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionata = boardList.getSelectedValue();
                if (selezionata != null) {
                    new ToDoGUI(controller, frame, selezionata); // 🔥 Apri direttamente la GUI
                    frame.setVisible(false); // Nascondi la finestra principale
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Seleziona una bacheca da visualizzare!");
                }
            }
        });

        btnCercaToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 🔹 Verifica se l'utente è loggato
                if (controller.getUtenteCorrente() == null) {
                    JOptionPane.showMessageDialog(frame, "Devi essere loggato per effettuare ricerche",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 🔥 Nasconde la finestra principale per aprire la ricerca
                frame.setVisible(false);
                try {
                    MostraRicercaDialog dialog = new MostraRicercaDialog(frame, controller);
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Errore durante l'apertura della ricerca: " + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
                frame.setVisible(true); // 🔹 Ripristina la visibilità della finestra principale
            }
        });

    }

    public void aggiornaListaBacheche() {
        boardListModel.clear();
        try {
            String username = controller.getUtenteCorrente().getUsername();
            System.out.println("Utente corrente: " + username); // Debug
            controller.inizializzaBachecheUtente(username);
            List<Bacheca> bacheche = controller.getListaBachecheAggiornate();
            System.out.println("Bacheche recuperate per " + username + ": " + bacheche);
            for (Bacheca board : bacheche) {
                boardListModel.addElement(board.getTitoloBacheca());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "❌ Errore nel recupero delle bacheche dal database!");
        }
    }


}
