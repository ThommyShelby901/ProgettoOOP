package org.example.model;
public class CheckList {

    private int idCheckList;
    private String descrizione;
    private boolean completato;
    private int idToDo;
    private StatoCheck stato;

    public StatoCheck getStato() {
        return stato;
    }

    public void setStato(StatoCheck stato) {
        this.stato = stato;
    }


    public CheckList() {
    }

    // Costruttore completo
    public CheckList(int idCheckList, String descrizione, boolean completato, int idToDo) {
        this.idCheckList = idCheckList;
        this.descrizione = descrizione;
        this.completato = completato;
        this.idToDo = idToDo;
    }

    // Costruttore senza id (per inserimento nuovo record)
    public CheckList(String descrizione, boolean completato, int idToDo) {
        this.descrizione = descrizione;
        this.completato = completato;
        this.idToDo = idToDo;
    }

    // Getters e Setters

    public int getIdCheckList() {
        return idCheckList;
    }

    public void setIdCheckList(int idCheckList) {
        this.idCheckList = idCheckList;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public boolean isCompletato() {
        return completato;
    }

    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

    public void setIdToDo(int idToDo) {
        this.idToDo = idToDo;
    }

    @Override
    public String toString() {
        return "CheckList{" +
                "idCheckList=" + idCheckList +
                ", descrizione='" + descrizione + '\'' +
                ", completato=" + completato +
                ", idToDo=" + idToDo +
                '}';
    }
}

