/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

/**
 *
 * @author silvanzeller
 */
public class SynchronizedStdOut {
    
    public synchronized void print(String message){
        System.out.print(message);
    }
    
    public synchronized void println(String message){
        System.out.println(message);
    }
    
}
