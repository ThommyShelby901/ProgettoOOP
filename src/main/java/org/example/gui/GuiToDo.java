package org.example.gui;

import org.example.controller.Controller;
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

/**
 * Questa classe gestisce la visualizzazione e la gestione dei to-do all'interno di una bacheca specifica.
 * <p>
 * Permette agli utenti di creare, modificare, eliminare, trasferire, riordinare, visualizzare dettagli e gestire immagini
 * e condivisioni dei to-do. E' un po' il cuore dei to-do dove vengono svolte ed eseguite tutte le operazioni a loro riguardo.
 */
public class GuiToDo {
    /** pannello principale che contiene tutti gli elementi della gui*/
    private JPanel todoPanel;
    /** visualizza la lista dei {@link ToDo} all'interno della bacheca selezionata*/
    private JList<ToDo> todoList;
    /** componente per rendere la lista scorrevole*/
    JScrollPane scroll;
    /** bottone per aggiungere un nuovo to-do*/
    private JButton btnAggiungiToDo;
    /** bottone per modificare un to-do esistente dalla lista*/
    private JButton btnModificaToDo;
    /** bottone per eliminare un to-do esistente dalla lista*/
    private JButton btnEliminaToDo;
    /** bottone per tornare alla schermata {@link GuiHome}, ovvero la lista delle bacheche*/
    private JButton btnIndietro;
    /** bottone per trasferire una to-do da una bacheca all'altra*/
    private JButton btnTrasferisci;
    /** bottone per spostare un to-do all'interno della lista*/
    private JButton spostaButton;
    /** bottone per visualizzare e gestire le condivisioni di un to-do selezionato, apre la {@link GuiCondivisioni}*/
    private JButton btnVediCondivisioni;
    /** bottone per visualizzare tutti i dettagli di un to-do*/
    private JButton btnVisualizzaToDo;
    /** bottone per aggiungere o rimuovere un'immagine da un to-do*/
    private JButton btnAggiungiImmagine;

    /** il jframe principale di questa finestra dei to-do*/
    private final JFrame frame;
    /** il frame della finestra chiamante ovvero {@link GuiHome}, che verrà reso nuovamente visibile alla chiusura di questa finestra*/
    JFrame frameChiamante;
    /** istanza del {@link Controller} che gestisce la logica*/
    private final Controller controller;
    /** il titolo della bacheca corrente di cui si stanno gestendo i to-do*/
    private final String titoloBacheca;
    /** gestisce i dati della {@link #todoList}*/
    private final DefaultListModel<ToDo> modello = new DefaultListModel<>();

