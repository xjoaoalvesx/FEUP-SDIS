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
		SAVECHUNK,
		ASK_FILE,
		FILE,
		DELETE_FILE,
		RECEIVED,
		DELETE_INFO
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

	public static Message deleteResponse(Type t, InetSocketAddress senderAddress, ArrayList<InetSocketAddress> peers){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = peers;
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

	public static Message deleteRequest(Type t, InetSocketAddress senderAddress, int id, String filePath){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.identifier = id;
		message.isOfTypeRequest = true;
		message.data = filePath;
		return message;
	}

	public static Message fileRequest(Type t, InetSocketAddress senderAddress, int id, String filePath){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.identifier = id;
		message.isOfTypeRequest = true;
		message.data = filePath;
		return message;
	}

	public static Message fileResponse(Type t, InetSocketAddress senderAddress, int id, String fileId){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.identifier = id;
		message.isOfTypeRequest = false;
		message.data = fileId;
		return message;
	}

	public static Message saveChunkResponse(Type t, InetSocketAddress senderAddress, String file){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = file;
		message.isOfTypeRequest = true;
		return message;
	}

	public static Message deleteFileRequest(Type t, InetSocketAddress senderAddress, String fileId){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = fileId;
		message.isOfTypeRequest = true;
		return message;
	}

	public static Message receivedResponse(Type t, InetSocketAddress senderAddress){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = null;
		message.isOfTypeRequest = false;
		return message;
	}

<<<<<<< Updated upstream:Proj2/src/network/Message.java
	public static Message deleteInfoResponse(Type t, InetSocketAddress senderAddress, String fileId){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = fileId;
=======
	public static Message restoreRequest(Type t, InetSocketAddress senderAddress, String filePath){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = filePath;
>>>>>>> Stashed changes:Proj2.1/src/network/Message.java
		message.isOfTypeRequest = true;
		return message;
	}

<<<<<<< Updated upstream:Proj2/src/network/Message.java
=======
	public static Message restoreResponse(Type t, InetSocketAddress senderAddress, ArrayList<InetSocketAddress> peersToRestore){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = peersToRestore;
		message.isOfTypeRequest = false;
		return message;
	}

	public static Message askFileResponse(Type t, InetSocketAddress senderAddress, byte[] file){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.data = file;
		message.isOfTypeRequest = false;
		return message;
	}

>>>>>>> Stashed changes:Proj2.1/src/network/Message.java

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
