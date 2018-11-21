/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientStartup;

import ClientView.HangmanClient;
/**
 *
 * @author silvanzeller
 */
public class Main {
    
    public static void main (String[] args) {
      
        Thread someThread = new Thread(new HangmanClient());
        someThread.start();    
    }
}
