package game2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class TCPClientThreadRead extends Thread
{
    private Socket clientSocket;

    public TCPClientThreadRead(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        try {
            // Server
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                String message = inFromServer.readLine().trim();
                GameEngine.fromServer(this.parseFromServer(message));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses message from server.
     * Returns key/value hash map.
     *
     * @param message
     * @return
     */
    private HashMap<String, String> parseFromServer(String message) {
        HashMap<String, String> params = new HashMap<String, String>();

        String[] pairs = message.split(";");
        for (String pair : pairs) {
            System.out.println(pair);
            String[] tmp = pair.split(":");
            params.put(tmp[0], tmp[1]);
        }

        return params;
    }
}
