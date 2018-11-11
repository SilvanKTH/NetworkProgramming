/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.server.serverNet;

import tcpsockets.common.MessageType;
/**
 *
 * @author silvanzeller
 */
class PlayerMessage {
    
    private String fullMessage;
    private String messageBody;
    private MessageType messageType;

    PlayerMessage(String fullMessage) {
        this.fullMessage = fullMessage;
        processMessage(fullMessage);
    }

    MessageType getMessageType() {
        return messageType;
    }
    
    String getMessageBody(){
        return messageBody;
    }

    private void processMessage(String fullMessage) {
        String[] message = fullMessage.split("\\s");
        switch (message[0].toUpperCase()){
            case "START":
                messageType = messageType.START;
                break;
            case "GUESS":
                messageType = messageType.GUESS;
                messageBody = message[1].toLowerCase();
                break;
            case "QUIT":
                messageType = messageType.QUIT;
                break;
            default:
                System.out.println("Errornous message type received");
        }
    }
    
}
