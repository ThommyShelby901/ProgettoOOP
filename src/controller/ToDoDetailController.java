package controller;

import gui.ToDoGUI;
import model.ToDo;

public class ToDoDetailController {
    private ToDo model;
    private ToDoGUI view;

    public ToDoDetailController(ToDo model, Runnable callback) {
        this.model = model;
        this.view = new ToDoGUI(model, callback); // Passa il ToDo alla GUI

        view.show();
    }
}
