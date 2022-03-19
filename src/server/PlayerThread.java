package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

public class PlayerThread extends Thread {

    private static int nextId = 1;
    private GameServer gameServer;

    // --- Player vars ---
    private int id;
    private String ip;
    private boolean ready;

    // --- Connection vars ----
    private Socket connSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public PlayerThread(GameServer gameServer, Socket connSocket) {
        this.gameServer = gameServer;
        this.connSocket = connSocket;
        this.ready = false;

        // Assign id
        this.id = PlayerThread.nextId++;

        // Get ip
        InetSocketAddress socketAddress = (InetSocketAddress) connSocket.getRemoteSocketAddress();
        this.ip = socketAddress.getAddress().getHostAddress();

        // Create in/out streams
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            this.outToClient = new DataOutputStream(connSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Listen for input from this player.
                String message = this.inFromClient.readLine().trim();
                HashMap<String, String> params = this.gameServer.parseMessage(message);

                // Broadcast message to all players (including this player).
                this.gameServer.broadcast(params); // Sync'ed method.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPlayerIp() {
        return this.ip;
    }

    public int getPlayerId() {
        return this.id;
    }

    public void setReady(boolean state) {
        this.ready = state;
    }

    public boolean isReady() {
        return this.ready;
    }

    /**
     * Send message to this player.
     * @param params
     */
    public void write(HashMap<String, String> params) {
        // *** TEST **************************
        if (true) {
            System.out.println("--- Server ---");
            System.out.println("Sending:");
            System.out.println(params);
        }
        // ***********************************

        String message = this.gameServer.composeMessage(params);

        try {
            this.outToClient.writeBytes(message + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
