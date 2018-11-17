/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientView;

/**
 *
 * @author silvanzeller
 */
class CommandLine {
    String DELIMITER = " ";
    private Command command;
    private String[] args;   

    CommandLine(String input) {
        getCommandType(input);
        getMessageParameters(input);
    }
    
    private void getCommandType(String input){
        try {
            String[] args = input.split(DELIMITER);
            command = Command.valueOf(args[0].toUpperCase());
        } catch (Exception e){
            command = Command.GUESS;
        }        
    }
    
    private void getMessageParameters(String input){
        args = removeCommand(input).split(DELIMITER);
    }
    
    private String removeCommand(String input){
        if(command != command.CONNECT){
            return input;
        }
        int commandLength = command.toString().length() +1;
        String onlyArgs = input.substring(commandLength);
        return onlyArgs;
    }

    public Command getCommand() {
        return command;
    }

    String[] getArgs() {
        return args;
    }
    
}
