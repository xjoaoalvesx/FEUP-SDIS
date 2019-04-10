package messages;

import java.util.Arrays;

public class Message{

    private String messageType;
    private String version;
    private String senderId;
    private String fileId;
    private String chunkNo;
    private String replicationDeg;
    private byte[] body;
    private byte[] message;
    
    public Message(String messageType, String version, String senderId, String fileId, String chunkNo, String replicationDeg, byte[] body){
        this.messageType = messageType;
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
        this.body = body;
        this.createMessage();
    }
    
    public Message(String messageType, String version, String senderId, String fileId, String chunkNo, String replicationDeg){
        byte[] empty = {};
        this.messageType = messageType;
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
        this.body = empty;
        this.createMessage();
    }
    
    public Message(byte[] message){
        this.message = message;
        this.decompose();
    }

    private void createMessage(){
        String header = this.messageType + " " + this.version + " " + this.senderId + " " + this.fileId + " " + this.chunkNo + "\r\n" + "\r\n";
        byte[] headerByte = header.getBytes();
        
        int sizeHeader = (int) headerByte.length;
        int sizeBody = (int) body.length;
        
        this.message = new byte[sizeHeader + sizeBody];
        for (int i = 0; i < sizeHeader; i++){
            message[i] = headerByte[i];
        }
        for (int i = sizeHeader; i < sizeBody; i++){
            message[i] = body[i - sizeHeader];
        }
    }
    
    private void decompose(){
        int pos = 0;
        int sizeMessage = (int) this.message.length - 1;
        byte cr = "\r\n".getBytes()[0];
        byte lf = "\r\n".getBytes()[1];
       
        for (byte b : this.message){
            if(b == cr && this.message[pos+1] == lf && this.message[pos+2] == cr && this.message[pos+3] == lf){
                break;
            }
            pos++;
        }

        
        byte[] header = Arrays.copyOfRange(this.message, 0, pos);
        this.body = Arrays.copyOfRange(this.message, pos+3, sizeMessage);
        
        String header_s = new String(header);
        this.fillHeader(header_s);
    }
    
    private void fillHeader(String header){
        int i = 0;
        String[] values = header.split(" ");
        int sizeArr = (int) values.length;
        
        i = this.skipSpaces(values, i);
        this.messageType = values[i];
        i++;
        
        i = this.skipSpaces(values, i);
        this.version = values[i];
        i++;
        
        i = this.skipSpaces(values, i);
        this.senderId = values[i];
        i++;
        
        i = this.skipSpaces(values, i);
        this.fileId = values[i];
        i++;
        
        i = this.skipSpaces(values, i);
        if(i >= sizeArr){
            return;
        }
        this.chunkNo = values[i];
        i++;
        
        System.out.println(i);
        i = this.skipSpaces(values, i);
        System.out.println(i);
        if(i >= sizeArr){
            return;
        }
        this.replicationDeg = values[i];
         
    }
    
    private int skipSpaces(String[] s, int n){
        int sizeArr = (int) s.length;
        if (n >= sizeArr){
            return n;
        }
        while (s[n] == ""){
            n++;
        }
        return n;
    }
    
    public byte[] getMessageInBytes(){
        return this.message;
    }
    
    public byte[] getBody(){
        if(this.body.length == 0){
            System.out.println("There is no body attatched");
        }
        return this.body;
    }
    
    public String getMessageType(){
        return this.messageType;
    }
    
    public String getVersion(){
        return this.version;
    }
    
    public String getSenderId(){
        return this.senderId;
    }
    
    public String getFileId(){
        return this.fileId;
    }
    
    public String getChunkNo(){
        return this.chunkNo;
    }
    
    public String getReplicationDeg(){
        return this.replicationDeg;
    }


}