package org.example.gui;

import org.example.controller.Controller;
import org.example.model.CheckList;
import org.example.model.StatoCheck;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GuiCheckList {
    private final JFrame frame;
    private JPanel mainPanel;
    JPanel panel1;
     JPanel panel2;
    JScrollPane j;
    private JList<String> checklistList;
    private final DefaultListModel<String> checklistModel;

    private JButton btnAggiungi;
    private JButton btnModifica;
    private JButton btnElimina;
    private JButton btnChiudi;

    private final Controller controller;
    String titoloToDo;

    public GuiCheckList(Controller controller, String titoloToDo) {
        this.controller = controller;
        this.titoloToDo = titoloToDo;

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


    private void caricaChecklist() {
        checklistModel.clear();
        try {
            List<CheckList> voci = controller.getChecklistPerToDo();  // ora ritorna List<CheckList>
            for (CheckList voce : voci) {
                checklistModel.addElement(voce.getDescrizione()); // mostri solo la descrizione
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Errore caricamento checklist: " + e.getMessage());
        }
    }


    private String voceSelezionata() {
        String voce = checklistList.getSelectedValue();
        if (voce == null) {
            JOptionPane.showMessageDialog(frame, "Seleziona una voce.");
        }
        return voce;
    }

    private void aggiungiVoce() {
        String nuovaVoce = JOptionPane.showInputDialog(frame, "Testo nuova voce:");
        if (nuovaVoce != null && !nuovaVoce.trim().isEmpty()) {
            try {
                controller.aggiungiVoceChecklist( nuovaVoce.trim());
                caricaChecklist();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Errore aggiunta voce: " + e.getMessage());
            }
        }
    }

    private void modificaVoce() {
        String selezionata = voceSelezionata();
        if (selezionata == null) return;

        String nuovaVoce = JOptionPane.showInputDialog(frame, "Modifica voce:", selezionata);
        if (nuovaVoce != null && !nuovaVoce.trim().isEmpty()) {
            // Chiedi anche lo stato
            String[] opzioniStato = {"INCOMPLETO", "COMPLETATO"};
            int scelta = JOptionPane.showOptionDialog(frame, "Seleziona lo stato:",
                    "Modifica stato",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    opzioniStato, opzioniStato[0]);

            if (scelta == JOptionPane.CLOSED_OPTION) return;

            StatoCheck nuovoStato = (scelta == 0) ? StatoCheck.INCOMPLETO : StatoCheck.COMPLETATO;

            try {
                controller.modificaVoceChecklist(selezionata, nuovaVoce.trim(), nuovoStato);
                caricaChecklist();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Errore modifica voce: " + e.getMessage());
            }
        }
    }


    private void eliminaVoce() {
        String selezionata = voceSelezionata();
        if (selezionata == null) return;

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare la voce \"" + selezionata + "\"?", "Conferma", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaVoceChecklist(selezionata);

                caricaChecklist();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Errore eliminazione voce: " + e.getMessage());
            }
        }
    }
}

