package subprotocols;

import network.Peer;
import network.Server;
import network.Message;

import java.util.ArrayList;

import java.io.IOException;

import java.net.InetSocketAddress;

public class Delete implements Runnable{
	
	private String file_path;
	private Peer peer;

	public Delete(Peer peer, String filePath){

		this.peer = peer;
		this.file_path = filePath;

		System.out.println("DELETE FILE STARTED --- " + this.file_path + " .");

	}

	@SuppressWarnings("unchecked")
	public ArrayList<InetSocketAddress> deleteToServer(InetSocketAddress server){

		System.out.println("\nRequesting DELETE to SERVER\n");

		Message request = Message.deleteRequest(Message.Type.DELETE, peer.getLocalAddress(), peer.getId(), this.file_path);

		Message response = peer.getMessageHandler().dispatchRequest(server, request);

		return (ArrayList<InetSocketAddress>) response.getMessageData();
	}

	public String requestFileToServer(InetSocketAddress server){

		System.out.println("\nRequesting fileID to SERVER : " + this.file_path);

		Message request = Message.deleteRequest(Message.Type.FILE, peer.getLocalAddress(), peer.getId(), this.file_path);
		Message response = peer.getMessageHandler().dispatchRequest(server, request);

		return (String) response.getMessageData();
	}



	@Override
	public void run(){
		
		ArrayList<InetSocketAddress> peers;
    	peers = deleteToServer(peer.getServerAddress());

    	String fileId;
    	fileId = requestFileToServer(peer.getServerAddress());
    	System.out.println(fileId);

    	for (int i = 0; i < peers.size(); i++){

    		Message request = Message.deleteFileRequest(Message.Type.DELETE_FILE, peer.getLocalAddress(), fileId);
    		Message response = peer.getMessageHandler().dispatchRequest(peers.get(i), request);

    	}



	}

}