    /**
     * Costruttore che configura il modello della lista dei to-do, allega i listener dei bottoni e carica la lista di
     * to-do se presenti per la bacheca selezionata
     * @param controller istanza del {@link Controller} per l'interazione con la logica dell'applicazione
     * @param frameChiamante il frame della finestra con cui è stata aperta ({@link Controller}
     * @param titoloBacheca di cui visualizzare e gestire i to-do
     */
    public GuiToDo(Controller controller, JFrame frameChiamante, String titoloBacheca) {
        this.controller=controller;
        this.frameChiamante=frameChiamante;
        this.titoloBacheca=titoloBacheca;

        frame = new JFrame("Gestione To‑Do – " + titoloBacheca); //titolo dinamico in base alla bacheca
        frame.setContentPane(todoPanel);
        frame.setSize(650, 440);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        todoList.setModel(modello);
        configuraRenderer();
        aggiornaLista();

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

    /**
     * Carica e aggiorna la lista dei {@link ToDo} visualizzati nella {@link #todoList} recuperandoli dal {@link Controller} per la bacheca corrente
     */
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

    /**
     * Restituisce il {@link ToDo} attualmente selezionato nella {@link #todoList}.
     * Se nnon viene selezionato nessun to-do viene mostrato un messaggio all'utente e restituisce null
     * @return il to-do selezionato
     */
    private ToDo toDoSelezionato() {
        ToDo t = todoList.getSelectedValue();
        if (t == null) {
            info("Seleziona un To‑Do.");
            return null;
        }
        return t;
    }

    /**
     * gestisce l'aggiunta di un nuovo {@link ToDo}
     * <p>
     * Per creare un to-do vengono richiesti (titolo, descrizione, data scadenza, url, stato, colore sfondo)
     * Sono tutti elementi opzionali tranne il titolo che ho preferito evitare potesse essere null, per non avere un to-do
     * senza titolo che potrebbe far perdere il senso all'applicazione. Delega l'operazione di aggiunta al {@link Controller} e aggiorna la lista.
     */
    private void creaToDo() {
        String titolo = getInput("Titolo:", "", true);
        if (titolo == null) return;
        String descr = getInput("Descrizione:", "", false);
        String dataStr = getInput("Data Scadenza(anno-mese-giorno):", "", false);
        LocalDate dataScad = parseDate(dataStr);
        String url = getInput("URL:", "", false);
        Color colore = getColorFromDialog();
        StatoToDo stato = StatoToDo.NONCOMPLETATO;

        try {
            controller.creaToDo(titolo, descr, dataScad, url, stato, titoloBacheca, colore);
            aggiornaLista();
            info("To‑Do creato.");
        } catch (SQLException ex) {
            errore(ex);
        }
    }

    /**
     * modifica un {@link ToDo} esistente. Permette all'utente di modificare titolo, descrizione, data scadenza, URL, stato e colore.
     * Delega l'operazione di modifica al {@link Controller} e aggiorna la lista.
     */
    private void modificaToDo() {
        ToDo t = toDoSelezionato();
        if (t == null) return;

        String titolo = getInput("Titolo:", t.getTitoloToDo(), true);
        if (titolo == null) titolo = t.getTitoloToDo();

        String descr = getInput("Descrizione:", t.getDescrizioneToDo(), false);
        if (descr == null || descr.trim().isEmpty()) {
            descr = t.getDescrizioneToDo();
        }

        String dataStr = getInput("Data Scadenza(anno-mese-giorno):",
                t.getDataScadenza() != null ? t.getDataScadenza().toString() : "", false);
        LocalDate dataScad = (dataStr == null || dataStr.isEmpty()) ? t.getDataScadenza() : parseDate(dataStr);

        String url = getInput("URL:", t.getUrl() != null ? t.getUrl() : "", false);
        if (url != null && url.isEmpty()) url = t.getUrl();

        StatoToDo stato = getStatoFromDialog();
        Color colore = getColorFromDialog();
        if (colore == null) {
            colore = Color.decode(t.getColoreSfondo()); // lo riconverti in Color se già salvato come String
        }

        String coloreHex = colorToHex(colore);

        t.setTitoloToDo(titolo);
        t.setDescrizioneToDo(descr);
        t.setDataScadenza(dataScad);
        t.setUrl(url);
        t.setStatoToDo(stato);
        t.setColoreSfondo(coloreHex);

        try {
            controller.modificaToDo(t, titolo, descr, dataScad, url, stato, colore);
            controller.aggiornaToDo(t);

            int index = todoList.getSelectedIndex();
            aggiornaLista();
            if (index >= 0) todoList.setSelectedIndex(index);

            info("To‑Do modificato con successo.");

            visualizzaDettagli();
        } catch (SQLException ex) {
            errore(ex);
        }
    }

    private String colorToHex(Color color) {
        if (color == null) return "#ffffff"; // o un colore di default
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }


    /**
     * elimina un {@link ToDo} selezionato
     * <p>
     * Chiede conferma all'utente prima di procedere e delega l'operazione al {@link Controller}
     */
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

    /**
     * Gestisce il cambio di ordine di un {@link ToDo} all'interno della lista corrente.
     * Se il numero di to-do è minore o inferiore a uno mostra un messaggio per indicare che non è possibile effettuare spostamento
     * Stessa cosa se la posizione non è valida.
     */
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

    /**
     * Gestisce il trasferimento di un {@link ToDo} selezionato ad un'altra bacheca.
     * Permette all'utente di scegliere una bacheca di destinazione tra quelle esistenti
     * e delega l'operazione al {@link Controller}.
     */
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

    /**
     * Gestisce l'aggiunta di un immagine a un {@link ToDo} selezionato
     * Apre un {@link JFileChooser} per permettere all'utente di selezionare un file immagine.
     * Se un'immagine viene selezionata, delega al {@link Controller} il compito di aggiungerla
     */
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

    /**
     * Apre la finestra {@link GuiCondivisioni} per gestire le condivisioni dei to-do, nascondendo il frame corrente.
     * All'apertura imposta la bacheca corrente come selezionata e carica i to-do.
     * Alla chiusura della finestra delle condivisioni, il frame corrente viene riattivato e la lista viene aggiornata
     * per riflettere eventuali cambiamenti.
     */
    private void apriGestioneCondivisioni() {
        frame.setVisible(false);
        try {
            GuiCondivisioni b = new GuiCondivisioni(controller, frame);
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

    /**
     * Apre un dialogo {@link GuiVisualizzazione} per visualizzare i dettagli completi
     */
    private void visualizzaDettagli() {
        ToDo t = toDoSelezionato();
        if (t != null) {
            new GuiVisualizzazione(frame, t.getTitoloToDo(), t.getBacheca(), controller);
        }
    }

    /**
     * Configura il renderer delle celle per la {@link #todoList}, influenzando l'aspetto visivo dei {@link ToDo} in base al loro stato.
     * Se un to-do è completato viene mostrato con grigio, se la data di scadenza è passata ed il to-do non è completato
     * con rosso e tutti gli altri in nero.
     */
    private void configuraRenderer() {
        todoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ToDo t) {
                    if (t.getStatoToDo() == StatoToDo.COMPLETATO) {
                        setForeground(Color.GRAY);  // completati in grigio
                    }
                    else if (t.getDataScadenza() != null && t.getDataScadenza().isBefore(LocalDate.now())) {
                        setForeground(Color.RED);   // scaduti non completati in rosso
                    }
                    else {
                        setForeground(Color.BLACK); // tutti gli altri neri
                    }
                }
                return this;
            }
        });
    }

    /**
     * Richiede un input testuale all'utente tramite JOptionPane, finchè la condizione non è rispettata viene ripetuto,
     * ammenocchè il campo non possa essere null.
     * @param msg messaggio da visualizzare nel dialogo
     * @param init valore iniziale del campo input
     * @param req se il campo è obbligatorio requisito true, altrimenti false
     * @return stringa inserita dall'utente.
     */
    private String getInput(String msg, String init, boolean req) {
        while (true) {
            String s = JOptionPane.showInputDialog(frame, msg, init);
            if (s == null) return null; // Annulla = esce

            s = s.trim();

            if (!req || !s.isEmpty()) return s;

            // Se req è true e input è vuoto, mostra messaggio e ripeti
            JOptionPane.showMessageDialog(frame, "Campo obbligatorio!", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * prende una stringa nel formato anno-mese-giorno e la converte in {@link LocalDate}.
     * Se la stringa è vuota viene restituito null, se il formato non è valido viene mostrato un messaggio di errore.
     * @param ds stringa della data
     * @return oggetto LocalDate corrispondente o null se è vuoto
     */
    private LocalDate parseDate(String ds) {
        if (ds == null || ds.isEmpty()) return null;
        try { return LocalDate.parse(ds); }
        catch (Exception _) { JOptionPane.showMessageDialog(frame, "Data non valida."); return null; }
    }

    /**
     * mostra un dialogo di conerma all'utente per determinare lo stato di un to-do
     * @return {@link StatoToDo#COMPLETATO} se l'utente seleziona "Sì", altrimenti {@link StatoToDo#NONCOMPLETATO}.
     */
    private StatoToDo getStatoFromDialog() {
        return JOptionPane.showConfirmDialog(frame, "Completato?", "", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;
    }

    /**
     * Mostra un selettore di colori {@link JColorChooser} e restituisce il colore scelto dall'utente.
     * Se l'utente annulla, viene restituito il colore bianco come valore predefinito.
     * @return colore selezionato dall'utente
     */
    private Color getColorFromDialog() {
        return JColorChooser.showDialog(frame, "Colore di sfondo", Color.WHITE);
    }

    /**
     * Mostra un messaggio informativo all'utente
     * @param m il messaggio da visualizzare
     */
    private void info(String m) {
        JOptionPane.showMessageDialog(frame, m, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostra un messaggio di errore all'utente
     * @param e L'eccezione che ha causato l'errore, il cui messaggio verrà mostrato all'utente.
     */
    private void errore(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
    }
}