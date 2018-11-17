/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.StringJoiner;

/**
 *
 * @author silvanzeller
 */
public class StringJoinerTest {
    
    public static void main (String[]args){
        String texta = "blah";
        String textb = "bloh";
        String textc = "bleh";
        String delim = "#";
    
        StringJoiner Join = new StringJoiner(delim);

        Join.add(texta);
        
        if(textb == null){
            Join.add(textb);
        }
    
        if (!"bleh".equals(textc)){
            Join.add(textc);
        }
        System.out.println(Join);
        
        
        String JoinString = Join.toString();
        String[] split = JoinString.split(delim);
        for (int i = 0; i < split.length; i++){
            System.out.println(split[i]);
        }

    }    
    
        
    
    
    
}
