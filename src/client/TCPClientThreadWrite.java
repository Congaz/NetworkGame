package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClientThreadWrite extends Thread
{
    private Socket clientSocket;

    public TCPClientThreadWrite(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        try {
            // Server
           DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
           // Input form user
           BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Write message to client:");
			    String message = inFromUser.readLine();
			    outToServer.writeBytes(message + '\n');
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
