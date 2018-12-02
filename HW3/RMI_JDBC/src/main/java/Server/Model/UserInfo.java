/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Model;

import Common.ClientMethods;

/**
 *
 * @author silvanzeller
 */
public class UserInfo {

    public long userId;
    public String username;
    public String password;
    public ClientMethods remoteUser;
    public boolean connected;
    
    public UserInfo(){
        userId = 0;
        remoteUser = null;
        username = null;
        password = null;  
        connected = false;
    }
    
    public UserInfo(ClientMethods remoteUser, String username, String password){
        userId = 0;
        this.remoteUser = remoteUser;
        this.username = username;
        this.password = password;
        connected = false;
    }
    
}
