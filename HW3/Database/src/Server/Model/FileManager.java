/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class FileManager {
    
    public long uploadFile (String name, String [] content){
        name = "files/"+name;
        File file = new File(name);
        try {
            PrintWriter toFile = new PrintWriter(new FileWriter(file));
            for (String s : content){
                toFile.println(s);
            }
            toFile.close();
            return file.length();
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public String [] downloadFile (String name){
        name = "files/"+name;
        boolean isReading = true;
        try {
            BufferedReader fromFile = new BufferedReader(new FileReader(name));
            String line = fromFile.readLine();
            String temp;
            while(isReading){
                if((temp = fromFile.readLine()) == null){
                    isReading = false;
                }
                else {
                    line = line + "\n" + temp;
                }
            }
            String [] allLines = line.split("\n");
            fromFile.close();
            return allLines;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void deleteFile(String name){
        name = "files/"+name;
        File file = new File(name);
        file.delete();
    }
    
    public boolean writeToFile(String name, String input, String method){
        name = "files/"+name;
        File file = new File(name);
        PrintWriter toFile;
        try {
            if (method.equalsIgnoreCase("append")){            
                toFile = new PrintWriter(new FileWriter(file));
                toFile.println(input);
                toFile.close();
                }
            else if (method.equalsIgnoreCase("replace")){
                toFile = new PrintWriter(new FileWriter(file, true));
                toFile.println(input);
                toFile.close();
            }
            else{
                return false;
            }
        }catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
