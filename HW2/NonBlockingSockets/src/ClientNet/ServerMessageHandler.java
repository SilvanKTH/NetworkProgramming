/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientNet;

import Common.Message;
/**
 *
 * @author silvanzeller
 */
public interface ServerMessageHandler{
    
    void handleMessage(Message message);
    void disconnected();
    
}
