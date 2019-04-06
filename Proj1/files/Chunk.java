package files

public class Chunk{

    private byte[] chunkID;
    private byte[] chunkData;
    private int replicationDegree;

    public Chunk(chunkID, chunkData, replicationDegree){
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
}