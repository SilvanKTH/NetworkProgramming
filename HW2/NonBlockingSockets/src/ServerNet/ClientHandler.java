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
    private LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();
    private Controller controller = new Controller();
    private static final int MAX_SIZE = 1024;
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
        //
        consoleOutput.println("+++in ClientHandler run()+++");
        //
        try {
            if(!messages.isEmpty()){
                Message message = messages.take();
                consoleOutput.println(message.toString());
                if (message.messageType.toString() == null){
                    Message invalid = new Message(MessageType.INFO, "invalid message received");
                    prepareMessage(invalid);
               } else {
                    switch (message.messageType.toString()){
                        case "QUIT":
                            //
                            consoleOutput.println("+++messageType = QUIT");
                            //
                            disconnect();
                            break;
                        case "START":
                            consoleOutput.println("+++messageType = START");
                            Message start = controller.startNewGame(message);
                            prepareMessage(start);
                            break;
                        case "GUESS":
                            consoleOutput.println("+++messageType = GUESS");
                            if (message.isGameRunning()){
                                Message guess = controller.makeGuess(message);
                                prepareMessage(guess);
                            } else {
                                Message startNewGame = new Message(MessageType.GAMEINFO, "Type start for new game", "", "", 0, message.getScore(), message.isGameRunning());
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
        sj.add(message.messageType.toString());
        
        if (message.messageType.equals(MessageType.INFO)){
            sj.add(message.getMessage());
            consoleOutput.println("DEBUG "+message.getMessage());
        } else if (message.messageType.equals(MessageType.GAMEINFO)){
            sj.add(message.getMessage());
            consoleOutput.println("DEBUG "+message.getMessage());
            sj.add(message.getCurrentWord());
            consoleOutput.println("DEBUG "+message.getCurrentWord());
            sj.add(message.getCorrectWord());
            consoleOutput.println("DEBUG "+message.getCorrectWord());
            sj.add(Integer.toString(message.getRemainingAttempts()));
            sj.add(Integer.toString(message.getScore()));
            sj.add(Boolean.toString(message.isGameRunning()));
        }
        wrapMessage(sj);
    }

    private void wrapMessage(StringJoiner sj) {
        ByteBuffer buffer = ByteBuffer.wrap(sj.toString().getBytes());
        synchronized (this){
            messagesToSend.add(buffer);
        }
        server.readyToSend(clientSocket);
    }

    void disconnect() throws IOException{
        //
        consoleOutput.println("+++in disconnect() method+++");
        //
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
        consoleOutput.println("+++in handleMessage()+++");
        messageFromClient.clear();
        consoleOutput.println("+++before int readBytes+++");
        int readBytes = clientSocket.read(messageFromClient);
        if (readBytes == -1){
            consoleOutput.println("+++readBytes = -1+++");
            throw new IOException ("Client closed the connection");
        }
        consoleOutput.println("+++before readFromBuffer()+++");
        Message message = readFromBuffer(messageFromClient);
        synchronized(this){
            messages.add(message);
        }
        ForkJoinPool.commonPool().execute(this);
    }

    private Message readFromBuffer(ByteBuffer byteBuffer) {
        consoleOutput.println("in readFromBuffer() method");
        byteBuffer.flip();
        byte[] remainingBytes = new byte[messageFromClient.remaining()];
        messageFromClient.get(remainingBytes);
        String messageAsString = new String(remainingBytes);
        Message receivedMessage = new Message(MessageType.INFO);
        String [] messageAsStringArray = messageAsString.split(receivedMessage.getDelimiter());
        consoleOutput.println(messageAsString);
        MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
        consoleOutput.println("DEBUG: "+messageType.toString());

        switch (messageType) {
            case QUIT:
                {
                    receivedMessage = new Message(messageType);
                    break;
                }
            case START:
                {
                    int score = Integer.valueOf(messageAsStringArray[1]);
                    boolean isConnected = Boolean.valueOf(messageAsStringArray[2]);
                    receivedMessage = new Message(messageType, score, isConnected);
                    break;
                }
            case GUESS:
                {
                    String guess = messageAsStringArray[1];
                    String currentWord = messageAsStringArray[2];
                    String correctWord = messageAsStringArray[3];
                    int remainingAttempts = Integer.valueOf(messageAsStringArray[4]);
                    int score = Integer.valueOf(messageAsStringArray[5]);
                    boolean gameRunning = Boolean.valueOf(messageAsStringArray[6]);
                    receivedMessage = new Message(messageType, guess, currentWord, correctWord, remainingAttempts, score, gameRunning);
                    break;
                }
        }
        return receivedMessage; 
    }    
}
