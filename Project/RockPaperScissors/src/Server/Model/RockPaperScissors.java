/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Common.ThreadSafeStdOut;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author silvanzeller
 */
public class RockPaperScissors {
    
    private ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    
    private static final String ADD_SUCESS = "You have been added to the game successfully";
    private static final String ADD_FAILURE = "Sorry, there are already too many players in the game";
    private static final String ADD_DUPLICATE = "This user already exists";
    private static final String DEFAULT_ELEMENT = "DEFAULT";
    private static final String ROCK_ELEMENT = "ROCK";
    private static final String PAPER_ELEMENT = "PAPER";
    private static final String SCISSORS_ELEMENT = "SCISSORS";
    private static final String DELIMITER = "#";
    private static final String BROADCAST = "BROADCAST";
    private static final String UNICAST = "UNICAST";
    
    
    private static final int numPlayers = 3;
    private static final int scoreToWin = 1;
    
    ArrayList<Player> players = new ArrayList();
    
    private String serverResponse = "";
    private int numElementsSet = 0;
    
    private boolean allElementsSet = false; // might have to be synchronized
    
    public String addPlayer(Player player){
        if(players.size() < numPlayers){
            if(findPlayerByUsername(player.getName()) == null){
                players.add(player);
                String username = player.getName();
                //consoleOutput.println("In RPS.addPlayer(); length = "+players.size()+" Player name: "+player.getName());
                return username+DELIMITER+ADD_SUCESS; 
            } else {
                return ADD_DUPLICATE;
            }               
        }
        return ADD_FAILURE;
    }
    
