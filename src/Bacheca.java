import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bacheca {
    private String titoloBacheca;
    private String descrizione;
    private static final List<Bacheca> listaBacheche=new ArrayList<>();
    private final List<ToDo> listaToDo;
    private static final Scanner scanner=new Scanner(System.in);

    public Bacheca(String titolo, String descrizione){
        this.titoloBacheca = titoloBacheca;
        this.descrizione = descrizione;
        this.listaToDo=new ArrayList<>();
    }

    public List<ToDo> getListaToDo(){
        return listaToDo;
    }

    public String getTitolo() {
        return titoloBacheca;
    }

    public static List<Bacheca> getListaBacheche() {
        return listaBacheche;
    }

    public static void inizBachecaPredefinita() {
        listaBacheche.add(new Bacheca("Università", "ins"));
        listaBacheche.add(new Bacheca("Lavoro", "ins"));
        listaBacheche.add(new Bacheca("Tempo Libero", "ins"));
    }
    //ogni bacheca ha un titolo e una descrizione dunque per esser creata definiamo tali attributi
    public void creaBacheca(){
        System.out.print("Titolo : ");
        String titolo=scanner.nextLine();
        System.out.print("Descrizione : ");
        String descrizione=scanner.nextLine();

        Bacheca nuovaBacheca=new Bacheca(titolo, descrizione);
        listaBacheche.add(nuovaBacheca);
    }

    public static void visualizzaBacheche() {
        if (listaBacheche.isEmpty()) {
            System.out.println("Nessuna bacheca disponibile.");
            return;
        }
        System.out.println("Lista delle bacheche:");
        for (int i = 0; i < listaBacheche.size(); i++) {
            Bacheca b = listaBacheche.get(i);
            System.out.println((i + 1) + ". " + b.titoloBacheca + " - " + b.descrizione);
        }
    }

    public static void modificaBacheca() {
        visualizzaBacheche();
        System.out.print("Seleziona il numero della bacheca da modificare: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();

        if (scelta < 1 || scelta > listaBacheche.size()) {
            System.out.println("Scelta non valida.");
            return;
        }

        Bacheca b = listaBacheche.get(scelta - 1);

        System.out.print("Nuovo titolo (invio per lasciare invariato): ");
        String nuovoTitolo = scanner.nextLine();
        if (!nuovoTitolo.isEmpty()) {
            b.titoloBacheca = nuovoTitolo;
        }

        System.out.print("Nuova descrizione (invio per lasciare invariata): ");
        String nuovaDescrizione = scanner.nextLine();
        if (!nuovaDescrizione.isEmpty()) {
            b.descrizione = nuovaDescrizione;
        }

        System.out.println("Bacheca modificata con successo!");
    }

    public static void eliminaBacheca() {
        if (listaBacheche.isEmpty()) {
            System.out.println("Nessuna bacheca disponibile.");
            return;
        }
        visualizzaBacheche();  // Usa la funzione già esistente
        System.out.print("Numero bacheca da eliminare: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();  // Pulisce il buffer

        if (scelta >= 1 && scelta <= listaBacheche.size()) {
            String titolo = listaBacheche.remove(scelta - 1).getTitolo();
            System.out.println("Bacheca \"" + titolo + "\" eliminata.");
        } else {
            System.out.println("Scelta non valida.");
        }
    }
}

