package client;

import client.gui.AlertFactory;
import client.gui.MainApp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.concurrent.CountDownLatch;

public class MainClient {
    public static void main(String[] args) {
        try {
            Application.launch(MainApp.class);
        }
        catch (Exception e) {
            // Latch-ditch effort to make a reasonable response to user in case of unchecked expections.
            // As we don't know if Exception was thrown by a non-JavaFX thread, we need to check.
            if (Platform.isFxApplicationThread()) {
                Alert alert = AlertFactory.serverError(e);
                alert.showAndWait();
            }
            else {
                final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
                Platform.runLater(() -> {
                    Alert alert = AlertFactory.serverError(e);
                    alert.showAndWait();
                    latchToWaitForJavaFx.countDown();
                });

                try {
                    // Wait for runLater() to finish.
                    latchToWaitForJavaFx.await();
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            System.exit(0); // 0 = Exit without warning.
        }
    }
}
