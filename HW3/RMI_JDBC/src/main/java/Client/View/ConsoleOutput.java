/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.View;

import Common.ClientMethods;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author silvanzeller
 */
public class ConsoleOutput extends UnicastRemoteObject implements ClientMethods {

    SynchronizedStdOut consoleOut = new SynchronizedStdOut();
    
    public ConsoleOutput() throws RemoteException {
        
    }
    
    @Override
    public void messageOnScreen(String message) throws RemoteException {
        consoleOut.println(message);
    }
    
}
