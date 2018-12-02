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
    public long login(ClientMethods user, String username, String password) throws RemoteException;
    public boolean logout(long userId) throws RemoteException;
    public List<String> list(long userId) throws RemoteException;
    public String[] readFile(String filename, long userId) throws RemoteException;
    public boolean hasWritePermission(String filename, long userId) throws RemoteException;
    public boolean writeFile(String filename, String input, long userId, String method) throws RemoteException;
    public String [] downloadFile(String filename, long userId) throws RemoteException;
    public boolean uploadFile(String filename, String[] content, long userId, boolean writePermission) throws RemoteException;
    public boolean deleteFile(String filename, long userId) throws RemoteException;

}