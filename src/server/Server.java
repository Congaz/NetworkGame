package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(9999);
		while (true) {
			Socket connSocket = welcomeSocket.accept();

			ServerThreadRead serverRead = new ServerThreadRead(connSocket);
			ServerThreadWrite serverWrite = new ServerThreadWrite(connSocket);
			serverRead.start();
			serverWrite.start();
		}
	}

}
