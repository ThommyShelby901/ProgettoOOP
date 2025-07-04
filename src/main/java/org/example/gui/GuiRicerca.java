package org.example.gui;

import org.example.controller.Controller;
import org.example.model.ToDo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Questa classe rappresenta una finestra di dialogo, da {@link GuiHome} che ci permette di cercare to-do presenti nella
 * bacheca dell'utente corrente, per titolo, data di scadenza, o scadenzq giornaliera.
 * Blocca l'interazione con la finestra padre agendo come finestra modale.
 */
public class GuiRicerca {
    /** Finestra di dialogo principale per la ricerca di to-do*/
    private final JDialog dialog;
    /** istanza del controller che gestisce la logica*/
    private final Controller controller;
    /** costante per i messaggi di errore da visualizzare*/
    private static final String MESSAGGIO_ERRORE = "Errore";
    /** pannello principale della gui in questione*/
    private JPanel pnlRicerca;
    /** campo di testo per l'inserimento del titolo del to-do da cercare*/
    private JTextField txtTitolo;
    /** campo di testo per l'inserimento della data di scadenza per la ricerca di un to-do*/
    private JTextField txtData;
    /** bottone per avviare la ricerca per titolo*/
    private JButton btnCercaTitolo;
    /** bottone per avviare la ricerca per data*/
    private JButton btnCercaData;
    /** bottone per visualizzare direttamente le scadenze giornaliere*/
    private JButton btnScadenzeOggi;
    /** JList che visualizza i risultati di ricerca*/
    private JList<String> lstRisultati;
    /** gestisce i dati dela {@link #lstRisultati}.*/
    private final DefaultListModel<String> modelRisultati;
    /** componente per rendere la lista dei risultati scorrevole*/
    JScrollPane scrollPane;
    /** etichetta per campo input titolo*/
    JLabel lblTitolo;
    /** etichetta per campo input data*/
    JLabel lblData;

    /**
     * costruttore della classe che inizializza la finestra di dialogo, impostd contenuto, dimensioni, posizione rispetto alla
     * finestra padre. Configura il modello per la lista di risultati e configura gli actionListener ai bottoni di ricerca
     * @param controller istanza del controller per l'interazione con la logica del programma contenuta in esso
     * @param parent frame della finestra padre, rispetto alla quale questa finestra di dialogo sarà centrata
     */
    public GuiRicerca(Controller controller, JFrame parent) {
        this.controller = controller;
        dialog = new JDialog(parent, "Ricerca ToDo", true);

        modelRisultati = new DefaultListModel<>();
        lstRisultati.setModel(modelRisultati);
        // Usa il pannello già disegnato nel GUI Builder
        dialog.setContentPane(pnlRicerca);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(parent);

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

    /**
     * Esegue una ricerca di to-do per titolo, recupera il testo dal label del titolo, delega la ricerc al controller
     * e aggiorna la lista di risultati. Gestisce anche errori relativi al formata data se necessario.
     */
    private void cercaPerTitolo() {
        String titolo = txtTitolo.getText().trim();
        if (titolo.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Inserisci un titolo", MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<ToDo> risultati = controller.cercaToDoPerTitolo(titolo);
            List<String> righeFormattate = controller.formattaRisultati(risultati);
            aggiornaListaRisultati(righeFormattate);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Errore durante la ricerca: " + ex.getMessage(), MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * ricerca i to-do per data di scadenza. Recupera il testo dal label data, tenta di convertire la data in LocalTime
     * e delega la ricerca al controller per i to-do in scadenza entro quella data.
     * Gestisce gli errori relativi al formato data nel caso non fosse valido
     */
    private void cercaPerData() {
        try {
            LocalDate data = LocalDate.parse(txtData.getText().trim());
            List<ToDo> risultati = controller.getToDoInScadenzaEntro(data);
            List<String> righeFormattate = controller.formattaRisultati(risultati);
            aggiornaListaRisultati(righeFormattate);
        } catch (Exception _) {
            JOptionPane.showMessageDialog(dialog, "Formato data non valido (AAAA-MM-GG)", MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ricerca i to-do che scadono nella data odierna, come tutto il resto in questo pattern bce delega la logica al controller
     * e visivamente viene aggiornata la lista dei risultati.
     */
    private void mostraScadenzeOggi() {
        try {
            List<ToDo> risultati = controller.getToDoInScadenzaOggi();
            List<String> righeFormattate = controller.formattaRisultati(risultati);
            aggiornaListaRisultati(righeFormattate);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Errore durante la ricerca: " + ex.getMessage(), MESSAGGIO_ERRORE, JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Aggiorna la lista dei risultati di ricerca con le stringhe formattate servite, aggiorna tutte le nuove righe,
     * è il punto in cui i dati vengono effettivamente visualizzati
     * @param righe tutte le righe della lista
     */
    private void aggiornaListaRisultati(List<String> righe) {
        modelRisultati.clear();
        for (String riga : righe) {
            modelRisultati.addElement(riga);
        }
    }

    /**
     * rende visibile la finestra di dialogo di ricerca, essendo una finestra modale blocca l'interazione con la
     * finestra padre fin quando non viene chiusa.
     */
    public void mostra() {
        dialog.setVisible(true);
    }
}