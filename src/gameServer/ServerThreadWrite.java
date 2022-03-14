package gameServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThreadWrite extends Thread{
	Socket connSocket;

	public ServerThreadWrite(Socket connSocket) {
		this.connSocket = connSocket;
	}
	public void run() {
		try {
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				System.out.println("Write message to client: ");
				String message = inFromUser.readLine();
				outToClient.writeBytes(message + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
