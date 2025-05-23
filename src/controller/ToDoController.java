package controller;

import gui.BachecaGUI;
import model.Bacheca;
import model.StatoToDo;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.util.List;

public class ToDoController {
    private BachecaGUI bachecaGUI;
    private Bacheca board;
    private Utente utente;
    private List<Utente> listaUtentiGlobali;

    public ToDoController(Utente utente, Bacheca board, BachecaGUI bachecaGUI ) {
        this.utente = utente;
        this.board = board;
        this.bachecaGUI = bachecaGUI;
        this.listaUtentiGlobali = Utente.getListaUtentiGlobali();

        initController();
        refreshToDoList();
    }

    private void initController() {
        bachecaGUI.getBtnCrea().addActionListener(e -> creaToDo());
        bachecaGUI.getBtnModifica().addActionListener(e -> modificaToDo());
        bachecaGUI.getBtnElimina().addActionListener(e -> eliminaToDo());
        bachecaGUI.getBtnSposta().addActionListener(e -> azioneSpostaToDo());
        bachecaGUI.getBtnTrasferisci().addActionListener(e -> trasferisciToDo());
        bachecaGUI.getBtnCerca().addActionListener(e -> cercaToDo());
        bachecaGUI.getTodoList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = bachecaGUI.getTodoList().getSelectedIndex();
                if (selectedIndex != -1) {
                    String titoloToDo = bachecaGUI.getTodoListModel().getElementAt(selectedIndex);
                    if (!"Nessun ToDo presente.".equals(titoloToDo)) {
                        ToDo todoSelezionato = board.getToDoByTitolo(titoloToDo);
                        aggiornaListaCondivisioni(todoSelezionato);
                    } else {
                        bachecaGUI.getCondivisoListModel().clear();
                    }
                } else {
                    bachecaGUI.getCondivisoListModel().clear();
                }
            }
        });

        // Listener per bottone aggiungi condivisione
        bachecaGUI.getBtnAggiungiCondivisione().addActionListener(e -> aggiungiCondivisione());

        // Listener per bottone rimuovi condivisione
        bachecaGUI.getBtnRimuoviCondivisione().addActionListener(e -> rimuoviCondivisione());
    }

    private void aggiornaListaCondivisioni(ToDo todo) {
        DefaultListModel<String> model = bachecaGUI.getCondivisoListModel();
        model.clear();
        if (todo != null) {
            for (Utente u : todo.getCondivisoCon()) {
                model.addElement(u.getNome()); // o getUsername(), come preferisci
            }
        }
    }

    private void aggiungiCondivisione() {
        int selectedIndex = bachecaGUI.getTodoList().getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Seleziona un ToDo prima!");
            return;
        }

        String titoloToDo = bachecaGUI.getTodoListModel().getElementAt(selectedIndex);
        if ("Nessun ToDo presente.".equals(titoloToDo)) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Nessun ToDo selezionato valido!");
            return;
        }

        ToDo todoSelezionato = board.getToDoByTitolo(titoloToDo);

        String nomeUtente = JOptionPane.showInputDialog(bachecaGUI.getFrame(), "Inserisci nome utente da condividere:");
        if (nomeUtente == null || nomeUtente.trim().isEmpty()) return;

        Utente utenteDaAggiungere = getUtenteByNome(nomeUtente.trim());
        if (utenteDaAggiungere == null) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Utente non trovato!");
            return;
        }

        todoSelezionato.aggiungiCondivisione(utente, utenteDaAggiungere);
        aggiornaListaCondivisioni(todoSelezionato);
    }

    private void rimuoviCondivisione() {
        int selectedToDoIndex = bachecaGUI.getTodoList().getSelectedIndex();
        int selectedUserIndex = bachecaGUI.getCondivisoList().getSelectedIndex();

        if (selectedToDoIndex == -1 || selectedUserIndex == -1) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Seleziona un ToDo e un utente da rimuovere!");
            return;
        }

        String titoloToDo = bachecaGUI.getTodoListModel().getElementAt(selectedToDoIndex);
        if ("Nessun ToDo presente.".equals(titoloToDo)) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Nessun ToDo selezionato valido!");
            return;
        }

        ToDo todoSelezionato = board.getToDoByTitolo(titoloToDo);

        String nomeUtente = bachecaGUI.getCondivisoListModel().getElementAt(selectedUserIndex);
        Utente utenteDaRimuovere = getUtenteByNome(nomeUtente);
        if (utenteDaRimuovere == null) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Utente non trovato!");
            return;
        }

        todoSelezionato.eliminaCondivisione(utente, utenteDaRimuovere);
        aggiornaListaCondivisioni(todoSelezionato);
    }

    public Utente getUtenteByNome(String nome) {
        return listaUtentiGlobali.stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }



    public void refreshToDoList() {
        DefaultListModel<String> model = bachecaGUI.getTodoListModel();
        model.clear();

        List<ToDo> allToDo = utente.getListaToDo();
        boolean hasToDo = false;

        for (ToDo t : allToDo) {
            if (t.getBacheca() != null &&
                    t.getBacheca().getTitoloBacheca().equalsIgnoreCase(board.getTitoloBacheca())) {
                model.addElement(t.getTitoloToDo());
                hasToDo = true;
            }
        }

        if (!hasToDo) {
            model.addElement("Nessun ToDo presente.");
        }
    }

    private void creaToDo() {
        String titolo = JOptionPane.showInputDialog(bachecaGUI.getFrame(), "Inserisci il titolo del ToDo:");
        if (titolo == null || titolo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Titolo non valido!");
            return;
        }

        String descrizione = JOptionPane.showInputDialog(bachecaGUI.getFrame(), "Inserisci la descrizione:");
        if (descrizione == null) descrizione = "";

        // Puoi aggiungere altri campi o usare default
        String sfondo = null;
        String coloreSfondo = null;
        String dataScadenza = null;
        String url = null;
        StatoToDo stato = StatoToDo.NONCOMPLETATO;

        utente.creaToDo(titolo, descrizione, sfondo, coloreSfondo, dataScadenza, url, stato, board);
        refreshToDoList();
    }

    private void modificaToDo() {
        int idx = bachecaGUI.getTodoList().getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Seleziona un ToDo da modificare!");
            return;
        }

        String titolo = bachecaGUI.getTodoListModel().getElementAt(idx);
        if ("Nessun ToDo presente.".equals(titolo)) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Nessun ToDo da modificare!");
            return;
        }

        ToDo toModify = utente.cercaToDoPerTitoloEBoard(titolo, board);
        if (toModify == null) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo non trovato!");
            return;
        }

        new ToDoDetailController(toModify, this::refreshToDoList);
    }

    private void eliminaToDo() {
        int idx = bachecaGUI.getTodoList().getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Seleziona un ToDo da eliminare!");
            return;
        }

        String titolo = bachecaGUI.getTodoListModel().getElementAt(idx);
        if ("Nessun ToDo presente.".equals(titolo)) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Nessun ToDo da eliminare!");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(
                bachecaGUI.getFrame(),
                "Sei sicuro di voler eliminare questo ToDo?",
                "Elimina ToDo",
                JOptionPane.YES_NO_OPTION);

        if (conferma != JOptionPane.YES_OPTION) return;

        ToDo toDelete = utente.cercaToDoPerTitoloEBoard(titolo, board);
        if (toDelete == null) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo non trovato.");
            return;
        }

        utente.eliminaToDo(toDelete);
        JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo eliminato.");
        refreshToDoList();
    }

    private void azioneSpostaToDo() {
        String daSpostare = JOptionPane.showInputDialog(bachecaGUI.getFrame(), "Titolo del ToDo da spostare:");
        if (daSpostare == null || daSpostare.trim().isEmpty()) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Titolo non valido.");
            return;
        }

        String davantiA = JOptionPane.showInputDialog(bachecaGUI.getFrame(),
                "Titolo del ToDo davanti al quale vuoi spostarlo (vuoto per prima posizione):");

        spostaToDo(daSpostare.trim(), davantiA != null ? davantiA.trim() : "");
    }

    public void spostaToDo(String titoloDaSpostare, String titoloPosizione) {
        List<ToDo> lista = board.getListaToDo();
        ToDo daSpostare = null;

        for (ToDo t : lista) {
            if (t.getTitoloToDo().equalsIgnoreCase(titoloDaSpostare)) {
                daSpostare = t;
                break;
            }
        }

        if (daSpostare == null) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo da spostare non trovato.");
            return;
        }

        lista.remove(daSpostare);

        if (titoloPosizione == null || titoloPosizione.isEmpty()) {
            lista.add(0, daSpostare);
        } else {
            int pos = 0;
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getTitoloToDo().equalsIgnoreCase(titoloPosizione)) {
                    pos = i;
                    break;
                }
            }
            lista.add(pos, daSpostare);
        }

        refreshToDoList();
    }

    private void cercaToDo() {
        String query = JOptionPane.showInputDialog(bachecaGUI.getFrame(), "Inserisci il titolo del ToDo da cercare:");
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Inserisci un criterio valido!");
            return;
        }

        List<ToDo> boardTodos = board.getListaToDo();
        int foundIndex = -1;

        for (int i = 0; i < boardTodos.size(); i++) {
            if (boardTodos.get(i).getTitoloToDo().equalsIgnoreCase(query.trim())) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex != -1) {
            bachecaGUI.getTodoList().setSelectedIndex(foundIndex);
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo trovato e selezionato.");
        } else {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo non trovato.");
        }
    }

    private void trasferisciToDo() {
        int idx = bachecaGUI.getTodoList().getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Seleziona un ToDo!");
            return;
        }

        String titolo = bachecaGUI.getTodoListModel().getElementAt(idx);
        if ("Nessun ToDo presente.".equals(titolo)) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Nessun ToDo!");
            return;
        }

        ToDo t = utente.cercaToDoPerTitoloEBoard(titolo, board);
        if (t == null) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo non trovato!");
            return;
        }

        String dest = JOptionPane.showInputDialog(bachecaGUI.getFrame(), "Nome bacheca destinazione:");
        if (dest == null || dest.trim().isEmpty()) {
            JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "Nome non valido!");
            return;
        }

        utente.trasferisciToDo(t, dest.trim());
        JOptionPane.showMessageDialog(bachecaGUI.getFrame(), "ToDo trasferito.");
        refreshToDoList();
    }
}
