package messages;


public class Putchunk extends Message{

    public Message(String version, String senderId, String fileId, String chunkNo, String replicationDeg, byte[] body){
        super("PUTCHUNK", version, senderId, fileId, chunkNo, replicationDeg, body);
    }

    
}