package model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

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

    //essendo opzionali andiamo a definirli con set
    public void setAutore(Utente autore){ this.autore=autore; }
    public void setUrl(String url){ this.url=url; }
    public void setTitoloToDo(String titoloToDo){ this.titoloToDo=titoloToDo; }
    public void setSfondo(String sfondo){ this.sfondo=sfondo; }
    public void setDescrizioneToDo(String descrizioneToDo){ this.descrizioneToDo=descrizioneToDo; }
    public void setColoreSfondo(String coloreSfondo){ this.coloreSfondo=coloreSfondo; }
    public void setDataScadenza(String dataScadenza){ this.dataScadenza=LocalDate.parse(dataScadenza); }
    public void setBacheca(Bacheca bacheca){ this.bacheca=bacheca; }
    public void setStatoToDo(StatoToDo statoToDo){ this.statoToDo=statoToDo; }

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

    public Bacheca getBacheca(){
        return bacheca;
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

    public boolean verificaScadenzaEntro(LocalDate dataLimite) {
        if (dataScadenza == null || dataLimite == null) return false;
        return !dataScadenza.isAfter(dataLimite);
    }

    public void visualizzaScadenza() {
        if (dataScadenza == null) {
            System.out.println(titoloToDo + " (nessuna scadenza)");
            return;
        }
        if (verificaScadenzaOggi()) {
            System.out.println("\u001B[33m" + titoloToDo + " - Scade oggi (" + dataScadenza + ")\u001B[0m");
        } else if (dataScadenza.isBefore(LocalDate.now())) {
            System.out.println("\u001B[31m" + titoloToDo + " - SCADUTO (" + dataScadenza + ")\u001B[0m");
        } else {
            System.out.println(titoloToDo + " - Scade il " + dataScadenza);
        }
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }


    public String getColoreSfondo() {
        return coloreSfondo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToDo)) return false;
        ToDo other = (ToDo) o;
        return this.titoloToDo != null && other.titoloToDo != null
                && this.titoloToDo.equalsIgnoreCase(other.titoloToDo)
                && this.autore != null && other.autore != null
                && this.autore.equals(other.autore)
                && this.bacheca != null && other.bacheca != null
                && this.bacheca.equals(other.bacheca);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                titoloToDo != null ? titoloToDo.toLowerCase() : "",
                autore,
                bacheca
        );
    }



}

