package network.workers;

import network.Message;
import network.Peer;
import network.Server;
import network.Node;
import filesystem.Chunk;

import static filesystem.PeerSystemManager.createDirectories;
import static filesystem.PeerSystemManager.saveFile;

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

	private Node node;
	private ExecutorService executor;


	public MessageHandler(Node node){
		this.node = node;
		this.executor = Executors.newFixedThreadPool(10);

	}


	public Message dispatchRequest(InetSocketAddress address, Message message){

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



	public void manageResponse(Message message){

		System.out.println("Received Request " + message.getMessageType() + " Message.");

		switch(message.getMessageType()){

			case RECEIVED:
				System.out.println("Received SAVECHUNK response\n");

			default:
				break;
		}
	}


	public Message manageRequest(Message message){

		System.out.println("Received Request " + message.getMessageType() + " Message.");

		Message response = null;

		switch(message.getMessageType()){
			case REGISTER:
				response = manageRegisterRequest(message);
				break;

			case BACKUP:
				response = manageBackupRequest(message);
				break;

			case CHUNK:
				executor.submit(() -> manageChunkRequest(message));
				break;

			case SAVECHUNK:
				response = getSaveChunkMessage(message);
				break;

			default:
				break;
		}

		return response;
	}


	private void manageChunkRequest(Message request){

		Boolean saved;
		Chunk chunk = (Chunk) request.getMessageData();

		byte[] data = chunk.getChunkData();

		String chunk_path = "peers/Peer" + node.getId() + "/backup/" + chunk.getFileID();


		createDirectories(chunk_path);

		try {
    		saved = saveFile(Integer.toString(chunk.getID()), chunk_path, data);
    } catch (IOException e) {
        System.out.println("Fail saving the chunk!");
       	saved = false;
    }

		if(saved){

			Message saveChunkResponse = manageSaveChunk(chunk.getFileID(), request.getSender());
			Message response = this.dispatchRequest(node.getServerAddress(), saveChunkResponse);
		}
	}

	private Message manageSaveChunk(String fileId, InetSocketAddress sender){

		return Message.saveChunkResponse(Message.Type.SAVECHUNK, sender, fileId);
	}

	private Message manageBackupRequest(Message request){

		return Message.backupResponse(Message.Type.BACKUP, node.getLocalAddress(), node.getPeers());
	}
	private Message manageRegisterRequest(Message request){


		System.out.println("New Peer Assigned : ID " + request.getMessageData() +"\n");

		node.addPeer(request.getSender(), (int)request.getMessageData());

		return Message.response(Message.Type.ACCEPTED, node.getLocalAddress(), request.getIdentifier());
	}

	// socket send and socket receive functions

	public SSLSocket makeConnection(InetAddress address_ip, int port) throws IOException{
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket ssocket;

		InetSocketAddress isa = new InetSocketAddress(address_ip, port);
		ssocket = (SSLSocket) factory.createSocket();
		ssocket.connect(isa, 5000);
		ssocket.setEnabledCipherSuites(ssocket.getSupportedCipherSuites());
		return ssocket;
	}

	private SSLSocket sendMessage(InetSocketAddress address, Message message){


		SSLSocket ssocket = null;
		try{
			ssocket = makeConnection(address.getAddress(), address.getPort());
			ObjectOutputStream out_stream = new ObjectOutputStream(ssocket.getOutputStream());
			out_stream.writeObject(message);
		}catch(IOException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
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

	private Message getSaveChunkMessage(Message request){

		node.addBackupFile((String) request.getMessageData(), request.getSender());

		return Message.response(Message.Type.RECEIVED, node.getLocalAddress(), node.getId());
	}
}
