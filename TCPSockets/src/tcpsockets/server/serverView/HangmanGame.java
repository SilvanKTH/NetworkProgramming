/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.server.serverView;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author silvanzeller
 */
public class HangmanGame {
    
    private NewHangman newHangman; 
    
    public void newHangmanInstance(){
        newHangman = new NewHangman();
    }
    
    public void newGame(){
        newHangman.startNewGame();
    }
    
    public void setGuess(String playerGuess){
        newHangman.getGuess(playerGuess);
    }
    
    public String getResponse(){
        return newHangman.getResult();
    }

    private class NewHangman {

        private int score;
        private boolean playing;
        private boolean win;
        private boolean wrongGuess;
        private boolean startNew;
        private List<String> guesses = new ArrayList<>();
        private String newWord;
        private String placeholder;
        private int count;

        public NewHangman() {
            this.score = 0;
        }

        private void startNewGame() {
            this.playing = true;
            this.win = false;
            this.wrongGuess = false;
            this.startNew = true;
            this.guesses.clear();
            String filepath = "test.txt";
            System.out.println("Now trying to load file ...");
            try {
                BufferedReader br = Files.newBufferedReader(Paths.get(filepath));
                List<String> wordsList = br.lines().collect(Collectors.toList());
                wordsList.replaceAll(String::toLowerCase);
                int randomNum = ThreadLocalRandom.current().nextInt(0, wordsList.size());
                this.newWord = wordsList.get(randomNum);
                this.placeholder = new String(new char[newWord.length()]).replace("\0","-");
                this.count = newWord.length();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        private void getGuess(String playerGuess) {
            if (count != 0 && placeholder.contains("-") && playing){
                guesses.add(playerGuess);
                hangmanAlgorithm(playerGuess);
            }
        }
        
        private void hangmanAlgorithm(String playerGuess){
            StringBuilder newPlaceholder = new StringBuilder();
            if (Objects.equals(playerGuess, newWord)){
                win = true;
                score++;
            }
            else {
                if (playerGuess.length() == 1){
                    for (int i = 0; i < newWord.length(); i++){
                        if (newWord.charAt(i) == playerGuess.charAt(0)){
                            newPlaceholder.append(playerGuess.charAt(0));
                        } else if (placeholder.charAt(i) != '-'){
                            newPlaceholder.append(newWord.charAt(i));
                        } else{
                            newPlaceholder.append("-");
                        }
                    }
                
                    if (placeholder.equals(newPlaceholder.toString())){
                        count--;
                        wrongGuess = true;
                        if(count == 0){
                            score--; 
                            }
                    } else{
                            placeholder = newPlaceholder.toString();
                            wrongGuess = false;
                        if (placeholder.equals(newWord)){
                            win = true;
                            score++;
                        }
                    }
                }
                else {
                    count--;
                    if (count == 0){
                        score--;
                    }
                    wrongGuess = true;
                }
            }
            
        }

        private String getResult() {
            String response;
            if (!playing){
                response = "Start a game";
                return response;
            }
            if (startNew){
                response = "Word:\t"+placeholder+
                        "\nGuesses:\t"+guesses+
                        "\nAttempts:\t"+count+
                        "\nScore:\t"+score+
                        "YOUR GUESS >>>";
                startNew = false;
                return response;
            }
            if (count == 0){
                response = "YOU LOST. Type start or quit"+
                        "\nScore:\t"+score+
                        ">>>";
            }
            if (!win && !wrongGuess){
                response = "Word:\t"+placeholder+
                        "\nGuesses:\t"+guesses+
                        "\nAttempts:\t"+count+
                        "\nScore:\t"+score+
                        "NEXT GUESS >>>";
                return response;
            }
            if (!win && !wrongGuess){
                response = "Word:\t"+placeholder+
                        "\nGuesses:\t"+guesses+
                        "\nAttempts:\t"+count+
                        "\nScore:\t"+score+
                        "NEXT GUESS >>>";
                return response;
            }
            else{
                response = "YOU WIN! "+newWord+" was the correct word."+
                        "\nScore:\t"+score+
                        "\nType start or quit"+
                        "\n>>>";
                return response;
            }
        }
    }
    
}
