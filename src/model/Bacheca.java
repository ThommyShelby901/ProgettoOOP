package model;

import java.util.ArrayList;
import java.util.List;

public class Bacheca {
    private String titoloBacheca;
    private String descrizioneBacheca;
    private final List<ToDo> listaToDo;

    public Bacheca(String titoloBacheca, String descrizioneBacheca) {
        this.titoloBacheca = titoloBacheca;
        this.descrizioneBacheca = descrizioneBacheca;
        this.listaToDo = new ArrayList<>();
    }

    public String getTitoloBacheca() {
        return titoloBacheca;
    }

    public void setTitoloBacheca(String titoloBacheca) {
        this.titoloBacheca = titoloBacheca;
    }

    public String getDescrizioneBacheca() {
        return descrizioneBacheca;
    }

    public void setDescrizioneBacheca(String descrizioneBacheca) {
        this.descrizioneBacheca = descrizioneBacheca;
    }

    public void aggiungiToDo(ToDo todo) {
        listaToDo.add(todo);
    }

    public void rimuoviToDo(ToDo todo) {
        listaToDo.remove(todo);
    }

    public void mostraToDo() {
        for (ToDo t : listaToDo) {
            System.out.println("- " + t.getTitoloToDo());
        }
    }

    public void spostaToDo(String titoloDaSpostare, String titoloPosizione) {
        // Questo metodo sarà richiamato dal controller,
        // ma non contiene la logica operativa.
    }

    public List<ToDo> getListaToDo() {
        return listaToDo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;               // se sono lo stesso oggetto, ok
        if (o == null || getClass() != o.getClass()) return false; // controlla classe
        Bacheca bacheca = (Bacheca) o;
        return titoloBacheca != null && titoloBacheca.equalsIgnoreCase(bacheca.titoloBacheca);
    }

    @Override
    public int hashCode() {
        return titoloBacheca == null ? 0 : titoloBacheca.toLowerCase().hashCode();
    }

    public ToDo getToDoByTitolo(String titolo) {
        for (ToDo t : listaToDo) {
            if (t.getTitoloToDo().equalsIgnoreCase(titolo)) {
                return t;
            }
        }
        return null; // non trovato
    }




}
