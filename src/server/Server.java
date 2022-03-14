package server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server
{
    private final static HashMap<String, Player> players = new HashMap<String, Player>();

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        ServerSocket welcomeSocket = new ServerSocket(9999);

        while (true) {
            Socket connSocket = welcomeSocket.accept();

            ServerThreadRead serverRead = new ServerThreadRead(connSocket);
            ServerThreadWrite serverWrite = new ServerThreadWrite(connSocket);
            Player player = new Player(serverRead, serverWrite);

            InetSocketAddress socketAddress = (InetSocketAddress) connSocket.getRemoteSocketAddress();
            String clientIp = socketAddress.getAddress().getHostAddress();
            //System.out.println("ClientIp: " + clientIp);
            players.put(clientIp, player);
            serverWrite.write("--- Du er forbindet til Game Server ---");
            serverWrite.write("Angiv dit spiller navn: ");


            serverRead.start();
            serverWrite.start();
        }
    }

    public static Player getPlayer(String clientIp) throws Exception
    {
        if (players.containsKey(clientIp)) {
            return players.get(clientIp);
        }
        else {
            throw new Exception("Client IP isn't a registered player.");
        }
    }

}
