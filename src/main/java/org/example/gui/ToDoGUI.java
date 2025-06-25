package org.example.gui;

import org.example.controller.AppController;
import org.example.model.StatoToDo;
import org.example.model.ToDo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ToDoGUI {

    /* GUI (creata dal .form) ------------------------------------------------ */
    private JPanel todoPanel;
    private JList<ToDo> todoList;
    JScrollPane scroll;
    private JButton btnAggiungiToDo;
    private JButton btnModificaToDo;
    private JButton btnEliminaToDo;
    private JButton btnIndietro;
    private JButton btnTrasferisci;
    private JButton spostaButton;
    private JButton btnVediCondivisioni;
    private JButton btnVisualizzaToDo;
    private JButton btnAggiungiImmagine;


    /* campi non‑GUI --------------------------------------------------------- */
    private final JFrame frame;
    JFrame frameChiamante;
    private final AppController controller;
    private final String titoloBacheca;
    private final DefaultListModel<ToDo> modello = new DefaultListModel<>();

    /* costruttore ----------------------------------------------------------- */
    public ToDoGUI(AppController controller, JFrame frameChiamante, String titoloBacheca) {
        this.controller      = controller;
        this.frameChiamante  = frameChiamante;
        this.titoloBacheca   = titoloBacheca;

        frame = new JFrame("Gestione To‑Do – " + titoloBacheca);
        frame.setContentPane(todoPanel);
        frame.setSize(650, 440);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        todoList.setModel(modello);
        aggiornaLista();

        /* listener (no lambda) --------------------------------------------- */
        btnAggiungiToDo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { creaToDo(); }});
        btnModificaToDo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { modificaToDo(); }});
        btnEliminaToDo .addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { eliminaToDo(); }});
        spostaButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { spostaToDo(); }});
        btnTrasferisci .addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { trasferisciToDo(); }});
        btnVediCondivisioni.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { apriGestioneCondivisioni(); }});
        btnVisualizzaToDo.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { visualizzaDettagli(); }});
        btnAggiungiImmagine.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { aggiungiImmagine(); }});
        btnIndietro.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
            frameChiamante.setVisible(true);
            frame.dispose();
        }});

        frame.setVisible(true);
    }

    private void aggiornaLista() {
        modello.clear();
        try {
            List<ToDo> lista = controller.getToDoPerBacheca(titoloBacheca);
            for (ToDo t : lista) {
                modello.addElement(t);
            }
        } catch (SQLException ex) {
            errore(ex);
        }
        frame.revalidate();
        frame.repaint();
    }


    private ToDo toDoSelezionato() {
        ToDo t = todoList.getSelectedValue();
        if (t == null) {
            info("Seleziona un To‑Do.");
            return null;
        }
        return t;
    }


    /* ====== CRUD ====== */

    private void creaToDo() {
        // Richiedo input
        String titolo = getInput("Titolo:", "", true);
        if (titolo == null) return;
        String descr = getInput("Descrizione:", "", true);
        if (descr == null) return;
        String dataStr = getInput("Scadenza yyyy-MM-dd (opzionale):", "", false);
        LocalDate dataScad = parseDate(dataStr);
        String url = getInput("URL (opzionale):", "", false);
        StatoToDo stato = getStatoFromDialog();
        Color colore = getColorFromDialog();

        try {
            controller.creaToDo(titolo, descr, dataScad, url, stato, titoloBacheca, colore);
            aggiornaLista();
            info("To‑Do creato.");
        } catch (SQLException ex) {
            errore(ex);
        }
    }

    private void modificaToDo() {
        ToDo t = toDoSelezionato();
        if (t == null) return;
        String titolo = getInput("Titolo:", t.getTitoloToDo(), true);
        if (titolo == null) return;
        String descr = getInput("Descrizione:", t.getDescrizioneToDo(), true);
        if (descr == null) return;
        String dataStr = getInput("Scadenza yyyy-MM-dd (opzionale):",
                t.getDataScadenza() != null ? t.getDataScadenza().toString() : "", false);
        LocalDate dataScad = parseDate(dataStr);
        String url = getInput("URL (opzionale):", t.getUrl(), false);
        StatoToDo stato = getStatoFromDialog();

        try {
            controller.modificaToDo(t, titolo, descr, dataScad, url, stato);
            aggiornaLista();
            info("To‑Do modificato.");
        } catch (SQLException ex) {
            errore(ex);
        }
    }

    private void eliminaToDo() {
        ToDo t = toDoSelezionato();
        if (t == null) return;
        int ok = JOptionPane.showConfirmDialog(frame,
                "Eliminare il To‑Do \"" + t.getTitoloToDo() + "\"?", "Conferma", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaToDo(t.getTitoloToDo(), titoloBacheca);
                aggiornaLista();
                info("To‑Do eliminato.");
            } catch (SQLException ex) {
                errore(ex);
            }
        }
    }

    /* ====== Extra ====== */

    private void spostaToDo() {
        ToDo t = toDoSelezionato();
        if (t == null) return;
        try {
            List<ToDo> todos = controller.getToDoPerBacheca(titoloBacheca);
            if (todos.size() <= 1) { info("Pochi To‑Do per spostare."); return; }
            int attuale = todos.indexOf(t) + 1;
            String in = getInput("Nuova posizione (1-" + todos.size() + "):", String.valueOf(attuale), true);
            if (in == null) return;
            int nuova = Integer.parseInt(in);
            if (nuova < 1 || nuova > todos.size()) { info("Posizione non valida."); return; }
            controller.spostaToDo(titoloBacheca, t.getTitoloToDo(), nuova - 1);
            aggiornaLista();
            info("Spostato in posizione " + nuova);
        } catch (Exception ex) {
            errore(ex);
        }
    }

    private void trasferisciToDo() {
        ToDo t = toDoSelezionato();
        if (t == null) return;
        String dest = getInput("Bacheca destinazione:", "", true);
        if (dest == null) return;
        try {
            controller.trasferisciToDo(t, dest);
            aggiornaLista();
            info("To‑Do trasferito.");
        } catch (Exception ex) {
            errore(ex);
        }
    }

    private void aggiungiImmagine() {
        ToDo t = toDoSelezionato();
        if (t == null) return;
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Immagini", "jpg","png","gif"));
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.aggiungiImmagineAToDo(t, fc.getSelectedFile().getAbsolutePath());
                info("Immagine aggiunta.");
            } catch (SQLException ex) {
                errore(ex);
            }
        }
    }

    private void apriGestioneCondivisioni() {
        frame.setVisible(false);
        try {
            BachecaGUI b = new BachecaGUI(controller, frame);
            b.getBoardList().setSelectedValue(titoloBacheca, true);
            b.aggiornaListaToDo(titoloBacheca);
            b.getFrame().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    frame.setVisible(true);
                    aggiornaLista();
                }
            });
        } catch (SQLException ex) {
            errore(ex);
            frame.setVisible(true);
        }
    }

    private void visualizzaDettagli() {
        ToDo t = toDoSelezionato();
        if (t != null) {
            new VisualizzaToDoDialog(frame, t, controller);
        }
    }

    /* ====== Utility ====== */

    private String getInput(String msg, String init, boolean req) {
        String s = JOptionPane.showInputDialog(frame, msg, init);
        if (s == null || (req && s.trim().isEmpty())) return null;
        return s.trim();
    }

    private LocalDate parseDate(String ds) {
        if (ds == null || ds.isEmpty()) return null;
        try { return LocalDate.parse(ds); }
        catch (Exception _) { JOptionPane.showMessageDialog(frame, "Data non valida."); return null; }
    }

    private StatoToDo getStatoFromDialog() {
        return JOptionPane.showConfirmDialog(frame, "Completato?", "", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;
    }

    private Color getColorFromDialog() {
        return JColorChooser.showDialog(frame, "Colore di sfondo", Color.WHITE);
    }

    private void info(String m) {
        JOptionPane.showMessageDialog(frame, m, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void errore(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
    }
}