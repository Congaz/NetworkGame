package client.model.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TcpClientThreadRead extends Thread {
    private Socket clientSocket;
    private TcpResponse listener;

    public TcpClientThreadRead(Socket clientSocket, TcpResponse listener) {
        this.clientSocket = clientSocket;
        this.listener = listener;
    }

    public void run() {
        try {
            // Create input stream
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Listen for input
            while (true) {
                String message = inFromServer.readLine().trim();
                this.listener.response(message);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
