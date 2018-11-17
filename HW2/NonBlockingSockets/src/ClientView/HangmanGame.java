/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientView;

import ClientController.Controller;
import ClientNet.ServerMessageHandler;
import Common.SynchronizedStdOut;
import Common.MessageType;
import Common.Message;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author silvanzeller
 */
public class HangmanGame implements Runnable {
    
    private final String WELCOME_MESSAGE = "Type 'connect HOST PORT' to connect to server and 'quit' to exit the game";
    private boolean clientRunning = false;
    private boolean gameRunning = false;
    private SynchronizedStdOut consoleOut;
    private Scanner input = new Scanner(System.in);
    private Controller controller = new Controller(); // (new ServerMessageOutput())
    private boolean connected = false;

    public void start(){
        gameRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (clientRunning){
            consoleOut.println(WELCOME_MESSAGE);
            try {
                CommandLine cmd = new CommandLine(input.nextLine());
                switch (cmd.getCommand()){
                    case CONNECT:
                        if(connected){
                            consoleOut.println("You're already connected");
                        }
                        else {
                            consoleOut.println("Trying to connect to server");
                            String host = cmd.getArgs()[0];
                            int port = Integer.valueOf(cmd.getArgs()[1]);
                            controller.connect(host, port);
                        }
                        break;
                    case QUIT:
                        if(connected){
                            controller.disconnect();
                            connected = false;
                        } else{
                            clientRunning = false;
                        }
                        break;
                    case START:
                        if(connected){
                            controller.startGame();
                            gameRunning = true;
                        } else {
                            consoleOut.println("Connect to server first");
                            consoleOut.println(WELCOME_MESSAGE);
                        }
                        break;
                    case GUESS:
                        if(connected){
                            if(gameRunning){
                                String guess = cmd.getArgs()[0];
                                controller.makeGuess(guess);
                            } else {
                                consoleOut.println("Type 'start' to begin a new game");
                            }
                        } else{
                            consoleOut.println("Connect to server first");
                            consoleOut.println(WELCOME_MESSAGE);
                        }
                        break;                        
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
            
    }
    
    public class ServerMessageOutput implements ServerMessageHandler{

        @Override
        public void handleMessage(Message message) {
            if (message.getMessageType() == MessageType.INFO){
                if (message.isConnectedToServer()){
                    connected = true;
                }
                consoleOut.println(message.getMessage());
                consoleOut.println(WELCOME_MESSAGE);
                consoleOut.println(">>>");
            }
            else if (message.getMessageType() == MessageType.GAMEINFO){
                gameRunning = message.isGameRunning();
                if (!message.getMessage().equals("")){
                    consoleOut.println(message.getMessage());
                }
                consoleOut.println(message.getCurrentWord()+"\tRemaining attempts: "+message.getRemainingAttempts());
                consoleOut.println("Score: "+message.getScore());
                if(!message.isGameRunning()){
                    consoleOut.println("Game ended. Type 'start' or 'quit");
                }
                consoleOut.println(">>>");
            }
        }

        @Override
        public void disconnected() {
            connected = false;
            consoleOut.println("Disconnected from server");
            consoleOut.println(WELCOME_MESSAGE);
            consoleOut.println(">>>");
        }        
    }
}
