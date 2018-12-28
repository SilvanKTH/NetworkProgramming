package com.example.silvanzeller.Controller;

import java.io.IOException;

import com.example.silvanzeller.Net.OutputHandler;
import com.example.silvanzeller.Net.SocketHandler;

public class ClientController {

    private final SocketHandler clientSocket = new SocketHandler();
    private OutputHandler outputHandler;
    private int serverPort;
    private String cmd;

    public void connect(final int serverPort, final OutputHandler outputHandler){
        this.serverPort = serverPort;
        this.outputHandler = outputHandler;
        new Thread(new Runnable() {
            @Override
            public void run() {
                clientSocket.connect(serverPort, outputHandler);
            }
        }).start();
    }

    public void disconnect(){
        clientSocket.disconnect();
    }

    public void sendMessage(String command) throws IOException {
        cmd = command;
        new Thread(new Runnable() {
            @Override
            public void run() {
                clientSocket.sendMessage(cmd);
            }
        }).start();
    }
}
