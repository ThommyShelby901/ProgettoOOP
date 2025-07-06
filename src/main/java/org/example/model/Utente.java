package org.example.model;

import java.util.*;
import java.time.LocalDate;
import java.util.List;

/**
 * La classe utente rappresenta un utente all'interno della nostra applicazione di gestione di to-do
 * la maggior parte delle responsabilità sono incluse in questa classe che riesce a collegare un po
 * tutta la gestione dell'applicazione, è un po il cuore del model.
 * <p>
 * Si trovano quasi tutte le attività e permette di riconnoscere a pieno la gestione della nostra app.
 * Un utente è riconosciuto da {@link #username username} e {@link #password password} univoci.
 */

public class Utente {
    private final String username;
    private final String password;
    private final List<ToDo> listaToDo;
    private List<Bacheca> listaBacheche;

    /**
     * Quando un utente si registra o viene caricato dal db usiamo questo.
     * inizializziamo le due liste di fondamentale importanza, ovvero dei to-do e delle bacheche.
     * @param username username univoco dell'utente(not null)
     * @param password password univoca dell'utente(not null)
     */

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();
        this.listaBacheche = new ArrayList<>();
    }

    /**
     * Crea le bacheche predefinite, cosi come ci viene imposto dalla traccia
     * <p>
     * Questo metodo popola la {@link #listaBacheche lista delle bacheche} con le bacheche standard("Università", "Lavoro", "Tempo Liberp")
     * @return ritorna la {@link #listaBacheche lista delle bacheche}  iniziali(predefinite)
     */
    public static List<Bacheca> inizializzaBacheche() {
        List<Bacheca> bachechePredefinite = new ArrayList<>();
        bachechePredefinite.add(new Bacheca("Università", "Attività di studio"));
        bachechePredefinite.add(new Bacheca("Lavoro", "Attività lavorative"));
        bachechePredefinite.add(new Bacheca("Tempo Libero", "Impegni personali"));
        return bachechePredefinite;
    }

    /**
     * Aggiunge una nuova bacheca alla lista delle bacheche che avevamo inizializzato e che prende
     * come riferimento un oggetto bacheca. Controlla che la bacheca non esista già.
     * @param bacheca oggetto {@link Bacheca} da aggiungere
     */
    public void aggiungiBacheca(Bacheca bacheca) {
        if (!listaBacheche.contains(bacheca)) {
            listaBacheche.add(bacheca);
        }
    }

    /**
     * Rimuove una bacheca dalla lista delle bacheche dell'utente, quando eliminiamo una bacheca tutti i
     * to-do al suo interno vengono eliminati ovviamente
     * @param titolo titolo bacheca da rimuovere
     */
    public void eliminaBacheca(String titolo) {
        for (Iterator<Bacheca> iterator = listaBacheche.iterator(); iterator.hasNext();) {
            Bacheca b = iterator.next();
            if (b.getTitoloBacheca().equalsIgnoreCase(titolo)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Aggiunge un to-do alla lista globale dell'utente associandolo a una bacheca, usato quando viene creato un to-dp
     * @param todo oggetto {@link ToDo} da aggiungere.
     * @param titoloBacheca titolo a cui associare to-do.
     */
    public void aggiungiToDo(ToDo todo, String titoloBacheca) {
        todo.setBacheca(titoloBacheca);

        // Aggiungi alla lista globale dell'utente
        if (!listaToDo.contains(todo)) {
            listaToDo.add(todo);
        }
    }

    /**
     * Cerca un to-do nella lista dell'utente
     * @param titolo del to-do da cercare
     * @param titoloBacheca in cui cercare.
     * @return il to-do trovato o null se non esiste.
     */
    public ToDo cercaToDoPerTitoloEBoard(String titolo, String titoloBacheca) {
        if (titolo == null || titoloBacheca == null) return null;

        for (ToDo t : listaToDo) {
            if (t.getBacheca() != null &&
                    t.getBacheca().equalsIgnoreCase(titoloBacheca) &&
                    t.getTitoloToDo() != null &&
                    t.getTitoloToDo().equalsIgnoreCase(titolo)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Verifica che l'autore sia autore del to-do prima di eliminarlo e lo rimuove dalla lista globale dell'utente
     * e degli utenti condivisi.
     * @param todo oggetto {@link ToDo} da eliminare.
     */
    public void eliminaToDo(ToDo todo) {
        if (todo == null || !todo.getAutore().equals(this)) {
            return;
        }

        listaToDo.remove(todo); // Rimuovi da listaToDo (che contiene tutti i to-do personali e condivisi)

        for (Utente u : todo.getCondivisoCon()) { // Rimuovi da eventuali utenti con cui è condiviso
            u.rimuoviToDoCondiviso(todo);
        }
        todo.getCondivisoCon().clear();

        todo.setBacheca(null);
    }

    /**
     * trasferisce un to-do esistente a una nuova bacheca, aggiorna la bacheca del to-do specificato
     * visto che si trova nella lista generale, gestisce l'aggiornamento del riferimento alla nuova bacheca.
     * Questo metodo aggiorna il riferimento alla bacheca usando {@link ToDo#setBacheca(String)}
     * @param todo to-do da trasferire, non può essere null.
     * @param nomeBachecaDestinazione neanche il nome della bacheca può essere null
     */
    public void trasferisciToDo(ToDo todo, String nomeBachecaDestinazione) {
        Objects.requireNonNull(todo, "ToDo non può essere null");
        Objects.requireNonNull(nomeBachecaDestinazione, "Nome bacheca non può essere null");

        todo.setBacheca(nomeBachecaDestinazione);
        aggiornaOrdineToDo(nomeBachecaDestinazione);
    }

    /**
     * sposta un to-do in una nuova posizione nella stessa bacheca.
     * @param titoloBacheca di riferimento.
     * @param titoloToDo da spostare.
     * @param nuovaPosizione posizione desiderata.
     */
    public void spostaToDo(String titoloBacheca, String titoloToDo, int nuovaPosizione) {
        ToDo toDoDaSpostare = null;

        for (ToDo t : listaToDo) {
            if (t.getBacheca() != null
                    && t.getBacheca().equalsIgnoreCase(titoloBacheca)
                    && t.getTitoloToDo().equalsIgnoreCase(titoloToDo)) {
                toDoDaSpostare = t;
                break;
            }
        }
        if (toDoDaSpostare == null) {
            throw new IllegalArgumentException("ToDo non trovato nella bacheca specificata.");
        }
        // Rimuovo e reinserisco nella nuova posizione
        listaToDo.remove(toDoDaSpostare);
        int newIndexInMainList = calcolaNuovoIndice(titoloBacheca, nuovaPosizione);
        listaToDo.add(newIndexInMainList, toDoDaSpostare);
        aggiornaOrdineToDo(titoloBacheca);
    }

    /**
     * E' usato per spostare un to-do nel metodo che troviamo sopra, calcola la nuova posizione
     * all'interno della quale un to-do dovrebbe essere inserito, tenendo conto solo di quelli appartenti a un'unica bacheca.
     * @param titoloBacheca titolo bacheca di riferimento
     * @param nuovaPosizione posizione desiderataa del to-do all'interno di quella bacheca.
     * @return indice effettivo nella lista to-do globale dove dev'esser posizionato, se l'indice è più grande, viene messo in fondo alla lista.
     */
    private int calcolaNuovoIndice(String titoloBacheca, int nuovaPosizione) {
        int newIndex = listaToDo.size(); // di default in coda
        int count = 0;

        for (int i = 0; i < listaToDo.size(); i++) {
            ToDo t = listaToDo.get(i);
            if (t.getBacheca() != null && t.getBacheca().equalsIgnoreCase(titoloBacheca)) {
                if (count == nuovaPosizione) {
                    newIndex = i;
                }
                count++;
            }
        }
        return newIndex;
    }

    /**
     * Anche questo usato nel metodo per spostare i to-do, assegna un nuovo ordine a tutti i to-do e ci permette di
     * visualizzarli in modo corretto in seguito a spostamenti
     * @param titoloBacheca bacheca dove i to-do devono essere riordinati
     */
    private void aggiornaOrdineToDo(String titoloBacheca) {
        int ordine = 0;
        for (ToDo t : listaToDo) {
            if (t.getBacheca() != null && t.getBacheca().equalsIgnoreCase(titoloBacheca)) {
                t.setOrdine(ordine++);
            }
        }
    }

    /**
     * cerca i to-do che scadono entro una certa data
     * @param dataLimite data massima di scadenza da considerare
     * @return lista di to-do che scadono fino alla dataLimite.
     */
    public List<ToDo> getToDoInScadenzaEntro(LocalDate dataLimite) {
        List<ToDo> result = new ArrayList<>();
        for (ToDo t : listaToDo) {
            if (t.getDataScadenza() != null && !t.getDataScadenza().isAfter(dataLimite)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Recupera la lista di to-do la cui scadenza è esattamente oggi
     * @return una lista di oggetti {@link ToDo} che scadono  oggi.
     */
    public List<ToDo> getToDoInScadenzaOggi() {
        LocalDate oggi = LocalDate.now();
        List<ToDo> entroOggi = getToDoInScadenzaEntro(oggi);
        List<ToDo> soloOggi = new ArrayList<>();

        for (ToDo t : entroOggi) {
            if (t.getDataScadenza() != null && t.getDataScadenza().isEqual(oggi)) {
                soloOggi.add(t);
            }
        }
        return soloOggi;
    }

    /**
     * filtra e restituisce i to-do appartenenti a una bacheca ordinandoli per un'ordine ben preciso.
     * @param titoloBacheca bacheca su cui lavorare.
     * @return lista di to-do appartenenti alla bacheca specifica.
     */
    public List<ToDo> getToDoPerBacheca(String titoloBacheca) {
        return listaToDo.stream()
                .filter(t -> titoloBacheca.equalsIgnoreCase(t.getBacheca()))
                .sorted(Comparator.comparingInt(ToDo::getOrdine))
                .toList();
    }

    /**
     * aggiunge un to-do che è stato condiviso con questo utente, serve per distinguere l'origine del to-do
     * @param todo oggetto to-do condiviso da aggiungere.
     */
    public void aggiungiToDoCondiviso(ToDo todo) {
        if (!listaToDo.contains(todo)) {
            listaToDo.add(todo);
        }
    }

    /**
     * rimuove un to-do condiviso dalla lista dell'utente. Quest'operazione viene richiamata internamente
     * dal metodo {@link #eliminaToDo(ToDo) eliminaToDo} quando un to-do è condiviso con altri utenti.  }
     * @param todo oggetto to-do condiviso da rimuovere.
     */
    public void rimuoviToDoCondiviso(ToDo todo) {
        listaToDo.remove(todo);
    }

    /**
     * cerca e restituisce una bacheca basandosi sul suo titolo
     * @param titolo Bacheca da cercare
     * @return oggetto bacheca se trovato, altrimenti null.
     */
    public Bacheca getBachecaByTitolo(String titolo) {
        if (titolo == null) return null;

        for (Bacheca bacheca : listaBacheche) {
            if (bacheca.getTitoloBacheca() != null &&
                    bacheca.getTitoloBacheca().equalsIgnoreCase(titolo)) {
                return bacheca;
            }
        }
        return null;
    }

    /**
     * imposta la lista delle bacheche dell'utente
     * @param listaBacheche nuova lista di oggetti Bacheca.
     */
    public void setListaBacheche(List<Bacheca> listaBacheche) { this.listaBacheche = listaBacheche; }

    /**
     * getter per lo username
     * @return username dell'utente.
     */
    public String getUsername() { return username;}

    /**
     * getter per la password
     * @return password dell'utente.
     */
    public String getPassword(){ return password; }

    /**
     * restituisce la lista dei to-do associati a quell'utente.
     * @return lista di oggetti di tutti i to-do dell'utente.
     */
    public List<ToDo> getListaToDo(){ return listaToDo; }

    /**
     * restituisce la lista di bacheche associate all'utente.
     * <p>
     * include tutte le bacheche create dall'utente o a cui l'utente ha accesso
     * @return una lista di oggetti {@link Bacheca bacheca}
     */
    public List<Bacheca> getListaBacheche(){ return listaBacheche; }
}


