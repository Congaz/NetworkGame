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
    private int playerId;
    private String ip;
    private int posX;
    private int posY;
    private String direction;
    private String color;
    private String playerName;
    private boolean ready;

    // --- Connection vars ----
    private Socket connSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public PlayerThread(GameServer gameServer, Socket connSocket,
                        int posX, int posY, String direction, String color) {
        this.gameServer = gameServer;
        this.connSocket = connSocket;
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
        this.color = color;
        this.playerName = "Unknown";
        this.ready = false;

        // Assign id
        this.playerId = PlayerThread.nextId++;

        // Get ip
        InetSocketAddress socketAddress = (InetSocketAddress) this.connSocket.getRemoteSocketAddress();
        this.ip = socketAddress.getAddress().getHostAddress();

        // Create in/out streams
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
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

                if (params.containsKey("broadcast") && params.get("broadcast").equals("false")) {
                    // Message is intended for server only.
                    this.gameServer.fromPlayer(params);
                }
                else {
                    // Broadcast message to all players (including this player).
                    this.gameServer.broadcast(params); // Sync'ed method.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPlayerIp() {
        return this.ip;
    }

    /**
     * Note: getId() method name is reserved by java.lang.Thread.
     * @return
     */
    public int getPlayerId() {
        return this.playerId;
    }

    /**
     * Note: getName() method name is reserved by java.lang.Thread.
     * @return
     *
     */
    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean state) {
        this.ready = state;
    }


    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public HashMap<String, String> createUpdatePackage() {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(this.playerId));
        params.put("name", this.playerName);
        params.put("posX", String.valueOf(this.posX));
        params.put("posY", String.valueOf(this.posY));
        params.put("direction", this.direction);
        params.put("color", this.color);
        params.put("ready", String.valueOf(this.ready));
        return params;
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
