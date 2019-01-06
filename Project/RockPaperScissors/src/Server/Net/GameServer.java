/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Net;

import Common.ThreadSafeStdOut;
import Server.Controller.Controller;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class GameServer implements Runnable{
    
    private ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    private final int BUFFER_SIZE = 1024;
    private Controller controller;
    private static final String DELIMITER = "#";
    private static final String BROADCAST = "BROADCAST";
    
    private DatagramSocket serverSocket;
    
    private byte[] in;
    private byte[] out;
    
    private ArrayList<InetAddress> userIPs = new ArrayList();
    private ArrayList<Integer> userPorts = new ArrayList();
    
    public GameServer() throws SocketException {
        serverSocket = new DatagramSocket(9999);
        this.controller = new Controller();
    }
    
    @Override
    public void run() {
        consoleOutput.println("Started Game Server on port "+serverSocket.getLocalPort());
        while(true){
            try{
                // create byte arrays for sending and receiving
                in = new byte[BUFFER_SIZE];
                out = new byte[BUFFER_SIZE];

                // create receiving datagram packet
                DatagramPacket receivedPacket = new DatagramPacket(in, in.length);
                serverSocket.receive(receivedPacket);               
                
                // extract data from packet, add client IP:port to array list
                String receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                consoleOutput.println("FROM CLIENT: "+receivedMessage+"FROM PORT"+receivedPacket.getPort());
                InetAddress userIP = receivedPacket.getAddress();
                int userPort = receivedPacket.getPort();
                userIPs.add(userIP);
                userPorts.add(userPort);                
                
                // receive answer String from controller
                String serverResponse = controller.getServerResponse(receivedMessage);                
                out = serverResponse.getBytes();
                
                // checks if a message needs to be sent out to all clients
                String[] checkIfBroadcast = serverResponse.split(DELIMITER);
                if(checkIfBroadcast[0].equalsIgnoreCase(BROADCAST)){
                    // sends same message to all clients
                    consoleOutput.println("Sending out broadcast message ..");
                    sendBroadcast(serverResponse);
                } else {               
                    // create outbound datagram packet and send it to single user
                    consoleOutput.println("Sending out unicast message ..");
                    consoleOutput.println("Server response is "+serverResponse);
                    DatagramPacket sendPacket = new DatagramPacket(out, out.length, userIP, userPort);
                    serverSocket.send(sendPacket);
                }               
                
            } catch (IOException ex) {
                consoleOutput.println("IOException in GameServer.run()");
            } 
        }
    } 

    private void sendBroadcast(String serverResponse) {
        consoleOutput.println("in sendBroadcast");
        for (int i = 0; i < userIPs.size(); i++){
            try {
                consoleOutput.println("Server response is: "+serverResponse);
                out = serverResponse.getBytes();
                InetAddress userIP = userIPs.get(i);
                int userPort = userPorts.get(i);
                DatagramPacket sendPacket = new DatagramPacket(out, out.length, userIP, userPort);
                consoleOutput.println("Sending packet to user on socket "+userIP+":"+userPort);
                serverSocket.send(sendPacket);
            } catch (IOException ex) {
                consoleOutput.println("IOException in GameServer.sendBroadcast");
            }
        }
    }
}
