/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.View;

import Common.ClientMethods;
import java.rmi.Naming;
import Common.ServerMethods;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author silvanzeller
 */
public class ClientInterpreter implements Runnable {

    SynchronizedStdOut consoleOut = new SynchronizedStdOut();
    private static boolean ThreadStarted = false;
    private ServerMethods fileServer;
    private boolean Test = true;
    private BufferedReader console;
    private ClientMethods remoteUserObj;
    private long userId;
    private boolean isConnected = false;
    private boolean operationSuccess = false;
    
    private final String HELP_MSG = ""
            + "\nregister <unique username> <password> --> create new user"
            + "\nunregister <your username> <your password>"
            + "\nlogin <your username> <your password> --> login to server"
            + "\nlogout --> logout from server"
            + "\nupload <local filepath> <filename in server> <write permission (true or false)> --> upload file to server"
            + "\ndownload <filename> --> download file from server"
            + "\nlist --> retrieve list of all available files from server"
            + "\nread <filename> --> read a file from server"
            + "\nwrite <filename> --> write to a file if you have the permission"
            + "\ndelete <filename> --> delete a file if you have the permission"
            + "\naccess <filename> --> check if a file is writable"
            + "\nhelp --> prints this help message"
            + "\nexit or quit --> logout and exit the program";

    private final String WELCOME_MSG = "Welcome to this file server application. "
            + "\nHere you find instructions for the possible commands"
            +"\n"+HELP_MSG;
    
    private final String PROMPT = ">>>";
    
    public ClientInterpreter() throws RemoteException {
        remoteUserObj = new ConsoleOutput();
        userId = -1;
    }
                
