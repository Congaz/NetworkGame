package client.model;

import client.gui.ConnectScene;
import client.gui.GameScene;
import client.model.network.TcpResponse;
import client.model.network.TcpClient;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class GameEngine {
    // --- Network ---
    private TcpClient tcpClient;

    // --- Gui ---
    private Stage stage;
    private ConnectScene connectScene;
    private GameScene gameScene;

    // --- This player ---
    private int id;
    private String name;

    // --- Game vars ---
    private String state;
    private final HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private String[] board = {
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

    public GameEngine(Stage stage) {
        // --- Gui ---
        this.stage = stage;
        this.connectScene = new ConnectScene(this);
        this.stage.setScene(this.connectScene.createScene());

        // --- State ---
        this.state = "connect";

        // *** TEST ********************
        if (false) {
            this.createPlayer(1, "Michael", 0, 0, "up");
            this.createPlayer(2, "Kim", 0, 0, "up");
            this.createPlayer(3, "Max", 0, 0, "up");
            this.createPlayer(4, "Lars", 0, 0, "up");
            this.createPlayer(5, "Steffen", 0, 0, "up");
            this.connectScene.updatePlayers();
        }
        // *****************************

        // --- Network ---
        TcpResponse listener = (String message) -> this.fromServer(message);
        this.tcpClient = new TcpClient(listener);

        //this.stateStart();
    }

    // *** Methods invoked by Gui ********************************************************************************

    /**
     * Establishes connection to game server.
     * Called by ConnectScene.
     * @param serverIp
     * @param playerName
     * @throws Exception
     */
    public void connectAction(String serverIp, String playerName) {
        try {
            this.name = playerName;
            // Connect to server.
            this.tcpClient.connect(serverIp);
            // Responses from server will invoke fromServer() method.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This player has clicked start.
     * Called by ConnectScene.
     */
    public void readyAction() {
        HashMap<String, String> params = new HashMap<>();
        params.put("message", "ready");
        params.put("id", String.valueOf(this.id));
        this.writeServer(params);
    }

    // ******************************************************************************************************

    /**
     * Invoked by TCPClientThreadRead whenever message is recieved from server.
     *
     * @param message
     */
    public void fromServer(String message) {
        // Parse message. Require that "message" key is present.
        HashMap<String, String> params = this.parseFromServer(message, new String[]{"message"});

        // *** TEST ***********
        if (true) {
            System.out.println("--- Client ---");
            System.out.println("From server:");
            System.out.println(params);
        }
        // ********************

        // --- Change state by server message ---
        if (params.get("message").equals("countdown")) {
            this.state = params.get("message");
        }
        else if (params.get("message").equals("start")) {
             this.state = params.get("message");
        }

        // --- Determine action ---
        try {
            if (this.state.equals("connect")) {
                this.stateConnect(params);
                Platform.runLater(() -> this.connectScene.updatePlayers());
                // Platform.runlater() is necessary to call an javaFX application method
                // from a non-JavaFx application thread.
            }
            else if (this.state.equals("acceptPlayer")) {
                this.stateAcceptPlayer(params);
                Platform.runLater(() -> this.connectScene.updatePlayers());
            }
            else if (this.state.equals("countdown")) {
               int countdown = Integer.parseInt(params.get("countdown"));
               Platform.runLater(() -> this.connectScene.updateCountdown(countdown));
            }
            else if (this.state.equals("start")) {
               this.stateStart();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }




    public HashMap<Integer, Player> getPlayers() {
        return this.players;
    }

    /**
     * Request move action for myPlayerId.
     *
     * @param delta_x
     * @param delta_y
     * @param direction
     */
    public void requestMove(int delta_x, int delta_y, String direction) {
        //this.broadcast(
        //        "id:" + this.myPlayerId + ";" +
        //                "posX:" + delta_x + ";" +
        //                "posY:" + delta_y + ";" +
        //                "direction:" + direction + ";"
        //);
    }

    // *** Pre-game logic ********************************************************************************************

    /**
     * Called upon inital connection with server
     * @param paramsIn
     */
    private void stateConnect(HashMap<String, String> paramsIn) throws Exception {
        String[] requiredKeys = {"message", "id", "posX", "posY", "direction"};
        this.checkRequiredKeys(paramsIn, requiredKeys);

        if (!paramsIn.get("message").equals("connected")) {
            throw new Exception("Unexpected message. Expected 'connected'. Recieved: " + paramsIn.get("message"));
        }

        // Change state
        this.state = "acceptPlayer";

        // Create this player object
        int id = Integer.parseInt(paramsIn.get("id"));
        int posX = Integer.parseInt(paramsIn.get("posX"));
        int posY = Integer.parseInt(paramsIn.get("posY"));
        String direction = paramsIn.get("direction");
        this.createPlayer(id, this.name, posX, posY, direction);
        this.id = id;

        // Broadcast to server that we are connected along with our player params.
        HashMap<String, String> paramsOut = new HashMap<>();
        paramsOut.put("message", "connected");
        //paramsOut.put("broadcastExcludeId", String.valueOf(this.id)); // Exclude ourselves from broadcast.
        paramsOut.put("broadcastExcludeId", String.valueOf(-1));
        paramsOut.put("id", String.valueOf(this.id));
        paramsOut.put("name", name);
        paramsOut.put("posX", String.valueOf(posX));
        paramsOut.put("posY", String.valueOf(posY));
        paramsOut.put("direction", direction);
        this.writeServer(paramsOut);
    }

    private void stateAcceptPlayer(HashMap<String, String> params) throws Exception {
        if (params.get("message").equals("connected")) {
            // --- Player has connected ---
            String[] requiredKeys = {"id", "name", "posX", "posY", "direction"};
            this.checkRequiredKeys(params, requiredKeys);

            // Fail safe: Ignore our own player id
            if (Integer.parseInt(params.get("id")) != this.id) {
                // Create foreign player object
                int id = Integer.parseInt(params.get("id"));
                String name = params.get("name");
                int posX = Integer.parseInt(params.get("posX"));
                int posY = Integer.parseInt(params.get("posY"));
                String direction = params.get("direction");
                this.createPlayer(id, name, posX, posY, direction);
            }
        }
        else if (params.get("message").equals("ready")) {
            // --- Player has clicked start ---
            String[] requiredKeys = {"id"};
            this.checkRequiredKeys(params, requiredKeys);

            Player p = this.players.get(Integer.parseInt(params.get("id")));
            p.setReady(true);
        }
        else {
            throw new Exception("Unexpected message. Recieved: " + params.get("message"));
        }
    }

    public void stateStart() {
        // Create game scene
        this.gameScene = new GameScene(this, this.board);

        Platform.runLater(() -> {
            // Display game scene
            this.stage.setScene(this.gameScene.getScene());
            forceWindowRefresh();
            this.gameScene.updateScore();
        });

        // Update state
        this.state = "running";
    }


    private void move(int playedId, int delta_x, int delta_y, String direction) {

    }

    // ***************************************************************************************************************

    private void createPlayer(int playerId, String name, int posX, int posY, String direction) {
        Player p = new Player(playerId, name, posX, posY, direction);
        this.players.put(playerId, p);
    }

    /**
     * Sends message to server.
     * Message will be broadcast to all connected players (including this).
     *
     * @param params
     */
    private void writeServer(HashMap<String, String> params) {
        // Convert HashMap to string and send to server.
        String message = this.composeMessage(params);
        this.tcpClient.write(message);
    }

    /**
     * Converts HashMap to string.
     * @param params
     * @return
     */
    private String composeMessage(HashMap<String, String> params) {
        StringBuilder message = new StringBuilder();
        for (String key : params.keySet()) {
            message.append(key);
            message.append(":");
            message.append(params.get(key));
            message.append(";");
        }
        return message.toString();
    }

    /**
     * Parses message from server.
     * Returns key/value hash map.
     *
     * @param message
     * @return
     */
    private HashMap<String, String> parseFromServer(String message) {
        HashMap<String, String> params = new HashMap<String, String>();

        String[] pairs = message.split(";");
        for (String pair : pairs) {
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
    private HashMap<String, String> parseFromServer(String message, String[] requiredKeys) {
        HashMap<String, String> params = parseFromServer(message);
        this.checkRequiredKeys(params, requiredKeys); // Throws Exception if any keys are missing,
        return params;
    }

    private void checkRequiredKeys(HashMap<String, String> params, String[] requiredKeys) {
        for (String requiredKey : requiredKeys) {
            if (!params.containsKey(requiredKey)) {
                throw new NoSuchElementException("Required key is missing: " + requiredKey);
            }
        }
    }

    private void forceWindowRefresh() {
        this.stage.getScene().getWindow().setWidth(this.stage.getScene().getWindow().getWidth() + 0.0001);
    }

}
