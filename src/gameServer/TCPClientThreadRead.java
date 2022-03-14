package gameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
                String response = inFromServer.readLine();
                System.out.println("From server: ");
                System.out.println(response);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
