package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Bacheca {
    private String titoloBacheca;
    private String descrizioneBacheca;
    private final List<ToDo> listaToDo;
    private List<String> listaCondivisioni;



    public Bacheca( String titoloBacheca, String descrizioneBacheca) {
        this.titoloBacheca = titoloBacheca;
        this.descrizioneBacheca = descrizioneBacheca;
        this.listaToDo = new ArrayList<>();
        this.listaCondivisioni= new ArrayList<>();
    }


    public List<String> getListaCondivisioni() {
        return listaCondivisioni;
    }

    public String getTitoloBacheca() {
        return titoloBacheca;
    }

    public ToDo getUltimoToDo() {
        return listaToDo.isEmpty() ? null : listaToDo.get(listaToDo.size() - 1);
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

    public List<ToDo> getListaToDo() {
        return listaToDo;
    }
}

