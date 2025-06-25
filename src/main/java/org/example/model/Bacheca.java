package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Bacheca {
    private String titoloBacheca;
    private String descrizioneBacheca;
    private final List<String> listaCondivisioni;

    public Bacheca( String titoloBacheca, String descrizioneBacheca) {
        this.titoloBacheca = titoloBacheca;
        this.descrizioneBacheca = descrizioneBacheca;

        this.listaCondivisioni= new ArrayList<>();
    }

    public List<String> getListaCondivisioni() {
        return listaCondivisioni;
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


}

