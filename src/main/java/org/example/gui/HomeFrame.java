package org.example.gui;

import org.example.controller.AppController;
import org.example.model.Bacheca;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HomeFrame {
    private final JFrame frame;
    private final AppController controller;

    JScrollPane scrollPaneBoardList;
    private JPanel homePanel;
    private JList<String> boardList;
    private DefaultListModel<String> boardListModel;
    private JButton logoutButton;
    private JButton btnCreaBacheca;
    private JButton btnModificaBacheca;
    private JButton btnEliminaBacheca;
    private JButton visualizzaButton;
    private JButton btnCercaToDo;
    private static final Logger logger = Logger.getLogger(HomeFrame.class.getName());

    public HomeFrame(AppController controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frame = frameChiamante;

        configuraFinestra();
        configuraListener(); //  I listener sono spostati in un metodo separato
        aggiornaListaBacheche();

        frame.setVisible(true);
    }

    private void configuraFinestra() {
        frame.setContentPane(homePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);
    }

    private void configuraListener() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestisciLogout();
            }
        });

        btnCreaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    creaBacheca();
                } catch (SQLException ex) {
                    throw new IllegalStateException("Errore creazione bacheca", ex);
                }
            }
        });

        btnModificaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificaBacheca();
            }
        });


        btnEliminaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminaBacheca();
            }
        });

        visualizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizzaBacheca();
            }
        });

        btnCercaToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaToDo();
            }
        });
    }

    private void gestisciLogout() {
        int conferma = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler uscire?", "Logout", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            controller.handleLogout(); // Chiama il controller per gestire il logout
            frame.dispose(); // Chiude la HomeFrame corrente
        }
    }


    private void creaBacheca() throws SQLException {
        String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo della nuova bacheca:");
        String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione della nuova bacheca:");

        if (titolo != null && !titolo.trim().isEmpty() && descrizione != null && !descrizione.trim().isEmpty()) {
            controller.creaBacheca(titolo.trim(), descrizione.trim());
            aggiornaListaBacheche();
        }
    }

    private void modificaBacheca() {
        String selezionata = boardList.getSelectedValue();
        if (selezionata != null) {
            String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo della bacheca:");
            String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione della bacheca:");

            if (nuovoTitolo != null && nuovaDescrizione != null && !nuovoTitolo.trim().isEmpty()) {
                controller.modificaBacheca(selezionata, nuovoTitolo.trim(), nuovaDescrizione.trim());
                aggiornaListaBacheche();
            }
        }
    }


    private void eliminaBacheca() {
        String selezionata = boardList.getSelectedValue();
        if (selezionata != null) {
            try {
                controller.eliminaBacheca(selezionata);
                aggiornaListaBacheche();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Errore durante l'eliminazione: " + ex.getMessage());
                logger.log(Level.SEVERE, "Errore durante l'eliminazione della bacheca", ex);
            }

        }
    }

    private void visualizzaBacheca() {
        String selezionata = boardList.getSelectedValue();
        if (selezionata != null) {
            new ToDoGUI(controller, frame, selezionata);
            frame.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da visualizzare!");
        }
    }

    private void cercaToDo() {
        if (controller.getUtenteCorrente() == null) {
            JOptionPane.showMessageDialog(frame, "Devi essere loggato per effettuare ricerche", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.setVisible(false);
        try {
            MostraRicercaDialog dialog = new MostraRicercaDialog(controller, frame);
            dialog.mostra();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore durante la ricerca: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Errore durante la ricerca ToDo", ex);
        }
        frame.setVisible(true);
    }

    public void aggiornaListaBacheche() {
        boardListModel.clear();
        try {
            for (Bacheca board : controller.getListaBachecheAggiornate()) {
                boardListModel.addElement(board.getTitoloBacheca());
            }
        } catch (SQLException _) {
            JOptionPane.showMessageDialog(frame, "Errore nel recupero delle bacheche dal database!");
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}
