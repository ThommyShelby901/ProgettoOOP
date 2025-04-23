import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ToDo {
    private Utente autore;
    private String url;
    private String titoloToDo;
    private String sfondo;
    private String descrizioneToDo;
    private String coloreSfondo;
    private LocalDate dataScadenza;
    private Bacheca bacheca;
    private StatoToDo stato;
    private List<Utente> condivisoCon=new ArrayList<>();
    private static final Scanner scanner=new Scanner(System.in);

    //essendo opzionali andiamo a definirli con set
    public void setAutore(Utente autore){ this.autore=autore; }
    public void setUrl(String url){ this.url=url; }
    public void setTitolo(String titolo){ this.titoloToDo=titoloToDo; }
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
        return titoloToDo;
    }

    public Bacheca getBacheca(){
        return bacheca;
    }

    public void aggiungiCondivisione(Utente utente) {
        if (this.autore.equals(utente)) {
            System.out.println("Non puoi aggiungere te stesso alla condivisione.");
            return;
        }

        if (!condivisoCon.contains(utente)) {
            condivisoCon.add(utente);
            utente.aggiungiToDoCondiviso(this);

            // Faccio scegliere all'utente la bacheca in cui inserire il ToDo
            System.out.println("Seleziona la bacheca in cui " + utente.getUsername() + " vuole inserire il ToDo condiviso:");
            List<Bacheca> listaBacheche = Bacheca.getListaBacheche();
            for (int i = 0; i < listaBacheche.size(); i++) {
                System.out.println((i + 1) + ". " + listaBacheche.get(i).getTitolo());
            }

            Scanner scanner = new Scanner(System.in);
            int scelta = scanner.nextInt();
            scanner.nextLine(); // pulizia buffer

            if (scelta < 1 || scelta > listaBacheche.size()) {
                System.out.println("Scelta non valida. Il ToDo non è stato aggiunto a nessuna bacheca.");
                return;
            }

            Bacheca bachecaSelezionata = listaBacheche.get(scelta - 1);
            bachecaSelezionata.getListaToDo().add(this);
            System.out.println("ToDo condiviso e aggiunto alla bacheca: " + bachecaSelezionata.getTitolo());
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

    public boolean verificaScadenzaEntro(LocalDate dataLimite) {
        return !dataScadenza.isAfter(dataLimite);
    }

    public void visualizzascadenza() {
        if (verificaScadenzaOggi()) {
            System.out.println(titoloToDo + " (Scadenza Oggi)");
        } else if (verificaScadenzaEntro(LocalDate.now())) {
            System.out.println("\u001B[31m" + titoloToDo + " (SCADUTO)\u001B[0m");  // Rosso se scaduto
        } else {
            System.out.println(titoloToDo);
        }
    }
}

