import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bacheca {
    private String titolo;
    private String descrizione;
    private List<Bacheca> listaBacheche;
    private static final Scanner scanner=new Scanner(System.in);

    public Bacheca(String titolo, String descrizione){
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.listaBacheche=new ArrayList<>();
        Bacheca universita = new Bacheca("Università", "dains");
        Bacheca lavoro = new Bacheca ("Lavoro", "dains");
        Bacheca tempoLibero = new Bacheca ( "Tempo Libero", "dains");

        listaBacheche.add(universita);
        listaBacheche.add(lavoro);
        listaBacheche.add(tempoLibero);
    }

    public List<Bacheca> getListaBacheche() {
        return listaBacheche;
    }

    public String getTitolo() {
        return titolo;
    }

    //ogni bacheca ha un titolo e una descrizione dunque per esser creata definiamo tali attributi
    public void creaBacheca(){
        System.out.print("Titolo : ");
        String titolo=scanner.nextLine();
        System.out.print("Descrizione : ");
        String descrizione=scanner.nextLine();

        Bacheca nuovaBacheca=new Bacheca(titolo, descrizione);


    }

    public void modificaBacheca(){


    }

    public void eliminaBacheca(){

    }


}
}
