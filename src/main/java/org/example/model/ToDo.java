package org.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

public class ToDo {
    private Utente autore;
    private String url;
    private String titoloToDo;
    private String sfondo;
    private String descrizioneToDo;
    private String coloreSfondo;
    private LocalDate dataScadenza;
    private Bacheca bacheca;
    private StatoToDo statoToDo;
    private final List<Utente> condivisoCon=new ArrayList<>();
    private int id;
    private String titoloBacheca;

    //essendo opzionali andiamo a definirli con set
    public void setAutore(Utente autore){ this.autore=autore; }
    public void setUrl(String url){ this.url=url; }
    public void setTitoloToDo(String titoloToDo){ this.titoloToDo=titoloToDo; }
    public void setSfondo(String sfondo){ this.sfondo=sfondo; }
    public void setDescrizioneToDo(String descrizioneToDo){ this.descrizioneToDo=descrizioneToDo; }
    public void setColoreSfondo(String coloreSfondo){ this.coloreSfondo=coloreSfondo; }
    public void setBacheca(String titoloBacheca) {
        this.titoloBacheca = titoloBacheca; // 🔥 Assegna direttamente il titolo!
    }

    public void setStatoToDo(StatoToDo statoToDo){ this.statoToDo=statoToDo; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void aggiungiCondivisione(Utente richiedente, Utente utenteDaAggiungere) {
        if (richiedente == null || utenteDaAggiungere == null) {
            System.out.println("Errore: parametri null non consentiti.");
            return;
        }
        if (!richiedente.equals(autore)) {
            System.out.println("Errore: solo l'autore può aggiungere condivisioni.");
            return;
        }
        if (autore.equals(utenteDaAggiungere)) {
            System.out.println("Errore: non puoi condividere con te stesso.");
            return;
        }
        if (condivisoCon.contains(utenteDaAggiungere)) {
            System.out.println("Errore: utente già presente nelle condivisioni.");
            return;
        }

        condivisoCon.add(utenteDaAggiungere);
        Bacheca bachecaDest = utenteDaAggiungere.getBachecaByTitolo(bacheca.getTitoloBacheca());
        if (bachecaDest != null) {
            bachecaDest.aggiungiToDo(this);
            utenteDaAggiungere.aggiungiToDoCondiviso(this);
        }
        System.out.println("Condivisione aggiunta con successo.");
    }

    public void eliminaCondivisione(Utente richiedente, Utente utenteDaRimuovere) {
        if (richiedente == null || utenteDaRimuovere == null) {
            System.out.println("Errore: parametri null non consentiti.");
            return;
        }
        if (!richiedente.equals(autore)) {
            System.out.println("Errore: solo l'autore può eliminare condivisioni.");
            return;
        }
        if (!condivisoCon.contains(utenteDaRimuovere)) {
            System.out.println("Errore: utente non presente nelle condivisioni.");
            return;
        }

        condivisoCon.remove(utenteDaRimuovere);
        Bacheca bachecaDest = utenteDaRimuovere.getBachecaByTitolo(bacheca.getTitoloBacheca());
        if (bachecaDest != null) {
            bachecaDest.rimuoviToDo(this);
            utenteDaRimuovere.rimuoviToDoCondiviso(this);
        }
        System.out.println("Condivisione rimossa con successo.");
    }

    public boolean verificaScadenzaOggi() {
        if (dataScadenza == null) return false;
        return dataScadenza.equals(LocalDate.now());
    }

    public void setDataScadenza(String dataScadenza) {
        try {
            this.dataScadenza = LocalDate.parse(dataScadenza);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
    }


    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public String getSfondo() {
        return sfondo;
    }

    public String getColoreSfondo() {
        return coloreSfondo;
    }

    public String getUrl() {
        return url;
    }

    public StatoToDo getStatoToDo() {
        return statoToDo;
    }


}

