package org.example.gui;

import org.example.controller.Controller;
import org.example.model.ToDo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Questa classe rappresenta una finestra di dialogo modale utilizzata per visualizzare i dettagli completi di un {@link ToDo} specifico.
 * permette di visualizzare titolo, descrizione, data scaenza, url, colore sfondo e immagine se presente.
 * Quindi tutti gli attributi sono visualizzati in questa finestra, lo stato è visualizzato direttamente nella rappresentazione
 * testuale del to-do nella home.
 */
public class GuiVisualizzazione {
    /** finestra di dialogo principale per la visualizzazione di to-do*/
    private final JDialog dialog;
    /** istanza del controller che gestisce la logica*/
    private final Controller controller;
    /** titolo del to-do di cui visualizzare i dettagli*/
    private final String titoloToDo;
    /** titolo della bacheca a cui appartiene il to-do*/
    private final String titoloBacheca;
    /** costante per i messaggi di errore*/
    private static final String ERRORE_TITOLO = "Errore";

    /** pannello principale della gui */
    private JPanel pnlDettagliToDo;
    /** Etichetta per visualizzare il titolo del To-Do */
    private JLabel lblTitolo;
    /** Etichetta per visualizzare la descrizione del To-Do */
    private JLabel lblDescrizione;
    /** Etichetta per visualizzare la data di scadenza del To-Do */
    private JLabel lblData;
    /** Etichetta per visualizzare l'url del To-Do */
    private JLabel lblUrl;
    /** Etichetta per visualizzare l'immagine allegata al To-Do */
    private JLabel lblImmagine;
    /** bottone per chiudere la finestra di dialogo*/
    private JButton btnChiudi;
    /** bottone per rimuovere l'immagine allegata al to-do*/
    private JButton btnRimuoviImmagine;

    /**
     * costruttore che inizializza la finestra di dialogo modale, carica i dettagli dei to-do necessari e
     * configura gli ActionListener
     * @param parent frame della finestra padre
     * @param titoloToDo da visualizzare
     * @param titoloBacheca a cui il to-do appartiene
     * @param controller istanza del controller per l'interazione con la logica dell'applicazione
     */
    public GuiVisualizzazione(JFrame parent, String titoloToDo, String titoloBacheca, Controller controller) {
        this.controller = controller;
        this.titoloToDo = titoloToDo;
        this.titoloBacheca = titoloBacheca;

        dialog = new JDialog(parent, "Dettagli To-Do", true);
        dialog.setSize(800, 800);
        dialog.setLocationRelativeTo(parent);
        dialog.setContentPane(pnlDettagliToDo);

        aggiornaDettagliToDo();

        // Rimuovi ogni interattività dal titolo
        lblTitolo.setCursor(Cursor.getDefaultCursor());

        btnChiudi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        btnRimuoviImmagine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ToDo todo = controller.getToDoPerTitoloEBoard(titoloToDo, titoloBacheca);
                    if (todo.getPercorsoImmagine() == null || todo.getPercorsoImmagine().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Nessuna immagine presente per questo ToDo",
                                "Informazione", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        rimuoviImmagine();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Errore nel recupero del ToDo: " + ex.getMessage(),
                            ERRORE_TITOLO, JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.setVisible(true);
    }

    /**
     * recupera i dettagli completi del to-do tramite il {@link Controller}, popola le etichette con informazioni pertinenti.
     * Una Map<String, Object> è una mappa che associa una chiave di tipo String a un valore generico di tipo Object.
     * Usata per comodità e flessibilità, carica anche l'immagine associata, se presente.
     */
    private void aggiornaDettagliToDo() {
        try {
            Map<String, Object> dettagli = controller.getDettagliCompletiToDo(titoloToDo, titoloBacheca);

            String coloreHex = (String) dettagli.get("coloreSfondo");
            Color colore = (coloreHex != null) ? Color.decode(coloreHex) : UIManager.getColor("Panel.background");
            pnlDettagliToDo.setBackground(colore);

            lblTitolo.setText("Titolo: " + dettagli.getOrDefault("titolo", "N/A"));
            lblDescrizione.setText("Descrizione: " + dettagli.getOrDefault("descrizione", "Nessuna"));
            lblData.setText("Scadenza: " + dettagli.getOrDefault("dataScadenza", "Nessuna"));

            String url = (String) dettagli.get("url");
            if (url != null && !url.trim().isEmpty()) {
                lblUrl.setText("<html><a href=''>" + url + "</a></html>");
                lblUrl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                lblUrl.setForeground(Color.BLUE);

                // Aggiungi MouseListener per gestire il click sul link
                lblUrl.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (IOException | URISyntaxException ex) {
                            JOptionPane.showMessageDialog(dialog, "Impossibile aprire il link: " + ex.getMessage(),
                                    ERRORE_TITOLO, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            } else {
                lblUrl.setText("Nessun link");
                lblUrl.setCursor(Cursor.getDefaultCursor());
                lblUrl.setForeground(Color.BLACK);
            }

            String percorsoImmagine = (String) dettagli.get("percorsoImmagine");
            caricaImmagine(percorsoImmagine);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Errore nel recupero dei dettagli del ToDo: " + ex.getMessage(),
                    ERRORE_TITOLO, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carica un'immagine dal percorso specificato e la visualizza nella {@link #lblImmagine}.
     * Se il percorso è valido e l'immagine può essere caricata, la scala per adattarsi alle dimensioni
     * della label. In caso di errore o percorso vuoto, l'icona della label viene rimossa.
     * @param percorsoImmagine Il percorso del file immagine da caricare.
     */
    private void caricaImmagine(String percorsoImmagine) {
        if (percorsoImmagine == null || percorsoImmagine.trim().isEmpty()) {
            lblImmagine.setIcon(null);
            lblImmagine.setText("Nessuna immagine");
            lblImmagine.setHorizontalAlignment(SwingConstants.CENTER);
            return;
        }

        try {
            ImageIcon icon = new ImageIcon(percorsoImmagine);
            Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblImmagine.setIcon(new ImageIcon(scaledImage));
            lblImmagine.setText(""); // Rimuove testo se presente
        } catch (Exception _) {
            lblImmagine.setIcon(null);
            lblImmagine.setText("Errore nel caricamento dell'immagine");
        }
    }

    /**
     * Delega l'operazione di rimozione dell'immagine al {@link Controller}.
     * Dopo la rimozione, aggiorna i dettagli del To-Do nell'interfaccia utente
     * e mostra un messaggio di successo all'utente.
     */
    private void rimuoviImmagine() {
        try {
            controller.rimuoviImmagineDaToDo(titoloToDo, titoloBacheca);
            aggiornaDettagliToDo();
            JOptionPane.showMessageDialog(dialog, "Immagine rimossa con successo!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Errore durante la rimozione: " + ex.getMessage(),
                    ERRORE_TITOLO, JOptionPane.ERROR_MESSAGE);
        }
    }

}