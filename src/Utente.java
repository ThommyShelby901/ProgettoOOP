import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Utente {
    private final String username;
    private final String password;
    public List<ToDo> listaToDo;
    private static final Scanner scanner=new Scanner(System.in);

    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();
    }

    public String getUsername(){
        return username;
    }

    public final void aggiungiToDoCondiviso(ToDo todo) {
        if (!listaToDo.contains(todo)) {
            listaToDo.add(todo);
        }
    }

    // Rimuovi un ToDo dalla lista dell'utente (cioè quando la condivisione è rimossa)
    public void rimuoviToDoCondiviso(ToDo todo) {
        listaToDo.remove(todo);
    }

    // Visualizza i ToDo condivisi con l'utente
    public void mostraToDoCondivisi() {
        if (listaToDo.isEmpty()) {
            System.out.println("Nessun ToDo condiviso con l'utente " + username);
        } else {
            System.out.println("ToDo condivisi con " + username + ":");
            for (ToDo todo : listaToDo) {
                System.out.println("- " + todo.getTitoloToDo());
            }
        }
    }

    public boolean esitoAccesso(String u, String p){
        if(username.equals(u) && password.equals(p) ){
            System.out.println("Accesso riuscito");
            return true;
        } else {
            System.out.println("Accesso negato");
            return false;
        }
    }

    public void creaToDo(){
        ToDo nuovo = new ToDo();

        System.out.println("Quali attributi vuoi inserire tra :  "+
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
                    nuovo.setDescrizioneToDo(scanner.nextLine());
                    break;
                case "3":
                    System.out.print("Sfondo : ");
                    nuovo.setSfondo(scanner.nextLine());
                    break;
                case "4":
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
        List<Bacheca> listaBacheche=Bacheca.getListaBacheche();
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
        bachecaScelta.getListaToDo().add(nuovo);
    }

    public void modificaToDo() {
        System.out.println("Ecco la lista dei tuoi ToDo:");
        for (int i = 0; i < listaToDo.size(); i++) {
            System.out.println((i + 1) + ". " + listaToDo.get(i).getTitoloToDo());
        }
        // Seleziona il ToDo da modificare
        System.out.print("Seleziona il numero del ToDo che desideri modificare: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();  // Pulisce il buffer

        if (scelta < 1 || scelta > listaToDo.size()) {
            System.out.println("Scelta non valida.");
            return;
        }
        ToDo todoDaModificare = listaToDo.get(scelta - 1);
        // Modifica del ToDo
        System.out.println("Modifica il ToDo: " + todoDaModificare.getTitoloToDo());

        // Modifica Titolo
        System.out.print("Nuovo Titolo (premi invio per mantenere attuale): ");
        String nuovoTitolo = scanner.nextLine();
        if (!nuovoTitolo.isEmpty()) {
            todoDaModificare.setTitolo(nuovoTitolo);
        }
        // Modifica Descrizione
        System.out.print("Nuova Descrizione (premi invio per mantenere attuale): ");
        String nuovaDescrizione = scanner.nextLine();
        if (!nuovaDescrizione.isEmpty()) {
            todoDaModificare.setDescrizioneToDo(nuovaDescrizione);
        }
        // Modifica Data Scadenza
        System.out.print("Nuova Data di Scadenza (yyyy-mm-dd, premi invio per mantenere attuale): ");
        String nuovaData = scanner.nextLine();
        if (!nuovaData.isEmpty()) {
            todoDaModificare.setDataScadenza(nuovaData);
        }
        // Chiedi se si vuole spostare il ToDo
        System.out.print("Vuoi spostare il ToDo in un'altra posizione (S/N)? ");
        String risposta = scanner.nextLine();
        if (risposta.equalsIgnoreCase("S")) {
            System.out.print("In quale posizione vuoi spostare il ToDo \"" + todoDaModificare.getTitoloToDo() + "\"? ");
            int nuovaPosizione = scanner.nextInt();
            scanner.nextLine(); // Pulisce il buffer
            if (nuovaPosizione < 1 || nuovaPosizione > listaToDo.size()) {
                System.out.println("Posizione non valida.");
            } else {
                listaToDo.remove(todoDaModificare);
                listaToDo.add(nuovaPosizione - 1, todoDaModificare);
                System.out.println("ToDo spostato con successo!");
            }
        } else {
            System.out.println("Nessun cambiamento di posizione.");
        }

        System.out.println("Modifica completata!");
    }

    public void trasferireAUnToDo() {
        // Seleziona il ToDo da spostare
        System.out.println("Seleziona il ToDo da spostare:");
        for (int i = 0; i < listaToDo.size(); i++) {
            System.out.println((i + 1) + ". " + listaToDo.get(i).getTitoloToDo());
        }

        int scelta = scanner.nextInt();
        if (scelta < 1 || scelta > listaToDo.size()) return;

        ToDo todoDaSpostare = listaToDo.get(scelta - 1);

        // Seleziona la nuova bacheca
        System.out.println("Seleziona la nuova bacheca:");
        List<Bacheca> listaBacheche = Bacheca.getListaBacheche();
        for (int i = 0; i < listaBacheche.size(); i++) {
            System.out.println((i + 1) + ". " + listaBacheche.get(i).getTitolo());
        }

        int sceltaBacheca = scanner.nextInt();
        if (sceltaBacheca < 1 || sceltaBacheca > listaBacheche.size()) return;

        // Rimuovi il ToDo dalla vecchia bacheca e aggiungilo alla nuova
        Bacheca nuovaBacheca = listaBacheche.get(sceltaBacheca - 1);
        todoDaSpostare.getBacheca().getListaToDo().remove(todoDaSpostare);
        nuovaBacheca.getListaToDo().add(todoDaSpostare);
        todoDaSpostare.setBacheca(nuovaBacheca);

        System.out.println("ToDo spostato con successo!");
    }

    public void eliminaToDo() {
        System.out.println("Ecco la lista dei tuoi ToDo:");
        for (int i = 0; i < listaToDo.size(); i++) {
            System.out.println((i + 1) + ". " + listaToDo.get(i).getTitoloToDo());
        }
        // Seleziona il ToDo da eliminare
        System.out.print("Seleziona il numero del ToDo che desideri eliminare: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();  // Pulisce il buffer

        if (scelta < 1 || scelta > listaToDo.size()) {
            System.out.println("Scelta non valida.");
            return;
        }
        // Rimuovi il ToDo dalla lista
        listaToDo.remove(scelta - 1);
        System.out.println("ToDo eliminato con successo!");
    }

    // Ricerca per titolo o nome del ToDo
    public void ricercaToDo(String query) {
        boolean trovato = false;
        for (ToDo todo : listaToDo) {
            // Verifica se il titolo o la descrizione contiene la query
            if (todo.getTitoloToDo().toLowerCase().contains(query.toLowerCase()) ||
                    todo.getDescrizioneToDo().toLowerCase().contains(query.toLowerCase())) {
                System.out.println("Trovato: " + todo.getTitoloToDo() + " - " + todo.getDescrizioneToDo());
                trovato = true;
            }
        }

        if (!trovato) {
            System.out.println("Nessun ToDo trovato con la ricerca: " + query);
        }
    }


}

