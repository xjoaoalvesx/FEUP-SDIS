package network;


import filesystem.Chunk;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;


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
		CHUNK,
		SAVECHUNK
	}


	private int identifier;

	private Type type;

	private Serializable data;

	private boolean isOfTypeRequest;

	private InetSocketAddress sender;

	private Message(Type t){
		this.type = t;
	}

	public static Message request(Type t, InetSocketAddress senderAddress, int nodeId){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.isOfTypeRequest = true;
		message.data = nodeId;

		return message;
	}

	public static Message response(Type t, InetSocketAddress senderAddress, int id){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.identifier = id;
		message.isOfTypeRequest = false;
		return message;
	}

	public static Message backupResponse(Type t, InetSocketAddress senderAddress, ArrayList<InetSocketAddress> peersToBackup){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = peersToBackup;
		message.isOfTypeRequest = false;
		return message;
	}

	public static Message chunkRequest(Type t, InetSocketAddress senderAddress, Chunk chunk){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = chunk;
		message.isOfTypeRequest = true;
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