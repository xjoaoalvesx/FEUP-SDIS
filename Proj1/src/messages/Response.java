package messages;


public class Response extends Message{

    public Response(String messageType, String version, String senderId, String fileId, String chunkNo){
    
        super(messageType, version, senderId, fileId, chunkNo, "");

        if(messageType != "STORED" && messageType != "CHUNK" && messageType != "REMOVE"){
            System.out.println("Invalid type of response provided (STORED/CHUNK/REMOVE)");
        }
    }

    
}