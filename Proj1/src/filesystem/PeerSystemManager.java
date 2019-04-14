package filesystem;

import service.Peer;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;


import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;





public class PeerSystemManager{

    private Peer parent_peer;
    private String path;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String> > > chunks_replication_map; //fileId -> chunkNo -> set(senderId)
    private ConcurrentMap<String, String> files_to_restore_by_path; // filePath -> fileID
    private ConcurrentMap<String, ConcurrentHashMap<Integer, Chunk>> files_restoring; 
    private ConcurrentMap<String, ConcurrentMap<Integer, Chunk>> backup_chunks; // fileId -> chunkNo -> Chunk
    private ConcurrentHashMap<String, int[]> replication_degree_map; //fileID + chunkNo -> [desired_degree, actual_degree]


    public PeerSystemManager(Peer parent_peer) {
        this.parent_peer = parent_peer;
        this.path = "peers/Peer" + parent_peer.getId() + "/";

        setupFileSystem();
        chunks_replication_map = new ConcurrentHashMap<>();
        files_to_restore_by_path = new ConcurrentHashMap<>();
        files_restoring = new ConcurrentHashMap<>();
        backup_chunks = new ConcurrentHashMap<>();
        replication_degree_map = new ConcurrentHashMap<>();
    }



    public static void createDirectories(String path){
        try{
            Files.createDirectories(Paths.get(path));
        } catch (IOException e){
            System.out.println("Error creating folder");
        }
    }

    public void setupFileSystem(){
        createDirectories(path + "backup/");
        createDirectories(path + "restored/");
    }

    public boolean saveFile(String name, String path, byte[] data) throws IOException {
        if(data.length > this.parent_peer.getAvailableSpace()){
            return false;
        }

        String file_path = path + "/" + name;
        
        if (Files.exists(Paths.get(file_path))) {
            return false;
        }        

        OutputStream out = Files.newOutputStream(Paths.get(file_path));
        out.write(data);
        out.close();

        this.setAvailableSpace();

        return true;   
    }

    public static Chunk[] check(String filepath, int repD) throws IOException{
        System.out.println(filepath);
        File file = new File(filepath);
        Chunk[] empty = {};	

        if(file.isFile()){
            return divider(filepath, file, repD);
        }else{
            System.out.println("Error reading the file");
            return empty;
        }
    }

    public static Chunk[] divider(String path, File file, int repD) throws IOException{
        
        String fileId = new String();
        try{
            String newId = path + ":" + String.valueOf(file.lastModified());
            fileId = encode(newId);
        } catch (NoSuchAlgorithmException n) {
            System.out.println("Algorithm not found!");
        }

        long size = file.length();

        int chunksSize = ((int) size) / 64000 + 1;
        Chunk[] chunks = new Chunk[chunksSize];
        
        byte[] buffer = Files.readAllBytes(file.toPath());
        
        int i = 0;
        int c = 0;
        while(i < (size - 64000)){
            byte[] tempbuf = Arrays.copyOfRange(buffer, i, i+64000);
            
            chunks[c] = new Chunk(c, fileId, tempbuf, repD);
            
            i = i + 64000;
            c++;
        }

        int lastSize = ((int) size) % 64000;

        byte[] lastbuf = Arrays.copyOfRange(buffer, i, i+lastSize);     

        chunks[c] = new Chunk(c, fileId, lastbuf, repD);
       // System.out.println(chunks[0].getChunkData().length);
        return chunks;

       
    }

    public int getNumChunks(String filepath) {

        File file = new File(filepath);

        long size = file.length();

        int num_chunks = ((int) size) / 64000 + 1;

        return num_chunks;

    }

    private static String encode(String string) throws NoSuchAlgorithmException{



        MessageDigest md = MessageDigest.getInstance("SHA-256");
       

        byte[] encodedhash = md.digest(string.getBytes(StandardCharsets.UTF_8));

        String hexadecimal = new String();

        for(byte b : encodedhash){
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1){
                hex += '0';
            }
            hexadecimal += hex;
        }

