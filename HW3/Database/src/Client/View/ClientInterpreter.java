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
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    private final String HELP_MSG = "register <unique username> <password> --> create new user"
            + "\nunregister <your username> <your password>"
            + "\nlogin <your username> <your password> --> login to server"
            + "\nlogout --> logout from server"
            + "\nlist --> retrieve list of all available files from server"
            + "\nread <filename> --> read a file"
            + "\nwrite <filename> --> write to a file if you have the permission"
            + "\nhelp --> prints this help message"
            + "\nexit OR quit --> logout and exit the program";

    private final String WELCOME_MSG = "Welcome to this file server application. "
            + "\nHere you find instructions for the possible commands"
            +"\n"+HELP_MSG;
    
    private final String PROMPT = ">>>";
    
    public ClientInterpreter() throws RemoteException {
        remoteUserObj = new ConsoleOutput();
        userId = -1;
    }

    
    
    /*
    @Override
    public void run() {
        ThreadStarted = true;
        try {
            fileServer = (ServerMethods) Naming.lookup("//"+ServerMethods.Host+"/"+ServerMethods.ServerName);
        } catch (Exception e){
            consoleOut.println("Could not connect to Server");
        }
        
        consoleOut.println(WELCOME_MSG);
        
        console = new BufferedReader(new InputStreamReader(System.in));
        String command;
        String [] userData;
                
        while(ThreadStarted){
            consoleOut.print(PROMPT); 
            try {
                command = console.readLine();
                command = command.trim();
                userData = command.split(" ");
                userData[0] = userData[0].toLowerCase();
                
                if(userData[0].equalsIgnoreCase("login") && userData.length == 3){
                    if(!isConnected){
                        userId = fileServer.login(userData[1], userData[2]);
                        if (userId == -1){
                            consoleOut.println("User name does not exist or password is wrong!\n");
                        }
                        else if (userId == 0){
                            consoleOut.println("You are already logged in!\n");
                        }
                        else {
                            consoleOut.println("Login successfull");
                            isConnected = true;
                        }
                    }
                    else {
                        consoleOut.println("You are already logged in!\n");
                    }
                }
                
                else if((userData[0].equalsIgnoreCase("logout") | userData[0].equalsIgnoreCase("quit") | userData[0].equalsIgnoreCase("exit")) && userData.length == 1){
                    operationSuccess = fileServer.logout(userId);
                    if (operationSuccess){
                        consoleOut.println("Logout successful!\n");
                        isConnected = false;
                    }
                    else {
                        consoleOut.println("You have to be logged in in order to logout!\n");
                    }
                } 
                
                else if(userData[0].equalsIgnoreCase("register") && userData.length == 3){
                    operationSuccess = fileServer.register(userData[1].trim(), userData[2].trim());
                    if (operationSuccess){
                        consoleOut.println(userData[0]+" , you are now registered!\n");
                    }
                    else {
                        consoleOut.println("The user "+userData[0]+" is already taken! Try another\n");
                    }
                }
                
                else if(userData[0].equalsIgnoreCase("unregister") && userData.length == 3){
                    operationSuccess = fileServer.unregister(userData[1], userData[2]);
                    if (operationSuccess){
                        consoleOut.println("User "+userData[0]+" has been removed from the server!\n");
                    }
                    else {
                        consoleOut.println("Username or password incorrect!\n");
                    }
                }
                
                else if(userData[0].equalsIgnoreCase("list") && userData.length == 1){
                    if (isConnected){    
                        List<String> allFiles = new ArrayList<String>();
                        allFiles = fileServer.list(userId);
                        consoleOut.println("+++The list of files is as follows:+++");
                        for (String s : allFiles){
                            consoleOut.println(s);
                        }
                    }
                    else {
                        consoleOut.println("You need to connect first!\n");
                    }                    
                }
                
                //////
                ////// IMPLEMENT UPLOAD AND DOWNLOAD FILE HERE
                //////
                
                else if (userData[0].equalsIgnoreCase("read") && userData.length == 2){
                    if (isConnected){
                        String fileName = userData[1];
                        String [] fileContent = fileServer.readFile(fileName, userId);
                        
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
                }
                
                else if (userData[0].equalsIgnoreCase("write") && userData.length >= 2){
                    if (isConnected){    
                        String fileName = userData[1];
                        boolean writePermission = fileServer.hasWritePermission(fileName, userId);
                        if(!writePermission){
                            consoleOut.println("You have not the permission to write to this file!\n");
                        }
                        else {
                            String input = console.readLine();
                            consoleOut.println("Write to the file:\n");
                            consoleOut.print(PROMPT);
                            boolean writeSuccessful = fileServer.writeFile(fileName, input);
                            if (writeSuccessful){
                                consoleOut.println(input+" has been written successfully!\n");
                            }
                            else {
                                consoleOut.println("Write error occured!\n");
                            }
                        }
                    }
                    else {
                        consoleOut.println("You need to login first!\n");
                    }
                }
                
                else if (userData[0].equalsIgnoreCase("help")){
                    consoleOut.println(HELP_MSG);
                }
                
                else if (userData[0].equalsIgnoreCase("exit") | userData[0].equalsIgnoreCase("quit")){
                    if (!isConnected) {
                        consoleOut.println("You have to be logged in in order to logout!\n");
                    }                    
                    else {
                        fileServer.logout(userId);
                        isConnected = false;
                    }
                    ThreadStarted = false;
                    consoleOut.println("You terminated the program successfully!\n");                    
                }    
                
                else {
                    consoleOut.println("Invalid command. Write 'help' to list available commands!\n" );
                }
    
            } catch (IOException ex) {
                consoleOut.println("IOException in ClientInterpreter.run()");
            }
        }
    }
                */
                

    ///BEGINING OF TEST METHOD///            
                
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
            
            try {
                boolean test = fileServer.register(userData[0], userData[1]);
                    if (test == true){
                    consoleOut.println("Successful method return!");
                    }
                    else {
                        consoleOut.println("Method returned unsuccessfully!");
                    }
                } catch (RemoteException ex) { //  | NotBoundException ex| MalformedURLException ex)
                    ex.printStackTrace();
                }
        }
    }
    
    /// END OF TEST METHOD ///
                
                
                
            
    
    
    
}
