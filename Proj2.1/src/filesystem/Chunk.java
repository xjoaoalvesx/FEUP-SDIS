package filesystem;

public class Chunk{

  private int chunkID;
  private String fileID;
  private byte[] chunkData;

  public Chunk(int chunkID; String fileID; byte[] chunkData){

    this.chunkID = chunkID;
    this.fileID = fileID;
    this.chunkData = chunkData;
  }

  public int getChunkID(){

    return this.chunkID;
  }

  public String getFileID(){

    return this.fileID;
  }

  public byte[] getChunkData(){

    return this.chunkData;
  }
}
