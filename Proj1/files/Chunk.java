package files;

public class Chunk{

    private byte[] chunkID;
    private char[] chunkData;
    private int replicationDegree;

    public Chunk(byte[] chunkID, char[] chunkData, int replicationDegree){
        this.chunkID = chunkID;
        this.chunkData = chunkData;
        this.replicationDegree = replicationDegree;
    }

    public byte[] getID(){
        return chunkID;
    }

    public char[] getChunkData(){
        return chunkData;
    }

    public int getReplicationDegree(){
        return replicationDegree;
    }
}