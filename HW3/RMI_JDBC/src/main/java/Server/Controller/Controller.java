/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Controller;

import Common.ClientMethods;
import Common.ServerMethods;
import Server.Integration.FileDAO;
import Server.Integration.UserDAO;
import Server.Model.FileManager;
import Server.Model.UserInfo;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author silvanzeller
 */
public class Controller extends UnicastRemoteObject implements ServerMethods {
    
    ///TEST FIELD
    List<String> testAllFiles = new ArrayList<>();
    String[] TestFile1 = null;
    String[] TestFile2 = new String [] {"1","2","3"};
    ///END TEST FIELD
    
    private long userId = 0;
    private boolean success = false;
    private final List<UserInfo> allConnectedUsers;
    private final UserDAO userDB;
    private final FileDAO fileDB;
    

    public Controller() throws RemoteException, ClassNotFoundException, SQLException {
        System.out.println("Creating new Controller instance");
        allConnectedUsers = new CopyOnWriteArrayList<>();
        System.out.println("Creating new UserDAO");
        userDB = new UserDAO();
        System.out.println("Creating new FileDAO");
        fileDB = new FileDAO();
    }

//    @Override
//    public synchronized boolean register(String username, String password) {
//        System.out.println(username+" "+password);
//        if (username.equals(password)){
//            success = false;
//        } else{
//            success = true;
//        }
//        return success;
//    }
    
    @Override
    public synchronized boolean register(String username, String password) {
        success = userDB.registerUser(username, password);
        return success;
    }

    @Override
    public synchronized boolean unregister(String username, String password) throws RemoteException{
        success = userDB.unregisterUser(username, password);
        return success;
    }
    
//    @Override
//    public boolean unregister(String username, String password) throws RemoteException {
//        System.out.println(username+" "+password);
//        if (username.equals(password)){
//            success = false;
//        } else{
//            success = true;
//        }
//        return success;
//    }
    
    @Override
    public synchronized long login(ClientMethods user, String username, String password) throws RemoteException{
        UserInfo userInfo = new UserInfo();
        userId = userDB.loginDB(user, username, password);
        for (UserInfo userEntity : allConnectedUsers){
            if (userEntity.userId == userId && userEntity.connected){
                return 0;
            }
        }
        if (userId > 0){
            userInfo.connected = true;
            userInfo.userId = userId;
            allConnectedUsers.add(userInfo);
        }
        return userId;
    }
    
//    @Override
//    public long login(String username, String password) throws RemoteException {
//        long userId = -1;
//        System.out.println(username+" "+password);
//        if (username.equals(password)){
//            userId = 0;
//        } else{
//            userId = 1;
//        }
//        return userId;       
//    }
    
    @Override
    public synchronized boolean logout(long userId) throws RemoteException {
        for (UserInfo userEntity : allConnectedUsers){
            if (userEntity.userId == userId && userEntity.connected){
                userEntity.connected = false;
                allConnectedUsers.remove(userEntity);
                success = true;
                if(allConnectedUsers.isEmpty()){
                    break;
                }
            }
            else {
                success = false;
            }
        } 
        return success;
    } 
    
    
//    @Override
//    public boolean logout(long userId) throws RemoteException {
//        System.out.println(userId);
//        if (userId <= 0){
//            success = false;
//        } else{
//            success = true;
//        }
//        return success;
//    }

    @Override
    public synchronized List<String> list(long userId) throws RemoteException {
        List<String> listOfFiles = new ArrayList<>();
        listOfFiles = fileDB.getList(userId);
        return listOfFiles;
    }       
    
//    @Override
//    public List<String> list(long userId) throws RemoteException{        
//        if (userId <= 0){
//            testAllFiles.add("failure");
//        } else{
//            testAllFiles.add("success!");
//        }
//        return testAllFiles;
//    }

    @Override
    public synchronized String[] readFile(String filename, long userId) throws RemoteException{
        FileManager fileManager = new FileManager();
        String user = Long.toString(userId);
        boolean canReadFile = fileDB.getFile(filename, user);
        if (canReadFile){
            String[] content = fileManager.downloadFile(user);
            String ownerId = fileDB.notifyOwner(filename, user);
            if(!ownerId.isEmpty()){
                for(UserInfo userEntity : allConnectedUsers){
                    if(userEntity.userId == Long.parseLong(ownerId) && userEntity.connected){
                        userEntity.remoteUser.messageOnScreen(""
                                + "\n+++User "+user+" read your file "+filename+"+++");
                        break;
                    }
                }
            }
            return content;
        }
        return null;
    }


    
//    @Override
//    public String[] readFile(String filename, long userId) throws RemoteException{
//        if (userId > 0){
//            return TestFile2;
//        } 
//        else {
//            return TestFile1;
//        }
//    }
    
    @Override
    public synchronized boolean hasWritePermission(String filename, long userId) throws RemoteException{
        String userIdString = Long.toString(userId);
        success = fileDB.hasWritePermission(filename, userIdString);
        return success;
    }
    
    @Override
    public synchronized boolean writeFile(String filename, String input, long userId, String method) throws RemoteException{
        FileManager fileManager = new FileManager();
        String user = Long.toString(userId);
        boolean canWrite = fileDB.writeFile(filename, user);
        if(canWrite){
            boolean writeSuccessful = fileManager.writeToFile(user, input, method);
            if(writeSuccessful){
                String ownerId = fileDB.notifyOwner(filename, user);
                if(!ownerId.isEmpty()){
                    for(UserInfo userEntity : allConnectedUsers){
                        if(userEntity.connected){
                            userEntity.remoteUser.messageOnScreen(""
                                    + "\n+++User "+user+" wrote your file "+filename+"+++");
                            break;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public synchronized boolean uploadFile(String filename, String[] content, long userId, boolean writePermission){
        FileManager fileManager = new FileManager();
        double fileSize = (double) fileManager.uploadFile(filename, content);
        String user = Long.toString(userId);
        success = fileDB.uploadFile(filename, fileSize, user, writePermission);
        if(success){
            return true;
        }
        return false;
    }
    
    @Override
    public String[] downloadFile(String filename, long userId) throws RemoteException {
        FileManager fileManager = new FileManager();
        String user = Long.toString(userId);
        boolean canDownload = fileDB.getFile(filename, user);
        if (canDownload){
            String[] content = fileManager.downloadFile(filename);
            return content;
        }
        return null;
    }
    
    @Override
    public boolean deleteFile(String filename, long userId) throws RemoteException {
        FileManager fileManager = new FileManager();
        String user = Long.toString(userId);
        boolean canDelete = fileDB.deleteFile(filename, user);
        if (canDelete){
            fileManager.deleteFile(filename);
            return true;
        }
        return false;
    }
}
