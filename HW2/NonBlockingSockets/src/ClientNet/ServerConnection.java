/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientNet;

import Common.Message;
import Common.MessageType;
import ClientView.HangmanClient.ServerMessageOutput;
import Common.SynchronizedStdOut;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;

/**
 *
 * @author silvanzeller
 */
public class ServerConnection implements Runnable {

    SynchronizedStdOut consoleOut = new SynchronizedStdOut();
    ServerMessageOutput messageHandler;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>(); 
    private volatile boolean connected;
    private SocketChannel socketChannel;
    Selector selector;
    private InetSocketAddress serverAddress;
    private static final int MAX_SIZE = 1024;
    private final ByteBuffer messageFromServer = ByteBuffer.allocateDirect(MAX_SIZE);
    private Message message;
    private volatile boolean timeToSend = false;

    public ServerConnection (ServerMessageOutput messageHandler){
        this.messageHandler = messageHandler;
    }
      
    
    @Override
    public void run() {
        try{
            initSocket();
            initSelector();
            
            while(connected || !messagesToSend.isEmpty()){
                
                if(timeToSend){
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }
                
                selector.select();
                for (SelectionKey key : selector.selectedKeys()){
                    selector.selectedKeys().remove(key);
                    if(!key.isValid()){
                        continue;
                    }
                    if (key.isConnectable()){
                        consoleOut.println("+++key is connectable+++");
                        establishConnection(key);                        
                    } 
                    else if (key.isReadable()){
                        consoleOut.println("+++key is readable+++");
                        receiveMessage();
                    }
                    else if (key.isWritable()){
                        consoleOut.println("+++key is writable+++");
                        sendMessage(key);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            forceDisconnect();
        } catch (Exception ioe){
            
        }
    }

    private void initSocket() throws IOException{
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    private void establishConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteIP = (InetSocketAddress) socketChannel.getRemoteAddress();
            consoleOut.println("+++trying to handle Message+++");
            messageHandler.handleMessage(new Message(MessageType.INFO, "Connected to "+remoteIP.getAddress()+":"+remoteIP.getPort(), true));
        } catch (IOException ioe){
            consoleOut.println("+++could not send message+++ ");
        }      
    }

    private void receiveMessage() throws IOException {
        messageFromServer.clear();
        int count = socketChannel.read(messageFromServer);
        if (count == -1){
            throw new IOException("No server connection");
        }
        message = readFromBuffer(messageFromServer);
        messageHandler.handleMessage(message);
    }

    private void sendMessage(SelectionKey key) throws IOException {
        consoleOut.println("in sendMessage()");
        ByteBuffer buffer;
        synchronized(messagesToSend){
            while((buffer = messagesToSend.peek()) != null){
                consoleOut.println("writing to buffer ..");
                socketChannel.write(buffer);
                if (buffer.hasRemaining()){
                    return;
                }
                messagesToSend.remove();
            }
            if (connected){
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void forceDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        synchronized(this){
            connected = false;
        }
        messageHandler.disconnected();
    }

    private Message readFromBuffer(ByteBuffer byteBuffer) {
        consoleOut.println("in readFromBuffer()");
        byteBuffer.flip();
        byte[] remainingBytes = new byte[messageFromServer.remaining()];
        messageFromServer.get(remainingBytes);
        String messageAsString = new String(remainingBytes);
        Message receivedMessage = new Message(MessageType.INFO);
        String[] messageAsStringArray = messageAsString.split(receivedMessage.getDelimiter());
        consoleOut.println(messageAsString);
        MessageType messageType = MessageType.valueOf(messageAsStringArray[0]);
        consoleOut.println("DEBUG: "+messageType.toString());
        switch(messageType){
            case INFO:
                String info = messageAsStringArray[1];
                receivedMessage = new Message(messageType, info);
                break;
            case GAMEINFO:
                String gameInfo = messageAsStringArray[1];
                String currentWord = messageAsStringArray[2];
                String correctWord = messageAsStringArray[3];
                int remainingAttempts = Integer.valueOf(messageAsStringArray[4]);
                int score = Integer.valueOf(messageAsStringArray[5]);
                boolean isGameRunning = Boolean.valueOf(messageAsStringArray[6]);
                receivedMessage = new Message(messageType, gameInfo, currentWord, correctWord, remainingAttempts, score, isGameRunning);
                break;
        }

        return receivedMessage; 
    }

    public void connect(String host, int port) {
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    public void disconnect() {
        Message disconnectMessage = new Message(MessageType.QUIT);
        prepareMessage(disconnectMessage);
        synchronized(this){
            connected = false;
        }
    }

    public void startGame() {
        if(message != null){
            message = new Message(MessageType.START, message.getScore(), connected);
        }
        else {
            message = new Message(MessageType.START, 0, connected);
        }
        
        consoleOut.println(message.messageType.toString());
        consoleOut.println(Integer.toString(message.getScore()));
        consoleOut.println(Boolean.toString(message.isConnectedToServer()));
        prepareMessage(message);
    }

    public void makeGuess(String guess) {
        if(message != null){
            message = new Message(MessageType.GUESS, guess, message.getCurrentWord(), message.getCorrectWord(), message.getRemainingAttempts(), message.getScore(), message.isGameRunning());
            prepareMessage(message);
        }
    }
    
    private void prepareMessage(Message message){
        StringJoiner sj = new StringJoiner(message.getDelimiter());
        sj.add(message.messageType.toString());
        consoleOut.println("DEBUG "+message.messageType.toString());
        if(message.messageType.equals(MessageType.START)){
            sj.add(Integer.toString(message.getScore()));
            consoleOut.println("DEBUG "+Integer.toString(message.getScore()));
            sj.add(Boolean.toString(message.isConnectedToServer()));
        } else if (message.messageType.equals(MessageType.GUESS)){
            sj.add(message.getMessage());
            consoleOut.println("DEBUG "+message.getMessage());
            sj.add(message.getCurrentWord());
            consoleOut.println("DEBUG "+message.getCurrentWord());
            sj.add(message.getCorrectWord());
            consoleOut.println("DEBUG "+message.getCorrectWord());
            sj.add(Integer.toString(message.getRemainingAttempts()));
            sj.add(Integer.toString(message.getScore()));
            sj.add(Boolean.toString(message.isGameRunning()));
        } else if (message.messageType.toString().equals(MessageType.INFO)){
            String s = message.getMessage();
            if (!s.equals(null)){
                sj.add(message.getMessage());
            }
        }
        wrapMessage(sj);
        timeToSend = true;
        selector.wakeup();
    }

    private void wrapMessage(StringJoiner sj) {
        ByteBuffer buffer = ByteBuffer.wrap(sj.toString().getBytes());
        synchronized (this){
            messagesToSend.add(buffer);
        }
    }    
}
