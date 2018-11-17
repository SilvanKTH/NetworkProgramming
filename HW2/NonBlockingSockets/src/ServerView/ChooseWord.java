/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author silvanzeller
 */
public class ChooseWord {
    Scanner scanner;
    ArrayList<String> words = new ArrayList();
    Random random = new Random();
    
    public ChooseWord() {
    }
    
    public void chooseWord() throws FileNotFoundException{
        scanner = new Scanner(new File("test.txt"));
        while(scanner.hasNextLine()){
            words.add(scanner.nextLine());
        }
        scanner.close();
    }
        
    public String getWord() throws FileNotFoundException {
        chooseWord();
        int index = random.nextInt(words.size());
        return words.get(index);
    }
    
}
