/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpsockets.client.clientView;

/**
 *
 * @author silvanzeller
 */
public class ProcessCommand {
    private Commands messageType;
    private String messageBody;
    private String fullMessage;
    
    ProcessCommand(String fullMessage){
        this.fullMessage = fullMessage;
        processMessage();
    } 


    String getMessageBody() {
        return messageBody;
    }

    Commands getCommand() {
        return messageType;
    }

    private void processMessage() {
        String[] words = fullMessage.split("\\s");
        switch (words[0].toUpperCase()){
            case "CONNECT":
                messageType = Commands.CONNECT;
                if (words.length != 1){
                    messageBody = null;
                }
                else {
                    messageBody = "";
                }
                break;
            case "START":
                messageType = Commands.START;
                if (words.length != 1){
                    messageBody = null;
                }
                else {
                    messageBody = "";
                }
                break;
            case "GUESS":
                messageType = Commands.GUESS;
                if (words.length != 2){
                    messageBody = null;
                }
                else {
                    messageBody = words[1];
                }
                break;
            case "QUIT":
                messageType = Commands.QUIT;
               if (words.length != 1){
                   messageBody = null;
               } 
               else {
                   messageBody = "";
               }
               break;
            default:
                messageType = Commands.WRONG_COMMAND;
                messageBody = null;
                break;
        }
    }
    
}
