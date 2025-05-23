import controller.LoginController;
import gui.LoginFrame;
import model.Bacheca;
import model.Utente;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        LoginFrame loginFrame = new LoginFrame();
        new LoginController(loginFrame);
        loginFrame.setVisible(true);

    }
}

