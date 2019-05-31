package network.workers;

import network.Message;
import network.Node;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.net.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class Listener extends Thread {


	private Node node;
	private MessageHandler messageHandler;
	private SSLServerSocket sslsocket;
	private AtomicBoolean up = new AtomicBoolean(true);

	private ExecutorService executor;


	public Listener(Node node, MessageHandler handler){

		this.node = node;
		this.messageHandler = handler;
		this.sslsocket = startSSocket();

		this.executor = Executors.newFixedThreadPool(8);
	}

	public SSLServerSocket startSSocket(){

		SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslServerSocket ;

        int port = node.getLocalAddress().getPort();

        try{
        	sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        	sslServerSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());
        }catch(IOException e){
        	throw new RuntimeException("Could not open port : " + port , e);
        }

        return sslServerSocket;

	}


	@Override
	public void run(){

		while(this.up.get()){
			this.work();
		}

		this.kill();
	}


	private void work(){

		SSLSocket socket;

		System.out.println("1");
		try{
			socket = (SSLSocket) sslsocket.accept();

		}catch(IOException e){
			throw new RuntimeException("Connection failed!", e);
		}

		System.out.println("2");
		executor.submit(() -> manageConnection(socket));
	}



	private void manageConnection(Socket s){

		ObjectInputStream input_stream;
		ObjectOutputStream output_stream;

		System.out.println("3");
		try{
			input_stream = new ObjectInputStream(s.getInputStream());
			output_stream = new ObjectOutputStream(s.getOutputStream());
		}catch(IOException e){
			throw new RuntimeException("Fail opening streams", e);
		}

		System.out.println("4");
		Message message = null;

		try{
			message = (Message) input_stream.readObject();
		}catch(IOException e){
			System.out.println("Error reading message object");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}

		System.out.println("5");
		Message response = null;

		if(message.isRequest()){
			response = messageHandler.manageRequest(message);
		}else{
			System.out.println("ITS TYPE RESP");
			messageHandler.manageResponse(message);
		}

		System.out.println("6");
		try{
			output_stream.writeObject(response);
			input_stream.close();
			output_stream.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		System.out.println("7");

	}






	public void kill(){
		try{
			this.sslsocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}



	public void setDown(){
		this.up.set(false);
	}

}
