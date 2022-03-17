package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PlayerThread extends Thread {

    private static int idNext = 1;

    private Socket connSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private int id;
    private String ip;

    public PlayerThread(Socket connSocket) {
        this.connSocket = connSocket;
        this.id = PlayerThread.idNext++;

        InetSocketAddress socketAddress = (InetSocketAddress) connSocket.getRemoteSocketAddress();
        this.ip = socketAddress.getAddress().getHostAddress();

        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            this.outToClient = new DataOutputStream(connSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


    }

    public int getPlayerId() {
        return this.id;
    }

   public void write(String message) {
		try {
			this.outToClient.writeBytes(message + "\n");
		} catch (Exception e ) {
			e.printStackTrace();
		}

	}

}
