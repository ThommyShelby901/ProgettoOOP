package org.example.model;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * classe che rappresenta i to-do del nostro progetto, titolo, descrizione, data scadenza, stato, sono gli attributi
 * che possiamo assegnare a un to-do che sono associati a una bacheca e possono essere condivisi con altri utenti.
 */

public class ToDo {
    private Utente autore;
    private String url;
    private String titoloToDo;
    private String descrizioneToDo;
    private LocalDate dataScadenza;
    private StatoToDo statoToDo;
    private final List<Utente> condivisoCon = new ArrayList<>();
    private int id;
    private String titoloBacheca;
    private int ordine;
    private Color coloreSfondo;
    private String percorsoImmagine;

    /**
     * Costruttore base che inizializza il To-Do come "non completato".
     */
    public ToDo() {
        this.statoToDo = StatoToDo.NONCOMPLETATO;
    }

    /**
     * aggiunge un utente alla lista di condivisione, usando direttamente lo username
     * <p>
     * l'utente viene aggiunto solo se non è null e non è gia presente nella lista {@link #condivisoCon condivisoCon}
     * @param utente ----username dell'utente con cui condividere.
     */
    public void aggiungiUtenteCondiviso(Utente utente) {
        if (utente != null && !condivisoCon.contains(utente)) {
            condivisoCon.add(utente);
        }
    }

    /**
     * rimuove un utente dalla lista di condivisione, usando sempre lo username.
     * <p>
     * L'utente viene rimosso solo se è presente nella lista {@link #condivisoCon condivisoCon}.
     * @param utente Username dell'utente da rimuovere dalla condivisione.
     */
    public void rimuoviUtenteCondiviso(Utente utente) {
        condivisoCon.remove(utente);
    }

    /**
     * metodo to-string, in questo caso rappresentazione testuale del to-do
     * @return Stringa nel formato titolo-stato(scadenza).
     */
    @Override
    public String toString() {
        return titoloToDo + " - " + statoToDo + (dataScadenza != null ? " (" + dataScadenza + ")" : "");
    }

    /**
     * Restituisce l'utente autore del to-do
     * @return L'oggetto {@link Utente} autore del To-Do */
    public Utente getAutore() { return autore; }

    /**
     * imposta l'utente autore di questo to-do
     * @param autore oggetto {@link Utente} che ha creato il To-Do */
    public void setAutore(Utente autore) { this.autore = autore; }

    /**
     * Restituisce l'url associato a questo to-do
     * @return URL associato (opzionale) */
    public String getUrl() { return url; }

    /**
     * Imposta l'url associato a questo to-do
     * @param url Link esterno relativo al To-Do */
    public void setUrl(String url) { this.url = url; }

    /**
     * Restituisce il titolo del to-do
     * @return Titolo del To-Do */
    public String getTitoloToDo() { return titoloToDo; }

    /**
     * setta il titolo del to-do
     * @param titoloToDo Il titolo da assegnare (obbligatorio) */
    public void setTitoloToDo(String titoloToDo) { this.titoloToDo = titoloToDo; }

    /**
     * La descrizione fornisce informazioni aggiuntive sul compito.
     * @return Descrizione estesa */
    public String getDescrizioneToDo() { return descrizioneToDo; }

    /**
     * imposta la descrizione dettagliata.
     * @param descrizioneToDo La descrizione dettagliata */
    public void setDescrizioneToDo(String descrizioneToDo) { this.descrizioneToDo = descrizioneToDo; }

    /**
     * Questa data indica entro quando il compito dovrebbe essere completato
     * @return Data di scadenza (se impostata) */
    public LocalDate getDataScadenza() { return dataScadenza; }

    /**
     * Setta la data di scadenza del to-do, se si imposta null si rimuove la data di scadenza impostata precedentemente
     * @param dataScadenza La data entro cui completare il To-Do */
    public void setDataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; }

    /**
     * Lo stato è definito dall'enumeration {@link StatoToDo}
     * @return Stato attuale (COMPLETATO/NONCOMPLETATO) */
    public StatoToDo getStatoToDo() { return statoToDo; }

    /**
     * Imposta lo stato del to-do, aggiornando lo stato di completamento.
     * @param statoToDo Nuovo {@link StatoToDo} del To-Do  che non può essere null*/
    public void setStatoToDo(StatoToDo statoToDo) { this.statoToDo = statoToDo; }

    /**
     * restituisce la lista degli utenti con cui il to-do è condiviso.
     * <p>
     * Questa lista contiene gli oggetti {@link Utente} che hanno accesso a questo To-Do.
     * @return Lista {@link Utente utenti} con cui è condiviso */
    public List<Utente> getCondivisoCon() { return condivisoCon; }

    /**
     * * Restituisce l'ID univoco di questo To-Do.
     * <p>
     * Questo ID è generato dal database e serve per identificare in modo univoco il To-Do nel sistema di persistenza.
     * @return ID univoco (usato per il database) */
    public int getId() { return id; }

    /**
     * * Imposta l'ID univoco di questo To-Do.
     * <p>
     * Questo metodo viene tipicamente usato dal DAO quando un To-Do viene caricato o salvato nel database.
     * @param id ID da assegnare (gestito automaticamente dal DAO) */
    public void setId(int id) { this.id = id; }

    /**
     * Restituisce il titolo della bacheca a cui questo To-Do appartiene.
     * @return Nome della bacheca di appartenenza */
    public String getBacheca() { return titoloBacheca; }

    /**
     * Imposta il titolo della bacheca a cui questo To-Do appartiene.
     * @param titoloBacheca Bacheca a cui assegnare il To-Do */
    public void setBacheca(String titoloBacheca) { this.titoloBacheca = titoloBacheca; }

    /**
     * Questo valore è utilizzato per determinare l'ordine di visualizzazione dei To-Do nella lista.
     * @return intero che rappresenta l'ordine dei to-do(usato per l'ordinamento) */
    public int getOrdine() { return ordine; }

    /**
     * L'ordine viene usato per la visualizzazione.
     * @param ordine Nuova posizione nella lista */
    public void setOrdine(int ordine) { this.ordine = ordine; }

    /**
     * Restituisce il colore di sfondo personalizzato per questo To-Do.
     * <p>
     * Questo colore viene utilizzato per scopi di visualizzazione nell'interfaccia utente.
     * @return Un oggetto {@link java.awt.Color Color} per la visualizzazione dello sfondo */
    public Color getColoreSfondo() { return coloreSfondo; }

    /**
     * Imposta il colore di sfondo personalizzato per questo To-Do.
     * <p>
     * Accetta un oggetto {@link java.awt.Color Color} per personalizzare l'aspetto del To-Do.
     * @param coloreSfondo Il {@link java.awt.Color Color} da assegnare come sfondo.*/
    public void setColoreSfondo(Color coloreSfondo) { this.coloreSfondo = coloreSfondo; }

    /**
     * Restituisce il percorso file dell'immagine allegata a questo To-Do.
     * @return Una {@link java.lang.String String} che rappresenta il percorso completo dell'immagine. */
    public String getPercorsoImmagine() { return percorsoImmagine; }

    /**
     * Questo metodo associa un'immagine al To-Do tramite il suo percorso sul file system.
     * @param percorsoImmagine Percorso del file immagine */
    public void setPercorsoImmagine(String percorsoImmagine) {
        this.percorsoImmagine = percorsoImmagine;
    }
}