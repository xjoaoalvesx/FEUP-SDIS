package filesystem;

import service.Peer;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;




public class PeerSystemManager{

    private Peer parent_peer;
    private String path;
    private ConcurrentHashMap< String, ConcurrentHashMap<String, Set<String> > > backup_chunks; //fileId -> (chunkNo -> set(senderId))
    private ConcurrentMap< String, String> files_to_restore_by_path; // filePath -> fileID
    private ConcurrentMap<String, ConcurrentHashMap<Integer, Chunk>> files_restoring;
    

    public PeerSystemManager(Peer parent_peer) {
        this.parent_peer = parent_peer;
        this.path = "src/filesystem/Peer" + parent_peer.getId() + "/";

        setupFileSystem();
        backup_chunks = new ConcurrentHashMap<>();
        files_to_restore_by_path = new ConcurrentHashMap<>();
        files_restoring = new ConcurrentHashMap<>();

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
        String file_path = path + "/" + name;
        
        if (Files.exists(Paths.get(file_path))) {
            return false;
        }

        //Files.write(file_path, data);

        OutputStream out = Files.newOutputStream(Paths.get(file_path));
        out.write(data);
        out.close();

        return true;   
    }

    public static Chunk[] check(String filepath) throws IOException{
        System.out.println(filepath);
        File file = new File(filepath);
	       
        Chunk[] empty = {};	

        if(file.isFile()){
            return divider(filepath, file);
        }else{
            System.out.println("Error reading the file");
            return empty;
        }
    }

    public static Chunk[] divider(String path, File file) throws IOException{
        
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
            
            //TODO Replication Degree 
            chunks[c] = new Chunk(c, fileId, tempbuf, 1);
            
            i = i + 64000;
            c++;
        }

        int lastSize = ((int) size) % 64000;

        byte[] lastbuf = Arrays.copyOfRange(buffer, i, i+lastSize);     

        //TODO Replication Degree 
        chunks[c] = new Chunk(c, fileId, lastbuf, 1);
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

    public void incDegree(String fileId, String chunkNo, String senderId){

        backup_chunks.putIfAbsent(fileId, new ConcurrentHashMap<>());
        backup_chunks.get(fileId).putIfAbsent(chunkNo, new HashSet<String>());
        backup_chunks.get(fileId).get(chunkNo).add(senderId);

    }

    public int getDegree(String fileId, String chunkNo){

        return backup_chunks.get(fileId).get(chunkNo).size();
        
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
            e.printStackTrace();
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
    


}
