package client.model.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class TcpClient {

    // --- Network ---
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private TcpClientThreadRead threadRead;

    private TcpResponse listener;

    public TcpClient(TcpResponse listener) {
        this.listener = listener;
    }

    public void connect(String serverIp) throws Exception {
        if (this.clientSocket != null) {
            throw new Exception("IllegalAction. Connection already established.");
        }

        try {
            // Connect to server
            this.clientSocket = new Socket(serverIp, 9999);
            // Create output stream
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            // Create read thread (establishes input stream)
            this.threadRead = new TcpClientThreadRead(this.clientSocket, this.listener);
            this.threadRead.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends message to server.
     * Message will be broadcast to all conneted players.
     *
     * @param message
     */
    public void write(String message) {
        try {
            this.outToServer.writeBytes(message + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


