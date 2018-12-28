/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author silvanzeller
 */
public class GameServer {

    private ServerSocket serverSocket;
    private Socket playerSocket;
    private ExecutorService executorService;
    private final int SO_LINGER_TIME = 10000;
    private final int SO_TIMEOUT_TIME = 100000;
    
    public void serve(int SERVER_PORT, int MAX_PLAYERS) {
        try{
            serverSocket = new ServerSocket(SERVER_PORT);
            playerSocket = new Socket();
            executorService = Executors.newFixedThreadPool(MAX_PLAYERS);
            System.out.println("Setup Server for an amount of max. "+MAX_PLAYERS+" players.");
            while(true){
                playerSocket = serverSocket.accept();
                playerSocket.setSoLinger(true, SO_LINGER_TIME);
                playerSocket.setSoTimeout(SO_TIMEOUT_TIME);
                Thread playerThread = new Thread(new GameHandler(playerSocket));
                playerThread.setPriority(Thread.MAX_PRIORITY);
                executorService.execute(playerThread);
            }
        } catch (IOException e){
            System.out.println("IOException in GameServer. Check if an instance is running already");
            e.printStackTrace();
        } finally {
            if(executorService != null){
                executorService.shutdown();
            }
        }
    }
    
}
