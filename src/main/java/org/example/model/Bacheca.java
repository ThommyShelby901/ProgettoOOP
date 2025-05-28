package org.example.model;

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


    public List<ToDo> getListaToDo() {
        return listaToDo;
    }


    }

