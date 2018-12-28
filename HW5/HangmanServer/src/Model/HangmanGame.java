/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;

/**
 *
 * @author silvanzeller
 */

// possible messages that can be sent to the user
// 1) "word: - - -#attempts: x#score: y"
// 2) "correct word: xxx#score: y"

public class HangmanGame {
    private final String SEPARATOR = "#";
    private final String CONTROLLER_SEPARATOR = "@";
    private final char PLACEHOLDER = '-';
    private final char SPACE = ' ';
    private int score = 0;
    private int attempt = 0;
    private String randomWord;
    private String screenOutput;
    public boolean gameOver = false;
    private String toUser;

    public HangmanGame(String randomWord) {
        this.randomWord = randomWord;
        this.attempt = randomWord.length();
    }
    
    private void increaseScore(){
        score++;
    }
    
    private void decreaseScore(){
        score--;
    }

    public String showState(StringBuilder wordOnScreen) {
        StringBuilder output = new StringBuilder(wordOnScreen.toString()+wordOnScreen.toString());
        for(int i = 0; i < wordOnScreen.length(); i++){
            output.setCharAt(2*i, wordOnScreen.charAt(i));
            output.setCharAt(2*i+1, SPACE);
        }
        screenOutput = output.toString();
        toUser = "Word: "+screenOutput+SEPARATOR+"Attempts: "+attempt+SEPARATOR+"Score: "+score;
        return toUser;
    }

    public String checkWord(String guessedWord, StringBuilder wordOnScreen) throws IOException {
        if(guessedWord.equals(randomWord.trim())){
            increaseScore();
            gameOver = true;
            screenOutput = randomWord;
            wordOnScreen = selectWord(wordOnScreen);
            toUser = "You won! The correct word is "+screenOutput+SEPARATOR+"Score: "+score;
            toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
        }
        else {
            attempt--;
            if(attempt == 0){
                decreaseScore();
                gameOver = true;
                toUser = "You lost! The correct word was "+screenOutput+SEPARATOR+"Score: "+score;
                wordOnScreen = selectWord(wordOnScreen);
                toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
            } else {
                toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
            }
        }
        return toUser;       
    }

    public String checkLetter(char userInput, StringBuilder wordOnScreen) throws IOException {
        boolean correctLetter = false;
        for (int i = 0; i < randomWord.length(); i++){
            if (userInput == randomWord.charAt(i)){
                if(wordOnScreen.charAt(i) == PLACEHOLDER){
                    wordOnScreen.setCharAt(i, userInput);
                    correctLetter = true;
                }
                else{
                    return wordOnScreen.toString();
                }
            }
        }
        
        if (correctLetter == true){
            if(wonGame(wordOnScreen)){
                increaseScore();
                screenOutput = randomWord;
                toUser = "You won! The correct word is "+screenOutput+SEPARATOR+"Score: "+score;
                //test this
                wordOnScreen = selectWord(wordOnScreen);
                toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
            } else {
                toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
            }
        } else {
            attempt--;
            if (attempt == 0){
                decreaseScore();
                gameOver = true;
                screenOutput = randomWord;
                toUser = "You lost! The correct word was "+screenOutput+SEPARATOR+"Score: "+score;
                wordOnScreen = selectWord(wordOnScreen);
                toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
            } else {
                toUser += CONTROLLER_SEPARATOR+wordOnScreen.toString();
            }
        }
        return toUser;
    }

    private boolean wonGame(StringBuilder wordOnScreen) {
        int testPlaceholder = wordOnScreen.toString().indexOf(PLACEHOLDER);
        if (testPlaceholder == -1){
            gameOver = true;
            return gameOver;
        }
        return gameOver;
    }

    private StringBuilder selectWord(StringBuilder wordOnScreen) throws IOException {
        RandomWord newWord = new RandomWord();
        String wordChosen = newWord.getWord();
        attempt = wordChosen.length();
        wordOnScreen = new StringBuilder(wordChosen);
        for (int i = 0; i < attempt; i++){
            wordOnScreen.setCharAt(i, PLACEHOLDER);
        }
        return wordOnScreen;
    }    
}
