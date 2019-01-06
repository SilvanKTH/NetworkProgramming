/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.View;

import Client.Net.ClientDatagramManager;
import Common.ThreadSafeStdOut;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class GameClient implements Runnable{
    
    ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();

    BufferedReader userInput; 
    ClientDatagramManager communicator;
    private ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    private String username = "ANONYMUS";
    private static final String DELIMITER = "#";
    private static final String SPACE = " ";
    private static final String QUIT = "QUIT";
    private static final String HELP = "HELP";
    private static final String USER = "USER";
    private boolean isConnected = false;    
    
    private static final String HELP_MSG = "welcome to the rock-paper-scissors game!\n"
            + "to start, simply type 'user <your_username>', without any whitespace\n"
            + "to play, type 'rock', 'paper' or scissors\n"
            + "to quit, type 'quit' in the console\n"
            + "to get help, simply type 'help'";
    private static final String DISCONNECTED_MSG = "successfully disconnected!\n"
            + "restart client to start a new round";
    private static final String PROMPT = ">>>";
    
    public GameClient() throws SocketException, UnknownHostException{
        this.communicator = new ClientDatagramManager(this);
        consoleOutput.println(HELP_MSG);
        consoleOutput.print(PROMPT);
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public void setConnection(boolean connectionStatus){
        this.isConnected = connectionStatus;
        if(isConnected){
            consoleOut.println("You can now send messages");
        } else {
            consoleOut.println("You need to be connected to send messages");
        }
    }
    
    @Override
    public void run() {
        while(true){
            try {
                userInput = new BufferedReader(new InputStreamReader(System.in));
                String userMessage = userInput.readLine().trim();
                String[] userMessageArray = userMessage.split(SPACE);
                String userMessageCmd = userMessageArray[0];
                //userMessage = username+DELIMITER+userMessage;
                if(userMessageCmd.equalsIgnoreCase(QUIT)){
                    if (isConnected){
                        userMessage = username+DELIMITER+userMessage;
                        String serverResponse = username+DELIMITER;
                        serverResponse += communicator.handleMessage(userMessage);
                        consoleOutput.println(serverResponse);
                        communicator.disconnect();
                        consoleOutput.println(DISCONNECTED_MSG);
                        setConnection(false);
                    } else {
                        consoleOutput.println("you need to connect first by creating a user!");
                        consoleOutput.print(PROMPT);
                    }
                    
                } else if (userMessageCmd.equalsIgnoreCase(HELP)){
                    consoleOutput.println(HELP_MSG);
                    consoleOutput.print(PROMPT);
                    
                } else if (userMessageCmd.equalsIgnoreCase(USER)){
                    if (!isConnected){
                        String[] usernameInputArray = userMessage.split(SPACE);
                        setUsername(usernameInputArray[1]);
                        setConnection(true);
                        String serverResponse = username+DELIMITER;
                        serverResponse += communicator.handleMessage(userMessage);
                        consoleOutput.println(serverResponse);
                        consoleOutput.print(PROMPT);
                    } else {
                        consoleOutput.println("you are already connected");
                        consoleOutput.print(PROMPT);
                    }
                    
                } else {
                    if (isConnected){
                        userMessage = username+DELIMITER+userMessage;
                        String serverResponse = username+DELIMITER;
                        serverResponse += communicator.handleMessage(userMessage);
                        consoleOutput.println(serverResponse);
                        consoleOutput.print(PROMPT);
                    } else {
                        consoleOutput.println("you need to connect first by creating a user!");
                        consoleOutput.print(PROMPT);
                    }
                    
                }        
            } catch (IOException ioe) {
                System.out.println("IOException caught in GameClient.run()");
                consoleOutput.println(HELP_MSG);
            }
        }   
    }   
}
