/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class RandomWord {
    private String word;
    private boolean endOfFile = false;
    private static final String LINEBREAK = "\n";
    private static final String PATH = "words.txt";

//    public RandomWord() {
//    }

    public String getWord() throws IOException {
        String[] words;
        words = this.readFile();
        Random randomNo = new Random();
        int randomLine = randomNo.nextInt(words.length);
        word = words[randomLine].toLowerCase();
        return word;
    }

    private String[] readFile() throws IOException {
        try {
            BufferedReader fromFile = new BufferedReader(new FileReader(PATH));
            String content = fromFile.readLine();
            String temp;
            while(!endOfFile){
                if((temp = fromFile.readLine()) == null){
                    endOfFile = true;
                } else {
                    content = content + LINEBREAK + temp;
                }
            }
            String[] words = content.split(LINEBREAK);
            return words;            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File could not be found, check filepath");
            return null;
        }
    }
    
}
