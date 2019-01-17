/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerView;

import Common.Message;
import Common.MessageType;
import Common.SynchronizedStdOut;
/**
 *
 * @author silvanzeller
 */
public class HangmanGame {
    private SynchronizedStdOut consoleOut = new SynchronizedStdOut();
    //private String correctWord;

    public Message startNewGame(Message init) {
        consoleOut.println("Starting new game");
        Message msg = null;
        try{
            int score = init.getScore();
            boolean gameRunning = true;
            
            ChooseWord randomWord = new ChooseWord();
            String correctWord = randomWord.getWord().toUpperCase();
            StringBuilder currentWord = new StringBuilder();
            int wordLength = correctWord.length();
            for (int i = 0; i < wordLength; i++){
                currentWord.append("-");
            }
            msg = new Message(MessageType.GAMEINFO, "Game started", currentWord.toString(), correctWord, wordLength, score, gameRunning);
            consoleOut.println("Debug: "+correctWord);
            consoleOut.println("Debug: "+currentWord.toString());
            
        }catch (Exception e){
            e.printStackTrace();
        }        
        return msg;
    }

    public Message makeGuess(Message guess) {
        consoleOut.println("in makeGuess()");
        String userGuess = guess.getMessage().toUpperCase();
        String correctWord = guess.getCorrectWord();
        StringBuilder currentWord = new StringBuilder(guess.getCurrentWord());
        int score = guess.getScore();
        int wordLength = guess.getCorrectWord().length();
        int remainingAttempts = guess.getRemainingAttempts();
        boolean gameRunning;
        Message msg = null;
        
        if((userGuess.length() > 1) && correctWord.equals(userGuess)){
            score++;
            gameRunning = false;
            
            for (int i = 0; i < correctWord.length(); i++){
                currentWord.setCharAt(i, correctWord.charAt(i));
            }
            msg = new Message(MessageType.GAMEINFO, "", currentWord.toString(), correctWord, remainingAttempts, score, gameRunning);
        } else if ((userGuess.length() == 1) && correctWord.contains(userGuess)){
            char character = userGuess.charAt(0);
            for (int i = 0; i < correctWord.length(); i++){
                if (correctWord.charAt(i) == character){
                    currentWord.setCharAt(i, character);
                }
            }
            if (currentWord.toString().equals(correctWord)){
                score++;
                gameRunning = false;
                msg = new Message(MessageType.GAMEINFO, "", currentWord.toString(), correctWord, remainingAttempts, score, gameRunning);
            }
            else {
                gameRunning = guess.isGameRunning();
                msg = new Message(MessageType.GAMEINFO, "", currentWord.toString(), correctWord, remainingAttempts, score, gameRunning);
            }    
        }
        else {
            remainingAttempts--;
            if (remainingAttempts == 0){
                gameRunning = false;
                score--;
                msg = new Message(MessageType.GAMEINFO, "", currentWord.toString(), correctWord, remainingAttempts, score, gameRunning);
            }
            else {
                gameRunning = guess.isGameRunning();
                msg = new Message(MessageType.GAMEINFO, "", currentWord.toString(), correctWord, remainingAttempts, score, gameRunning);
            }
        }
        
        return msg;
    }
    
}
