///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ClientController;
//
//import ClientNet.ServerConnection;
//import ClientNet.ServerResponse;
//import ClientView.HangmanClient;
//import ClientView.HangmanClient.ServerMessageOutput;
//
///**
// *
// * @author silvanzeller
// */
//public class Controller {
//    
//    //ServerResponse serverMessageHandler;
//    ServerMessageOutput serverMessageOutput;
//    
//    private ServerConnection sc;
//
//    public Controller(ServerMessageOutput serverMessageOutput) {
//        this.serverMessageOutput = serverMessageOutput;
//        this.sc = new ServerConnection(serverMessageOutput);
//    }
//
//    public void connect(String host, int port) {
//        sc.connect(host, port);
//    }
//
//    public void disconnect() {
//        sc.disconnect();
//    }
//
//    public void startGame() {
//        sc.startGame();
//    }
//
//    public void makeGuess(String guess) {
//        sc.makeGuess(guess);
//    }
//    
//}
