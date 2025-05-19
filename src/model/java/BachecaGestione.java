package model.java;

import java.util.List;
import java.util.ArrayList;


public class BachecaGestione {
    private static final List<Bacheca> listaBacheche = new ArrayList<>();

    public static void creaBacheca(String titolo, String descrizione) {
        listaBacheche.add(new Bacheca(titolo, descrizione));
    }

    public static List<Bacheca> getListaBacheche() {
        return listaBacheche;
    }

    public static void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) {
        for (Bacheca b : listaBacheche) {
            if (b.getTitoloBacheca().equalsIgnoreCase(titoloCorrente)) {
                b.setTitoloBacheca(nuovoTitolo);
                b.setDescrizioneBacheca(nuovaDescrizione);
                return;
            }
        }
        System.out.println("model.java.Bacheca non trovata.");
    }

    public static void eliminaBacheca(String titolo) {
        listaBacheche.removeIf(b -> b.getTitoloBacheca().equalsIgnoreCase(titolo));
    }

    public static void visualizzaBacheche() {
        for (Bacheca b : listaBacheche) {
            System.out.println("- " + b.getTitoloBacheca() + ": " + b.getDescrizioneBacheca());
        }
    }
}
