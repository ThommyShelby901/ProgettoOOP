import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ToDo {
    private Utente autore;
    private String url;
    private String titolo;
    private String sfondo;
    private String descrizioneToDo;
    private String coloreSfondo;
    private LocalDate dataScadenza;
    private Bacheca bacheca;
    private StatoToDo stato;
    private List<Utente> condivisoCon=new ArrayList<>();

    //essendo opzionali andiamo a definirli con set
    public void setAutore(Utente autore){ this.autore=autore; }
    public void setUrl(String url){ this.url=url; }
    public void setTitolo(String titolo){ this.titolo=titolo; }
    public void setSfondo(String sfondo){ this.sfondo=sfondo; }
    public void setDescrizioneToDo(String descrizione){ this.descrizioneToDo=descrizioneToDo; }
    public void setColoreSfondo(String coloreSfondo){ this.coloreSfondo=coloreSfondo; }
    public void setDataScadenza(String dataScadenza){ this.dataScadenza=LocalDate.parse(dataScadenza); }
    public void setBacheca(Bacheca bacheca){ this.bacheca=bacheca; }
    public void setStato(String stato){ this.stato=StatoToDo.NonCompletato; }


    public String getDescrizioneToDo(){
        return descrizioneToDo;
    }

    public String getTitoloToDo() {
        return titolo;
    }

    public Bacheca getBacheca(){
        return bacheca;
    }

    // Aggiungi un utente alla lista di condivisione (solo se l'utente non è già condiviso)
    public void aggiungiCondivisione(Utente utente) {
        if (this.autore.equals(utente)) {
            System.out.println("Non puoi aggiungere te stesso alla condivisione.");
            return;
        }

        if (!condivisoCon.contains(utente)) {
            condivisoCon.add(utente);
            System.out.println(utente.getUsername() + " ha ora accesso al ToDo.");
        } else {
            System.out.println(utente.getUsername() + " ha già accesso a questo ToDo.");
        }
    }

    // Rimuovi un utente dalla lista di condivisione (solo se l'utente è già condiviso)
    public void eliminaCondivisione(Utente utente) {
        if (!condivisoCon.contains(utente)) {
            System.out.println(utente.getUsername() + " non ha accesso al ToDo.");
            return;
        }

        condivisoCon.remove(utente);
        System.out.println(utente.getUsername() + " non ha più accesso a questo ToDo.");
    }

    public boolean verificaScadenzaOggi() {
        return dataScadenza.equals(LocalDate.now());
    }

    // Verifica se il ToDo scade entro una data specificata dall'utente
    public boolean verificaScadenzaEntro(LocalDate dataLimite) {
        return !dataScadenza.isAfter(dataLimite);
    }

    // Metodo per visualizzare il ToDo, evidenziando quelli scaduti
    public void visualizzascadenza() {
        if (verificaScadenzaOggi()) {
            System.out.println(titolo + " (Scadenza Oggi)");
        } else if (verificaScadenzaEntro(LocalDate.now())) {
            System.out.println("\u001B[31m" + titolo + " (SCADUTO)\u001B[0m");  // Rosso se scaduto
        } else {
            System.out.println(titolo);
        }
    }
}

