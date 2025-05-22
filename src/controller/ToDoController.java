package controller;

import gui.ToDoFrame;
import model.Bacheca;
import model.StatoToDo;
import model.ToDo;
import model.Utente;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoController {
    private ToDoFrame toDoFrame;
    private Bacheca board;    // La bacheca corrente
    private Utente utente;    // L'utente loggato, che gestisce la lista globale dei ToDo

    public ToDoController(ToDoFrame toDoFrame, Bacheca board, Utente utente) {
        this.toDoFrame = toDoFrame;
        this.board = board;
        this.utente = utente;
        initController();
        refreshToDoList();
    }

    // Associa un ActionListener a ciascun pulsante della ToDoFrame
    private void initController() {
        toDoFrame.getBtnCrea().addActionListener(e -> creaToDo());
        toDoFrame.getBtnModifica().addActionListener(e -> modificaToDo());
        toDoFrame.getBtnElimina().addActionListener(e -> eliminaToDo());
        toDoFrame.getBtnTrasferisci().addActionListener(e -> trasferisciToDo());
        toDoFrame.getBtnSposta().addActionListener(e -> spostaToDo());
        toDoFrame.getBtnCerca().addActionListener(e -> cercaToDo());
        toDoFrame.getBtnBack().addActionListener(e -> toDoFrame.dispose());
    }

    // Aggiorna la JList filtrando i ToDo dell'utente per quelli appartenenti alla bacheca corrente
    private void refreshToDoList() {
        DefaultListModel<String> model = toDoFrame.getTodoListModel();
        model.clear();
        List<ToDo> allTodos = utente.getListaToDo(); // Ora getListaToDo() è definito nella classe Utente
        List<ToDo> boardTodos = new ArrayList<>();
        for (ToDo t : allTodos) {
            if (t.getBacheca() != null &&
                    t.getBacheca().getTitoloBacheca().equalsIgnoreCase(board.getTitoloBacheca())) {
                boardTodos.add(t);
            }
        }
        if (boardTodos.isEmpty()) {
            model.addElement("Nessun ToDo presente.");
            return;
        }
        for (ToDo t : boardTodos) {
            model.addElement(t.getTitoloToDo());
        }
    }

    // Crea un nuovo ToDo e lo aggiunge alla lista globale tramite il metodo dell'utente
    private void creaToDo() {
        String titolo = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Inserisci il titolo del ToDo:");
        if (titolo == null || titolo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Titolo non valido!");
            return;
        }
        String descrizione = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Inserisci la descrizione:");
        if (descrizione == null) {
            descrizione = "";
        }

        // Altri campi opzionali (puoi aggiungere dialog se vuoi)
        String sfondo = null; // esempio placeholder
        String coloreSfondo = null;
        String dataScadenza = null;
        String url = null;
        StatoToDo stato = StatoToDo.COMPLETATO; // o uno stato di default

        utente.creaToDo(titolo, descrizione, sfondo, coloreSfondo, dataScadenza, url, stato, board);

        refreshToDoList();
    }

    // Modifica un ToDo selezionato delegando al metodo già esistente in Utente
    private void modificaToDo() {
        int index = toDoFrame.getTodoList().getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Seleziona un ToDo da modificare!");
            return;
        }
        String currentTitle = toDoFrame.getTodoListModel().getElementAt(index);
        if (currentTitle.equals("Nessun ToDo presente.")) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Nessun ToDo da modificare!");
            return;
        }
        // Utilizza il metodo di ricerca del modello
        ToDo selectedToDo = utente.cercaToDoPerTitoloEBoard(currentTitle, board);
        if (selectedToDo == null) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo non trovato!");
            return;
        }
        String newTitle = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Inserisci il nuovo titolo:", selectedToDo.getTitoloToDo());
        if (newTitle == null || newTitle.trim().isEmpty()) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Titolo non valido!");
            return;
        }
        String newDesc = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Inserisci la nuova descrizione:", selectedToDo.getDescrizioneToDo());
        if (newDesc == null) {
            newDesc = "";
        }
        // La modifica viene delegata al metodo dell'utente
        utente.modificaToDo(selectedToDo, utente, newTitle, newDesc, null, null, null, null, null);
        JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo modificato con successo.");
        refreshToDoList();
    }



    private void eliminaToDo() {
        int index = toDoFrame.getTodoList().getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Seleziona un ToDo da eliminare!");
            return;
        }

        String title = toDoFrame.getTodoListModel().getElementAt(index);
        if ("Nessun ToDo presente.".equals(title)) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Nessun ToDo da eliminare!");
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(
                toDoFrame.getFrame(),
                "Sei sicuro di voler eliminare questo ToDo?",
                "Elimina ToDo",
                JOptionPane.YES_NO_OPTION
        );

        if (conferma != JOptionPane.YES_OPTION) {
            return;
        }

        ToDo selectedToDo = utente.cercaToDoPerTitoloEBoard(title, board);

        if (selectedToDo == null) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo non trovato.");
            return;
        }

        utente.eliminaToDo(selectedToDo);
        JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo eliminato correttamente.");
        refreshToDoList();
    }



    private void spostaToDo() {
        int i = toDoFrame.getTodoList().getSelectedIndex();
        if (i == -1) return;

        String title = toDoFrame.getTodoListModel().getElementAt(i);
        if ("Nessun ToDo presente.".equals(title)) return;

        ToDo t = utente.cercaToDoPerTitoloEBoard(title, board);
        if (t == null) return;

        String posStr = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Nuova posizione:");
        if (posStr == null) return;

        try {
            int newPos = Integer.parseInt(posStr);
            int oldPos = board.getListaToDo().indexOf(t);
            if (oldPos == -1) return;

            board.spostaToDo(oldPos, newPos);
            refreshToDoList();
            toDoFrame.getTodoList().setSelectedIndex(newPos);
        } catch (NumberFormatException e) { }
    }



    // Cerca un ToDo per titolo all'interno della bacheca corrente e lo seleziona se trovato
    private void cercaToDo() {
        String query = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Inserisci il titolo del ToDo da cercare:");
        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Inserisci un criterio valido!");
            return;
        }
        List<ToDo> boardTodos = new ArrayList<>();
        for (ToDo t : utente.getListaToDo()) {
            if (t.getBacheca() != null &&
                    t.getBacheca().getTitoloBacheca().equalsIgnoreCase(board.getTitoloBacheca())) {
                boardTodos.add(t);
            }
        }
        int foundIndex = -1;
        for (int i = 0; i < boardTodos.size(); i++) {
            if (boardTodos.get(i).getTitoloToDo().equalsIgnoreCase(query.trim())) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != -1) {
            toDoFrame.getTodoList().setSelectedIndex(foundIndex);
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo trovato e selezionato.");
        } else {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo non trovato.");
        }
    }

    private void trasferisciToDo() {
        int idx = toDoFrame.getTodoList().getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Seleziona un ToDo!");
            return;
        }
        String title = toDoFrame.getTodoListModel().getElementAt(idx);
        if ("Nessun ToDo presente.".equals(title)) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Nessun ToDo!");
            return;
        }
        ToDo t = utente.cercaToDoPerTitoloEBoard(title, board);
        if (t == null) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo non trovato!");
            return;
        }
        String dest = JOptionPane.showInputDialog(toDoFrame.getFrame(), "Nome bacheca destinazione:");
        if (dest == null || dest.trim().isEmpty()) {
            JOptionPane.showMessageDialog(toDoFrame.getFrame(), "Nome non valido!");
            return;
        }
        utente.trasferisciToDo(t, dest.trim());
        JOptionPane.showMessageDialog(toDoFrame.getFrame(), "ToDo trasferito.");
        refreshToDoList();
    }

}
