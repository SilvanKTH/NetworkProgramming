/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Startup;

import Server.Controller.Controller;
import Common.ServerMethods;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author silvanzeller
 */
public class Startup {
    
    
    public static void main (String [] args) throws RemoteException, MalformedURLException{
        
        new Startup().startDB();
        try {
            System.out.println("Trying to create new controller instance");
            Controller fileServerController = new Controller();
            System.out.println("Created new controller instance");
            Naming.rebind(Controller.ServerName, fileServerController);  
            System.out.println("Server is running ...");
        } catch (Exception e){
            System.out.println("Could not start up server ...");
        }   
    }
    
    private void startDB () throws RemoteException{
        try {
            LocateRegistry.getRegistry().list();
            System.out.println("Fetching registry");
        } catch (RemoteException re){ 
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("Creating registry");
        }
    }
        
    
}
