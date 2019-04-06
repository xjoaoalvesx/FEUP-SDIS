package files

import java.io.File;
import java.io.IOException;


public class FileManager{

    private String filepath;
    byte[][] chunks;
    private File file

    public static void main(String[] args){
        FileManager f = new FileManager(args[0]);
    }

    public FileManager(filepath){
        this.filepath = filepath;
        this.check();
    }

    private check(){
        file = new File(filepath)

        if(file.isFile()){
            this.divider();
        }else{
            System.out.println("Error reading the file");
        }
    }

    private divider(){
        long size = file.getTotalSpace();
        System.out.println("The file size is " + size + "bytes")
    }
}