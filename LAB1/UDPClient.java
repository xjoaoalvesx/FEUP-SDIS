import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress;
import java.io.*;

class Client
{

	public static void main(String[] args) throws IOException 
	{

		String host_name = args[0];
		int port = Integer.parseInt(args[1]);
		String operation = args[2];




		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      	DatagramSocket clientSocket = new DatagramSocket();

      	byte[] sendData = new byte[256];
    	byte[] receiveData = new byte[256];

    	InetAddress IPAddress = InetAddress.getByName("localhost");

    	
    	String msg = operation + " " + args[3];
    	sendData = msg.getBytes();
    	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
    	clientSocket.send(sendPacket);
    	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    	clientSocket.receive(receivePacket);
    	String modifiedSentence = new String(receivePacket.getData());
    	System.out.println("FROM SERVER:" + modifiedSentence);
    	clientSocket.close();

	}

	

}
