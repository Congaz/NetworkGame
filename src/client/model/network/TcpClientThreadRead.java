package client.model.network;

import client.gui.AlertFactory;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public class TcpClientThreadRead extends Thread {
    private Socket clientSocket;
    private TcpResponse listener;

    public TcpClientThreadRead(Socket clientSocket, TcpResponse listener) {
        this.clientSocket = clientSocket;
        this.listener = listener;
    }

    public void run() {
        try {
            // --- Create input stream ---
            // Throws: IOException
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Listen for input
            while (true) {
                // Throws: IOException
                String message = inFromServer.readLine().trim();
                this.listener.response(message);
            }
        }
        catch (SocketException e) {
            // On lost connection
            final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
            Platform.runLater(() -> {
                Alert alert = AlertFactory.serverConnectionLost(e);
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

            System.exit(0); // 0 = Exit without warning.
        }
        catch (IOException e) {
            System.out.println("TCPClientThreadRead IOException.");
            e.printStackTrace();
        }
    }

}
