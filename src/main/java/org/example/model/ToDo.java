package org.example.model;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ToDo {
    private Utente autore;
    private String url;
    private String titoloToDo;
    private String descrizioneToDo;
    private LocalDate dataScadenza;
    private StatoToDo statoToDo;
    private final List<Utente> condivisoCon=new ArrayList<>();
    private int id;
    private String titoloBacheca;
    private int ordine;
    private Color coloreSfondo;
    private String percorsoImmagine;

    //essendo opzionali andiamo a definirli con set
    public void setAutore(Utente autore){ this.autore=autore; }
    public void setUrl(String url){ this.url=url; }
    public void setTitoloToDo(String titoloToDo){ this.titoloToDo=titoloToDo; }
    public void setColoreSfondo(Color coloreSfondo){this.coloreSfondo=coloreSfondo;}
    public void setDescrizioneToDo(String descrizioneToDo){ this.descrizioneToDo=descrizioneToDo; }
    public void setPercorsoImmagine(String percorsoImmagine) {
        this.percorsoImmagine = percorsoImmagine;
    }

    public void setBacheca(String titoloBacheca) {
        this.titoloBacheca = titoloBacheca;
    }




    public void setStatoToDo(StatoToDo statoToDo){ this.statoToDo=statoToDo; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Color getColoreSfondo() {
        return coloreSfondo;
    }



    public List<Utente> getCondivisoCon() {
        return condivisoCon;
    }

    public Utente getAutore(){
        return autore;
    }

    public String getDescrizioneToDo(){
        return descrizioneToDo;
    }

    public ToDo() {
        this.statoToDo = StatoToDo.NONCOMPLETATO;  // Imposta il valore predefinito
    }

    public String getTitoloToDo() {
        return titoloToDo;
    }

    public String getBacheca(){
        return titoloBacheca;
    }

    public void aggiungiUtenteCondiviso(Utente utente) {
        if (utente != null && !condivisoCon.contains(utente)) {
            condivisoCon.add(utente);
        }
    }

    public void rimuoviUtenteCondiviso(Utente utente) {
        condivisoCon.remove(utente);
    }


    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getPercorsoImmagine() {
        return percorsoImmagine;
    }
    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public String getUrl() {
        return url;
    }

    public StatoToDo getStatoToDo() {
        return statoToDo;
    }

    public int getOrdine() {
        return ordine;
    }

    public void setOrdine(int ordine) {
        this.ordine=ordine;
    }

    @Override
    public String toString() {
        String scad = (getDataScadenza() == null) ? "(Nessuna scadenza)" : "(Scadenza: " + getDataScadenza() + ")";
        return getTitoloToDo() + " " + scad;
    }

}

