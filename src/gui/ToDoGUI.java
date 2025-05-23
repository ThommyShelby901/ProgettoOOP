package gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import model.ToDo;

public class ToDoGUI {
    private JPanel panel;
    private JTextField titoloField;
    private JTextField descrizioneField;
    private JTextField dataField;
    private JTextField coloreField;
    private JButton salvaBtn;
    private JLabel lblTitolo;
    private JLabel lblDescrizione;
    private JLabel lblDataScadenza;
    private JLabel lblColoreSfondo;
    private JFrame frame;

    private ToDo toDo;
    private Runnable callback;

    public ToDoGUI(ToDo toDo, Runnable callback) {
        this.toDo = toDo;
        this.callback = callback;

        frame = new JFrame("Modifica ToDo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel); // Usa il pannello definito nel `.form`
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Imposta i valori nelle etichette
        lblTitolo.setText("Titolo:");
        lblDescrizione.setText("Descrizione:");
        lblDataScadenza.setText("Data Scadenza (yyyy-MM-dd):");
        lblColoreSfondo.setText("Colore Sfondo:");

        // Imposta i valori nei campi di input
        titoloField.setText(toDo.getTitoloToDo() != null ? toDo.getTitoloToDo() : "");
        descrizioneField.setText(toDo.getDescrizioneToDo() != null ? toDo.getDescrizioneToDo() : "");
        dataField.setText(toDo.getDataScadenza() != null ? toDo.getDataScadenza().toString() : "");
        coloreField.setText(toDo.getColoreSfondo() != null ? toDo.getColoreSfondo() : "");

        salvaBtn.addActionListener(e -> salvaModifiche());
    }

    private void salvaModifiche() {
        toDo.setTitoloToDo(titoloField.getText());
        toDo.setDescrizioneToDo(descrizioneField.getText());

        try {
            String dataStr = dataField.getText();
            if (!dataStr.isEmpty()) {
                toDo.setDataScadenza(LocalDate.parse(dataStr).toString());
            } else {
                toDo.setDataScadenza(null);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Data non valida! Usa formato yyyy-MM-dd");
            return;
        }

        toDo.setColoreSfondo(coloreField.getText());

        JOptionPane.showMessageDialog(frame, "ToDo salvato!");
        frame.dispose();

        if (callback != null) {
            callback.run(); // Aggiorna la lista principale
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}
