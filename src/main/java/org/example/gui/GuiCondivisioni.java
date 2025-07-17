package org.example.gui;

import org.example.controller.Controller;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Questa classe gestisce l'interfaccia utente per le condivisioni con altri utenti. Ci permette di visualizzare una bacheca corrente,
 * i to-do al suo interno e la lista degli utente registrati con cui poter condividere un to-do.
 * Interagisce con il controller come per il resto delle gui seguendo il pattern bce+dao e facendo in modo che il controller,
 * gestisca la logica del programma.
 */
public class GuiCondivisioni {
    /** Componente che rende scorrevole la lista degli utenti*/
    JScrollPane scrollPaneUtenti;
    /** frame principale di questa finestra*/
    private final JFrame frame;
    /** frame della finestra chiamante, a cui si ritornerà nel caso questa finestra principale venga chiusa*/
    final JFrame frameChiamante;
    /** Istanza del controller che gestisce la logica*/
    private final Controller controller;
    /** pannello principale che contiene tutti gli elementi della gui*/
    private JPanel bachecaPanel;
    /** lista che visualizza i to-do della bacheca selezionata*/
    private JList<String> todoList;
    /** lista che visualizza le bacheche disponibili per la selezione*/
    private JList<String> boardList;
    /** DefaultListModel che gestisce i dati della {@link #boardList}.*/
    private final DefaultListModel<String> boardListModel;
    /** visualizza la lista degli utenti con cui condividere il to-do*/
    private JList<String> listUtenti;
    /** DefaultListModel gestisce i dati della {@link #listUtenti}..*/
    private final DefaultListModel<String> listUtentiModel;
    /** bottone per tornare alla finestra chiamante e chiudere questa finestra*/
    private JButton btnIndietro;
    /** bottone per aggiungere la condivisione di un to-do con un utente selezionato*/
    private JButton btnAggiungiCondivisione;
    /** bottone per rimuovere la condivisione di un to-do con un utente selezionato*/
    private JButton btnRimuoviCondivisione;
    /** titolo bacheca attualmente selezionata*/
    private String titoloBachecaSelezionata;

    /** logger per la registrazione di eventi e errori*/
    private static final Logger logger = Logger.getLogger(GuiCondivisioni.class.getName());

    /**
     * costruttore che inizializza la finestra di dialogo, imposta dimensioni, contenuto, posizione rispetto alla finestra
     * padre. Configura i modelli per le liste e carica i dati degli utenti. compone gli ActionListener
     * @param controller istanza del controller per l'interazione con la logica dell'applicazione
     * @param frameChiamante frame della finestra padre a cui si tornerà dopo la chiusura
     * @throws SQLException se si verifica un errore sul caricamento dei dati dal database
     */
    public GuiCondivisioni(Controller controller, JFrame frameChiamante) throws SQLException {
        this.controller = controller;
        this.frameChiamante = frameChiamante;
        frame = new JFrame("Gestione Condivisioni");
        frame.setContentPane(bachecaPanel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        boardListModel = new DefaultListModel<>();
        boardList.setModel(boardListModel);

        listUtentiModel = new DefaultListModel<>();
        listUtenti.setModel(listUtentiModel);
        listUtenti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        aggiornaListaUtenti();
        aggiornaListaBacheche();


        boardList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!boardList.getValueIsAdjusting()) {
                    String titoloSelezionato = boardList.getSelectedValue();
                    if (titoloSelezionato != null) {
                        titoloBachecaSelezionata = titoloSelezionato.split(" \\[Condivisa con:")[0].trim();
                        aggiornaListaToDo(titoloBachecaSelezionata);
                    }
                }
            }
        });

        btnIndietro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameChiamante.setVisible(true);
                frame.dispose();
            }
        });

        btnAggiungiCondivisione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestisciCondivisione(true);
            }
        });

        btnRimuoviCondivisione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestisciCondivisione(false);
            }
        });


        frame.setVisible(true);
    }

    /**
     * gestisce l'aggiunta o la rimozione delle condivisioni per utente, recupera utente e to-do e se non vengono mostrati
     * errori da un errato utilizzo di selezioni viene delegata la logica al controller, se uno tra l'utente e il to-do non
     * viene selezionato mostra messaggi di errore.
     * @param aggiungi boolean true per aggiungere condivisioni, false per rimuoverle
     */
    private void gestisciCondivisione(boolean aggiungi) {
        List<String> utentiSelezionati = listUtenti.getSelectedValuesList();
        String titoloToDo = todoList.getSelectedValue();

        if (utentiSelezionati.isEmpty() || titoloToDo == null || titoloBachecaSelezionata == null) {
            JOptionPane.showMessageDialog(frame, "Errore: Seleziona un To-Do e almeno un utente!");
            return;
        }

        try {
            String titoloPulito = titoloToDo.split(" \\[Condiviso con:")[0].trim();

            controller.gestisciCondivisioneToDo(titoloPulito, titoloBachecaSelezionata, utentiSelezionati, aggiungi);

            JOptionPane.showMessageDialog(frame, (aggiungi ? "To-Do condiviso con " : "Condivisioni rimosse per ") + String.join(", ", utentiSelezionati));

            controller.caricaDatiUtente();
            aggiornaListaBacheche();
            aggiornaListaToDo(titoloBachecaSelezionata);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Errore: " + ex.getMessage());
        }
    }


    /**
     * aggiorna la lista degli utenti registrati, recupera tutti gli username degli utenti tramite il controller,
     * li aggiunge alla lista escludendo l'utete attualmente loggato.
     */
    private void aggiornaListaUtenti() {
        try {
            listUtentiModel.clear();
            for (String utente : controller.getListaUtenti()) {
                if (!utente.equals(controller.getUtenteCorrente().getUsername())) {
                    listUtentiModel.addElement(utente);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'aggiornamento della lista utenti", e);
            JOptionPane.showMessageDialog(frame, "Errore nel caricamento della lista utenti: " + e.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Aggiorna la lista delle bacheche visualizzata in {@link #boardList}.
     * Recupera la lista aggiornata delle bacheche dell'utente corrente tramite il {@link Controller}.
     * Per ogni bacheca, costruisce una stringa che include il titolo della bacheca e se presenti,
     * gli username degli utenti con cui i To-Do di quella bacheca sono condivisi.
     * @throws SQLException Se si verifica un errore durante il recupero delle bacheche
     */
    private void aggiornaListaBacheche() throws SQLException {
        boardListModel.clear();
        for (String titolo : controller.getTitoliBacheche()) {
            boardListModel.addElement(titolo);
        }
    }

    /**
     * Aggiorna la lista dei To-Do visualizzata in {@link #todoList} per una bacheca specifica.
     * Recupera i To-Do associati alla bacheca specificata dall'utente corrente.
     * Ogni To-Do viene formattato in una stringa che include il suo titolo e se presente gli username degli utenti con cui è condiviso.
     * @param titoloBacheca di cui aggiornare la lista di to-do
     */
    public void aggiornaListaToDo(String titoloBacheca) {
        if (titoloBacheca != null && !titoloBacheca.isEmpty()) {
            String[] todoTitles = controller.getToDoFormattatiPerBacheca(titoloBacheca);
            todoList.setListData(todoTitles);
        }
    }


    /**
     * restituisce il jframe principale di quest'interfaccia
     * @return {@link JFrame} corrente.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * restituisce la lista che visualizza le bacheche
     * @return {@link JList} delle bacheche.
     */
    public JList<String> getBoardList() {
        return boardList;
    }
}
