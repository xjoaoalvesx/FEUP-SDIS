package filesystem;

import service.Peer;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.security.*;
import java.util.Arrays;


public class PeerSystemManager{

    private Peer parent_peer;
    private String path;

    public PeerSystemManager(Peer parent_peer) {
        this.parent_peer = parent_peer;
        this.path = "src/filesystem/Peer" + parent_peer.getId() + "/";

        setupFileSystem();


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

        OutputStream out = Files.newOutputStream(Paths.get(file_path));
        out.write(data);
        out.close();

        return true;   
    }

    public static Chunk[] check(String filepath) throws IOException{
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
        System.out.println(chunksSize);
        
        byte[] buffer = Files.readAllBytes(file.toPath());
        
        int i = 0;
        int c = 0;
        while(c < (size - 64000)){
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
        System.out.println(chunks[0].getChunkData().length);
        return chunks;

       
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

    /*
        private ArrayList<Chunk> splitIntoChunks(byte[] data) {
        ArrayList<Chunk> ret = new ArrayList<>();

        int n = data.length / (Chunk.MAX_SIZE) + 1;

        for(int i = 0; i < n; i++) {

            byte[] chunk_data;

            if(i == n-1) {
                if(data.length % Chunk.MAX_SIZE ==0) {
                    chunk_data= new byte[0];
                } else {
                    chunk_data= Arrays.copyOfRange(data, i*Chunk.MAX_SIZE, i*Chunk.MAX_SIZE + (data.length % Chunk.MAX_SIZE));
                }
            } else {
                chunk_data= Arrays.copyOfRange(data, i*Chunk.MAX_SIZE, i*Chunk.MAX_SIZE + Chunk.MAX_SIZE);
            }

            Chunk chunk=new Chunk(fileId, i, repDegree, chunk_data);
            ret.add(chunk);
        }

        return ret;
    }

     */


/*

    private void check() throws IOException, NoSuchAlgorithmException{
        file = new File(filepath);

        if(file.isFile()){
            this.divider();
        }else{
            System.out.println("Error reading the file");
        }
    }

    
    
    
    
    private void rebuilder(Chunk[] chunks) throws IOException{
        File newfile = new File("temp");
        if(newfile.exists()){
            newfile.delete();
        }
        newfile.createNewFile();
        
        try (FileOutputStream stream = new FileOutputStream("temp")) {
            for(Chunk c : chunks){
                stream.write(c.getChunkData());
            }
            stream.close();
        }
    }
    */

}
