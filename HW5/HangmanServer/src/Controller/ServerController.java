/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.HangmanGame;
import Model.RandomWord;
import java.io.IOException;


/**
 *
 * @author silvanzeller
 */
public class ServerController {
    
    // possible messages to be received: 
    // 1) "start" --> starts a game
    // 2) "word" --> solution to a round of the game
    // 3) "exit" --> exits the game; handled in net layer
    // 4) "<single character>" --> places a guess
    // 5) other message --> invalid, return error message
    
    private final String START = "start";
    private final String WORD = "word";
    private final String DELIMITER = "#";
    private final String CONTROLLER_SEPARATOR = "@";
    private final String SPACE = " ";
    private final String EXIT = "exit";
    private final char PLACEHOLDER = '-';
    
    private HangmanGame hangmanGame;
    private String messageToPlayer;
    private StringBuilder wordOnScreen;

    private boolean gameRunning = false;
    private boolean activeRound = false;
    private boolean flag = false;
    private static String randomWord;
    private char guessedChar;
    
    
    
    public String checkUserInput(String userInput) throws IOException {

// start the game, chooses random word and invokes new instance of HangmanGame        
        if(userInput.startsWith(START)){ //start the application
            if(!gameRunning){
                if(!activeRound){
                    RandomWord newWord = new RandomWord();
                    String wordChosen = newWord.getWord();
                    wordOnScreen = new StringBuilder(wordChosen);
                    for (int i = 0; i < wordOnScreen.length(); i++){
                        wordOnScreen.setCharAt(i, PLACEHOLDER);
                    }
                    hangmanGame = new HangmanGame(wordChosen);
                    messageToPlayer = hangmanGame.showState(wordOnScreen);
                    gameRunning = true;
                } else {
                    messageToPlayer = hangmanGame.showState(wordOnScreen);
                    activeRound = false;
                    gameRunning = true;
                }
            } else {
                if (!activeRound){
                    messageToPlayer = "You already started the game.";
                    messageToPlayer += hangmanGame.showState(wordOnScreen);
                }
            }
        }
        else if(userInput.startsWith(WORD)){ // whole word guess
            if(gameRunning){
                String[] userMessage = userInput.split(SPACE);
                userInput = userMessage[1].trim();
                messageToPlayer = hangmanGame.checkWord(userInput, wordOnScreen);
                
                if(!hangmanGame.gameOver){
                    messageToPlayer = hangmanGame.showState(wordOnScreen);
                } else {
                    activeRound = true;
                    gameRunning = false;
                    hangmanGame.gameOver = false;
                }
            } else {
                messageToPlayer = "You need to start the game before placing a guess.";
            }
        }
        else { // other kinds of messages 
            if(userInput.length() == 0){ // empty message 
                messageToPlayer = "You need to enter a character first.";
            } else { // message contains a letter
                userInput = userInput.trim();
                guessedChar = userInput.charAt(0);
                if ((userInput.length() == 1) && (gameRunning == true)){
                    messageToPlayer = hangmanGame.checkLetter(guessedChar, wordOnScreen);
                    if(messageToPlayer.indexOf(CONTROLLER_SEPARATOR) != -1){
                        String[] temp = messageToPlayer.split(CONTROLLER_SEPARATOR);
                        messageToPlayer = temp[0];
                        wordOnScreen = new StringBuilder(temp[1]);
                    } else {
                        wordOnScreen = new StringBuilder(messageToPlayer);
                        flag = true;
                    }
                    if(!hangmanGame.gameOver && !flag){
                        messageToPlayer = hangmanGame.showState(wordOnScreen);
                    } else if (!hangmanGame.gameOver && flag){
                        messageToPlayer = hangmanGame.showState(wordOnScreen);
                        flag = false;
                    } else {
                        activeRound = true;
                        gameRunning = false;
                        hangmanGame.gameOver = false;
                    }
                } else { // no valid message received
                    messageToPlayer = hangmanGame.showState(wordOnScreen);
                }
            }
        }
        return messageToPlayer;
    }
            
                    
}
