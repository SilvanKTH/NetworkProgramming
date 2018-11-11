/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.server.serverNet;

import java.io.IOException;
import static java.lang.Thread.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author silvanzeller
 */
public class GameServer {
    
    private static int port = 1111;
    private static final int LINGER_TIME = 10000;
    private static final int TIMEOUT_TIME = 100000;
    private List<PlayerHandler> allPlayers = new ArrayList<>();
   
/*    public static void main (String [] args) throws IOException{
        
        GameServer gs = new GameServer();
        gs.listenSocket();
    }*/
    
    public void listenSocket() throws IOException{
        ServerSocket socket = new ServerSocket(port);
        while(true){
            System.out.println("Waiting for clients ...");
            Socket player = socket.accept();
            System.out.println("Handling connection from new player ...");
            handlePlayer(player);
        }
    }

    private void handlePlayer(Socket player) throws SocketException {
        player.setSoLinger(true, LINGER_TIME);
        player.setSoTimeout(TIMEOUT_TIME);
        
        PlayerHandler ph = new PlayerHandler(this, player);
        allPlayers.add(ph);       
        Thread handlerThread = new Thread();
        handlerThread.setPriority(MAX_PRIORITY);
        handlerThread.start();
    }
    
    public void removePlayer(PlayerHandler player){
        allPlayers.remove(player);
    }
    
}
