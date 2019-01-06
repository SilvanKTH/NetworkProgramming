/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Common.ThreadSafeStdOut;

/**
 *
 * @author silvanzeller
 */
public class Player {
    
    public static final String DEFAULT_ELEMENT = "DEFAULT";
    private ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();
    
    String name;
    String element;
    int score;
    int roundScore;
    
    public Player(String name){
        this.name = name;
        this.element = DEFAULT_ELEMENT;
        this.score = 0;
        this.roundScore = 0;
        consoleOut.println("Created new Player with name "+name);
    }
    
//    public Player(String name, String element, int score, int roundScore){
//        this.name = name;
//        this.element = element;
//        this.score = score;
//        this.roundScore = roundScore;
//    }
    
    public String getName(){
        return name;
    }
    
    public String getElement(){
        return element;
    }
    
    public int getScore(){
        return score;
    }
    
    public int getRoundScore(){
        return roundScore;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setElement(String element){
        this.element = element;
    }
    
    public void increaseScore(){
        this.score++;
    }
    
    public void setBackScore(){
        this.score = 0;
    }
    
    public void increaseRoundScore(){
        this.roundScore++;
    }
    
    public void setBackRoundScore(){
        this.roundScore = 0;
    }
    
}
