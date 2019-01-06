/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.Startup;

import Client.Net.ClientDatagramManager;
import Client.View.GameClient;
import Common.ThreadSafeStdOut;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author silvanzeller
 */
public class Main {
    
    private static ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    
    public static void main (String[] args) throws SocketException, UnknownHostException{
        consoleOutput.println("starting new client ..");
        new Thread(new GameClient()).start();
    }
    
}
