package org.example.model;

/**
 * Rappresenta una voce della checklist associata a un To-Do.
 * Ogni voce ha un ID univoco, una descrizione, uno stato di completamento e
 * un riferimento all'id del To-Do a cui appartiene.
 */
public class CheckList {
    private int idCheckList;
    private String descrizione;
    private boolean completato;
    int idToDo;
    private StatoCheck stato;

    /**
     * Restituisce lo stato della voce della checklist.
     * @return stato della voce (es. INCOMPLETO, COMPLETATO)
     */
    public StatoCheck getStato() {
        return stato;
    }

    /**
     * Imposta lo stato della voce della checklist.
     * @param stato nuovo stato da assegnare
     */
    public void setStato(StatoCheck stato) {
        this.stato = stato;
    }

    /**
     * Restituisce l'ID univoco della voce.
     * @return ID della voce della checklist
     */
    public int getIdCheckList() {
        return idCheckList;
    }

    /**
     * Imposta l'ID univoco della voce.
     * @param idCheckList ID da assegnare
     */
    public void setIdCheckList(int idCheckList) {
        this.idCheckList = idCheckList;
    }

    /**
     * Restituisce la descrizione della voce.
     * @return descrizione testuale
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta la descrizione della voce.
     * @param descrizione testo da assegnare
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Indica se la voce è completata.
     * @return true se completata, false altrimenti
     */
    public boolean isCompletato() {
        return completato;
    }

    /**
     * Imposta il flag di completamento della voce.
     * @param completato true per completata, false altrimenti
     */
    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

    /**
     * Imposta l'ID del To-Do a cui appartiene questa voce.
     * @param idToDo ID del To-Do
     */
    public void setIdToDo(int idToDo) {
        this.idToDo = idToDo;
    }

    /**
     * Rappresentazione testuale della voce della checklist,
     * con lo stato tra parentesi quadre seguito dalla descrizione.
     * @return stringa rappresentativa della voce
     */
    @Override
    public String toString() {
        return "[" + stato.toString() + "] " + descrizione;
    }
}


