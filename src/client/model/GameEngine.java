package client.model;

import client.gui.AlertFactory;
import client.gui.ConnectScene;
import client.gui.GameScene;
import client.model.network.TcpResponse;
import client.model.network.TcpClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

public class GameEngine {
    // --- Network ---
    private TcpClient tcpClient;

    // --- Gui ---
    private Stage stage;
    private ConnectScene connectScene;
    private GameScene gameScene;

    // --- This player ---
    private int playerId;
    private String playerName;

    // --- Game vars ---
    private String state;
    private final HashMap<Integer, Player> players = new HashMap<Integer, Player>();

    // --- Board ---
    // Required board size
    private final int BOARD_NUM_OF_COLS = 20;
    private final int BOARD_NUM_OF_ROWS = 20;
    // Board array.
    private String[] board;

    public GameEngine(Stage stage) {
        // --- Gui ---
        this.stage = stage;
        this.connectScene = new ConnectScene(this);
        this.stage.setScene(this.connectScene.createScene());

        // --- State ---
        this.state = "connect";

        // *** TEST ********************
        if (false) {
            Player p;
            int id = 1;
            p = new Player(id++, "Max", 0, 0, "up", "white");
            this.players.put(id, p);
            p = new Player(id++, "Kim", 0, 0, "up", "white");
            this.players.put(id, p);
            p = new Player(id++, "Lars", 0, 0, "up", "white");
            this.players.put(id, p);
            p = new Player(id++, "Steffen", 0, 0, "up", "white");
            this.players.put(id, p);
            p = new Player(id++, "Line", 0, 0, "up", "white");
            this.players.put(id, p);
            this.connectScene.updatePlayers();
        }
        // *****************************

        // --- Network ---
        TcpResponse listener = (String message) -> this.fromServer(message);
        this.tcpClient = new TcpClient(listener);

   }

