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

    /**
     * Connects to server.
     * Throws UnsupportedOperationException if connection is already established.
     * Throws UnknownHostException if IP adress was unresponsive.
     * Throws IOException on failure to create I/O streams.
     * Throws IllegalThreadStateException if TCPClientThreadRead was already started.
     * @param serverIp
     * @throws Exception
     */
    public void connect(String serverIp) throws IOException {
        if (this.clientSocket != null) {
            throw new UnsupportedOperationException("Connection already established.");
        }

        try {
            // --- Connect to server ---
            // Throws: IOException: If I/O error occurs when creating socket
            // Throws: UnknownHostException (sub-class of IOException): IP adress can not be resolved.
            this.clientSocket = new Socket(serverIp, 9999);

            // --- Create output stream ---
            // Throws: IOException: If I/O error occurs when creating output stream
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

            // --- Create read thread (establishes input stream) ---
            this.threadRead = new TcpClientThreadRead(this.clientSocket, this.listener);
            // Throws: IllegalThreadStateException: If the thread was already started
            this.threadRead.start();
        }
        catch (IllegalThreadStateException e) {
            // --- TCPCLientThreadRead already started ---
            // Handled by ConnectScene class.
            throw e;
        }
        catch (UnknownHostException e) {
            // --- IP adress incorrect/non-responsive ---
             // Handled by ConnectScene class.
            throw e;
        }
        catch (IOException e) {
            // --- Failure to create I/O streams ---
             // Handled by ConnectScene class.
            throw e;
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


