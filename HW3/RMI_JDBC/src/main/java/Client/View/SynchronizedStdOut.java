/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client.View;

/**
 *
 * @author silvanzeller
 */
public class SynchronizedStdOut {
    synchronized void print(String consoleOut) {
        System.out.print(consoleOut);
    }
    synchronized void println(String consoleOut){
        System.out.println(consoleOut);
    }
}
