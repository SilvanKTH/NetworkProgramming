/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Net;

import Client.View.GameClient;
import Common.ThreadSafeStdOut;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class ClientDatagramManager{ //implements Runnable {
    
    private ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    private final int BUFFER_SIZE = 1024;
    private final String PROMPT = ">>>";
    private final String DELIMITER = "#";
    private final String BROADCAST = "BROADCAST";
    private final String UNICAST = "UNICAST";
    
    private DatagramSocket clientSocket;
    private InetAddress serverIP;
    private int serverPort;
    //private BufferedReader userInput;
    private String response = "";
    private GameClient gameClient;
    
    
    private byte[] in;
    private byte[] out;
    //private String fromUser;
    
    private boolean receivedUnicast = false;
    
    public ClientDatagramManager(GameClient gameClient) throws SocketException, UnknownHostException{ 
        this.gameClient = gameClient;
        this.clientSocket = new DatagramSocket();
        this.serverIP = InetAddress.getByName("localhost");
        this.serverPort = 9999;
        this.consoleOutput.println("Client started on port "+clientSocket.getLocalPort());
    }
    
    public void disconnect(){
        clientSocket.close();
    }
    
    public String handleMessage(String userInput) {
        try{
            // byte arrays for storing in- and outgoing messages
            in = new byte[BUFFER_SIZE];
            out = new byte[BUFFER_SIZE];
            
            // write to and send DatagramPacket 
            out = userInput.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(out, out.length, serverIP, serverPort);
            clientSocket.send(sendPacket);
  
            // creates DatagramPacket for server response
            DatagramPacket receivePacket = new DatagramPacket(in, in.length);
            clientSocket.receive(receivePacket);
            
            // converts byte array to String
            response = "FROM SERVER: ";
            String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
            response += serverResponse;
            
            // check message header if unicast message is received
            if(checkIfUnicastMessage(serverResponse)){
                
                // own thread for receiving broadcast messages              
                Thread receiverThread = new Thread(new ReceiveMessage());
                receiverThread.start();
            }
            
            return response;
        } catch (IOException ioe) {
            consoleOutput.println("IOException caught in ClientDatagramManager.handleMessage()");
        }
        return null;
    }
    
    private boolean checkIfUnicastMessage(String serverResponse){
        String[] stringArray = serverResponse.split(DELIMITER);
        String messageType = stringArray[0];
        if (messageType.equalsIgnoreCase(UNICAST)){
            return true;
        } else {
            return false;
        }        
    }
    

    private class ReceiveMessage implements Runnable {

        @Override
        public void run() {
            consoleOutput.println("in receiveMessage");
            receivedUnicast = true;
            while (receivedUnicast){
                try {   
                    // byte array for incoming messages
                    in = new byte[BUFFER_SIZE];

                    // creates DatagramPacket for server response
                    DatagramPacket receivePacket = new DatagramPacket(in, in.length);
                    clientSocket.receive(receivePacket);

                    response = "FROM SERVER: ";
                    response += new String(receivePacket.getData(), 0, receivePacket.getLength());
                    consoleOutput.println(response);
                    
                    // returns to listening 
                    gameClient.run();
                    
                    receivedUnicast = false;
                } catch (IOException ex) {
                    consoleOutput.println("IOException caught in ClientDatagramManager.receiveMessage()");
                }
            }         
        }        
    }
}
