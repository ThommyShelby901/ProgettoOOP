package org.example.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Utente {
    private final String username;
    private final String password;
    private final List<ToDo> listaToDo;
    private List<Bacheca> listaBacheche;

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();
        this.listaBacheche = new ArrayList<>();
    }

    public List<Bacheca> getListaBacheche() {
        return listaBacheche;
    }

    public List<ToDo> getListaToDo(){return listaToDo; }


    public Bacheca creaBacheca(String titolo, String descrizione) {
        Bacheca nuova = new Bacheca(titolo, descrizione);
        this.listaBacheche.add(nuova); // 🔥 La bacheca viene aggiunta SOLO all'utente corrente!
        return nuova;
    }

    public static List<Bacheca> inizializzaBacheche() {
        List<Bacheca> bachechePredefinite = new ArrayList<>();
        bachechePredefinite.add(new Bacheca("Università", "Attività di studio"));
        bachechePredefinite.add(new Bacheca("Lavoro", "Attività lavorative"));
        bachechePredefinite.add(new Bacheca("Tempo Libero", "Impegni personali"));
        return bachechePredefinite;
    }





    public String getPassword(){
        return password;
    }


    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        for (Bacheca b : listaBacheche) {
            if (b.getTitoloBacheca().equalsIgnoreCase(titoloCorrente)) {
                b.setTitoloBacheca(nuovoTitolo);
                b.setDescrizioneBacheca(nuovaDescrizione);
                return;
            }
        }
    }

    public void eliminaBacheca(String titolo) {
        listaBacheche.removeIf(b -> b.getTitoloBacheca().equalsIgnoreCase(titolo));
    }

    public ToDo creaToDo(String titolo, String descrizione,
                         String dataScadenza, String url, StatoToDo stato, String titoloBacheca) {
        if (titolo == null || descrizione == null || titoloBacheca == null) {
            return null;
        }

        // Validazione della data di scadenza
        if (dataScadenza == null || dataScadenza.trim().isEmpty()) {
            return null;
        }

        ToDo nuovoToDo = new ToDo();
        nuovoToDo.setTitoloToDo(titolo);
        nuovoToDo.setDescrizioneToDo(descrizione);
        nuovoToDo.setDataScadenza(dataScadenza); // Il controllo si sposta nel setter
        nuovoToDo.setUrl(url);
        nuovoToDo.setStatoToDo(stato);
        nuovoToDo.setBacheca(titoloBacheca);
        nuovoToDo.setAutore(this); // Imposta l'utente come autore

        listaToDo.add(nuovoToDo); // Aggiunge alla lista dell'utente
        // Aggiunge alla lista della Bacheca

        return nuovoToDo;
    }


    public void modificaToDo(ToDo todo, Utente utenteRichiedente,
                             String nuovoTitolo, String nuovaDescrizione,
                             String nuovaDataScadenza, String nuovoUrl,
                             StatoToDo nuovoStato) {

        if (!listaToDo.contains(todo)) {
            return;
        }
        // Controllo che l'utente sia l'autore
        if (!todo.getAutore().equals(utenteRichiedente)) {
            return;
        }
        // Modifiche (solo se i parametri non sono null)
        if (nuovoTitolo != null) todo.setTitoloToDo(nuovoTitolo);
        if (nuovaDescrizione != null) todo.setDescrizioneToDo(nuovaDescrizione);
        if (nuovaDataScadenza != null) todo.setDataScadenza(nuovaDataScadenza);
        if (nuovoUrl != null) todo.setUrl(nuovoUrl);
        if (nuovoStato != null) todo.setStatoToDo(nuovoStato);

    }

    public ToDo cercaToDoPerTitoloEBoard(String titolo, String titoloBacheca) {
        if (titolo == null || titoloBacheca == null) return null;

        for (ToDo t : listaToDo) {
            if (t.getBacheca() != null &&
                    t.getBacheca().equalsIgnoreCase(titoloBacheca) && // 🔥 Ora confrontiamo le stringhe
                    t.getTitoloToDo() != null &&
                    t.getTitoloToDo().equalsIgnoreCase(titolo)) {
                return t;
            }
        }
        return null;
    }



    public void eliminaToDo(ToDo todo) {
        if (todo == null) {
            return;
        }
        if (!todo.getAutore().equals(this)) {
            return;
        }

        // 🔹 Troviamo la bacheca giusta cercandola nella lista interna
        String titoloBacheca = todo.getBacheca();
        for (Bacheca bacheca : getListaBacheche()) { // 🔥 Scorriamo la lista direttamente nel Model
            if (bacheca.getTitoloBacheca().equalsIgnoreCase(titoloBacheca)) {
                bacheca.rimuoviToDo(todo);
                break; // 🔥 Fermiamo il ciclo una volta trovata la bacheca giusta
            }
        }
    }


    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) {
        if (todo == null || !todo.getAutore().equals(this)) return;

        // 🔹 Troviamo la bacheca attuale del To-Do cercando il titolo
        String titoloBachecaCorrente = todo.getBacheca();
        for (Bacheca b : getListaBacheche()) {
            if (b.getTitoloBacheca().equalsIgnoreCase(titoloBachecaCorrente)) {
                b.rimuoviToDo(todo);
                break; // 🔥 Fermiamo il ciclo una volta trovata la bacheca giusta
            }
        }

        // 🔹 Troviamo la nuova bacheca e trasferiamo il To-Do
        for (Bacheca b : getListaBacheche()) {
            if (b.getTitoloBacheca().equalsIgnoreCase(nomeBachecaDestinazione)) {
                b.aggiungiToDo(todo);
                todo.setBacheca(nomeBachecaDestinazione); // 🔥 Ora assegniamo il nuovo titolo della bacheca
                return;
            }
        }
    }

    public void spostaToDo(ToDo toDoDaSpostare, String titoloBacheca, int nuovaPosizione) {
        Bacheca board = getBachecaByTitolo(titoloBacheca);
        if (board == null) {
            System.out.println("❌ Bacheca non trovata.");
            return;
        }

        if (!board.getListaToDo().contains(toDoDaSpostare) || nuovaPosizione < 0 || nuovaPosizione >= board.getListaToDo().size()) {
            System.out.println("❌ Posizione non valida.");
            return;
        }

        // 🔹 Sposta il To-Do nella lista locale
        board.getListaToDo().remove(toDoDaSpostare);
        board.getListaToDo().add(nuovaPosizione, toDoDaSpostare);
    }




    public void aggiungiToDoCondiviso(ToDo todo) {
        if (!listaToDo.contains(todo)) {
            listaToDo.add(todo);
        }
    }

    public void rimuoviToDoCondiviso(ToDo todo) {
        listaToDo.remove(todo);
    }

    public Bacheca getBachecaByTitolo(String titolo) {
        for (Bacheca bacheca : getListaBacheche()) { // lista statica di tutte le bacheche
            if (bacheca.getTitoloBacheca().equalsIgnoreCase(titolo)) {
                return bacheca;
            }
        }
        return null;
    }
    public void setListaBacheche(List<Bacheca> listaBacheche) {
        this.listaBacheche = listaBacheche;
    }

    public String getUsername() {
        return username;
    }
}


