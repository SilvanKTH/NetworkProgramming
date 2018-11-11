/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.client.clientStartup;

import tcpsockets.client.clientView.NonBlockingInterpreter;

/**
 *
 * @author silvanzeller
 */
public class Main {
    
    
    public static void main(String [] args){        
        NonBlockingInterpreter nbi = new NonBlockingInterpreter();
        nbi.start();
    }
    
}
