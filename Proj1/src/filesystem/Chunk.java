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
        return chunkID;
    }

    public String getFileID(){
        return fileID;
    }

    public byte[] getChunkData(){
        return chunkData;
    }

    public int getReplicationDegree(){
        return replicationDegree;
    }
    
    public int getSize(){
        return (int) chunkData.length;
    }
}
