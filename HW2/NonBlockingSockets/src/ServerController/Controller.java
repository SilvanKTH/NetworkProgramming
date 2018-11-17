/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerController;

import Common.Message;
import Common.MessageType;
import ServerView.HangmanGame;

/**
 *
 * @author silvanzeller
 */
public class Controller {
    
    private HangmanGame hg = new HangmanGame();

    public Message startNewGame(Message message) {
        return hg.startNewGame(message);
    }

    public Message makeGuess(Message guess) {
        return hg.makeGuess(guess);
    }
    
}
