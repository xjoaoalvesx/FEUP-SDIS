package filesystem;

import java.io.Serializable;

public class Chunk implements Serializable{

    static final long serialVersionUID = 423L;

    private int chunkID;
    private String fileID;
    private byte[] chunkData;
    private int replicationDegree;
    private int currentReplication;
    private String filePath;

    public Chunk(int chunkID, String fileID, byte[] chunkData, int replicationDegree, String filePath){
        this.chunkID = chunkID;
        this.fileID = fileID;
        this.chunkData = chunkData;
        this.replicationDegree = replicationDegree;
        this.filePath = filePath;
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

    public String getFilePath(){
        return this.filePath;
    }
}

