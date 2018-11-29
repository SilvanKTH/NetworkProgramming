/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Controller;

import Common.ServerMethods;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author silvanzeller
 */
public class Controller extends UnicastRemoteObject implements ServerMethods {

    private boolean success = false;
    List<String> allFiles = new ArrayList<>();
    String[] file1 = null;
    String[] file2 = new String [] {"1","2","3"};

    public Controller() throws RemoteException {
    }

    @Override
    public synchronized boolean register(String username, String password) {
        System.out.println(username+" "+password);
        if (username.equals(password)){
            success = false;
        } else{
            success = true;
        }
        return success;
    }
    
    @Override
    public boolean unregister(String username, String password) throws RemoteException {
        System.out.println(username+" "+password);
        if (username.equals(password)){
            success = false;
        } else{
            success = true;
        }
        return success;
    }
    @Override
    public long login(String username, String password) throws RemoteException {
        long userId = -1;
        System.out.println(username+" "+password);
        if (username.equals(password)){
            userId = 0;
        } else{
            userId = 1;
        }
        return userId;       
    }
    
    @Override
    public boolean logout(long userId) throws RemoteException {
        System.out.println(userId);
        if (userId <= 0){
            success = false;
        } else{
            success = true;
        }
        return success;
    }
    
    @Override
    public List<String> list(long userId) throws RemoteException{        
        if (userId <= 0){
            allFiles.add("failure");
        } else{
            allFiles.add("success!");
        }
        return allFiles;
    }
    
    @Override
    public String[] readFile(String filename, long userId) throws RemoteException{
        if (userId > 0){
            return file2;
        } 
        else {
            return file1;
        }
    }
    
    @Override
    public boolean hasWritePermission(String filename, long userId) throws RemoteException{
        System.out.println(userId);
        if (userId <= 0){
            success = false;
        } else{
            success = true;
        }
        return success;
    }
    
    @Override
    public boolean writeFile(String filename, String input) throws RemoteException{
        if (filename.equalsIgnoreCase(input)){
            success = false;
        } else{
            success = true;
        }
        return success;
    }
/*

    //@Override
    //public void upload(String filename, String owner, int size) throws RemoteException {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}

    //@Override
    //public void download(String filename) throws RemoteException {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}
    
*/    
}
