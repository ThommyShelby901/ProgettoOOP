package org.example.model;

/**
 * Classe che rappresenta una bacheca contenitore di to-do, i to-do al suo interno possono essere condivisi individualmente.\
 * <p>
 * Ogni bacheca è identificata da un {@link #titoloBacheca titolo univoco} e può avere una
 * {@link #descrizioneBacheca descrizione} opzionale.
 */

public class Bacheca {
    private String titoloBacheca;
    private String descrizioneBacheca;


    /**
     * crea una nuova bacheca vuota
     * <p>
     * viene inizializzata come una {@link java.util.ArrayList ArrayList} vuota.
     * @param titoloBacheca nome identificativo dell'attività della bacheca
     * @param descrizioneBacheca scopo della bacheca
     */
    public Bacheca( String titoloBacheca, String descrizioneBacheca) {
        this.titoloBacheca = titoloBacheca;
        this.descrizioneBacheca = descrizioneBacheca;
    }



    /**
     * Restituisce il nome attuale della bacheca.
     * @return nome attuale della bacheca.
     */
    public String getTitoloBacheca() {
        return titoloBacheca;
    }

    /**
     * rinomina la bacheca
     * @param titoloBacheca nuovo titolo sostituirà quello vecchio quando modifichiamo
     */
    public void setTitoloBacheca(String titoloBacheca) {
        this.titoloBacheca = titoloBacheca;
    }

    /**
     * Restituisce la descrizione corrente della bacheca.
     * @return La stringa contenente la descrizione dettagliata della bacheca.
     */
    public String getDescrizioneBacheca() {
        return descrizioneBacheca;
    }

    /**
     * cambia la descrizione della bacheca.
     * @param descrizioneBacheca nuova descrizione.
     */
    public void setDescrizioneBacheca(String descrizioneBacheca) {
        this.descrizioneBacheca = descrizioneBacheca;
    }
}

