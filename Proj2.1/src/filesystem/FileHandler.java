package filesystem;

public class FileHandler{

  private static int CHUNK_MAX_SIZE = 64000;

  private File file;
  private String path;
  private ArrayList<Chunk> fileChunks;
  private int neccessaryChunks;
  private String fileID;

  public FileHandler(){
    this.path = null;
    this.file = null;
    this.fileChunks = new ArrayList<Chunk>();
    this.neccessaryChunks = 0;
    this.fileID = "";
  }

  public void setFileManagerPath(String path){
    this.path = path;
    this.file = new File(path);

    if(!this.file.exists()){
      System.out.println("Such file does not exist");
    }else{
      createFileID();
      createFileChunks();
    }
  }

  public void createFileChunks(){
    int fileSize = (int) file.length();
    this.neccessaryChunks = calculateNecessaryChunks(filesize);
    int createdSize = 0;

    try{
      FileInputStream fileInputStream = new FileInputStream(this.file);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

      for(int i = 0; i < necessaryChunks; i++){

        byte[] buf = new byte[CHUNK_MAX_SIZE];
        bufferedInputStream.read(buf);

        if(i == (necessaryChunks - 1)){
          byte[] content = Arrays.copyOf(buf, filesize - createdSize);
          Chunk newChunk = new Chunk(i, this.fileID, content, fileSize - createdSize);
          this.fileChunks.add(newChunk);
        }else{
          byte[] content = Arrays.copyOf(buf, CHUNK_MAX_SIZE);
          Chunk newChunk = new Chunk(i, this.fileID, content, CHUNK_MAX_SIZE);
          this.fileChunks.add(newChunk);
          createdSize += CHUNK_MAX_SIZE;
        }
      }
    } catch(IOException exception){
        exception.printStackTrace();
    }
  }

  private int calculateNecessaryChunks(double fileSize){

    if(fileSize % CHUNK_MAX_SIZE == 0)
      return (int) ((fileSize / CHUNK_MAX_SIZE) + 1);
    else
      return (int) Math.ceil(fileSize / CHUNK_MAX_SIZE)
  }

  public void createFileID(){
    String fileName = this.file.getName();
    String fileModificationDate = Long.toString(this.file.lastModified());
    String fileOwner = "";

    Path path = Paths.get(this.path);
    FileOwnerAttributeView foav = Files.getFileAttributeView(path, FileOwnerAttributeView.class);

    try{
      fileOwner = foav.getOwner().toString();
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    this.fileID = hash256(fileName + "|" + fileModificationDate + "|" + fileOwner);
  }

  private static String hash256(String input){
    try{

    }
    catch(NoSuchAlgorithmException e){
      System.out.println ("Exception thrown" + "for incorrect algorithm " + e);
      return null;
    }
  }

  public File getFile(){
    return this.file;
  }

  public String getPath(){
    return this.path;
  }

  public ArrayList<Chunk> getFileChunks(){
    return this.fileChunks;
  }

  public int getNecessaryChunks(){
    return this.necessaryChunks;
  }

  public String getFileID(){
    return this.fileID;
  }

  public long getUsedSpace(){
    if(this.fileChunks.isEmpty()){
      System.out.println("No stored chunks!");
      return 0;
    }
    long totalSpace = 0;

    for(int i = 0; i < this.fileChunks.size(); i++){
      totalSpace += this.fileChunks.get(i).getSize();
    }
    return totalSpace;
  }

  public Chunk getMaxSizeChunk(){
    if(this.fileChunk.isEmpty()){
      System.out.println("No stored chunks!");
      return null;
    }

    Chunk chunk = Collections.max(this.fileChunks, Comparator.comparing(c->c.getSize()));

    return chunk;
  }


}
