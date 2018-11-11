/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.client.clientNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import tcpsockets.common.MessageType;

/**
 *
 * @author silvanzeller
 */
public class ServerConnection {
    private static final int SOCKET_TIMEOUT = 100000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private OutputHandler serverResponse;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;


    public void connect(String host, int port, OutputHandler serverResponse) throws IOException{
        this.serverResponse = serverResponse;
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        connected = true;
        toServer = new PrintWriter(socket.getOutputStream(), true);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(new Listener(serverResponse)).start();
    }
    
    public void startGame(){
        toServer.println(MessageType.START);
    } 
    
    public void makeGuess(String guess){
        toServer.println(MessageType.GUESS+" "+guess);
    }
    
    public void disconnect() throws IOException{
        toServer.println(MessageType.QUIT);
        socket.close();
        socket = null;
        connected = false;
    }
    
    public boolean isConnected(){
        return connected;
    }


    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        public Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try{
                while(true){
                    outputHandler.ServerMessage(fromServer.readLine());
                }
            } catch (Throwable connectionFailure){
                if (connected){
                    outputHandler.ServerMessage("no connection");
                }
            }

        }

    }
}