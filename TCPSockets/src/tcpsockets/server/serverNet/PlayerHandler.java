/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.server.serverNet;

import tcpsockets.common.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import tcpsockets.server.serverController.Controller;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class PlayerHandler implements Runnable{

    private final GameServer server;
    private final Socket player;
    private Controller controller = new Controller();
    private boolean connected = false;
    private BufferedReader fromClient;
    private PrintWriter toClient;

    PlayerHandler(GameServer server, Socket player) {
        this.server = server;
        this.player = player;
        connected = true;
        controller.HangmanGame();
        
    }

    @Override
    public void run() {
        try{
            fromClient = new BufferedReader(new InputStreamReader (player.getInputStream()));
            toClient = new PrintWriter(player.getOutputStream(), true);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
                
        while(connected){
            try {
                PlayerMessage pm = new PlayerMessage(fromClient.readLine());
                switch (pm.getMessageType()){
                    case START:
                        System.out.println("Starting a new Game");
                        controller.startNewGame();
                        ServerResponse(controller.getResponse());
                        break;
                    case GUESS:
                        System.out.println("Received a new guess");
                        controller.newGuess(pm.getMessageBody());
                        ServerResponse(controller.getResponse());
                        break;
                    case QUIT:
                        System.out.println("Disconnected");
                        disconnectPlayer();
                        break;
                    default:
                        throw new StreamCorruptedException("pm.getMessageType()");
                }
            } catch (IOException ex) {
                disconnectPlayer();
                System.err.println("Player disconnected");
            }  
        }
    }

    private void ServerResponse(String response){
        toClient.println(MessageType.SERVER_RESPONSE.toString()+"\n"+response);
    }
    
    private void disconnectPlayer() {
        try {
            player.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        server.removePlayer(this);
    }
    
}