    @Override
    public void run (){
        
        ThreadStarted = true;
        try {
            fileServer = (ServerMethods) Naming.lookup("//"+ServerMethods.Host+"/"+ServerMethods.ServerName);
        } catch (Exception e){
            consoleOut.println("Could not connect to Server");
        }
        
        consoleOut.println(WELCOME_MSG);
        consoleOut.print(PROMPT);
        
        console = new BufferedReader(new InputStreamReader(System.in));
        String command;
        String [] userData = null;
              
        while(ThreadStarted){
            
            try {
                command = console.readLine();
                command = command.trim();
                userData = command.split(" ");
            } catch (IOException ex) {
                consoleOut.println("IOError occured");
            }
            
            if (userData[0].equalsIgnoreCase("register") && userData.length == 3){
                try {
                    consoleOut.println("before");
                    operationSuccess = fileServer.register(userData[1], userData[2]);
                    consoleOut.println("after");
                    if (operationSuccess == true){
                        consoleOut.println("Registration successful, "+userData[1].trim()+"!");
                    }
                    else {
                        consoleOut.println("Could not register, try different username!");
                    }
                } catch (RemoteException ex) {
                    consoleOut.println("RemoteException");//  | NotBoundException ex| MalformedURLException ex)
                    ex.printStackTrace();
                }
                consoleOut.print(PROMPT);
            }
            
            else if (userData[0].equalsIgnoreCase("unregister") && userData.length == 3){
                try{
                    operationSuccess = fileServer.unregister(userData[1].trim(), userData[2].trim());
                    if (operationSuccess == true){
                        consoleOut.println("You have been deleted from the server!");
                    }
                    else {
                        consoleOut.println("Unregistration failed!");
                    }
                } catch (RemoteException ex) { //  | NotBoundException ex| MalformedURLException ex)
                    ex.printStackTrace();
                }
                consoleOut.print(PROMPT);
            }
            
            else if(userData[0].equalsIgnoreCase("login") && userData.length == 3){
                if(!isConnected){
                    try {
                        userId = fileServer.login(remoteUserObj, userData[1], userData[2]);
                        if (userId == -1){
                            consoleOut.println("Username does not exist or password is wrong!\n");
                        }
                        else if (userId == 0){
                            consoleOut.println("You are already logged in!\n");
                        }
                        else {
                            consoleOut.println("Login successfull");
                            isConnected = true;
                        }
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }         
                }
                else {
                    consoleOut.println("You are already logged in!\n");
                }
                consoleOut.print(PROMPT);
            }
            
            else if(userData[0].equalsIgnoreCase("logout") && userData.length == 1){
                try {
                    operationSuccess = fileServer.logout(userId);
                    if (operationSuccess){
                        consoleOut.println("Logout successful!\n");
                        isConnected = false;
                    }
                    else {
                        consoleOut.println("You have to be logged in in order to logout!\n");
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
                consoleOut.print(PROMPT);    
            }
            
            else if(userData[0].equalsIgnoreCase("list") && userData.length == 1){
                if (isConnected){    
                    List<String> allFiles = new ArrayList<>();
                    try {
                        allFiles = fileServer.list(userId);
                        consoleOut.println("+++The list of files is as follows:+++");
                        for (String s : allFiles){
                            consoleOut.println(s);
                        }
                        consoleOut.println("---END OF LIST---");
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
                
                else {
                    consoleOut.println("You need to connect first!\n");
                }
                consoleOut.print(PROMPT);    
            }
            
            else if (userData[0].equalsIgnoreCase("upload") && userData.length == 4){
                if(isConnected){
                    boolean isUploadable = true;
                    boolean writePermission = false;
                    String localFileAddress = userData[1];
                    String fileName = userData[2];
                    String writePermissionAsString = userData[3];
                    String[] content = readFile(localFileAddress);
                    if (content.equals(null)){
                        consoleOut.println("File in "+userData[1]+" not found!");
                        isUploadable = false;
                        break;
                    }
                    if (!(writePermissionAsString.equalsIgnoreCase("true")) || !(writePermissionAsString.equalsIgnoreCase("false"))){
                        consoleOut.println("Please specify the write permission setting with 'true' or 'false'!");
                        isUploadable = false; 
                        break;
                    }
                    if(isUploadable){
                        try {
                            operationSuccess = fileServer.uploadFile(fileName, content, userId, writePermission);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(operationSuccess){
                        consoleOut.println("Upload successfull!");
                    }
                    else {
                        consoleOut.println("Upload failed!");
                    }
                    
                }
                else {
                    consoleOut.println("You need to connect first!\n");
                }
                consoleOut.print(PROMPT);  
            }
            
            else if (userData[0].equalsIgnoreCase("download") && userData.length ==2 ){
                if(isConnected){
                    String filename = userData[1];
                    String filepath = filename;
                    try {
                        String[] content = fileServer.downloadFile(filename, userId);
                        if (content == null){
                            consoleOut.println("Error downloading the file. Check spelling and accessibility");
                        }
                        else {
                            operationSuccess = storeFile(filepath, content);
                            if (operationSuccess){
                                consoleOut.println("Download successful!");
                            }
                            else{
                                consoleOut.println("Error downloading file!");
                            }
                        }
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    consoleOut.println("You need to connect first!\n");
                }
                consoleOut.print(PROMPT); 
            }
            
            
            else if (userData[0].equalsIgnoreCase("read") && userData.length == 2){
                if (isConnected){
                    String fileName = userData[1];
                    String [] fileContent = null;
                    try {
                        fileContent = fileServer.readFile(fileName, userId);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }

                    if (fileContent == null){
                        consoleOut.println("File "+userData[1]+" does not exist!\n");
                    }
                    else {
                        consoleOut.println("---BEGIN---");
                        for (String s: fileContent){
                            consoleOut.println(s);
                        }
                        consoleOut.println("----END----");
                    }
                }
                else{
                    consoleOut.println("You need to login first!\n");
                }
                consoleOut.print(PROMPT);
            }
            
            else if (userData[0].equalsIgnoreCase("write") && userData.length == 3){
                if (isConnected){    
                    String fileName = userData[1];
                    String method = userData[2];
                    boolean writePermission = false;
                    try {
                        writePermission = fileServer.hasWritePermission(fileName, userId);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                    if(!writePermission){
                        consoleOut.println("You have not the permission to write to this file!\n");
                    }
                    else {
                        consoleOut.println("Write a String to write to the file");
                        consoleOut.println("Write to the file here:\n");
                        consoleOut.print(PROMPT);
                        String input = null;
                        try {
                            input = console.readLine();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        boolean writeSuccessful = false;
                        try {
                            writeSuccessful = fileServer.writeFile(fileName, input, userId, method);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        if (writeSuccessful){
                            consoleOut.println(input+" has been written successfully!\n");
                        }
                        else {
                            consoleOut.println("Write error occured!\n");
                        }
                    }
                } else {
                    consoleOut.println("You need to login first!\n");
                }
                consoleOut.print(PROMPT);
            }  
            
            else if (userData[0].equalsIgnoreCase("delete") && userData.length == 2){
                if(isConnected){
                    String fileName = userData[1];
                    boolean success = false;
                    try {
                        success = fileServer.deleteFile(fileName, userId);
                        if (success){
                            consoleOut.println("Deletion successful!");
                        } else {
                            consoleOut.println("Could not delete file .. ");
                        }
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    consoleOut.println("You need to login first!\n");
                }
                consoleOut.print(PROMPT);
            }
            
            else if (userData[0].equalsIgnoreCase("access") && userData.length == 3){
                if (isConnected){
                    String fileName = userData[1];
                    boolean success = false;
                    try {
                        success = fileServer.hasWritePermission(fileName, userId);
                        if (success){
                            consoleOut.println("You can write to file "+fileName);
                        } else {
                            consoleOut.println("Write permission for "+fileName+" denied or file does not exist");
                        }
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    consoleOut.println("You need to login first!\n");
                }
                consoleOut.print(PROMPT);
            }
                
            else if (userData[0].equalsIgnoreCase("help")){
                consoleOut.println(HELP_MSG);
                consoleOut.print(PROMPT);
            }
                
            else if (userData[0].equalsIgnoreCase("exit") | userData[0].equalsIgnoreCase("quit")){
                if (!isConnected) {
                    consoleOut.println("You have to be logged in in order to logout!\n");
                }                    
                else {
                    try {
                        fileServer.logout(userId);
                    } catch (RemoteException ex) {
                        consoleOut.println("Error occured whilst logging out...");
                        ex.printStackTrace();
                    }
                    isConnected = false;
                }
                ThreadStarted = false;
                consoleOut.println("You terminated the program successfully!\n");
            }
                
            else {
                consoleOut.println("Invalid command. Write 'help' to list available commands!\n" );
                consoleOut.print(PROMPT);
            }    
        }
    }

    public String[] readFile (String location){
        boolean isReadable = true;
        try {
            BufferedReader fromFile = new BufferedReader(new FileReader(location));
            String line = fromFile.readLine();
            String temp;
            while(isReadable){
                if ((temp = fromFile.readLine()) == null){
                    isReadable = false;
                }
                else {
                    line = line +"\n"+temp;
                }
            }
            String[] content = line.split("\n");
            fromFile.close();
            return content;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean storeFile(String filepath, String[] content) {
        File file = new File(filepath);
        try {
            PrintWriter toFile = new PrintWriter(new FileWriter(filepath));
            for (String s : content){
                toFile.println(s);
            }
            toFile.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    
}
