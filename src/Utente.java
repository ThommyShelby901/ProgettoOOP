import java.util.List;
import java.util.ArrayList;

public class Utente {
    private final String username;
    private final String password;
    private final List<ToDo> listaToDo;

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();
    }

    public boolean esitoAccesso(String u, String p) {
        if (username.equals(u) && password.equals(p)) {
            System.out.println("Accesso riuscito");
            return true;
        } else {
            System.out.println("Accesso negato");
            return false;
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
            System.out.println("Errore: Solo l'autore può modificare il ToDo.");
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

    public ToDo cercaToDoPerTitolo(String titolo) {
        for (ToDo todo : listaToDo) {
            if (todo.getTitoloToDo().equalsIgnoreCase(titolo)) {
                return todo;
            }
        }
        System.out.println("Nessun ToDo trovato con il titolo: " + titolo);
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
        if (todo == null || todo.getAutore() == null || !todo.getAutore().equals(this)) {
            System.out.println("Non sei l'autore di questo ToDo. Operazione non consentita.");
            return;
        }

        Bacheca bachecaOrigine = todo.getBacheca();
        if (bachecaOrigine != null) {
            bachecaOrigine.rimuoviToDo(todo);
        }

        for (Bacheca bacheca : BachecaGestione.getListaBacheche()) {
            if (bacheca.getTitoloBacheca().equalsIgnoreCase(nomeBachecaDestinazione)) {
                bacheca.aggiungiToDo(todo);
                todo.setBacheca(bacheca);
                System.out.println("ToDo trasferito nella bacheca: " + nomeBachecaDestinazione);
                return;
            }
        }

        System.out.println("Bacheca di destinazione non trovata.");
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
        for (Bacheca bacheca : BachecaGestione.getListaBacheche()) { // lista statica di tutte le bacheche
            if (bacheca.getTitoloBacheca().equalsIgnoreCase(titolo)) {
                return bacheca;
            }
        }
        return null;
    }
}


