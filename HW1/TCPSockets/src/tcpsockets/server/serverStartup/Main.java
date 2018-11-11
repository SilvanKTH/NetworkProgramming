/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.server.serverStartup;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tcpsockets.server.serverNet.GameServer;


/**
 *
 * @author silvanzeller
 */


public class Main {
            
    public static void main(String[] args) {
        
        GameServer gs = new GameServer();
        try {
            gs.listenSocket();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
