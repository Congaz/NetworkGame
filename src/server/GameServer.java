package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameServer {
    // --- Server ---
    private String serverState;
    private final int COUNTDOWN_TIME = 5; // Define countdown time in seconds.
    private int countdown;

    // --- Player ---
    private final HashMap<Integer, PlayerThread> playerThreads = new HashMap<Integer, PlayerThread>();
    private final String[] directions = {"up", "down", "left", "right"};
    private ArrayList<String> myArrayList = new ArrayList<>();

    // --- Board ---
    private String[] board;
    private final boolean[][] occupiedStartPos = new boolean[20][20]; // x/y.

    /**
     *
     */
    public GameServer() {
        System.out.println("--- Server ---");
        System.out.println("on-line.");

        try {
            BoardFactory.verfifyAll();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

        this.reset();
        this.serverState = "acceptConnections";

        try {
            ServerSocket welcomeSocket = new ServerSocket(9999);

            // Waiting for connections from players.
            while (this.serverState.equals("acceptConnections")) {
                // Accept connection
                Socket connSocket = welcomeSocket.accept();

                // --- Create start params for player ---
                int[] startPos = getRandomStartPos();
                String direction = getRandomStartDirection();
                String color = "white"; // HARDCODED FOR THE TIME BEING.

                // --- Create player thread ---
                PlayerThread pt = new PlayerThread(this, connSocket, startPos[0], startPos[1], direction, color);
                playerThreads.put(pt.getPlayerId(), pt);

                // --- Send start params and game board to this player ---
                HashMap<String, String> params = new HashMap<>();
                params.put("message", "connected");
                params.put("id", String.valueOf(pt.getPlayerId()));
                params.put("posX", String.valueOf(startPos[0]));
                params.put("posY", String.valueOf(startPos[1]));
                params.put("direction", direction);
                params.put("color", color);
                params.put("board", BoardFactory.convertBoard2String(this.board));
                pt.write(params);

                // Start broadcast mirroring from this player.
                pt.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerState() {
        return this.serverState;
    }

    /**
     * Called by any PlayerThread that recieves a message with broadcast:false.
     * This means that the message is ment for the server only.
     * Throws IllegalArgumentException of required keys are missing.
     * Throws NoSuchElementException if player id couldn't be resolved to player object.
     * @param params
     */
    public synchronized void fromPlayer(HashMap<String, String> params) throws RuntimeException {
        this.checkRequiredKeys(params, new String[]{"message"});

        if (params.get("message").equals("connected")) {
            // Player acknowledges connection. We expect players id and player name to be included.
            this.checkRequiredKeys(params, new String[]{"id", "name"});
            PlayerThread pt = playerThreads.get(Integer.parseInt(params.get("id")));
             if (pt == null) {
                throw new NoSuchElementException("Unable to retrieve player object from id: " + params.get("id"));
            }
            pt.setPlayerName(params.get("name"));

            // Players presence must now be broadcast to all but the player that sent the message.
            HashMap<String , String> paramsOut = pt.createUpdatePackage();
            paramsOut.put("message", "connected");
            paramsOut.put("broadcastExcludeId", String.valueOf(pt.getPlayerId()));
            this.broadcast(paramsOut);

            // The player that sent the message must now be updated on all, already connected players.
            Set<Integer> idSet = this.playerThreads.keySet();
            for (int id : idSet) {
                if (id != pt.getPlayerId()) {
                    PlayerThread oldPlayer = this.playerThreads.get(id);
                    HashMap<String, String> paramsConnected = oldPlayer.createUpdatePackage();
                    paramsConnected.put("message", "connected");
                    pt.write(paramsConnected);
                }
            }
        }
    }

    /**
     * Broadcasts message to all players.
     * Called by PlayerThread(s) whenever they recieve input (except if message includes broadcast:false).
     *
     * @param params
     */
    public synchronized void broadcast(HashMap<String, String> params) {
        // Check if any playerThreads should be excluded from broadcast.
        int excludeId = -1;
        if (params.containsKey("broadcastExcludeId")) {
            excludeId = Integer.parseInt(params.get("broadcastExcludeId"));
        }

        // Broadcast
        for (PlayerThread pt : playerThreads.values()) {
            if (excludeId == -1 || pt.getPlayerId() != excludeId) {
                pt.write(params);
            }
        }

        // --- Perform state dependent operations ---
        if (this.serverState.equals("acceptConnections")) {
            this.stateAcceptConnections(params);
        }
    }

    /**
     * Resets game related params.
     */
    private void reset() {
        // Reset countdown
        this.countdown = this.COUNTDOWN_TIME;

        // Create board
        this.board = BoardFactory.createRandomBoard();

        // Fill occupiedStartPos with false.
        for (boolean[] cols : occupiedStartPos) {
            Arrays.fill(cols, false);
        }
    }

    // *** Pre-game state methods **********************************************************************************

    /**
     * Throws IllegalArgumentException on missing keys.
     * @param params
     */
    private void stateAcceptConnections(HashMap<String, String> params) throws IllegalArgumentException {
        this.checkRequiredKeys(params, new String[]{"message"});

       if (params.get("message").equals("ready")) {
            // --- Player has clicked start ---
            this.checkRequiredKeys(params, new String[]{"id"});
            PlayerThread pt = this.playerThreads.get(Integer.parseInt(params.get("id")));
            pt.setReady(true);

            // --- Check if all players are ready ---
            boolean ready = true;
            Iterator<Map.Entry<Integer, PlayerThread>> it = this.playerThreads.entrySet().iterator();
            while (it.hasNext() && ready) {
                Map.Entry<Integer, PlayerThread> set = it.next();
                PlayerThread plt = set.getValue();
                ready = plt.isReady();
            }

            if (ready) {
                // All players have clicked start. Begin countdown to game start.
                this.serverState = "countdown";
                this.stateCountdown();
            }
        }
    }

    /**
     * Performs countdown to game start.
     */
    private void stateCountdown() {
        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                if (countdown > 0) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("message", "countdown");
                    params.put("countdown", String.valueOf(countdown--));
                    broadcast(params);
                } else {
                    cancel();
                    //timer.cancel();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("message", "start");
                    broadcast(params);
                }
            }
        };

        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(countdownTask, 0, 1000);
    }

    // ****************************************************************************************************************

    /**
     * Creates and returns random start position for player.
     *
     * @return
     */
    private int[] getRandomStartPos() {
        int border = 1; // Thickness of wall border on board-map.
        int posX, posY;

        do {
            posX = this.getRandomSignedInt(border, this.board[0].length() - border); // min, num-of-columns
            posY = this.getRandomSignedInt(border, this.board.length - border); // min, num-of-rows.
        } while (this.occupiedStartPos[posX][posY] || this.board[posY].charAt(posX) == 'w');

        this.occupiedStartPos[posX][posY] = true;
        return new int[]{posX, posY};
    }

    /**
     * Creates and returns random start direction for player.
     *
     * @return
     */
    private String getRandomStartDirection() {
        return this.directions[getRandomSignedInt(0, this.directions.length - 1)];
    }

    /**
     * Returns random signed integer.
     * Pre: max > min.
     *
     * @param min Signed minimum value (inclusive).
     * @param max Signed maximum value (inclusive).
     * @return
     */
    private int getRandomSignedInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /**
     * Converts HashMap to string.
     *
     * @param params
     * @return
     */
    public String composeMessage(HashMap<String, String> params) {
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
     * Parses message from client.
     * Returns key/value hash map.
     *
     * @param message
     * @return
     */
    public HashMap<String, String> parseMessage(String message) {
        HashMap<String, String> params = new HashMap<String, String>();
        String[] pairs = message.split(";");

        for (String pair : pairs) {
            String[] tmp = pair.split(":");
            params.put(tmp[0], tmp[1]);
        }

        return params;
    }

    public void checkRequiredKeys(HashMap<String, String> params, String[] requiredKeys) throws IllegalArgumentException {
        for (String requiredKey : requiredKeys) {
            if (!params.containsKey(requiredKey)) {
                throw new IllegalArgumentException("Required key is missing: " + requiredKey);
            }
        }
    }

}
