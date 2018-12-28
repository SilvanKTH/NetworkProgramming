/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Startup;

import Net.GameServer;

/**
 *
 * @author silvanzeller
 */
public class Startup {
    
    public static final int SERVER_PORT = 8080; 
    public static final int MAX_PLAYERS = 10;
    
    public static void main (String[] args){
        
        GameServer gs = new GameServer();
        gs.serve(SERVER_PORT, MAX_PLAYERS);
        
    }
    
}
