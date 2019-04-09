package filesystem;

public class Chunk{

    private byte[] chunkID;
    private byte[] chunkData;
    private int replicationDegree;

    public Chunk(byte[] chunkID, byte[] chunkData, int replicationDegree){
        this.chunkID = chunkID;
        this.chunkData = chunkData;
        this.replicationDegree = replicationDegree;
    }

    public byte[] getID(){
        return chunkID;
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