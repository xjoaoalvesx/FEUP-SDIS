package subprotocols;

import network.Peer;
import network.Message;
import filesystem.Chunk;
import static filesystem.PeerSystemManager.check;

import java.util.ArrayList;

import java.io.IOException;

import java.net.InetSocketAddress;


public class Backup implements Runnable{

  private Peer parentPeer;
  private String path;
  private int repDeg;

  public Backup(Peer parentPeer, String path, int replicationDegree){

    this.parentPeer = parentPeer;
    this.path = path;
    this.repDeg = replicationDegree;
    System.out.println("The Backup Started.");
  }

  @SuppressWarnings("unchecked")
  public ArrayList<InetSocketAddress> backupToServer(InetSocketAddress server){

    System.out.println("\nRequesting Backup to Server\n");

    Message request = Message.request(Message.Type.BACKUP, parentPeer.getLocalAddress(), parentPeer.getId());

    Message response = parentPeer.getMessageHandler().dispatchRequest(server, request);
    return (ArrayList<InetSocketAddress>) response.getMessageData();

  }

  @Override
  public void run(){

    Chunk[] chunks = null;
    try{
      chunks = check(this.path, this.repDeg);
    }catch (IOException e){
      e.printStackTrace();
    }

    ArrayList<InetSocketAddress> peers;
    peers = backupToServer(parentPeer.getServerAddress());

    for(int i = 0; i < peers.size(); i++){

      if(! peers.get(i).equals(parentPeer.getLocalAddress())){
        for(int j = 0; j < chunks.length; j++){

          Message request = Message.chunkRequest(Message.Type.CHUNK, parentPeer.getLocalAddress(), chunks[j]);
          Message response = parentPeer.getMessageHandler().dispatchRequest(peers.get(i), request);
          // System.out.print("response Message Type: ");
          // System.out.println(response.getMessageType());
        }
      }
    }



    System.out.println("backup run");
  }
}
