package files;

import java.io.File;
import java.io.*;
import java.security.*;
import java.nio.charset.StandardCharsets;



public class FileManager{

    private String filepath;
    Chunk[] chunks;
    private File file;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
        FileManager f = new FileManager(args[0]);
    }

    public FileManager(String filepath) throws IOException, NoSuchAlgorithmException{
        this.filepath = filepath;
        this.check();
    }

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
        
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        
        int i = 0;
        int c = 0;
        while(i < (size - 64000)){
            char[] tempbuf = new char[64000];
            buffer.read(tempbuf, i, 64000);
            
            byte[] chunkID = encode(filepath, c);
            
            /*TODO Replication Degree */
            this.chunks[c] = new Chunk(chunkID, tempbuf, 1);
            
            i = i + 64000;
            c++;
        }

        int lastSize = ((int) size) % 64000;

        char[] lastbuf = new char[lastSize];
        buffer.read(lastbuf, i, lastSize);

        byte[] finalChunkID = encode(filepath, c);        

        /*TODO Replication Degree */
        this.chunks[c] = new Chunk(finalChunkID, lastbuf, 1);
       
    }
    
    private byte[] encode(String filename, int chunkno) throws NoSuchAlgorithmException{
        String cod = filename + String.valueOf(chunkno);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(cod.getBytes(StandardCharsets.UTF_8));

        return encodedhash;
    
    }
}