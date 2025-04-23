import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Inizializza bacheche predefinite
        Bacheca.inizBachecaPredefinita();

        System.out.println("Registrazione utente:");
        System.out.print("Inserisci username: ");
        String username = scanner.nextLine();
        System.out.print("Inserisci password: ");
        String password = scanner.nextLine();
        Utente utente = new Utente(username, password);

        // Login
        System.out.println("\nLogin richiesto:");
        System.out.print("Username: ");
        String u = scanner.nextLine();
        System.out.print("Password: ");
        String p = scanner.nextLine();

        if (!utente.esitoAccesso(u, p)) {
            System.out.println("Programma terminato.");
            return;
        }
        int scelta;
        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Crea nuovo ToDo");
            System.out.println("2. Modifica ToDo");
            System.out.println("3. Elimina ToDo");
            System.out.println("4. Visualizza ToDo condivisi");
            System.out.println("5. Ricerca ToDo");
            System.out.println("6. Crea bacheca");
            System.out.println("7. Visualizza bacheche");
            System.out.println("8. Modifica bacheca");
            System.out.println("9. Elimina bacheca");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");
            scelta = scanner.nextInt();
            scanner.nextLine(); // pulisce il buffer

            switch (scelta) {
                case 1 -> utente.creaToDo();
                case 2 -> utente.modificaToDo();
                case 3 -> utente.eliminaToDo();
                case 4 -> utente.mostraToDoCondivisi();
                case 5 -> {
                    System.out.print("Inserisci termine di ricerca: ");
                    String query = scanner.nextLine();
                    utente.cercaPerTitoloONome(query);
                }
                case 6 -> new Bacheca("", "").creaBacheca(); // usiamo un oggetto fittizio solo per accedere al metodo
                case 7 -> Bacheca.visualizzaBacheche();
                case 8 -> Bacheca.modificaBacheca();
                case 9 -> Bacheca.eliminaBacheca();
                case 0 -> System.out.println("Chiusura del programma.");
                default -> System.out.println("Scelta non valida.");
            }

        } while (scelta != 0);

        scanner.close();
    }
}