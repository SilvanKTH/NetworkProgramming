/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import Common.MessageType;
/**
 *
 * @author silvanzeller
 */
public class Message {

    private MessageType messageType;
    private String message;
    private boolean connectedToServer;
    private String currentWord;
    private String correctWord;
    private int remainingAttempts;
    private int score;
    private boolean gameRunning;
    private MessageType MessageType;
    private String delimiter = "#";
    
    public Message(MessageType messageType){
        this.messageType = messageType;
    }
    
    public Message(MessageType messageType, String message){
        this.messageType = messageType;
        this.message = message;
    }
    
    public Message(MessageType messageType, String message, boolean connectedToServer){
        this.messageType = messageType;
        this.message = message;
        this.connectedToServer = connectedToServer;
    }
    
    public Message(MessageType messageType, int score, boolean connectedToServer){
        this.messageType = messageType;
        this.score = score;
        this.connectedToServer = connectedToServer;
    }
    
    public Message(MessageType messageType, String message, String currentWord, int remainingAttempts, int score, boolean gameRunning){
        this.messageType = messageType;
        this.message = message;
        this.currentWord = currentWord;
        this.remainingAttempts = remainingAttempts;
        this.score = score;
        this.gameRunning = gameRunning;
    }
    
    public Message(MessageType messageType, String message, String currentWord, String correctWord, int remainingAttempts, int score, boolean gameRunning){
        this.messageType = messageType;
        this.message = message;
        this.currentWord = currentWord;
        this.correctWord = correctWord;
        this.remainingAttempts = remainingAttempts;
        this.score = score;
        this.gameRunning = gameRunning;
    }
    
    public MessageType getMessageType(){
        return MessageType;
    }
    
    public String getMessage(){
        return message;
    }
    
    public String getCurrentWord(){
        return currentWord;
    }
    
    public String getCorrectWord(){
        return correctWord;
    }
    
    public int getRemainingAttempts(){
        return remainingAttempts;
    }
    
    public int getScore(){
        return score;
    }
    
    public boolean isGameRunning(){
        return gameRunning;
    }
    
    public boolean isConnectedToServer(){
        return connectedToServer;
    }
    
    public String getDelimiter(){
        return delimiter;
    }
}
