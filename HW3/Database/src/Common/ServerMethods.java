/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
 *
 * @author silvanzeller
 */
public interface ServerMethods extends Remote {
    
    public static final String ServerName = "FileServer";
    public static final String Host = "localhost:1099";
    
    public boolean register(String username, String password) throws RemoteException;
    public boolean unregister(String username, String password) throws RemoteException;
    public long login(String username, String password) throws RemoteException;
    public boolean logout(long userId) throws RemoteException;
    public List<String> list(long userId) throws RemoteException;
    public String[] readFile(String filename, long userId) throws RemoteException;
    public boolean hasWritePermission(String filename, long userId) throws RemoteException;
    public boolean writeFile(String filename, String input) throws RemoteException;
    //public void notifyClient() throws RemoteException;
    //public void upload(String filename, String owner, int size) throws RemoteException;
    //public void download(String filename) throws RemoteException;

    

}