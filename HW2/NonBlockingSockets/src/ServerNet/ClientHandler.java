/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerNet;

import Common.MessageType;
import Common.SynchronizedStdOut;
import Common.Message;
import ServerController.Controller;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class ClientHandler implements Runnable {
    
    private final SocketChannel clientSocket;
    private final GameServer server;
    private String clientID;
    private final SynchronizedStdOut consoleOutput = new SynchronizedStdOut();
    private LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue();
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque();
    private Controller controller = new Controller();
    private static final int MAX_SIZE = 512;
    private final ByteBuffer messageFromClient = ByteBuffer.allocateDirect(MAX_SIZE);



    public ClientHandler(GameServer server, SocketChannel clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        
        try {
            InetSocketAddress IPaddr = (InetSocketAddress) clientSocket.getRemoteAddress();
            clientID = IPaddr.getAddress().getHostAddress()+":"+IPaddr.getPort();
            consoleOutput.println("connected to "+clientID);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
    }

    @Override
    public void run() {
        try {
            if(!messages.isEmpty()){
                Message message = messages.take();
                
                if (message.getMessageType() == null){
                    Message invalid = new Message(MessageType.INFO, "invalid message received");
                    prepareMessage(invalid);
               } else {
                    switch (message.getMessageType()){
                        case QUIT:
                            disconnect();
                            break;
                        case START:
                            Message start = controller.startNewGame(message);
                            prepareMessage(start);
                            break;
                        case GUESS:
                            if (message.isGameRunning()){
                                Message guess = controller.makeGuess(message);
                                prepareMessage(guess);
                            } else {
                                Message startNewGame = new Message(MessageType.INFO, "Type start for new game", "", message.getRemainingAttempts(), message.getScore(), message.isGameRunning());
                                prepareMessage(startNewGame);
                            }
                            break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private void prepareMessage(Message message) {
        StringJoiner sj = new StringJoiner(message.getDelimiter());
        sj.add(message.getMessageType().toString());
        if(message.getMessage() != null){
            sj.add(message.getMessage());
        }
        if("false".equals(String.valueOf(message.isConnectedToServer()))){
            sj.add(String.valueOf(message.isConnectedToServer()));
            wrapMessage(sj);
        } else {
            if (message.getCurrentWord() != null){
                sj.add(message.getCurrentWord());
            }
            if (message.getCorrectWord().equals(message.getCurrentWord())){
                sj.add(message.getCorrectWord());
                sj.add(String.valueOf(message.getRemainingAttempts()));
                sj.add(String.valueOf(message.getScore()));
                sj.add(String.valueOf(message.isGameRunning()));
            } else {
                sj.add(String.valueOf(message.getRemainingAttempts()));
                sj.add(String.valueOf(message.getScore()));
                sj.add(String.valueOf(message.isGameRunning()));
            }
            wrapMessage(sj);
        }        
    }

    private void wrapMessage(StringJoiner sj) {
        ByteBuffer buffer = ByteBuffer.wrap(sj.toString().getBytes());
        synchronized (this){
            messagesToSend.add(buffer);
        }
        server.readyToSend(clientSocket);
    }

    void disconnect() throws IOException{
        clientSocket.close();
        consoleOutput.println("Disconnecting client "+clientID);
    }

    void sendMessages() throws IOException{
        ByteBuffer message = null;
        synchronized (messagesToSend){
            while((message = messagesToSend.peek()) != null){
                sendMessage(message);
                messagesToSend.remove();
            }
        }
    }
    
    private void sendMessage(ByteBuffer message) throws IOException{
        clientSocket.write(message);
        if(message.hasRemaining()){
            throw new RuntimeException("Buffer to small");
        }
    }

    void handleMessage() throws IOException {
        messageFromClient.clear();
        int readBytes;
        readBytes = clientSocket.read(messageFromClient);
        if (readBytes == -1){
            throw new IOException ("Client closed the connection");
        }
        Message message = readFromBuffer(messageFromClient);
        synchronized(this){
            messages.add(message);
        }
        ForkJoinPool.commonPool().execute(this);
    }

    private Message readFromBuffer(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        byte[] remainingBytes = new byte[messageFromClient.remaining()];
        messageFromClient.get(remainingBytes);
        String messageAsString = StandardCharsets.UTF_8.decode(byteBuffer).toString();
        Message receivedMessage = new Message(MessageType.INFO);
        String [] messageAsStringArray = messageAsString.split(receivedMessage.getDelimiter());
        switch (messageAsStringArray.length) {
            case 1:
                {
                    MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
                    receivedMessage = new Message(messageType);
                    break;
                }
            case 2:
                {
                    MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
                    String message = messageAsStringArray[1];
                    receivedMessage = new Message(messageType, message);
                    break;
                }
            case 3:
                {
                    MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
                    String message = messageAsStringArray[1];
                    boolean connectedToServer = Boolean.valueOf(messageAsStringArray[2]);
                    receivedMessage = new Message(messageType, message, connectedToServer);
                    break;
                }
            case 6:
                {
                    MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
                    String message = messageAsStringArray[1];
                    String currentWord = messageAsStringArray[2];
                    int remainingAttempts = Integer.valueOf(messageAsStringArray[3]);
                    int score = Integer.valueOf(messageAsStringArray[4]);
                    boolean isGameRunning = Boolean.valueOf(messageAsStringArray[5]);        
                    receivedMessage = new Message(messageType, message, currentWord, remainingAttempts, score, isGameRunning);
                    break;
                }
            default:
                {
                    MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
                    String message = messageAsStringArray[1];
                    String currentWord = messageAsStringArray[2];
                    String correctWord = messageAsStringArray[3];
                    int remainingAttempts = Integer.valueOf(messageAsStringArray[4]);
                    int score = Integer.valueOf(messageAsStringArray[5]);
                    boolean isGameRunning = Boolean.valueOf(messageAsStringArray[6]);
                    receivedMessage = new Message(messageType, message, currentWord, correctWord,remainingAttempts, score, isGameRunning);
                    break;
                }
        }
        return receivedMessage; 
    }    
}
