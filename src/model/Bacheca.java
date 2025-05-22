package model;

import java.util.List;
import java.util.ArrayList;

public class Bacheca {
    private String titoloBacheca;
    private String descrizioneBacheca;
    private final List<ToDo> listaToDo;

    // Costruttore
    public Bacheca(String titoloBacheca, String descrizioneBacheca) {
        this.titoloBacheca = titoloBacheca;
        this.descrizioneBacheca = descrizioneBacheca;
        this.listaToDo = new ArrayList<>();
    }

    // Getter e Setter
    public String getTitoloBacheca() {
        return titoloBacheca;
    }

    public void setTitoloBacheca(String titoloBacheca) {
        this.titoloBacheca = titoloBacheca;
    }

    public String getDescrizioneBacheca() {
        return descrizioneBacheca;
    }

    public void setDescrizioneBacheca(String descrizioneBacheca) {
        this.descrizioneBacheca = descrizioneBacheca;
    }

    public List<ToDo> getListaToDo() {
        return listaToDo;
    }

    // Metodi di gestione ToDo
    public void aggiungiToDo(ToDo todo) {
        listaToDo.add(todo);
    }

    public void rimuoviToDo(ToDo todo) {
        listaToDo.remove(todo);
    }

    public void mostraToDo() {
        for (ToDo t : listaToDo) {
            System.out.println("- " + t.getTitoloToDo());
        }
    }

    // Metodi statici opzionali, se vuoi gestire le bacheche globalmente
    private static final List<Bacheca> listaBacheche = new ArrayList<>();

    public static void creaBacheca(String titolo, String descrizione) {
        listaBacheche.add(new Bacheca(titolo, descrizione));
    }

    public static void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        for (Bacheca b : listaBacheche) {
            if (b.getTitoloBacheca().equalsIgnoreCase(titoloCorrente)) {
                b.setTitoloBacheca(nuovoTitolo);
                b.setDescrizioneBacheca(nuovaDescrizione);
                return;
            }
        }
        System.out.println("Bacheca non trovata.");
    }

    public static void eliminaBacheca(String titolo) {
        listaBacheche.removeIf(b -> b.getTitoloBacheca().equalsIgnoreCase(titolo));
    }

    public static void visualizzaBacheche() {
        for (Bacheca b : listaBacheche) {
            System.out.println("- " + b.getTitoloBacheca() + ": " + b.getDescrizioneBacheca());
        }
    }

    public static List<Bacheca> getListaBacheche() {
        return listaBacheche;
    }
}
