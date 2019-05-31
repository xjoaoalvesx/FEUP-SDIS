package filesystem;

import network.Peer;

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
    // private ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String> > > chunks_replication_map; //fileId -> chunkNo -> set(senderId)
    // private ConcurrentMap<String, String> files_to_restore_by_path; // filePath -> fileID
    // private ConcurrentMap<String, ConcurrentHashMap<Integer, Chunk>> files_restoring; 
    // private ConcurrentMap<String, ConcurrentMap<Integer, Chunk>> backup_chunks; // fileId -> chunkNo -> Chunk
    // private ConcurrentHashMap<String, int[]> replication_degree_map; //fileID + chunkNo -> [desired_degree, actual_degree]


    public PeerSystemManager(Peer parent_peer) {
        this.parent_peer = parent_peer;
        this.path = "peers/Peer" + parent_peer.getId() + "/";

        setupFileSystem();
        // chunks_replication_map = new ConcurrentHashMap<>();
        // files_to_restore_by_path = new ConcurrentHashMap<>();
        // files_restoring = new ConcurrentHashMap<>();
        // backup_chunks = new ConcurrentHashMap<>();
        // replication_degree_map = new ConcurrentHashMap<>();
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

    public static boolean saveFile(String name, String path, byte[] data) throws IOException {
        // if(data.length > this.parent_peer.getAvailableSpace()){
        //     return false;
        // }

        String file_path = path + "/" + name;
        
        if (Files.exists(Paths.get(file_path))) {
            return false;
        }        

        OutputStream out = Files.newOutputStream(Paths.get(file_path));
        out.write(data);
        out.close();

        // this.setAvailableSpace();

        return true;   
    }

    public static Chunk[] check(String filepath, int repD) throws IOException{
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
            
            chunks[c] = new Chunk(c, fileId, tempbuf, repD, path);
            
            i = i + 64000;
            c++;
        }

        int lastSize = ((int) size) % 64000;

        byte[] lastbuf = Arrays.copyOfRange(buffer, i, i+lastSize);     

        chunks[c] = new Chunk(c, fileId, lastbuf, repD, path);
       // System.out.println(chunks[0].getChunkData().length);
        return chunks;

       
    }

    public int getNumChunks(String filepath) {

        File file = new File(filepath);

        long size = file.length();

        int num_chunks = ((int) size) / 64000 + 1;

        return num_chunks;

    }

    public static String encode(String string) throws NoSuchAlgorithmException{



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
 
    public byte[] getFileFromSystem(String fileId) throws IOException{
       
        String path_of_file = this.path + "backup/" + fileId;
        int number_chunks = new File(path_of_file).list().length;
        String[] children = new File(path_of_file).list();
        byte[][] file_bytes = new byte[number_chunks][];

        int c = 0;
        for(String s : children){
            String p = path_of_file + "/" + s;
            byte[] temp_bytes = loadDataFromPath(p);
            file_bytes[c] = temp_bytes;
            c++;
        }


        int size = 0;
        for (int i = 0; i < number_chunks; i++){
            size += file_bytes[i].length;
        }
        byte[] file = new byte[size];
        for (int i = 0; i < size; i++){
            int count = 0;
            for (int s = 0; s < file_bytes[i].length; s++){
                file[i+s] = file_bytes[i][s];
                count = s;
            }
            i += count;
        }
        return file;

    }

    public static byte[] loadDataFromPath(String path){
        
        byte[] getBytes = null;

        try {
            File file = new File(path);
            System.out.println(file.length());
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return getBytes;
    }

    public void restoreFile(byte[] info, String path1) throws IOException {
        String path_of_file = this.path + "restored/" + path1;
        if (Files.exists(Paths.get(path_of_file))) {
            return;
        }        

        OutputStream out = Files.newOutputStream(Paths.get(path_of_file));
        out.write(info);
        out.close();
    }
    

    public String getPath(){
        return this.path;
    }


    
}
