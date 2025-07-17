package org.example.gui;

import org.example.controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Questa classe rappresenta l'interfaccia grafica principale dell'applicazione dopo che l'utente ha
 * correttamente effettuato il login.
 * <p>
 * mostra la lista delle bacheche dell'utente e fornisce funzionalità per la gestione, ovvero creazione, modifica, eliminazione,
 * visualizzazione delle bacheche e ricerca dei to-do intesa con l'apertura della GuiRicerca dove possiamo cercare un to-do
 * per titolo, data scadenza odierna o selezionata dall'utente.
 */
public class GuiHome {
    /** frame principale per la schermata home*/
    private final JFrame frame;
    /** istanza del controller che gestisce la logica*/
    private final Controller controller;
    /** componente per rendere scorrevole la lista di bacheche e consentirne la visualizzazione nel caso fossero tante*/
    JScrollPane scrollPaneBacList;
    /** pannello principale che contiene tutti gli elementi della gui*/
    private JPanel homePanel;
    /** la lista che visualizza i titoli delle bacheche dell'utente*/
    private JList<String> bacList;
    /** gestisce i dati della {@link #bacList}*/
    private final DefaultListModel<String> bachecaListModel;
    /** bottone per effettuare il logout, e tornare alla schermata di login*/
    private JButton logoutButton;
    /** bottone per creare una nuova bacheca*/
    private JButton btnCreaBacheca;
    /** bottone per modificare una bacheca esistente dalla lista*/
    private JButton btnModificaBacheca;
    /** bottone per eliminare una bacheca esistente dalla lista*/
    private JButton btnEliminaBacheca;
    /** bottone per visualizzare i to-do all'interno di una bacheca*/
    private JButton visualizzaButton;
    /** bottone per avviare le funzionalita di ricera di un to-do*/
    private JButton btnCercaToDo;
    /** logger per la resgistrazione di errori e eventi specifici di questa classe*/
    private static final Logger logger = Logger.getLogger(GuiHome.class.getName());

    /**
     * costruttore che inizializza il frame riutilizzando quello della finestra chiamante, configura contenuti, dimensioni,
     * configura tutti gli actionListener per i bottoni e popola la lista delle bacheche predefinite.
     * Non tutti i metodi sono stati implementati direttamente qui per non avere una complessità elevata segnalataci da sonarqube, abbiamo
     * deciso di lasciare l'implementazione di alcuni listener in metodi che chiameremo all'interno di essi.
     * @param controller istanza del {@link Controller per l'interazione con la logica dell'applicazione}
     * @param frameChiamante il jframe da cui è stata lanciata questa finestra ovvero quello del GuiLogin
     */
    public GuiHome(Controller controller, JFrame frameChiamante) {
        this.controller = controller;
        this.frame = frameChiamante;

        frame.setTitle("Bacheche");

        frame.setContentPane(homePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        bachecaListModel = new DefaultListModel<>();
        bacList.setModel(bachecaListModel);
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
                    String titolo = JOptionPane.showInputDialog(frame, "Inserisci il titolo della nuova bacheca:");
                    String descrizione = JOptionPane.showInputDialog(frame, "Inserisci la descrizione della nuova bacheca:");

                    if (titolo == null || titolo.trim().isEmpty() || descrizione == null || descrizione.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Titolo e descrizione sono obbligatori.");
                        return;
                    }

                    controller.creaBacheca(titolo.trim(), descrizione.trim());
                    aggiornaListaBacheche();
                } catch (SQLException ex) {
                    throw new IllegalStateException("Errore creazione bacheca", ex);
                }
            }
        });

        btnModificaBacheca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selezionata = bacList.getSelectedValue();
                if (selezionata != null) {
                    String nuovoTitolo = JOptionPane.showInputDialog(frame, "Modifica il titolo della bacheca:");
                    String nuovaDescrizione = JOptionPane.showInputDialog(frame, "Modifica la descrizione della bacheca:");

                    if (nuovoTitolo != null && nuovaDescrizione != null ) {
                        controller.modificaBacheca(selezionata, nuovoTitolo.trim(), nuovaDescrizione.trim());
                        aggiornaListaBacheche();
                        JOptionPane.showMessageDialog(frame, "Bacheca modificata con successo.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da modificare.");
                }
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
                String selezionata = bacList.getSelectedValue();
                if (selezionata != null) {
                    GuiToDo.mostra(controller, frame, selezionata); // usa il metodo statico per chiarezza
                } else {
                    JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da visualizzare");
                }
            }
        });


        btnCercaToDo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cercaToDo();
            }
        });
        aggiornaListaBacheche();

        frame.setVisible(true);
    }

    /**
     * Gestisce la procedura di logout dell'utente, se l'utente conferma delega l'operazione al {@link Controller} e chiude il frame corrente
     */
    private void gestisciLogout() {
        int conferma = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler uscire?", "Logout", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            controller.logout(); // Chiama il controller per gestire il logout
            frame.dispose(); // Chiude la GuiHome corrente
        }
    }

    /**
     * gestisce l'operazione di eliminazione di una bacheca
     * <p>
     * chiede conferma all'utente prima di procedere all'operazione, se l'utente conferma delega l'operazione al {@link Controller} e aggiorna la lista
     */
    private void eliminaBacheca() {
        String selezionata = bacList.getSelectedValue();
        if (selezionata == null) {
            JOptionPane.showMessageDialog(frame, "Seleziona una bacheca da eliminare.");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(frame,
                "Eliminare la bacheca \"" + selezionata + "\"?", "Conferma", JOptionPane.YES_NO_OPTION);

        if (conferma == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaBacheca(selezionata);
                aggiornaListaBacheche();
                JOptionPane.showMessageDialog(frame, "Bacheca eliminata con successo.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Errore: " + ex.getMessage());
                logger.log(Level.SEVERE, "Errore eliminazione bacheca", ex);
            }
        }
    }

    /**
     * apre la finestra {@link GuiRicerca} per permettere di cercare un to-do tramite titolo o data di scadenza
     * o di visualizzare le scadenze odierne.
     * Nasconde temporaneamente il frame corrente e lo rende nuovamente visibile al ritorno dalla finestra di ricerca.
     */
    private void cercaToDo() {
        if (controller.getUtenteCorrente() == null) {
            JOptionPane.showMessageDialog(frame, "Devi essere loggato per effettuare ricerche", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.setVisible(false);
        try {
            GuiRicerca dialog = new GuiRicerca(controller, frame);
            dialog.mostra();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore durante la ricerca: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Errore durante la ricerca ToDo", ex);
        }
        frame.setVisible(true);
    }

    /**
     * Aggiorna la {@link #bacList} recuperando la lista più recente delle bacheche dall'{@link Controller}
     * Dovrebbe essere chiamato sempre per riflettere lo stato aggiornato.
     */
    public void aggiornaListaBacheche() {
        bachecaListModel.clear();
        try {
            for (String titolo : controller.getTitoliBacheche()) {
                bachecaListModel.addElement(titolo);
            }
        } catch (SQLException _) {
            JOptionPane.showMessageDialog(frame, "Errore nel recupero delle bacheche dal database");
        }
    }

    /**
     *  Restituisce il {@link JFrame} principale di questa interfaccia Home. Utile per finestre modali
     * @return L'istanza di {@link JFrame} associata a questa GUI.
     */
    public JFrame getFrame() {
        return frame;
    }
}