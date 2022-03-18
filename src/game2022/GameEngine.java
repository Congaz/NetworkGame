package game2022;

import javafx.application.Platform;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class GameEngine
{
    // --- Network ---
    private Socket clientSocket;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private TCPClientThreadRead threadRead;

    // --- Game objects ---
    private final HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private int myPlayerId;
    private Stage stage;
    private GameScene gameScene;
    private final static String[] board = {
            // 20x20
            "wwwwwwwwwwwwwwwwwwww",
            "w        ww        w",
            "w w  w  www w  w  ww",
            "w w  w   ww w  w  ww",
            "w  w               w",
            "w w w w w w w  w  ww",
            "w w     www w  w  ww",
            "w w     w w w  w  ww",
            "w   w w  w  w  w   w",
            "w     w  w  w  w   w",
            "w ww ww        w  ww",
            "w  w w    w    w  ww",
            "w        ww w  w  ww",
            "w         w w  w  ww",
            "w        w     w  ww",
            "w  w              ww",
            "w  w www  w w  ww ww",
            "w w      ww w     ww",
            "w   w   ww  w      w",
            "wwwwwwwwwwwwwwwwwwww"
    };

    public GameEngine(Stage stage)
    {
        this.stage = stage;
        this.gameScene = new GameScene(this, board);

    }

    public void connect(String serverIp, String playerName) throws Exception
    {
        try {
            // Connect to server and create in/out streams.
            this.clientSocket = new Socket(serverIp, 9999);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

             this.threadRead = new TCPClientThreadRead(this.clientSocket);
             this.threadRead.start();

            // Parse server response
            //String[] requiredKeys = {"id", "posX", "posY", "direction"};
            //HashMap<String, String> params = this.parseFromServer(this.inFromServer.readLine().trim(), requiredKeys);
            //
            //int playerId = Integer.parseInt(params.get("id"));
            //int posX = Integer.parseInt(params.get("posX"));
            //int posY = Integer.parseInt(params.get("posY"));
            //String direction = params.get("direction");
            //this.createPlayer(playerId, playerName, posX, posY, direction);
            //this.myPlayerId = playerId;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fromServer(HashMap<String, String> params) {
        System.out.println("--- Client: from server ---");
        System.out.println(params);
    }


    public void startGame()
    {
        // Display game scene
        this.stage.setScene(this.gameScene.getScene());
        forceWindowRefresh();
        this.gameScene.updateScore();

    }

    public HashMap<Integer, Player> getPlayers() {
        return this.players;
    }

    /**
     * Request move action for myPlayerId.
     * @param delta_x
     * @param delta_y
     * @param direction
     */
    public void requestMove(int delta_x, int delta_y, String direction)
    {
        this.write(
            "id:" + this.myPlayerId + ";" +
            "posX:" + delta_x + ";" +
            "posY:" + delta_y + ";" +
            "direction:" + direction + ";"
        );
    }

    private void forceWindowRefresh() {
        this.stage.getScene().getWindow().setWidth(this.stage.getScene().getWindow().getWidth() + 0.0001);
    }



    private void move(int playedId, int delta_x, int delta_y, String direction)
    {

    }



    private void createPlayer(int playerId, String name, int posX, int posY, String direction)
    {
        Player p = new Player(playerId, name, posX, posY, direction);
        this.players.put(playerId, p);
        System.out.println(p);
    }

    /**
     * Sends message to server.
     * @param message
     */
    private void write(String message) {
        try {
            this.outToServer.writeBytes(message + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses message from server.
     * Returns key/value hash map.
     *
     * @param message
     * @return
     */
    private HashMap<String, String> parseFromServer(String message)
    {
        HashMap<String, String> params = new HashMap<String, String>();

        String[] pairs = message.split(";");
        for (String pair : pairs) {
            System.out.println(pair);
            String[] tmp = pair.split(":");
            params.put(tmp[0], tmp[1]);
        }

        return params;
    }

    /**
     * Parses message from server and checks for required keys.
     * Returns key/value hash map.
     * Throws exception if any required keys are absent.
     *
     * @param message
     * @param requiredKeys
     * @return
     */
    private HashMap<String, String> parseFromServer(String message, String[] requiredKeys)
    {
        HashMap<String, String> params = parseFromServer(message);
        for (String requiredKey : requiredKeys) {
            if (!params.containsKey(requiredKey)) {
                throw new NoSuchElementException("Required key is missing: " + requiredKey);
            }
        }
        return params;
    }

}
