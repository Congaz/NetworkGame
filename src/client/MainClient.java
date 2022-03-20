package client;

import client.gui.AlertFactory;
import client.gui.MainApp;
import javafx.application.Application;
import javafx.scene.control.Alert;

public class MainClient {
    public static void main(String[] args) {
        try {
            Application.launch(MainApp.class);
        }
        catch (Exception e) {
            Alert alert = AlertFactory.unrecoverableError(e);
            alert.showAndWait();
            System.exit(0);
        }
    }
}
