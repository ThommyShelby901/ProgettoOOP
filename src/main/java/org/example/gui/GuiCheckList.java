package org.example.gui;

import org.example.controller.Controller;
import org.example.model.CheckList;
import org.example.model.StatoCheck;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Classe grafica per la gestione delle voci di checklist associate a un to-do.
 * <p>
 * Permette di visualizzare, aggiungere, modificare ed eliminare voci della checklist.
 * Comunica con il {@link Controller} per eseguire operazioni sul modello.
 * <p>
 * Quando la checklist viene modificata, notifica il cambiamento tramite un {@link AggiornaListener},
 * applicando il pattern Observer.
 */
public class GuiCheckList {
    private final JFrame frame;
    private JPanel mainPanel;
    JPanel panel1;
     JPanel panel2;
    JScrollPane j;
    private JList<CheckList> checklistList;
    private final DefaultListModel<CheckList> checklistModel;

    private JButton btnAggiungi;
    private JButton btnModifica;
    private JButton btnElimina;
    private JButton btnChiudi;

    private final Controller controller;
    String titoloToDo;
    private final AggiornaListener listener;

    /**
     * Costruisce la finestra per gestire la checklist di un to-do.
     * @param controller  il controller per accedere al modello
     * @param titoloToDo  il titolo del to-do a cui è associata la checklist
     * @param listener    il listener da notificare quando avvengono modifiche
     */
    public GuiCheckList(Controller controller, String titoloToDo,AggiornaListener listener) {
        this.controller = controller;
        this.titoloToDo = titoloToDo;
        this.listener = listener;

        frame = new JFrame("CheckList per ToDo – " + titoloToDo); //titolo dinamico in base alla bacheca
        frame.setContentPane(mainPanel);
        frame.setSize(650, 440);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        checklistModel = new DefaultListModel<>();
        checklistList.setModel(checklistModel);

        caricaChecklist();

        btnAggiungi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aggiungiVoce();
            }
        });

        btnModifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificaVoce();
            }
        });

        btnElimina.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminaVoce();
            }
        });

        btnChiudi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.setVisible(true);

    }

    /**
     * Carica le voci della checklist dal controller e aggiorna la lista grafica.
     */
    private void caricaChecklist() {
        checklistModel.clear();
        List<CheckList> lista = controller.getChecklistPerToDo();
        for (CheckList voce : lista) {
            checklistModel.addElement(voce); // il toString() sarà usato automaticamente
        }
    }

    /**
     * Restituisce la voce attualmente selezionata nella lista.
     *
     * @return la voce selezionata o {@code null} se nessuna è selezionata
     */
    private CheckList voceSelezionata() {
        CheckList voce = checklistList.getSelectedValue();
        if (voce == null) {
            JOptionPane.showMessageDialog(frame, "Seleziona una voce.");
        }
        return voce;
    }

    /**
     * Aggiunge una nuova voce alla checklist, chiedendo all’utente il testo.
     */
    private void aggiungiVoce() {
        String nuovaVoce = JOptionPane.showInputDialog(frame, "Testo nuova voce:");
        if (nuovaVoce != null && !nuovaVoce.trim().isEmpty()) {
            try {
                controller.aggiungiVoceChecklist( nuovaVoce.trim());
                caricaChecklist();
                if (listener != null) listener.aggiorna();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Errore aggiunta voce: " + e.getMessage());
            }
        }
    }

    /**
     * Modifica la voce selezionata, permettendo all'utente di cambiare testo e stato.
     */
    private void modificaVoce() {
        CheckList selezionata = voceSelezionata();
        if (selezionata == null) return;

        String nuovaVoce = JOptionPane.showInputDialog(frame, "Modifica voce:", selezionata.getDescrizione());
        if (nuovaVoce != null && !nuovaVoce.trim().isEmpty()) {
            // Chiedi anche lo stato
            String[] opzioniStato = {"INCOMPLETO", "COMPLETATO"};
            int scelta = JOptionPane.showOptionDialog(frame, "Seleziona lo stato:",
                    "Modifica stato",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    opzioniStato, selezionata.getStato().name());

            if (scelta == JOptionPane.CLOSED_OPTION) return;

            StatoCheck nuovoStato = (scelta == 0) ? StatoCheck.INCOMPLETO : StatoCheck.COMPLETATO;

            try {
                controller.modificaVoceChecklist(selezionata.getDescrizione(), nuovaVoce.trim(), nuovoStato);
                caricaChecklist();
                if (listener != null) listener.aggiorna();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Errore modifica voce: " + e.getMessage());
            }
        }
    }

    /**
     * Elimina la voce selezionata dalla checklist, previa conferma dell'utente.
     */
    private void eliminaVoce() {
        CheckList selezionata = voceSelezionata();
        if (selezionata == null) return;

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare la voce \"" + selezionata.getDescrizione() + "\"?", "Conferma", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaVoceChecklist(selezionata.getDescrizione());
                caricaChecklist();
                if (listener != null) listener.aggiorna();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Errore eliminazione voce: " + e.getMessage());
            }
        }
    }
}

