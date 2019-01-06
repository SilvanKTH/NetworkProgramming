/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Controller;

import Common.ThreadSafeStdOut;
import Server.Model.Player;
import Server.Model.RockPaperScissors;

/**
 *
 * @author silvanzeller
 */
public class Controller {
    
    ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    
    private final static String SPACE = " ";
    private final static String DELIMITER = "#";
    private final static String USER = "USER";
    private final static String QUIT = "QUIT";
    private final static String [] ELEMENTS = {"ROCK", "PAPER", "SCISSORS"};
    
    private RockPaperScissors game;
    private String returnMessage = "default message";

    public Controller(){
        this.game = new RockPaperScissors();
    }
    
    
    public String getServerResponse(String receivedMessage){
        receivedMessage = receivedMessage.trim();
        String[] receivedMessageArray1 = receivedMessage.split(SPACE);
        
        // for testing purposes
        for (String s : receivedMessageArray1){
            consoleOutput.println("Received message: "+s);
        }
        
        // sets up new user 
        if(receivedMessageArray1[0].equalsIgnoreCase(USER)){
            String username = receivedMessageArray1[1];
            Player player = new Player(username);
            returnMessage = game.addPlayer(player);
            
        } else {
            String[] receivedMessageArray2 = receivedMessageArray1[0].split(DELIMITER);
            if(receivedMessageArray2.length == 2){
                String username = receivedMessageArray2[0];
                //consoleOutput.println("TEST"+username);
                String command = receivedMessageArray2[1];
                //consoleOutput.println("TEST"+command);
                if (command.equalsIgnoreCase(QUIT)){
                    //consoleOutput.println("Deleted player "+username);
                    returnMessage = game.deletePlayerByUsername(username);
                    
                } else if (command.equalsIgnoreCase(ELEMENTS[0])){
                    consoleOutput.println("CONTROLLER: in elements == rock");
                    returnMessage = game.playRound(username, command);
                    
                } else if (command.equalsIgnoreCase(ELEMENTS[1])){
                    consoleOutput.println("CONTROLLER: in elements == paper");
                    returnMessage = game.playRound(username, command);
                    
                } else if (command.equalsIgnoreCase(ELEMENTS[2])){
                    consoleOutput.println("CONTROLLER: in elements == scissors");
                    returnMessage = game.playRound(username, command);
                    
                } else {
                    returnMessage = "received invalid message";
                    
                }
            }

        }
        return returnMessage;
    }
    
//    TEST METHOD  
//    public String getServerResponse(String receivedMessage) {
//        receivedMessage = receivedMessage.trim();
//        String[] receivedMessageArray1 = receivedMessage.split(SPACE);
//        
//        if(receivedMessageArray1[0].equalsIgnoreCase(USER)){
//            if(receivedMessageArray1.length >= 2){
//                String username = receivedMessageArray1[1];
//                Player player = new Player(username);
//                returnMessage = game.addPlayer(player);                
//            }
//        } else {
//            String[] receivedMessageArray2 = receivedMessageArray1[0].split(DELIMITER);
//            if (receivedMessageArray2[1].equalsIgnoreCase(QUIT)){
//                String username = receivedMessageArray2[0];
//                returnMessage = game.deletePlayerByUsername(username);
//            } else if (receivedMessageArray2[1].equalsIgnoreCase(ELEMENTS[0]) 
//                    || receivedMessageArray2[1].equalsIgnoreCase(ELEMENTS[1]) 
//                    || receivedMessageArray2[1].equalsIgnoreCase(ELEMENTS[2])){
//                String username = receivedMessageArray2[0];
//                String element = receivedMessageArray2[1];
//                Player player = game.findPlayerByUsername(username);
//                player.setElement(element);
//                returnMessage = game.playRound(player);
//            } else {
//                returnMessage = "Unknown command received";
//            }
//        }
//        return returnMessage;    
//    }
}
