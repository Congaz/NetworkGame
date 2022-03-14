package gameServer;
import java.io.*;
import java.net.*;

public class TCPClient {
	public static void main(String[] argv) throws Exception{
		String sentence;
		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Angiv IP du vil forbinde til: ");
		String domainIp = inFromUser.readLine();


		//System.out.print("Angiv server port: ");
		//int serverPort = Integer.parseInt(inFromUser.readLine());

		// xjkljhvjksdhfjkh

		// Client Socket
		Socket clientSocket= new Socket(domainIp, 9999);


		TCPClientThreadRead clientRead = new TCPClientThreadRead(clientSocket);
		TCPClientThreadWrite clientWrite = new TCPClientThreadWrite(clientSocket);
		clientRead.start();
		clientWrite.start();


	}

	private static String resolveDomain(String domainName) throws Exception
	{
		/// DNS IP
		String dnsIp = "10.10.131.243";

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(dnsIp);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		sendData = domainName.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String domainIp = new String(receivePacket.getData());
		clientSocket.close();
		return domainIp;
	}
}


