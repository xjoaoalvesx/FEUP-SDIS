package messages;


public class Delete extends Message{

    public Delete(String version, String senderId, String fileId){
        super("DELETE", version, senderId, fileId, "", "");
    }

    
}