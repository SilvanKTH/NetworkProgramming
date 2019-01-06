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
public class ThreadSafeStdOut {
    
    public synchronized void print(String output){
        System.out.print(output);
    }
    
    public synchronized void println(String output){
        System.out.println(output);
    }
    
}
