/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author silvanzeller
 */
public interface ClientMethods extends Remote {
    
    void messageOnScreen(String message) throws RemoteException;
    
}
