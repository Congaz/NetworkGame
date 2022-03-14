package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThreadRead extends Thread{
	Socket connSocket;
	
	public ServerThreadRead(Socket connSocket) {
		this.connSocket = connSocket;
	}
	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());

			while (true) {
				// Do the work and the communication with the client here
				// The following two lines are only an example
				String clientSentence = inFromClient.readLine();
				System.out.println("From client: ");
				System.out.println(clientSentence);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
