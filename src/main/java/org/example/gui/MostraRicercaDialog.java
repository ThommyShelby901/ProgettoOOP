package org.example.gui;

import org.example.controller.AppController;
import org.example.model.ToDo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MostraRicercaDialog extends JDialog {
    private final AppController controller;

    // Componenti UI
    private JTextField txtRicerca;
    private JButton btnCercaTesto;
    private JButton btnScadenzaOggi;
    private JButton btnScadenzaEntro;
    private JList<String> lstRisultati;
    private DefaultListModel<String> modelRisultati;

    public MostraRicercaDialog(JFrame parent, AppController controller) {
        super(parent, "Ricerca ToDo", true);
        this.controller = controller;
        initUI();
        setupListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(650, 450);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Pannello ricerca
        JPanel pnlRicerca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlRicerca.setBorder(BorderFactory.createTitledBorder("Parametri di ricerca"));

        txtRicerca = new JTextField(25);
        btnCercaTesto = new JButton("Cerca testo");
        btnScadenzaOggi = new JButton("Scadenze oggi");
        btnScadenzaEntro = new JButton("Scadenze entro data");

        pnlRicerca.add(new JLabel("Testo:"));
        pnlRicerca.add(txtRicerca);
        pnlRicerca.add(btnCercaTesto);
        pnlRicerca.add(btnScadenzaOggi);
        pnlRicerca.add(btnScadenzaEntro);

        // Pannello risultati
        JPanel pnlRisultati = new JPanel(new BorderLayout());
        pnlRisultati.setBorder(BorderFactory.createTitledBorder("Risultati"));

        modelRisultati = new DefaultListModel<>();
        lstRisultati = new JList<>(modelRisultati);
        lstRisultati.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(lstRisultati);
        pnlRisultati.add(scrollPane, BorderLayout.CENTER);

        // Aggiunta al content pane
        add(pnlRicerca, BorderLayout.NORTH);
        add(pnlRisultati, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnCercaTesto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaPerTesto(e);
            }
        });

        btnScadenzaOggi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaScadenzaOggi(e);
            }
        });

        btnScadenzaEntro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaScadenzaEntro(e);
            }
        });
    }

    private void cercaPerTesto(ActionEvent e) {
        String testo = txtRicerca.getText().trim();
        if (testo.isEmpty()) {
            showWarning("Inserire un testo da cercare");
            return;
        }

        try {
            updateResults(controller.cercaToDoPerTesto(testo));
        } catch (SQLException ex) {
            showError("Errore durante la ricerca: " + ex.getMessage());
        }
    }

    private void cercaScadenzaOggi(ActionEvent e) {
        try {
            updateResults(controller.getToDoInScadenzaOggi());
        } catch (SQLException ex) {
            showError("Errore durante la ricerca: " + ex.getMessage());
        }
    }

    private void cercaScadenzaEntro(ActionEvent e) {
        String input = JOptionPane.showInputDialog(
                this,
                "Inserisci data (AAAA-MM-GG):",
                "Ricerca per scadenza",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) return;

        try {
            LocalDate data = LocalDate.parse(input);
            updateResults(controller.getToDoInScadenzaEntro(data));
        } catch (Exception ex) {
            showError("Formato data non valido. Usare AAAA-MM-GG");
        }
    }

    private void updateResults(List<ToDo> risultati) {
        modelRisultati.clear();

        if (risultati.isEmpty()) {
            modelRisultati.addElement("Nessun risultato trovato");
            return;
        }

        risultati.forEach(todo -> {
            String scadenza = todo.getDataScadenza() != null
                    ? todo.getDataScadenza().toString()
                    : "Nessuna scadenza";

            modelRisultati.addElement(String.format(
                    "%s - %s (Scadenza: %s)",
                    todo.getTitoloToDo(),
                    todo.getBacheca(),
                    scadenza
            ));
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Errore",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Attenzione",
                JOptionPane.WARNING_MESSAGE
        );
    }
}