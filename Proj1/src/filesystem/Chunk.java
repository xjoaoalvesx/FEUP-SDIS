package filesystem;

public class Chunk{

    private int chunkID;
    private String fileID;
    private byte[] chunkData;
    private int replicationDegree;
    private int currentReplication;

    public Chunk(int chunkID, String fileID, byte[] chunkData, int replicationDegree){
        this.chunkID = chunkID;
        this.fileID = fileID;
        this.chunkData = chunkData;
        this.replicationDegree = replicationDegree;
    }

    public int getID(){
        return this.chunkID;
    }

    public String getFileID(){
        return this.fileID;
    }

    public byte[] getChunkData(){
        return this.chunkData;
    }

    public int getReplicationDegree(){
        return this.replicationDegree;
    }
    
    public int getSize(){
        return (int) chunkData.length;
    }
}
