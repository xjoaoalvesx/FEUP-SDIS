package messages;


public class Response extends Message{

    public Message(String messageType, String version, String senderId, String fileId, String chunkNo){
        super(messageType, version, senderId, fileId, chunkNo, "", {});
    }

    
}