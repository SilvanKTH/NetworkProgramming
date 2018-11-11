/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.client.clientView;

import tcpsockets.client.clientNet.OutputHandler;
import tcpsockets.client.clientController.Controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class NonBlockingInterpreter implements Runnable {
    public static final String IP_ADDRESS = "127.0.0.1"; // localhost
    public static final int PORT = 1111; // undefined port
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private boolean newInput = false;
    private Controller controller;
    

    public void start() {
        if (newInput){
            return;
        }
        newInput = true;
        controller = new Controller();
        new Thread(this).start();            
    }
    
    
    @Override
    public void run() {
        System.out.println("New game of hangman."+
                "\nconnect"+
                "\nstart"+
                "\nguess"+
                "\nquit");
        System.out.println(">>>");
        while(newInput){
            try {
                ProcessCommand processCommand = new ProcessCommand(br.readLine());
                if (processCommand.getMessageBody() == null || processCommand.getCommand() == Commands.WRONG_COMMAND){
                    System.out.println("Wrong command. Choose from connect, start, guess, quit");
                    System.out.println(">>>");
                }
                else {
                    if ((processCommand.getCommand() != Commands.CONNECT && !controller.isConnected()) && processCommand.getCommand() != Commands.QUIT){
                        System.out.println("You need to connect first before starting the game");
                        System.out.println(">>>");
                        continue;
                    }
                    switch (processCommand.getCommand()){
                        case CONNECT:
                            controller.connect(IP_ADDRESS, PORT, new ServerResponse());
                            break;
                        case START:
                            controller.startNewGame();
                            break;
                        case GUESS:
                            controller.makeGuess(processCommand.getMessageBody());
                            break;
                        case QUIT:
                            newInput = false;
                            controller.disconnect();
                            break;
                    }
                }
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public class ServerResponse implements OutputHandler{
        @Override
        public void ServerMessage(String message){
            System.out.println(message);
        }
    }    
}
