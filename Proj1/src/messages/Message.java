package messages;


public class Message{

    private String messageType;
    private String version;
    private String senderId;
    private String fileId;
    private String chunkNo;
    private String replicationDeg;
    private String CRLF;
    private String CRLF2;
    private byte[] body;

    public Message(String messageType, String version, String senderId, String fileId, String chunkNo, String replicationDeg, String CRLF, byte[] body){
        this.messageType = messageType;
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
        this.CRLF = CRLF;
        this.CRLF2 = CRLF;
        this.body = body;
    }

    
}