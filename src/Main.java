import gui.LoginFrame;
import model.Bacheca;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            // Associa qui il controller, se necessario:
            new controller.LoginController(loginFrame);
            loginFrame.setVisible(true);
        });

        // Esempio di creazione di bacheche predefinite
        if (Bacheca.getListaBacheche().isEmpty()) {
            Bacheca.creaBacheca("Università", "Bacheca per le attività universitarie");
            Bacheca.creaBacheca("Lavoro", "Bacheca per le attività lavorative");
            Bacheca.creaBacheca("Tempo Libero", "Bacheca per le attività di svago");
        }
    }
}

