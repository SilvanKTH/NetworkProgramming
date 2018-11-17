/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientController;

import ClientNet.Connection;
import ClientNet.ServerMessageHandler;

/**
 *
 * @author silvanzeller
 */
public class Controller {
    
    ServerMessageHandler serverMessageHandler;
    
    private Connection c = new Connection(serverMessageHandler);

    public void connect(String host, int port) {
        c.connect(host, port);
    }

    public void disconnect() {
        c.disconnect();
    }

    public void startGame() {
        c.startGame();
    }

    public void makeGuess(String guess) {
        c.makeGuess(guess);
    }
    
}
