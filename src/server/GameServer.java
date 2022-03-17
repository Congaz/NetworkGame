package server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameServer
{
    private final static HashMap<Integer, PlayerThread> playerThreads = new HashMap<Integer, PlayerThread>();
    private final static boolean[][] occupiedStartPos = new boolean[20][20];

    private  final static String[] board = {    // 20x20
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
        for (int x = 0; x < occupiedStartPos.length; x++) {
            Arrays.fill(occupiedStartPos[x], false);
        }

        ServerSocket welcomeSocket = new ServerSocket(9999);

        while (true) {
            Socket connSocket = welcomeSocket.accept();

            PlayerThread pt = new PlayerThread(connSocket);
            playerThreads.put(pt.getPlayerId(), pt);


            int[] startPos = getRandomStartPos();
            pt.write("id:" + pt.getPlayerId() + "posX:" + startPos[0] + ";posY:" + startPos[1]);

            System.out.println("--- Connected ---");
            System.out.println("PlayerId: " + pt.getPlayerId());



        }
    }

    private static int[] getRandomStartPos() {
        int min = 0;
        int max = 20;
        int posX = 0;
        int posY = 0;

        while (occupiedStartPos[posX][posY] || board[posY].charAt(posX) == 'w') {
            posX = (int)(Math.random() * ((max - min) + 1)) + min;;
            posY = (int)(Math.random() * ((max - min) + 1)) + min;;
        };

        occupiedStartPos[posX][posY] = true;
        return new int[]{posX, posY};
    }

}
