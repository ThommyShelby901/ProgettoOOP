package org.example.gui;

import org.example.controller.AppController;
import org.example.model.StatoToDo;
import org.example.model.ToDo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ToDoGUI {

    /* GUI (creata dal .form) ------------------------------------------------ */
    private JPanel todoPanel;
    private JList<String> todoList;
    JScrollPane scroll;
    private JButton btnAggiungiToDo;
    private JButton btnModificaToDo;
    private JButton btnEliminaToDo;
    private JButton btnIndietro;
    private JButton btnTrasferisci;
    private JButton btnSpostaToDo;
    private JButton btnVediCondivisioni;
    private JButton btnVisualizzaToDo;
    private JButton btnAggiungiImmagine;

    /* campi non‑GUI --------------------------------------------------------- */
    private final JFrame frame;
    JFrame frameChiamante;
    private final AppController controller;
    private final String titoloBacheca;
    private final DefaultListModel<String> modello = new DefaultListModel<>();

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
        btnSpostaToDo  .addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { spostaToDo(); }});
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

    /* ====================== CRUD principali ================================= */

    private void creaToDo() {
        Optional<DatiToDo> o = dialogCreaModifica(null);
        if (!o.isPresent()) return;

        DatiToDo d = o.get();
        try {
            controller.creaToDo(d.titolo, d.descrizione, d.dataScadenza, d.url,
                    d.stato, titoloBacheca, d.colore);
            aggiornaLista();
            info("To‑Do creato con successo.");
        } catch (SQLException ex) { errore(ex); }
    }

    private void modificaToDo() {
        Optional<ToDo> toSel = selezionato();
        if (!toSel.isPresent()) return;

        Optional<DatiToDo> o = dialogCreaModifica(toSel.get());
        if (!o.isPresent()) return;

        DatiToDo d = o.get();
        try {
            // Rimossa la passaggio dell'utente corrente
            controller.modificaToDo(toSel.get(), d.titolo, d.descrizione,
                    d.dataScadenza, d.url, d.stato);
            aggiornaLista();
            info("To‑Do modificato.");
        } catch (Exception ex) { errore(ex); }
    }

    private void eliminaToDo() {
        Optional<ToDo> toSel = selezionato();
        if (!toSel.isPresent()) return;

        int ok = JOptionPane.showConfirmDialog(frame,
                "Eliminare il To‑Do \"" + toSel.get().getTitoloToDo() + "\"?",
                "Conferma", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            controller.eliminaToDo(toSel.get().getTitoloToDo(), titoloBacheca);
            aggiornaLista();
            info("To‑Do eliminato.");
        } catch (SQLException ex) { errore(ex); }
    }

    /* ====================== azioni extra =================================== */

    private void spostaToDo() {
        Optional<ToDo> sel = selezionato();
        if (!sel.isPresent()) return;

        String posStr = JOptionPane.showInputDialog(frame,
                "Nuova posizione (1-" + modello.size() + "):");
        try {
            int nuovaPos = Integer.parseInt(posStr) - 1;
            controller.spostaToDo(titoloBacheca, sel.get().getTitoloToDo(), nuovaPos);
            aggiornaLista();
            info("To‑Do spostato.");
        } catch (Exception ex) { errore(ex); }
    }

    private void trasferisciToDo() {
        Optional<ToDo> sel = selezionato();
        if (!sel.isPresent()) return;

        String dest = JOptionPane.showInputDialog(frame, "Nuova bacheca di destinazione:");
        if (dest == null || dest.trim().isEmpty()) return;

        try {
            controller.trasferisciToDo(sel.get(), dest.trim());
            aggiornaLista();
            info("To‑Do trasferito.");
        } catch (Exception ex) { errore(ex); }
    }

    private void visualizzaDettagli() {
        Optional<ToDo> sel = selezionato();
        if (!sel.isPresent()) return;

        // Apre la finestra di dialogo personalizzata invece di usare JOptionPane
        new VisualizzaToDoDialog(frame, sel.get(), controller);
    }

    private void aggiungiImmagine() {
        Optional<ToDo> sel = selezionato();
        if (!sel.isPresent()) return;

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Immagini", "jpg","jpeg","png","gif"));
        if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;

        try {
            controller.aggiungiImmagineAToDo(sel.get(), fc.getSelectedFile().getAbsolutePath());
            info("Immagine aggiunta.");
        } catch (SQLException ex) { errore(ex); }
    }

    /* apri gestione condivisioni in BachecaGUI ------------------------------ */
    private void apriGestioneCondivisioni() {
        frame.setVisible(false);
        try {
            BachecaGUI bachecaGUI = new BachecaGUI(controller, frame);
            bachecaGUI.getBoardList().setSelectedValue(titoloBacheca, true);
            bachecaGUI.aggiornaListaToDo(titoloBacheca);
            bachecaGUI.getFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    frame.setVisible(true);
                    aggiornaLista();
                }
            });
        } catch (SQLException ex) {
            errore(ex);
            frame.setVisible(true);
        }
    }

    /* ====================== helper comuni ================================= */

    /** Mostra finestre di input per creare o modificare un To‑Do. */
    private Optional<DatiToDo> dialogCreaModifica(ToDo originale) {
        String tInit  = originale != null ? originale.getTitoloToDo()       : "";
        String dInit  = originale != null ? originale.getDescrizioneToDo()  : "";
        String dsInit = originale != null ? String.valueOf(originale.getDataScadenza()) : "";
        String uInit  = originale != null ? originale.getUrl()              : "";

        String titolo = JOptionPane.showInputDialog(frame, "Titolo:", tInit);
        if (titolo == null || titolo.trim().isEmpty()) return Optional.empty();

        String descr  = JOptionPane.showInputDialog(frame, "Descrizione:", dInit);
        if (descr == null || descr.trim().isEmpty()) return Optional.empty();

        String dataScad = JOptionPane.showInputDialog(frame,
                "Data scadenza (AAAA-MM-GG):", dsInit);
        if (dataScad != null && dataScad.trim().isEmpty()) dataScad = null;

        String url = JOptionPane.showInputDialog(frame, "URL (opzionale):", uInit);
        if (url != null && url.trim().isEmpty()) url = null;

        StatoToDo stato = JOptionPane.showConfirmDialog(frame,
                "Il To‑Do è completato?", "Stato",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION
                ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;

        Color colore = JColorChooser.showDialog(frame, "Colore di sfondo (opzionale)", Color.WHITE);

        return Optional.of(new DatiToDo(titolo.trim(), descr.trim(), dataScad, url,
                stato, colore));
    }

    private Optional<ToDo> selezionato() {
        String sel = todoList.getSelectedValue();
        if (sel == null) {
            info("Seleziona un To‑Do.");
            return Optional.empty();
        }
        String titolo = sel.replaceFirst("^\\d+\\.\\s*", "").replaceFirst("\\s*\\(Scadenza.*$", "").trim();
        return Optional.ofNullable(controller.getToDoPerTitoloEBoard(titolo, titoloBacheca));
    }

    private void aggiornaLista() {
        modello.clear();
        try {
            List<ToDo> lista = controller.getToDoPerBacheca(titoloBacheca); // Cambiato da getTuttiToDo
            int idx = 1;
            for (ToDo t : lista) {
                String scad = (t.getDataScadenza() == null) ? "(Nessuna scadenza)"
                        : "(Scadenza: " + t.getDataScadenza() + ")";
                modello.addElement(idx++ + ". " + t.getTitoloToDo() + " " + scad);
            }
        } catch (SQLException ex) { errore(ex); }
        frame.revalidate();
        frame.repaint();
    }

    private void info(String msg)  { JOptionPane.showMessageDialog(frame, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void errore(Exception e){ JOptionPane.showMessageDialog(frame, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE); }

    /* semplice contenitore per i dati raccolti ----------------------------- */
    private static class DatiToDo {
        final String titolo;
        final String descrizione;
        final String dataScadenza;
        final String url;
        final StatoToDo stato;
        final Color colore;
        DatiToDo(String t, String d, String ds, String u, StatoToDo s, Color c) {
            titolo=t; descrizione=d; dataScadenza=ds; url=u; stato=s; colore=c;
        }
    }
}