    public String playRound(String username, String command){
        // check if player exists
        Player player = findPlayerByUsername(username);
        
        if(player == null){
            serverResponse = UNICAST+DELIMITER+"User not found";
        } else {
            // set rock-paper-scissors element
            player.setElement(command);
            // check if there are enough players in the round
            if(players.size() < numPlayers){
                int missingPlayers = numPlayers - players.size();
                serverResponse = UNICAST+DELIMITER+"Still waiting on "
                        +missingPlayers+" players to join";
            } else {
                // check if all players have set their element
                checkElements(players);
                if(!allElementsSet){
                    int missingElements = numPlayers - numElementsSet;
                    serverResponse = UNICAST+DELIMITER+"Still waiting on "
                            +missingElements+" players to choose an element";
                } else {
                    // iterates over each Player
                    for(int i = 0; i < players.size(); i++){

                        // checks against each player if an individual round was won
                        for (int j = 0; j < players.size(); j++){
                            Player playerA = players.get(i);
                            Player playerB = players.get(j);

                            // only won rounds increase momentary score
                            if (playerA.getElement().equalsIgnoreCase(ROCK_ELEMENT) 
                                    && playerB.getElement().equalsIgnoreCase(SCISSORS_ELEMENT)){
                                playerA.increaseRoundScore();
                                consoleOutput.println("TEST (ROCK): round score of "
                                        +playerA.getName()+" is now: "+playerA.getRoundScore());
                            } else if (playerA.getElement().equalsIgnoreCase(PAPER_ELEMENT) 
                                    && playerB.getElement().equalsIgnoreCase(ROCK_ELEMENT)){
                                playerA.increaseRoundScore();
                                consoleOutput.println("TEST (PAPER): round score of "
                                        +playerA.getName()+" is now: "+playerA.getRoundScore());
                            } else if (playerA.getElement().equalsIgnoreCase(SCISSORS_ELEMENT) 
                                    && playerB.getElement().equalsIgnoreCase(PAPER_ELEMENT)){
                                playerA.increaseRoundScore();
                                consoleOutput.println("TEST (SCISSORS): round score of "
                                        +playerA.getName()+" is now: "+playerA.getRoundScore());
                            } else {
                                //do nothing
                                consoleOutput.println("TEST (NO ELEMENT): round score of "
                                        +playerA.getName()+" is now: "+playerA.getRoundScore());
                            }
                        }
                    }
                    serverResponse = BROADCAST+DELIMITER+"All elements have been set";
                    serverResponse += getAllElements();
                    
                    // determine if there is a single round winner
                    Player roundWinner = getRoundWinner();
                    
                    if (roundWinner == null){
                        setBackElements(players);
                        serverResponse += "\n\nIt's a tie! Try again!";
                    } else {
                        setBackElements(players);                    
                        serverResponse += getCurrentScores();
                        
                        // check if a player won the game
                        if(roundWinner.getScore() == scoreToWin){
                            setBackScores(players);
                            serverResponse += "\n\n"+roundWinner.getName()+"has won this round!";
                        } else {
                            serverResponse += "\n\nLet's play another round";
                        }
                    }
                }
            }
        }
        return serverResponse;
    }
    
    
    private Player getRoundWinner() {
        consoleOutput.println("In getRoundWinner");
        Player tempPlayer = players.get(0);
        int maxRoundScore = 0;
        int counter = 0;
        
        // check for maximum round score 
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).roundScore > tempPlayer.roundScore){
                tempPlayer = players.get(i);
                maxRoundScore = tempPlayer.getRoundScore();
            }
        }
        
        // check if there is an unique winner
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).getRoundScore() == maxRoundScore){
                counter++;
            }
        }
        
        // set back round scores to zero
        for (int i = 0; i < players.size(); i++){
            players.get(i).setBackRoundScore();
        }
        
        // increase score, return unique winner of the round, else return null
        if (counter == 1){
            tempPlayer.increaseScore();
            return tempPlayer;
        } else {
            return null;
        }
    }

    private void setBackScores(ArrayList<Player> players) {
        consoleOutput.println("in setBackScores");
        for (Player p : players){
            p.setBackScore();
        }
    }
    
    private void checkElements(ArrayList<Player> players){
        consoleOutput.println("in checkElements");
        int counter = 0;
        for (Player p : players){
            if (!p.getElement().equals(DEFAULT_ELEMENT)){
                consoleOutput.println("TEST: player: "+p.getName()+" element:"+p.getElement());
                counter++;
            } else{
                consoleOutput.println("TEST: player: "+p.getName()+" has not set element yet: "+p.getElement());
            }
        }
        if (counter == numPlayers){
            consoleOutput.println("TEST: all "+counter+"players have set their elements");
            numElementsSet = 0;
            allElementsSet = true;    
        } else {
            numElementsSet = counter;
            allElementsSet = false;
        }
    }

    private void setBackElements(ArrayList<Player> players) {
        consoleOutput.println("in setBackElements");
        for (Player p : players){
            p.setElement(DEFAULT_ELEMENT);
        }
    }

    public String deletePlayerByUsername(String username) {
        consoleOutput.println("in deletePlayerByUsername");
        consoleOutput.println(""+players.size());
        String message = "";        
        for(Iterator<Player> iterator = players.iterator(); iterator.hasNext();){
            Player p = iterator.next();
            if (p.getName().equals(username)){
                iterator.remove();
                message += username+"sucessfully deleted";
            }
        }
        consoleOutput.println(""+players.size());
        return message;
    }
    
    public Player findPlayerByUsername(String username){
        consoleOutput.println("in findPlayerByUsername");
        Player player = null;
        for (Player p : players){
            if (p.getName().equals(username)){
                player = p;
            }
        }
        return player;
    }
    
    public String getAllElements(){
        consoleOutput.println("in getAllElements");
        String message = "";
        for (Player p : players){
            message += "\n"+p.getName()+"'s choice is '"+p.getElement();
        }
        return message;
    }
    
    public String getCurrentScores(){
        consoleOutput.println("in getCurrentScores");
        String message = "The current scores are:";
        for (Player p : players){
            message += "\n"+p.getName()+"'s number of won rounds is "+p.getScore();
        }
        return message;
    }
}
