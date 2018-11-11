/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.server.serverController;

import tcpsockets.server.serverView.HangmanGame;

/**
 *
 * @author silvanzeller
 */
public class Controller {
    
    private HangmanGame hg = new HangmanGame();

    public void HangmanGame() {
        hg.newHangmanInstance();
    }
    
    public void startNewGame(){
        hg.newGame();
    }
    
    public void newGuess(String guess){
        hg.setGuess(guess);
    }
    
    public String getResponse(){
        return hg.getResponse();
    } 
}
