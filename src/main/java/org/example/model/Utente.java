package org.example.model;

import java.awt.*;
import java.util.*;
import java.time.LocalDate;
import java.util.List;

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

    public List<ToDo> getListaToDo(){return listaToDo; }


    public void aggiungiBacheca(Bacheca bacheca) {
        if (!listaBacheche.contains(bacheca)) {
            listaBacheche.add(bacheca);
        }
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
        for (Iterator<Bacheca> iterator = listaBacheche.iterator(); iterator.hasNext();) {
            Bacheca b = iterator.next();
            if (b.getTitoloBacheca().equalsIgnoreCase(titolo)) {
                iterator.remove();
                break;
            }
        }
    }

    public ToDo creaToDo(String titolo, String descrizione, LocalDate dataScadenza, String url, StatoToDo stato, String titoloBacheca, Color coloreSfondo) {
        if (titolo == null || descrizione == null || titoloBacheca == null) {
            return null;
        }


        ToDo nuovoToDo = new ToDo();
        nuovoToDo.setTitoloToDo(titolo);
        nuovoToDo.setDescrizioneToDo(descrizione);
        nuovoToDo.setDataScadenza(dataScadenza);
        nuovoToDo.setUrl(url);
        nuovoToDo.setStatoToDo(stato);
        nuovoToDo.setBacheca(titoloBacheca);
        nuovoToDo.setAutore(this);
        nuovoToDo.setColoreSfondo(coloreSfondo);

        listaToDo.add(nuovoToDo);


        return nuovoToDo;
    }


    public void modificaToDo(ToDo todo, String nuovoTitolo, String nuovaDescrizione, LocalDate nuovaDataScadenza, String nuovoUrl, StatoToDo nuovoStato) {
        if (!listaToDo.contains(todo)) {
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
                    t.getBacheca().equalsIgnoreCase(titoloBacheca) &&
                    t.getTitoloToDo() != null &&
                    t.getTitoloToDo().equalsIgnoreCase(titolo)) {
                return t;
            }
        }
        return null;
    }



    public void eliminaToDo(ToDo todo) {
        if (todo == null || !todo.getAutore().equals(this)) {
            return;
        }

        // Rimuovi da listaToDo (che contiene tutti i ToDo personali e condivisi)
        listaToDo.remove(todo);

        // Rimuovi da eventuali utenti con cui è condiviso
        for (Utente u : todo.getCondivisoCon()) {
            u.rimuoviToDoCondiviso(todo);
        }
        todo.getCondivisoCon().clear();

        todo.setBacheca(null);
    }


    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) {
        Objects.requireNonNull(todo, "ToDo non può essere null");
        Objects.requireNonNull(nomeBachecaDestinazione, "Nome bacheca non può essere null");

        // Verifica che la bacheca destinazione esista
        boolean bachecaEsiste = listaBacheche.stream()
                .anyMatch(b -> b.getTitoloBacheca().equalsIgnoreCase(nomeBachecaDestinazione));

        if (!bachecaEsiste) {
            throw new IllegalArgumentException("Bacheca destinazione non trovata");
        }

        todo.setBacheca(nomeBachecaDestinazione);
    }

    public void spostaToDo(String titoloBacheca, String titoloToDo, int nuovaPosizione) {

        ToDo toDoDaSpostare = null;
        int indexInMainList = -1;

        for (int i = 0; i < listaToDo.size(); i++) {
            ToDo t = listaToDo.get(i);
            if (t.getBacheca() != null
                    && t.getBacheca().equalsIgnoreCase(titoloBacheca)
                    && t.getTitoloToDo().equalsIgnoreCase(titoloToDo)) {
                toDoDaSpostare = t;
                indexInMainList = i;
                break;
            }
        }

        if (toDoDaSpostare == null) {
            throw new IllegalArgumentException("ToDo non trovato nella bacheca specificata.");
        }

        listaToDo.remove(indexInMainList);

        int newIndexInMainList = calcolaNuovoIndice(titoloBacheca, nuovaPosizione);

        listaToDo.add(newIndexInMainList, toDoDaSpostare);

        aggiornaOrdineToDo(titoloBacheca);
    }

    private int calcolaNuovoIndice(String titoloBacheca, int nuovaPosizione) {
        int newIndex = listaToDo.size(); // di default in coda
        int count = 0;

        for (int i = 0; i < listaToDo.size(); i++) {
            ToDo t = listaToDo.get(i);
            if (t.getBacheca() != null && t.getBacheca().equalsIgnoreCase(titoloBacheca)) {
                if (count == nuovaPosizione) {
                    newIndex = i;
                }
                count++;
            }
        }

        return newIndex;
    }

    private void aggiornaOrdineToDo(String titoloBacheca) {
        int ordine = 0;
        for (ToDo t : listaToDo) {
            if (t.getBacheca() != null && t.getBacheca().equalsIgnoreCase(titoloBacheca)) {
                t.setOrdine(ordine++);
            }
        }
    }





    public List<ToDo> getToDoInScadenzaEntro(LocalDate dataLimite) {
        List<ToDo> result = new ArrayList<>();
        for (ToDo t : listaToDo) {
            if (t.getDataScadenza() != null && !t.getDataScadenza().isAfter(dataLimite)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<ToDo> getToDoPerBacheca(String titoloBacheca) {
        return listaToDo.stream()
                .filter(t -> titoloBacheca.equalsIgnoreCase(t.getBacheca()))
                .sorted(Comparator.comparingInt(ToDo::getOrdine))
                .toList(); // Usa 'toList()' per ottenere una lista direttamente (Java 16+)
    }


    public void aggiungiToDo(ToDo todo, String titoloBacheca) {
        todo.setBacheca(titoloBacheca);

        // Aggiungi alla lista globale dell'utente
        if (!listaToDo.contains(todo)) {
            listaToDo.add(todo);
        }
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
        if (titolo == null) return null;

        for (Bacheca bacheca : listaBacheche) {
            if (bacheca.getTitoloBacheca() != null &&
                    bacheca.getTitoloBacheca().equalsIgnoreCase(titolo)) {
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


