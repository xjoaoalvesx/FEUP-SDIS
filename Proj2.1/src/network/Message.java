package network;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Message implements Serializable{
	
	static final long serialVersionUID = 214321L;


	public enum Type {
		BACKUP,
		DELETE,
		RESTORE,
		REGISTER,
		ACCEPTED,
		CHUNK
	}


	private int identifier;

	private Type type;

	private Serializable data;

	private boolean isOfTypeRequest;

	private InetSocketAddress sender;

	private Message(Type t){
		this.type = t;
	}

	public static Message request(Type t, InetSocketAddress senderAddress){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.isOfTypeRequest = true;

		return message;
	}

	public static Message response(Type t, int id){
		Message message = new Message(t);
		message.identifier = id;
		message.isOfTypeRequest = false;
		return message;
	}


	public int getIdentifier(){
		return identifier;
	}

	public Type getMessageType(){
		return type;
	}

	public InetSocketAddress getSender(){
		return sender;
	}

	public boolean isRequest(){
		return isOfTypeRequest;
	}

	public Serializable getMessageData(){
		return data;
	}

}