        return hexadecimal;
    
    }
    public void storeDegree(String fileId, String chunkNo, int desired_repDegree){
        String file_chunk = fileId + chunkNo;
        int[] temp = new int[2];
        temp[0] = desired_repDegree;
        temp[1] = 0;
        this.replication_degree_map.putIfAbsent(file_chunk, temp);
        this.updateDegree(fileId, chunkNo);
    }

    public void incDegree(String fileId, String chunkNo, String senderId){
        chunks_replication_map.putIfAbsent(fileId, new ConcurrentHashMap<>());
        chunks_replication_map.get(fileId).putIfAbsent(chunkNo, new HashSet<String>());
        chunks_replication_map.get(fileId).get(chunkNo).add(senderId);
        this.updateDegree(fileId, chunkNo);
    }  

    public void updateDegree(String fileId, String chunkNo){
        String key = fileId + chunkNo;
        int[] temp = replication_degree_map.get(key);
        temp[1] = calculateDegree(fileId, chunkNo);
        replication_degree_map.put(key, temp);
        System.out.print("REP :");
        System.out.println(temp[1]);
        
    }

    public int calculateDegree(String fileId, String chunkNo){

        if (chunks_replication_map.get(fileId) == null){
            System.out.println("null");
            return 0;
        }
        else if(chunks_replication_map.get(fileId).get(chunkNo) == null){
            return 0;
        }
            
        else  return chunks_replication_map.get(fileId).get(chunkNo).size();

    }
    
    //key = fileId + chunkNo
    public int getDesiredDegree(String key){
        return this.replication_degree_map.get(key)[0];
    }

    public int getCurrentDegree(String key){
        return this.replication_degree_map.get(key)[1];
    }

    public void addFileToRestore(String filePath, String fileId){
        try{
            files_to_restore_by_path.put(filePath, fileId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public String getFileIdbyPath(String filePath) {
        return files_to_restore_by_path.get(filePath);
    }

    public static byte[] loadDataFromPath(String path){
        
        byte[] getBytes = {};

        try {
            File file = new File(path);
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        } catch (FileNotFoundException e) {
            // if doesnt exists byte[] = null;
            byte[] r = null;
            return r;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getBytes;
    }

    public byte[] getChunkData(String fileId, String chunkNo){
        String p = path + "backup/" + fileId + "/" + chunkNo;
        return loadDataFromPath(p);
    }

    public void setRestoring(boolean restoring, String fileId){
        if(restoring){
            files_restoring.putIfAbsent(fileId, new ConcurrentHashMap<>());
        }
        else {
            files_restoring.remove(fileId);
        }
    }

    public boolean getRestoringState(String fileId){
        return files_restoring.containsKey(fileId);
    }

    

    public ConcurrentMap<Integer,Chunk> getChunksRestored(String fileId){
        return files_restoring.get(fileId);
    }

    public void addChunkToFileRestore(Chunk chunk){
        files_restoring.get(chunk.getFileID()).putIfAbsent(chunk.getID(), chunk);
    }

    

    public static byte[] joinChunks(ArrayList<Chunk> chunks){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for (int i = 0; i < chunks.size(); i++) {
            try {
                os.write(chunks.get(i).getChunkData());
            } catch (IOException e) {
                System.out.println("Failed joining chunks");
            }
        }

        return os.toByteArray();
    } 
    
    public void removeFileToRestore(String path){
        files_to_restore_by_path.remove(path);
    }

    public void addBackupChunk(Chunk c){
        ConcurrentMap<Integer, Chunk> chunks_of_file;
        chunks_of_file = backup_chunks.getOrDefault(c.getFileID(), new ConcurrentHashMap<>());
        chunks_of_file.putIfAbsent(c.getID(), c);
        backup_chunks.putIfAbsent(c.getFileID(), chunks_of_file);
    }

    public boolean hasChunksofFile(String fileId){
        return backup_chunks.containsKey(fileId);
    }

    public ConcurrentMap<Integer,Chunk> removeChunksOfFileBackup(String fileId){
        return backup_chunks.remove(fileId); // removes key and corresponding values from map , returns V
    }

    public void removeDirFromSystem(String fileId) throws IOException{
       
        String path_of_file = this.path + "backup/" + fileId;

        Path directory = Paths.get(path_of_file);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }   
        });

    }
 
    //returns free space
    public int setAvailableSpace(){ 
        int used_space = 0; 
        String peer_directory_backup = this.path + "/backup"; 
        File backup_dir = new File(peer_directory_backup); 
        File[] dirs = backup_dir.listFiles(); 
 
        for(File dir : dirs){ 
            File[] chunks = dir.listFiles(); 
            for(File chunk : chunks){ 
                used_space += (int) chunk.length(); 
            } 
        } 
 
        this.parent_peer.setAvailableSpace(this.parent_peer.getMaxSpace() - used_space); 
 
        return this.parent_peer.getAvailableSpace(); 
    }

    public String getPath(){
        return this.path;
    }

    public void removeChunkFromMap(String fileId, String chunkNo, String senderId){
        this.chunks_replication_map.get(fileId).get(chunkNo).remove(senderId);
        this.updateDegree(fileId, chunkNo);
    }

    public void removeChunkFromPeer(String fileId, String chunkNo){
    
        try {
            String path_of_chunk = this.path + "backup/" + fileId + "/" + chunkNo;
            File chunk = new File(path_of_chunk);
            chunk.delete();
            this.backup_chunks.get(fileId).remove(Integer.parseInt(chunkNo));
            this.removeChunkFromMap(fileId, chunkNo, String.valueOf(this.parent_peer.getId()));
            this.updateDegree(fileId, chunkNo);
        } catch (Exception e) {
            System.out.println("Error deleting chunk");
        }

    }

    public void removeDirFromPeer(String fileId) {
       

        try {
            String path_of_dir = this.path + "backup/" + fileId;
            File dir = new File(path_of_dir);
            dir.delete();
            this.backup_chunks.remove(fileId);
        } catch (Exception e) {
            System.out.println("Error deleting directory");
        }

    }

    public boolean fileOriginatedFromPeer(String fileId){
        return chunks_replication_map.containsKey(fileId);
    }

    public int checkReplicationDegree(String fileId, String chunkNo){
        return this.replication_degree_map.get(fileId + chunkNo)[1] - this.replication_degree_map.get(fileId + chunkNo)[0];
    }

    public boolean hasChunk(String fileId, String chunkNo){
        boolean one;
        try {
            one = backup_chunks.get(fileId).containsKey(Integer.parseInt(chunkNo));
            return one;
        } catch (Exception e) {
            return false;
        } 
    }

    public void publishInformation(){

        String allInfo = "";

        allInfo += "\nBackup Chunks:\n";

        for(Map.Entry<String, ConcurrentHashMap<String, Set<String>>> file : chunks_replication_map.entrySet()){
            allInfo += "\nFile: " + file.getKey() ;
            for(Map.Entry<String, Set<String>> chunkMap : file.getValue().entrySet()){
                
                String chunkNo = chunkMap.getKey();
                String temp = file.getKey() + chunkNo;
                allInfo += "\n chunkId: " + chunkNo +
                           "\n desired replication degree : " + getDesiredDegree(temp) +
                           "\n perceived replication degree : " + getCurrentDegree(temp);
            }
        }

        allInfo += "\n\nStored Chunks:\n";

        for(Map.Entry<String, ConcurrentMap<Integer, Chunk>> file : backup_chunks.entrySet()){
            allInfo += "\nFile: " + file.getKey() ;
            for(Map.Entry<Integer, Chunk> chunkMap : file.getValue().entrySet()){
                
                Chunk chunk = chunkMap.getValue();
                String temp = file.getKey() + Integer.toString(chunk.getID());
                allInfo += "\n chunkId: " + Integer.toString(chunk.getID()) +
                           "\n chunkSize: " + chunk.getSize()/1000 +
                           "\n perceived replication degree : " + getCurrentDegree(temp);
            }
        }


        allInfo += "\n\nPeer space usage:\n";
        allInfo += "\n Max space: " + String.valueOf((float) this.parent_peer.getMaxSpace() / 1000) + " KB";
        allInfo += "\n Free space: " + String.valueOf((float) this.parent_peer.getAvailableSpace() / 1000) + " KB";

        System.out.println(allInfo);
    }
}
