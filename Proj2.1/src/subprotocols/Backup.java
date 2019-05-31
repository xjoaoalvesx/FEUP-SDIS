package subprotocols;

import service.Peer;
import filesystem.Chunk;

public class Backup implements Runnable{

  private Peer parentPeer;
  private String path;

  public Backup(Peer parentPeer, String path){

    this.parentPeer = parentPeer;
    this.path = path;
    System.out.println("The Backup Started.");
  }

  @Override
  public void run(){

    // Chunks[] chunks = null;
    System.out.println("backup run");
  }
}
