package messages;


public class Getchunk extends Message{

    public Getchunk(String version, String senderId, String fileId, String chunkNo){
        super("GETCHUNK", version, senderId, fileId, chunkNo, "");
    }

    
}