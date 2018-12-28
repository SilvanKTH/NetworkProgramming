package com.example.silvanzeller.Net;

import android.content.SyncStatusObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketHandler {

    private Socket clientSocket;
    private InetSocketAddress serverPort;

    private boolean connected = false;

    private PrintWriter toServer;
    private BufferedReader fromServer;
    private Thread listenerThread;

    private final int SO_LINGER_TIME = 10000;
    private final int SO_TIMEOUT_TIME = 100000;

    private final String IP = "10.0.2.2";

    public void connect(int port, OutputHandler screenInterface){
        try {
            clientSocket = new Socket();
            clientSocket.setSoLinger(true, SO_LINGER_TIME);
            clientSocket.setSoTimeout(SO_TIMEOUT_TIME);
            serverPort = new InetSocketAddress(IP, port);
            clientSocket.connect(serverPort);
            connected = true;
            toServer = new PrintWriter(clientSocket.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            listenerThread = new Thread(new Listener(screenInterface, fromServer));
            listenerThread.start();
        } catch (SocketException e) {
            System.out.println("Error in creating socket connection!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error in IO handling!");
            e.printStackTrace();
        }
    }

    public void disconnect(){
        if(connected){
            toServer.println("Client is quitting game");
            try {
                clientSocket.close();
                connected = false;
            } catch (IOException e) {
                System.out.println("Error in closing socket connection!");
                e.printStackTrace();
            }
        } else {
            System.out.println("You are not connected to the server!");
        }
    }

    public void sendMessage(String guess){
        if(connected){
            toServer.println(guess);
        } else {
            System.out.println("You are not connected to the server!");
        }
    }

}
