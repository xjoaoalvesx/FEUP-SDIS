package subprotocols;

import network.Peer;
import network.Message;
import filesystem.Chunk;
import static filesystem.PeerSystemManager.check;
import static filesystem.PeerSystemManager.encode;

import java.util.ArrayList;

import java.io.IOException;

import java.net.InetSocketAddress;

public class Restore implements Runnable{
	
	private Peer peer;
	private String path;

	public Restore(Peer peer, String path){
		this.peer = peer;
		this.path = path;
		System.out.println("RESTORE FILE STARTED -> " + this.path + " .");
	}

	@SuppressWarnings("unchecked")
	public ArrayList<InetSocketAddress> restoreToServer(InetSocketAddress server){
		
		System.out.println("\nRequesting Restore to Server\n");

		Message request = Message.restoreRequest(Message.Type.RESTORE, peer.getLocalAddress(), path);

		Message response = peer.getMessageHandler().dispatchRequest(server, request);
		return (ArrayList<InetSocketAddress>) response.getMessageData();
	}

	// public ArrayList<InetSocketAddress> getRestorePeer(){

	// 	ArrayList<InetSocketAddress> list = new ArrayList<>();

	// 	list = peer.getBackupFilesMap(this.fileID);

	// 	return list;
	// }

	@Override
	public void run(){
		System.out.println("run");
		ArrayList<InetSocketAddress> peers;
		System.out.println("run1");
		peers = restoreToServer(peer.getServerAddress());
		System.out.println(peers);
		if (peers.size() == 0){
			System.out.print("No file to restore...");
			return;
		}

		Message request_file_id = Message.fileRequest(Message.Type.FILE, peer.getLocalAddress(), peer.getId(), this.path);
		Message response_file_id = peer.getMessageHandler().dispatchRequest(peer.getServerAddress(), request_file_id);
		String file_id = (String) response_file_id.getMessageData();
		System.out.println(file_id);

		int i = 0;
		Message request = Message.fileRequest(Message.Type.ASK_FILE, peer.getLocalAddress(), peer.getId(), file_id);
        Message response = peer.getMessageHandler().dispatchRequest(peers.get(i), request);

		byte[] file = (byte[]) response.getMessageData();
		System.out.println(file.length);
		System.out.println("restorefile");
		String[] strings = this.path.split("/");
		try{
			peer.getManager().restoreFile(file, strings[strings.length - 1]);
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}	
}