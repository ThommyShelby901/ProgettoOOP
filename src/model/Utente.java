package model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Utente {
    private final String username;
    private final String password;
    private final List<ToDo> listaToDo;
    private List<Bacheca> listaBacheche;
    private static List<Utente> listaUtentiGlobali = new ArrayList<>();

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();
        this.listaBacheche = new ArrayList<>();
    }

    public List<Bacheca> getListaBacheche() {
        return listaBacheche;
    }

    public static List<Utente> getListaUtentiGlobali() {
        return listaUtentiGlobali;
    }

    public static void inizializzaUtenti() {
        if (listaUtentiGlobali.isEmpty()) {
            listaUtentiGlobali.add(new Utente("mario", "1234"));
            listaUtentiGlobali.add(new Utente("luigi", "1234"));
            listaUtentiGlobali.add(new Utente("peach", "1234"));
        }
    }


    public Bacheca creaBacheca(String titolo, String descrizione) {
        Bacheca nuova = new Bacheca(titolo, descrizione);
        listaBacheche.add(nuova);
        return nuova;
    }

    public String getNome() {
        return this.username; // supponendo che il campo nome si chiami così
    }

    public String getPassword(){
        return password;
    }


    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        for (Bacheca b : listaBacheche) {
            if (b.getTitoloBacheca().equalsIgnoreCase(titoloCorrente)) {
                b.setTitoloBacheca(nuovoTitolo);
                b.setDescrizioneBacheca(nuovaDescrizione);
                return;
            }
        }
        System.out.println("Bacheca non trovata.");
    }

    public void eliminaBacheca(String titolo) {
        listaBacheche.removeIf(b -> b.getTitoloBacheca().equalsIgnoreCase(titolo));
    }

    public void inizializzaBacheche() {
        if (listaBacheche.isEmpty()) { // 🔥 Evita la duplicazione
            listaBacheche.add(new Bacheca("Progetti", "Gestione dei progetti importanti"));
            listaBacheche.add(new Bacheca("Personale", "Appunti e idee personali"));
            listaBacheche.add(new Bacheca("Lavoro", "Attività legate al lavoro"));
        }
    }



    public void creaToDo(String titolo, String descrizione, String sfondo, String coloreSfondo,
                         String dataScadenza, String url, StatoToDo stato, Bacheca bacheca) {
        ToDo nuovo = new ToDo();
        nuovo.setAutore(this); // imposta l'autore come l'utente corrente

        if (titolo != null) nuovo.setTitoloToDo(titolo);
        if (descrizione != null) nuovo.setDescrizioneToDo(descrizione);
        if (sfondo != null) nuovo.setSfondo(sfondo);
        if (coloreSfondo != null) nuovo.setColoreSfondo(coloreSfondo);
        if (dataScadenza != null) nuovo.setDataScadenza(dataScadenza);
        if (url != null) nuovo.setUrl(url);
        if (stato != null) nuovo.setStatoToDo(stato);
        if (bacheca != null) {
            nuovo.setBacheca(bacheca);
            bacheca.aggiungiToDo(nuovo);
        }
        this.listaToDo.add(nuovo);
    }

    public void modificaToDo(ToDo todo, Utente utenteRichiedente,
                             String nuovoTitolo, String nuovaDescrizione,
                             String nuovoSfondo, String nuovoColoreSfondo,
                             String nuovaDataScadenza, String nuovoUrl,
                             StatoToDo nuovoStato) {

        if (!listaToDo.contains(todo)) {
            System.out.println("Errore: Il ToDo non appartiene a questa bacheca.");
            return;
        }

        // Controllo che l'utente sia l'autore
        if (!todo.getAutore().equals(utenteRichiedente)) {
            System.out.println("Errore: solo l'autore può modificare il ToDo.");
            return;
        }
        // Modifiche (solo se i parametri non sono null)
        if (nuovoTitolo != null) todo.setTitoloToDo(nuovoTitolo);
        if (nuovaDescrizione != null) todo.setDescrizioneToDo(nuovaDescrizione);
        if (nuovoSfondo != null) todo.setSfondo(nuovoSfondo);
        if (nuovoColoreSfondo != null) todo.setColoreSfondo(nuovoColoreSfondo);
        if (nuovaDataScadenza != null) todo.setDataScadenza(nuovaDataScadenza);
        if (nuovoUrl != null) todo.setUrl(nuovoUrl);
        if (nuovoStato != null) todo.setStatoToDo(nuovoStato);

        System.out.println("ToDo modificato con successo.");
    }

    public ToDo cercaToDoPerTitoloEBoard(String titolo, Bacheca board) {
        if (titolo == null || board == null) return null;
        for (ToDo t : listaToDo) {
            if (t.getBacheca() != null &&
                    t.getBacheca().equals(board) &&
                    t.getTitoloToDo() != null &&
                    t.getTitoloToDo().equalsIgnoreCase(titolo)) {
                return t;
            }
        }
        return null;
    }


    public void eliminaToDo(ToDo todo) {
        if (todo == null) {
            System.out.println("Errore: ToDo nullo.");
            return;
        }
        if (!todo.getAutore().equals(this)) {
            System.out.println("Non sei l'autore di questo ToDo. Non puoi eliminarlo.");
            return;
        }

        Bacheca bacheca = todo.getBacheca();
        if (bacheca != null) {
            bacheca.rimuoviToDo(todo);
        }
        if (listaToDo.remove(todo)) {
            System.out.println("ToDo eliminato correttamente.");
        } else {
            System.out.println("ToDo non trovato nella tua lista.");
        }
    }

    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) {
        if (todo == null || !todo.getAutore().equals(this)) return;

        if (todo.getBacheca() != null) todo.getBacheca().rimuoviToDo(todo);

        for (Bacheca b : getListaBacheche()) {
            if (b.getTitoloBacheca().equalsIgnoreCase(nomeBachecaDestinazione)) {
                b.aggiungiToDo(todo);
                todo.setBacheca(b);
                return;
            }
        }
        System.out.println("Bacheca destinazione non trovata.");
    }

    public void aggiungiToDoCondiviso(ToDo todo) {
        if (!listaToDo.contains(todo)) {
            listaToDo.add(todo);
        }
    }

    public void rimuoviToDoCondiviso(ToDo todo) {
        listaToDo.remove(todo);
    }

    public Bacheca getBachecaByTitolo(String titolo) {
        for (Bacheca bacheca : getListaBacheche()) { // lista statica di tutte le bacheche
            if (bacheca.getTitoloBacheca().equalsIgnoreCase(titolo)) {
                return bacheca;
            }
        }
        return null;
    }
}