    public int getPlayerId() {
        return this.playerId;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    // *** Methods invoked by Gui ********************************************************************************

    /**
     * Establishes connection to game server.
     * Called by ConnectScene.
     *
     * @param serverIp
     * @param playerName
     * @throws Exception
     */
    public void connectAction(String serverIp, String playerName) throws IOException {
        this.playerName = playerName;
        // --- Connect to server ---
        // Responses from server will invoke fromServer() method.
        this.tcpClient.connect(serverIp);
    }

    /**
     * This player has clicked start.
     * Called by ConnectScene.
     */
    public void readyAction() {
        HashMap<String, String> params = new HashMap<>();
        params.put("message", "ready");
        params.put("id", String.valueOf(this.playerId));
        this.writeServer(params);
    }

    // ******************************************************************************************************

    /**
     * Invoked by TCPClientThreadRead whenever message is recieved from server.
     *
     * @param message
     */
    public synchronized void fromServer(String message) throws RuntimeException {
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
        } else if (params.get("message").equals("start")) {
            this.state = params.get("message");
        }

        // --- Determine action ---
        try {
            switch (this.state) {
                case "connect":
                    this.stateConnect(params);
                    Platform.runLater(() -> this.connectScene.updatePlayers());
                    // Platform.runlater() is necessary to call an javaFX application method
                    // from a non-JavaFx application thread.
                    break;
                case "acceptPlayer":
                    this.stateAcceptPlayer(params);
                    Platform.runLater(() -> this.connectScene.updatePlayers());
                    break;
                case "countdown":
                    int countdown = Integer.parseInt(params.get("countdown"));
                    Platform.runLater(() -> this.connectScene.updateCountdown(countdown));
                    break;
                case "start":
                    this.stateStart();
                    break;
                case "running":
                    this.stateRunning(params);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown state: " + this.state);
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

    }


    public HashMap<Integer, Player> getPlayers() {
        return this.players;
    }



    // *** Pre-game logic ********************************************************************************************

    /**
     * Called upon inital connection with server
     * Throws IllegalArgumentException if passed params are invalid.
     * @param paramsIn
     */
    private void stateConnect(HashMap<String, String> paramsIn) throws IllegalArgumentException {
        String[] requiredKeys = {"message", "id", "board"};
        this.checkRequiredKeys(paramsIn, requiredKeys);

        if (!paramsIn.get("message").equals("connected")) {
            throw new IllegalArgumentException("Expected 'connected' message. Recieved: " + paramsIn.get("message"));
        }

        // Change state
        this.state = "acceptPlayer";

        // --- Create board ---
        // If board is invalid, alert will be shown and app will exit.
        this.board = this.convertString2Board(paramsIn.get("board"));

        // --- Create this player object ---
        paramsIn.put("name", this.playerName); // Add name to params.
        this.createPlayer(paramsIn);
        this.playerId = Integer.parseInt(paramsIn.get("id"));

        // Send to server that we are connected along with our id and name.
        HashMap<String, String> paramsOut = new HashMap<>();
        paramsOut.put("message", "connected");
        paramsOut.put("broadcast", "false"); // Prevents server from broadcasting message.
        paramsOut.put("id", String.valueOf(this.playerId));
        paramsOut.put("name", this.playerName);
        this.writeServer(paramsOut);
    }

    /**
     * Accepts newly connected players and ready message from connected players.
     * Throws IllegalArgumentException if requiredKeys are missing, or if message is unknown.
     * Throws NoSuchElementException if no player objects exists for passed player id.
     * @param params
     * @throws IllegalArgumentException
     */
    private void stateAcceptPlayer(HashMap<String, String> params) throws RuntimeException {
        if (params.get("message").equals("connected")) {
            // --- Player has connected ---
            String[] requiredKeys = {"id", "name", "posX", "posY", "direction", "color"};
            this.checkRequiredKeys(params, requiredKeys);

            // Fail safe: Ignore our own player id
            if (Integer.parseInt(params.get("id")) != this.playerId) {
                // Create foreign player object
                this.createPlayer(params);
            }
        }
        else if (params.get("message").equals("ready")) {
            // --- Player has clicked start ---
            this.checkRequiredKeys(params, new String[]{"id"});

            Player p = this.players.get(Integer.parseInt(params.get("id")));
            if (p == null) {
                throw new NoSuchElementException("Unable to retrieve player object from id: " + params.get("id"));
            }
            p.setReady(true);
        } else {
            throw new IllegalArgumentException("Unexpected message. Recieved: " + params.get("message"));
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
            this.gameScene.updatePlayers();
        });

        // Update state
        this.state = "running";
    }



    // **** Game logic ************************************************************************************************

     /**
     * Request move action for myPlayerId.
     *
     * @param delta_x
     * @param delta_y
     * @param direction
     */
    public void requestMove(int delta_x, int delta_y, String direction) {
        HashMap<String, String> params = new HashMap<>();
        params.put("message", "requestMove");
        params.put("id", String.valueOf(this.playerId));
        params.put("delta_x", String.valueOf(delta_x));
        params.put("delta_y", String.valueOf(delta_y));
        params.put("direction", direction);
        this.tcpClient.write(this.composeMessage(params));

    }

    private void stateRunning(HashMap<String, String> params) {
        this.checkRequiredKeys(params, new String[]{"message", "id", "delta_x", "delta_y", "direction"});

        switch (params.get("message")) {
            case "move":
                this.gameLogic(params);
                break;
            default:
                throw new IllegalArgumentException("Unexpected message: " + params.get("message"));
        }

    }

    private void gameLogic(HashMap<String, String> params) {

        Player p = this.players.get(Integer.parseInt(params.get("id")));
        p.setDirection(params.get("direction"));
        int delta_x = Integer.parseInt(params.get("delta_x"));
        int delta_y = Integer.parseInt(params.get("delta_y"));

         // --- Game logic ---------------------------------
        if (this.board[p.getPosY() + delta_y].charAt(p.getPosX() + delta_x) == 'w') {
            // --- Destination pos is a wall ---
            // Ignore move request and award points.
            p.addPoints(-1);
        }
        else {
            // Get any player that may occupy destination pos.
            Player opponent = getPlayerAt(p.getPosX() + delta_x, p.getPosY() + delta_y);

            if (opponent != null) {
                // --- Destination pos is occupied ---
                // Ignore move request and award points.
                p.addPoints(10);
                opponent.addPoints(-10);
            }
            else {
                // --- Destination pos is a floor tile ---
                // Move is allowed.
                // Update position post-move.
                p.setPosX(p.getPosX() + delta_x);
                p.setPosY(p.getPosY() + delta_y);

                // Award points.
                p.addPoints(1);
            }
        }
        // ------------------------------------------------

        Platform.runLater(() -> {
            this.gameScene.updatePlayer(p);
            this.gameScene.updateScore();
        });
    }

    /**
     * Returns player at passed grid-pos (if any), or null.
     *
     * @param x
     * @param y
     * @return
     */
    private Player getPlayerAt(int x, int y) {
        for (Player p : this.players.values()) {
            if (p.getPosX() == x && p.getPosY() == y) {
                return p;
            }
        }
        return null;
    }

    // ****************************************************************************************************************

    /**
     * Creates board from passed string.
     * FOR NOW:
     * If board is invalid, alert will shown and app will exit.
     * @param source
     * @return
     */
    private String[] convertString2Board(String source) {
        // Create array from source
        String[] board = source.split(",");

        // *** TEST ********************************
        if (false) {
            // Make board invalid.
            board[0] = board[0] + "bla bla bla";
        }
        //*******************************************

        try {
            // --- Verify board size ---
            // Throws IllegalStateException if board is invalid.
            this.verifyBoardSize(board);
        }
        catch (IllegalStateException e) {
            // For now we show and alert and exit app.
            // Regarding CountDownLatch see:
            // https://thesoftwareprogrammer.com/2018/01/26/how-can-i-run-something-in-another-thread-and-wait-for-the-result-in-a-different-thread-using-java-javafx/

            final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);

            Platform.runLater(() -> {
                Alert alert = AlertFactory.serverError(e);
                alert.showAndWait();
                latchToWaitForJavaFx.countDown();
            });

            try {
                // Wait for runLater() to finish.
                latchToWaitForJavaFx.await();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            System.exit(0); // 0 = Exit without warning.
        }

        return board;
    }

    /**
     * Throws IllegalArgumentException if required keys are missing.
     * @param params
     */
    private void createPlayer(HashMap<String, String> params) throws IllegalArgumentException {
        // --- Check for required keys ---
        String[] requiredKeys = {"id", "name", "posX", "posY", "direction", "color"};
        this.checkRequiredKeys(params, requiredKeys);
        // --- Prepare values ---
        int id = Integer.parseInt(params.get("id"));
        String name = params.get("name");
        int posX = Integer.parseInt(params.get("posX"));
        int posY = Integer.parseInt(params.get("posY"));
        String direction = params.get("direction");
        String color = params.get("color");
        // --- Create player ---
        Player p = new Player(id, name, posX, posY, direction, color);
        // Add to players hashMap.
        this.players.put(id, p);
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
     *
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
    private HashMap<String, String> parseFromServer(String message, String[] requiredKeys) throws IllegalArgumentException {
        HashMap<String, String> params = parseFromServer(message);
        this.checkRequiredKeys(params, requiredKeys); // Throws Exception if any keys are missing,
        return params;
    }

    private void checkRequiredKeys(HashMap<String, String> params, String[] requiredKeys) throws IllegalArgumentException {
        for (String requiredKey : requiredKeys) {
            if (!params.containsKey(requiredKey)) {
                throw new IllegalArgumentException("Required key is missing: " + requiredKey);
            }
        }
    }

    /**
     * Verifies board size.
     * Throws IllegalStateException if board is invalid.
     * @param board
     * @throws IllegalStateException
     */
    private void verifyBoardSize(String[] board) throws IllegalStateException {
        if (board.length != this.BOARD_NUM_OF_ROWS) {
            throw new IllegalStateException(
                "Board is invalid. " +
                "Rows required: " + this.BOARD_NUM_OF_ROWS + " Rows present: " + board.length
            );
        } else {
            // Check length of each row
            boolean passed = true;
            int i = 0;
            while (i < board.length && passed) {
                if (board[i].length() != this.BOARD_NUM_OF_COLS) {
                    passed = false;
                } else {
                    i++;
                }
            }
            // Check if board passed col inspection.
            if (!passed) {
                throw new IllegalStateException(
                    "Board is invalid. " +
                    "Cols required: " + this.BOARD_NUM_OF_COLS + " " +
                    "Cols present in row(" + (i + 1) + "): " + board[i].length()
                );
            }
        }
    }

    private void forceWindowRefresh() {
        this.stage.getScene().getWindow().setWidth(this.stage.getScene().getWindow().getWidth() + 0.0001);
    }

}
