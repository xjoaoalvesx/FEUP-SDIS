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
        this.path = "../Peers/Peer" + parent_peer.getId() + "/";
        createFolder("filesystem/Peer" + parent_peer.getId() + "/");
    }



    public void createFolder(String path){
        try{
            Files.createDirectories(Paths.get(path));
        } catch (IOException e){
            System.out.println("Error creating folder");
        }
    }




/*

    private void check() throws IOException, NoSuchAlgorithmException{
        file = new File(filepath);

        if(file.isFile()){
            this.divider();
        }else{
            System.out.println("Error reading the file");
        }
    }

    private void divider() throws IOException, FileNotFoundException, NoSuchAlgorithmException{
        long size = file.length();

        int chunksSize = ((int) size) / 64000 + 1;
        this.chunks = new Chunk[chunksSize];
        
        byte[] buffer = Files.readAllBytes(file.toPath());
        
        int i = 0;
        int c = 0;
        while(i < (size - 64000)){
            byte[] tempbuf = Arrays.copyOfRange(buffer, i, i+64000);
            
            byte[] chunkID = encode(filepath, c);
            
            //TODO Replication Degree 
            this.chunks[c] = new Chunk(chunkID, tempbuf, 1);
            
            i = i + 64000;
            c++;
        }

        int lastSize = ((int) size) % 64000;

        byte[] lastbuf = Arrays.copyOfRange(buffer, i, i+lastSize);

        byte[] finalChunkID = encode(filepath, c);        

        //TODO Replication Degree 
        this.chunks[c] = new Chunk(finalChunkID, lastbuf, 1);

       
    }
    
    private byte[] encode(String filename, int chunkno) throws NoSuchAlgorithmException{
        String cod = filename + String.valueOf(chunkno);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(cod.getBytes(StandardCharsets.UTF_8));

        return encodedhash;
    
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