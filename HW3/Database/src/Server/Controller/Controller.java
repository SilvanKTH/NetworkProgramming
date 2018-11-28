/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Controller;

import Common.ServerMethods;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 *
 * @author silvanzeller
 */
public class Controller extends UnicastRemoteObject implements ServerMethods {

    private boolean success = false;

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
/*
    @Override
    public long login(String username, String password) throws RemoteException {
        return 0;
    }

    //@Override
    //public void upload(String filename, String owner, int size) throws RemoteException {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}

    //@Override
    //public void download(String filename) throws RemoteException {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}

    @Override
    public boolean unregister(String username, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean logout(long userId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> list(long userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] readFile(String filname, long userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean writeFile(String filename, String input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasWritePermission(String filename, long userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/    
}
