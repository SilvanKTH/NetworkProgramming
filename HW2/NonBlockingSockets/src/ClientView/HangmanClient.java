/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientView;

//import ClientController.Controller;
import ClientNet.ServerConnection;
import Common.SynchronizedStdOut;
import Common.MessageType;
import Common.Message;

import java.io.IOException;
import java.util.Scanner;
import ClientNet.ServerResponse;

/**
 *
 * @author silvanzeller
 */
public class HangmanClient implements Runnable {
    
    private final String WELCOME_MESSAGE = "Type 'connect HOST PORT' to connect to server and 'quit' to exit the game";
    private final String PROMPT = ">>>";
    private boolean clientRunning = true;
    private boolean gameRunning = false;
    private boolean connected = false;
    private final SynchronizedStdOut consoleOut = new SynchronizedStdOut();
    private final Scanner input = new Scanner(System.in);
    //private final Controller controller = new Controller(new ServerMessageOutput()); // (new ServerMessageOutput())
    private final ServerConnection serverConnection = new ServerConnection(new ServerMessageOutput());
    
    @Override
    public void run() {
        consoleOut.println(WELCOME_MESSAGE);

        while (clientRunning){
            consoleOut.print(PROMPT);
            try {
                CommandLine cli = new CommandLine(input.nextLine().toUpperCase());
                switch (cli.getCommand()){
                    case CONNECT:
                        if(connected){
                            consoleOut.println("You're already connected");
                            consoleOut.print(PROMPT);
                        }
                        else {
                            consoleOut.println("Trying to connect to server");
                            String host = cli.getArgs()[0];
                            int port = Integer.valueOf(cli.getArgs()[1]);
                            //controller.connect(host, port);
                            serverConnection.connect(host, port);
                            connected = true;
                        }
                        break;
                    case QUIT:
                        if(connected){
                            //controller.disconnect();
                            serverConnection.disconnect();
                            consoleOut.println("+++Called disconnect+++");
                            connected = false;
                        } else{
                            consoleOut.println("+++No socket connection+++");
                            clientRunning = false;
                        }
                        break;
                    case START:
                        if(connected){
                            //controller.startGame();
                            serverConnection.startGame();
                            gameRunning = true;
                        } else {
                            consoleOut.println("Connect to server first");
                            consoleOut.println(WELCOME_MESSAGE);
                            //consoleOut.print(PROMPT);
                        }
                        break;
                    case GUESS:
                        if(connected){
                            if(gameRunning){
                                String guess = cli.getArgs()[0];
                                //controller.makeGuess(guess);
                                serverConnection.makeGuess(guess);
                                //consoleOut.print(PROMPT);
                            } else {
                                consoleOut.println("Type 'start' to begin a new game");
                                //consoleOut.print(PROMPT);
                            }
                        } else{
                            consoleOut.println("Connect to server first");
                            consoleOut.println(WELCOME_MESSAGE);
                            //consoleOut.print(PROMPT);
                        }
                        break;                        
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }            
    }
    
    public class ServerMessageOutput implements ServerResponse{

        @Override
        public void handleMessage(Message message) {
            consoleOut.println("+++in handleMessage+++");
            consoleOut.println(message.toString());
            consoleOut.println(message.getMessage());
            if (message.messageType == MessageType.INFO){
                if (message.isConnectedToServer()){
                    connected = true;
                }
                consoleOut.println(message.getMessage());
                consoleOut.println(WELCOME_MESSAGE);
                consoleOut.print(PROMPT);
            }
            else if (message.messageType == MessageType.GAMEINFO){
                gameRunning = message.isGameRunning();
                if (!message.getMessage().equals("")){
                    consoleOut.println(message.getMessage());
                }
                consoleOut.println("\n########################");
                consoleOut.println(message.getCurrentWord()+"\tRemaining attempts: "+message.getRemainingAttempts());
                consoleOut.println("Score: "+message.getScore());
                consoleOut.println("########################\n");
                if(!message.isGameRunning()){
                    consoleOut.println("Game ended. Type 'start' or 'quit");
                }
                consoleOut.print(PROMPT);
            }
//            else {
//                consoleOut.println("+++exited conditionals+++");
//            } 
        }

        @Override
        public void disconnected() {
            connected = false;
            consoleOut.println("Disconnected from server");
            consoleOut.println(WELCOME_MESSAGE);
            consoleOut.print(PROMPT);
        }        
    }
}
