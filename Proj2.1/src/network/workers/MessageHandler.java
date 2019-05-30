package network.workers;

import network.Message;
import network.Peer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MessageHandler extends Thread{
	
	private Peer peer;
	private ExecutorService executor;


	public MessageHandler(Peer peer){
		this.peer = peer;
		this.executor = Executors.newFixedThreadPool(3);

	}

	
	public Message request(InetSocketAddress address, Message message){

		if(!message.isRequest()){
			throw new IllegalArgumentException("Invalid type of Message to be sent.");
		}

		SSLSocket ssocket = sendMessage(address, message);

		try{
			Thread.sleep(100);
		}catch(InterruptedException e){
			e.printStackTrace();
		}

		Message response = null;
		
		try{
			response = getResponseMessage(ssocket);
		}catch(IOException e){
			System.out.println("Could not get response");
		}


		try{
			ssocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}


		return response;

	}






	// socket send and socket receive functions
	
	public SSLSocket makeConnection(InetAddress address_ip, int port) throws IOException{
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket ssocket;

		ssocket = (SSLSocket) factory.createSocket(address_ip, port);
		
		return ssocket;
	}
	
	private SSLSocket sendMessage(InetSocketAddress address, Message message){


		SSLSocket ssocket = null;
		try{
			ssocket = makeConnection(address.getAddress(), address.getPort());
			ObjectOutputStream out_stream = new ObjectOutputStream(ssocket.getOutputStream());
			out_stream.writeObject(message);
		}catch(IOException e){
			System.out.println("Failed to connect to Server");
		}

		return ssocket;
	}

	private Message getResponseMessage(SSLSocket socket) throws IOException{
		ObjectInputStream input_stream = new ObjectInputStream(socket.getInputStream());

		try{
			return (Message) input_stream.readObject();
		}catch(ClassNotFoundException e){
			System.out.println("Exception: ClassNotFound - serialization made wrong");
		}catch(ClassCastException e){
			System.out.println("Object must be of type Message");
		}

		return null;
	}


}