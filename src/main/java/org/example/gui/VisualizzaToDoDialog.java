package org.example.gui;
import org.example.controller.AppController;
import org.example.model.ToDo;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VisualizzaToDoDialog {
    private final  JDialog dialog;
    private final ToDo todo;

    // Componenti gestiti nella form
    private JPanel pnlDettagliToDo;
    private JLabel lblTitolo;
    private JLabel lblDescrizione;
    private JLabel lblData;
    private JLabel lblUrl;
    private JLabel lblImmagine;
    private JButton btnChiudi;
    private JButton btnRimuoviImmagine;

    // In VisualizzaToDoDialog.java, modifica il costruttore:

    public VisualizzaToDoDialog(JFrame parent, ToDo todo, AppController controller) {
        this.todo = todo;
        dialog = new JDialog(parent, "Dettagli To-Do", true);
        dialog.setSize(800, 800);
        dialog.setLocationRelativeTo(parent);

        dialog.setContentPane(pnlDettagliToDo);
        aggiornaDettagliToDo();

        // Rimuovi ogni interattività dal titolo
        lblTitolo.setCursor(Cursor.getDefaultCursor());

        btnChiudi.addActionListener(e -> dialog.dispose());

        btnRimuoviImmagine.addActionListener(e -> {
            if (todo.getPercorsoImmagine() == null || todo.getPercorsoImmagine().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nessuna immagine presente per questo ToDo",
                        "Informazione", JOptionPane.INFORMATION_MESSAGE);
            } else {
                rimuoviImmagine(controller);
            }
        });

        dialog.setVisible(true);
    }

    private void aggiornaDettagliToDo() {
        lblTitolo.setText("Titolo: " + todo.getTitoloToDo());
        lblDescrizione.setText("Descrizione: " +
                (todo.getDescrizioneToDo() != null ? todo.getDescrizioneToDo() : "Nessuna"));
        lblData.setText("Scadenza: " +
                (todo.getDataScadenza() != null ? todo.getDataScadenza() : "Nessuna"));
        lblUrl.setText("URL: " +
                (todo.getUrl() != null ? todo.getUrl() : "Nessun link"));

        if (todo.getPercorsoImmagine() != null && !todo.getPercorsoImmagine().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(todo.getPercorsoImmagine());
                Image img = icon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                lblImmagine.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                lblImmagine.setIcon(null);
                JOptionPane.showMessageDialog(dialog, "Errore nel caricamento dell'immagine: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            lblImmagine.setIcon(null);
        }
    }

    private void rimuoviImmagine(AppController controller) {
        try {
            controller.rimuoviImmagineDaToDo(todo);
            todo.setPercorsoImmagine(null);
            aggiornaDettagliToDo();
            JOptionPane.showMessageDialog(dialog, "Immagine rimossa con successo!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Errore durante la rimozione: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}