package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class GameServer
{
    private final static HashMap<Integer, PlayerThread> playerThreads = new HashMap<Integer, PlayerThread>();
    private final static boolean[][] occupiedStartPos = new boolean[20][20]; // x/y.
    private final static String[] directions = {"up", "down", "left", "right"};

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

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        // Fill occupiedStartPos with false.
        for (boolean[] cols : occupiedStartPos) {
            Arrays.fill(cols, false);
        }

        ServerSocket welcomeSocket = new ServerSocket(9999);

        // Accept connections from players.
        while (true) {
            // Accept connection
            Socket connSocket = welcomeSocket.accept();

            // Create player thread (also creates playerId).
            PlayerThread pt = new PlayerThread(connSocket);
            playerThreads.put(pt.getPlayerId(), pt);
            pt.start();

            // Create start params for player
            int[] startPos = getRandomStartPos();
            String direction = getRandomStartDirection();

            // Send start params to client/player.
            pt.write(
                "ip:" + pt.getPlayerIp() + ";" +
                "id:" + pt.getPlayerId() + ";" +
                "posX:" + startPos[0] + ";" +
                "posY:" + startPos[1] + ";" +
                "direction:" + direction + ";"
            );

            System.out.println("--- Server: player connected ---");
            System.out.println("PlayerIp: " + pt.getPlayerIp());
            System.out.println("PlayerId: " + pt.getPlayerId());
        }
    }

    public static synchronized void messageAllPlayers(String message) {
        for (PlayerThread pt : playerThreads.values()) {
            pt.write(message);
        }
    }

    /**
     * Creates and returns random start position for player on board.
     * @return
     */
    private static int[] getRandomStartPos()
    {
        int border = 1; // Thickness of wall border in board.
        int posX, posY;

        do {
            posX = getRandomSignedInt(border, board[0].length() - border); // min, num-of-columns
            posY = getRandomSignedInt(border, board.length - border); // min, num-of-rows.
        } while (occupiedStartPos[posX][posY] || board[posY].charAt(posX) == 'w');

        occupiedStartPos[posX][posY] = true;
        return new int[]{posX, posY};
    }

    /**
     * Creates and returns random start direction for player.
     * @return
     */
    private static String getRandomStartDirection()
    {
        return directions[getRandomSignedInt(0, directions.length - 1)];
    }

    /**
     * Returns random signed integer.
     * Pre: max > min.
     *
     * @param min Signed minimum value (inclusive).
     * @param max Signed maximum value (inclusive).
     * @return
     */
    private static int getRandomSignedInt(int min, int max)
    {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

}
