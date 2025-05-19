import model.java.*;

public class Main {
    public static void main(String[] args) {
        // Creazione bacheche iniziali (comuni a tutti)
        BachecaGestione.creaBacheca("Università", "Compiti, esami, progetti universitari");
        BachecaGestione.creaBacheca("Lavoro", "Task e appuntamenti di lavoro");
        BachecaGestione.creaBacheca("Tempo Libero", "Hobby, viaggi, film da vedere");


        Utente lorenzo = new Utente("Lorenzo", "1234");
        Utente alessandro = new Utente("Alessandro", "abcd");


        lorenzo.esitoAccesso("mario", "1234");


        Bacheca universita = lorenzo.getBachecaByTitolo("Università");
        lorenzo.creaToDo("Studiare Java", "Ripassare OOP per l'esame", null, "#ffffff",
                "2025-05-20", null, StatoToDo.NonCompletato, universita);


        Bacheca lavoro = lorenzo.getBachecaByTitolo("Lavoro");
        lorenzo.creaToDo("Call con il cliente", "Riunione alle 10:00", null, "#cccccc",
                "2025-05-16", null, StatoToDo.NonCompletato, lavoro);

        for (Bacheca b : BachecaGestione.getListaBacheche()) {
            System.out.println("\nmodel.java.Bacheca: " + b.getTitoloBacheca());
            b.mostraToDo();
        }

        ToDo taskUniversita = lorenzo.cercaToDoPerTitolo("Studiare Java");
        taskUniversita.aggiungiCondivisione(lorenzo, alessandro);

        System.out.println("\nmodel.java.ToDo condivisi con Alessandro nella bacheca Università:");
        Bacheca universitaLucia = alessandro.getBachecaByTitolo("Università");
        if (universitaLucia != null) {
            universitaLucia.mostraToDo();
        }

        lorenzo.modificaToDo(taskUniversita, lorenzo, "Ripassare Java", null,
                null, null, null, null, null);


        ToDo taskLavoro = lorenzo.cercaToDoPerTitolo("Call con il cliente");
        lorenzo.eliminaToDo(taskLavoro);


        System.out.println("\nBacheche dopo eliminazione/modifica:");
        for (Bacheca b : BachecaGestione.getListaBacheche()) {
            System.out.println("\nmodel.java.Bacheca: " + b.getTitoloBacheca());
            b.mostraToDo();
        }

        System.out.println("\nVerifica scadenze:");
        for (ToDo t : alessandro.getBachecaByTitolo("Università").getListaToDo()) {
            t.visualizzaScadenza();
        }
    }
}
