/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author silvanzeller
 */
public class StringByte {
    public static void main (String [] args) throws UnsupportedEncodingException{
        String s1 = "Test#Test";
        byte[] b = s1.getBytes();
        String s2 = new String(b);
        String s3 = b.toString();
        System.out.println(s2);
        System.out.println(s3);
    }
}
