import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress;
import java.io.*;
import java.io.IOException;


class Server
{

	public static void main(String[] args) throws IOException 
	{
		DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
		byte[] receiveData = new byte[256];
		byte[] sendData = new byte[256];


		while(true)
		{
       		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

       		serverSocket.receive(receivePacket);

       		String request = new String(receivePacket.getData());

       		//indentifies the type of request 
       		String[] msg = request.split(" ");
       		String operation = msg[0].toUpperCase();

       		if(operation.equals("REGISTER"))
       		{
       			
       		}

       		else if(operation.equals("LOOKUP"))
       		{
       			
       		}

       		InetAddress IPAdd = receivePacket.getAddress();
       		int port = receivePacket.getPort();

       		sendData = operation.getBytes();

       		DatagramPacket responsePacket = new DatagramPacket(sendData, sendData.length, IPAdd, port);
       		serverSocket.send(responsePacket);


		}
	}


	public static void register()
	{

	}


	public static void lookup()
	{

	}




}