package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThreadWrite extends Thread{
	Socket connSocket;
	DataOutputStream outToClient;
	BufferedReader inFromUser;

	public ServerThreadWrite(Socket connSocket) {
		this.connSocket = connSocket;
		try {
			outToClient = new DataOutputStream(connSocket.getOutputStream());
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (true) {
				System.out.println("Write message to client: ");
				String message = inFromUser.readLine();
				outToClient.writeBytes(message + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String message) {
		try {
			outToClient.writeBytes(message + "\n");
		} catch (Exception e ) {
			e.printStackTrace();
		}

	}
}
