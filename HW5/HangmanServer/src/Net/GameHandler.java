/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Net;

import Controller.ServerController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class GameHandler implements Runnable {

    private Socket playerSocket;
    private boolean isConnected = false;
    private ServerController controller;
    
    private static int numPlayers = 0;
    
    private PrintWriter toPlayer;
    private BufferedReader fromPlayer;
    private String message;
    
    
    public GameHandler(Socket playerSocket) {
        this.playerSocket = playerSocket;
        this.isConnected = true;
    }

    @Override
    public void run() {
        System.out.println("A user connected ..");
        numPlayers++;
        System.out.println("The number of players is "+numPlayers);
        try {
            toPlayer = new PrintWriter(new OutputStreamWriter(playerSocket.getOutputStream()), true);
            fromPlayer = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            
            String userInput; 
            controller = new ServerController();
            
            while(isConnected && ((userInput = fromPlayer.readLine()) != null)){
                if (userInput.startsWith("exit")){
                    System.out.println("Disconnecting client...");
                    numPlayers--;
                    System.out.println("The number of players is "+numPlayers);
                    playerSocket.close();
                    isConnected = false;
                }
                else{
                    System.out.println("Processing message ..");
                    message = controller.checkUserInput(userInput);
                    toPlayer.println(message);
                }
            }
            
        } catch (IOException e) {
            System.out.println("IOException in GameHandler occured. Check network connection.");
            e.printStackTrace();
        }
    }
    
}
