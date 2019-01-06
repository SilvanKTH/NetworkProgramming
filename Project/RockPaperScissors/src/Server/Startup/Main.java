/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Startup;

import Common.ThreadSafeStdOut;
import Server.Net.GameServer;
import java.net.SocketException;

/**
 *
 * @author silvanzeller
 */
public class Main {
    
    private static ThreadSafeStdOut consoleOutput = new ThreadSafeStdOut();
    
    public static void main (String[] args) throws SocketException{
        consoleOutput.println("Starting Game Server ...");
        new Thread(new GameServer()).start();
    }
    
}
