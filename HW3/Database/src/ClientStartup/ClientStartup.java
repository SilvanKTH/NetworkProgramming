/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientStartup;

import Client.View.ClientInterpreter;
import java.rmi.RemoteException;

/**
 *
 * @author silvanzeller
 */
public class ClientStartup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException{
        Thread userThread = new Thread(new ClientInterpreter());
        userThread.start();
    }
    
    
}
