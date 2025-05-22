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

    public void spostaToDo(int from, int to) {
        if (from < 0 || from >= listaToDo.size() || to < 0 || to >= listaToDo.size()) return;
        ToDo t = listaToDo.remove(from);
        listaToDo.add(to, t);
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



}
