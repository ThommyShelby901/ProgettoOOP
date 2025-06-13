package org.example.gui;

import org.example.controller.AppController;
import org.example.model.ToDo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MostraRicercaDialog {
    private final JDialog dialog;
    private final AppController controller;
    private static final String MESSAGGIO_ERRORE = "Errore";


    //  Componenti UI definiti nel form grafico (GUI Builder)
    private JPanel pnlRicerca;
    private JTextField txtTitolo;
    private JTextField txtData;
    private JButton btnCercaTitolo;
    private JButton btnCercaData;
    private JButton btnScadenzeOggi;
    private JList<String> lstRisultati;
    private final DefaultListModel<String> modelRisultati;
    JScrollPane scrollPane;
    JLabel lblTitolo;
    JLabel lblData;

    public MostraRicercaDialog(AppController controller, JFrame parent) {
        this.controller = controller;
        dialog = new JDialog(parent, "Ricerca ToDo", true);

        modelRisultati = new DefaultListModel<>();
        lstRisultati.setModel(modelRisultati);
        // 🔥 Usa il pannello già disegnato nel GUI Builder
        dialog.setContentPane(pnlRicerca);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(parent);

        configuraEventi();
    }

    private void configuraEventi() {
        btnCercaTitolo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaPerTitolo();
            }
        });

        btnCercaData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaPerData();
            }
        });

        btnScadenzeOggi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraScadenzeOggi();
            }
        });
    }

    private void cercaPerTitolo() {
        String titolo = txtTitolo.getText().trim();
        if (titolo.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Inserisci un titolo", MESSAGGIO_ERRORE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<ToDo> risultati = controller.cercaToDoPerTitolo(titolo);
            aggiornaListaRisultati(risultati);
        } catch (Exception _) {
            JOptionPane.showMessageDialog(dialog, "Formato data non valido (AAAA-MM-GG)", MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cercaPerData() {
        try {
            LocalDate data = LocalDate.parse(txtData.getText().trim());
            List<ToDo> risultati = controller.getToDoInScadenzaEntro(data);
            aggiornaListaRisultati(risultati);
        } catch (Exception _) {
            JOptionPane.showMessageDialog(dialog, "Formato data non valido (AAAA-MM-GG)", MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
        }

    }

    private void mostraScadenzeOggi() {
        try {
            List<ToDo> risultati = controller.getToDoInScadenzaOggi();
            aggiornaListaRisultati(risultati);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Errore durante la ricerca: " + ex.getMessage(), MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aggiornaListaRisultati(List<ToDo> risultati) {
        modelRisultati.clear();
        if (risultati.isEmpty()) {
            modelRisultati.addElement("Nessun risultato trovato");
            return;
        }
        for (ToDo todo : risultati) {
            modelRisultati.addElement(todo.getTitoloToDo() + " - Scadenza: " + todo.getDataScadenza());
        }
    }

    public void mostra() {
        dialog.setVisible(true);
    }
}