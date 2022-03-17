package server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer
{
    private final static HashMap<Integer, PlayerThread> playerThreads = new HashMap<Integer, PlayerThread>();

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        ServerSocket welcomeSocket = new ServerSocket(9999);

        while (true) {
            Socket connSocket = welcomeSocket.accept();

            PlayerThread pt = new PlayerThread(connSocket);
            playerThreads.put(pt.getPlayerId(), pt);

            pt.write("id:" + pt.getPlayerId());


        }
    }



}
