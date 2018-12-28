package com.example.silvanzeller.Net;

import java.io.BufferedReader;
import java.io.IOException;

public class Listener implements Runnable{

    private BufferedReader fromServer;
    private OutputHandler outputHandler;
    private String message;

    public Listener(OutputHandler outputHandler, BufferedReader fromServer){
        this.outputHandler = outputHandler;
        this.fromServer = fromServer;
    }

    @Override
    public void run() {
        for(;;){
            try {
                message = fromServer.readLine();
                outputHandler.messageOnScreen(message);
            } catch (IOException e) {
                System.out.println("Connection lost!");
                e.printStackTrace();
            }
        }
    }
}
