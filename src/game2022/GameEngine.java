package game2022;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class GameEngine {
    private final HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private GUI gui;
    private Socket clientSocket;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;

    public GameEngine() {

    }

    public void connect(String serverIp, String playerName) throws Exception
    {
        try {
            this.clientSocket = new Socket(serverIp, 9999);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

            HashMap<String, String> params = this.parseFromServer(this.inFromServer.readLine());

            if (!params.containsKey("id")) {
                throw new Exception("Expected key id is missing.");
            }

            int playerId = Integer.parseInt(params.get("id"));
            int posX = Integer.parseInt(params.get("posX"));
            int posY = Integer.parseInt(params.get("posY"));

            this.createPlayer(playerId, playerName, posX, posY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {

    }

    public void requestMove(int delta_x, int delta_y, String direction) {

    }

    public void move(int playedId, int delta_x, int delta_y, String direction) {

    }

    private void createPlayer(int playerId, String name, int posX, int posY) {
        Player p = new Player(playerId, name, posX, posY, "up");
        this.players.put(playerId, p);
        System.out.println(p);
    }


    private HashMap<String, String> parseFromServer(String message) {
        HashMap<String, String> params = new HashMap<String, String>();

        String[] pairs = message.split(";");
        for (String pair : pairs) {
            String[] tmp = pair.split(":");
            params.put(tmp[0], tmp[1]);
        }

		return params;
    }
}
