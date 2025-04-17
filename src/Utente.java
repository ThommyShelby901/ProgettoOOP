import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;



public class Utente {
    private String username;
    private String password;
    public List<ToDo> listaToDo;
    private static final Scanner scanner=new Scanner(System.in);

    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();

    }

    public boolean esitoAccesso(String u, String p){
        if(username.equals(u) && password.equals(p) && u!=null && p!=null){
            System.out.println("Accesso riuscito");
            return true;
        } else {
            System.out.println("Accesso negato");
            return false;
        }
    }

    public void creaToDo(String titolo, String descrizione){
        ToDo nuovo = new ToDo();

        System.out.println("Quali attributi vuoi inserire tra :\n "+
                "1.Titolo, 2.Descrizione, 3.Sfondo, 4.Colore sfondo, 5.Data scadenza, 6.Url, 7.Stato\n"+
                "Inserisci i numeri di quelli che intendi inserire : ");

        String input=scanner.nextLine();

        String[] scelta=input.split(" ");

        for(String s : scelta) {
            switch (s.trim()) {
                case "1":
                    System.out.print("Titolo : ");
                    nuovo.setTitolo(scanner.nextLine());
                    break;
                case "2":
                    System.out.print("Descrizione : ");
                    nuovo.setDescrizione(scanner.nextLine());
                    break;
                case "3:
                    System.out.print("Sfondo : ");
                    nuovo.setSfondo(scanner.nextLine());
                    break;
                case "4:
                    System.out.print("Colore sfondo : ");
                    nuovo.setColoreSfondo(scanner.nextLine());
                    break;
                case "5":
                    System.out.print("Data scadenza : ");
                    nuovo.setDataScadenza(scanner.nextLine());
                    break;
                case "6":
                    System.out.print("url");
                    nuovo.setUrl(scanner.nextLine());
                    break;
            }
        }
                System.out.println("Seleziona una bacheca in cui inserire il ToDo:");
                List<Bacheca> listaBacheche=new Bacheca(" ", "").getListaBacheche();
                for(int i=0;i<listaBacheche.size();i++){
                    System.out.println((i+1)+". "+listaBacheche.get(i).getTitolo());
                }
                int sceltaBacheca=scanner.nextInt();
                scanner.nextLine();

                if(sceltaBacheca<1 || sceltaBacheca > listaBacheche.size()){
                    System.out.println("Scelta non valida");
                    return;
                }
                Bacheca bachecaScelta=listaBacheche.get(sceltaBacheca-1);
                bachecaScelta.getListaBacheche().add(nuovo);
    }









}
