/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.client.clientController;

import tcpsockets.client.clientNet.OutputHandler;
import tcpsockets.client.clientNet.ServerConnection;
import tcpsockets.client.clientView.NonBlockingInterpreter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author silvanzeller
 */
public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    public void connect(String host, int port, OutputHandler serverResponse) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, serverResponse);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }).thenRun(() -> serverResponse.ServerMessage("Connected to "+host+":"+port+"\n>>>"));
    }

    public void startNewGame() {
        CompletableFuture.runAsync(() -> {
            serverConnection.startGame();
        });
    }

    public void makeGuess(String guess) {
        CompletableFuture.runAsync(() -> {
           serverConnection.makeGuess(guess);
        });
    }

    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    public boolean isConnected() {
        return serverConnection.isConnected();
    }

}
