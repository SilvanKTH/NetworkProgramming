/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientNet;

import Common.Message;
import Common.MessageType;
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

    ServerResponse messageHandler;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>(); 
    private volatile boolean connected;
    private SocketChannel socketChannel;
    Selector selector;
    private InetSocketAddress serverAddress;
    private static final int MAX_SIZE = 512;
    private final ByteBuffer messageFromServer = ByteBuffer.allocate(MAX_SIZE);
    private Message message = null;
    private volatile boolean timeToSend = false;

    public ServerConnection (ServerResponse messageHandler){
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
                        establishConnection(key);                        
                    } 
                    else if (key.isReadable()){
                        receiveMessage();
                    }
                    else if (key.isWritable()){
                        sendMessage(key);
                    }
                }
            }
        } catch (Exception ioe){
            
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
        InetSocketAddress remoteIP = (InetSocketAddress) socketChannel.getRemoteAddress();
        messageHandler.handleMessage(new Message(MessageType.INFO, "Connected to "+remoteIP.getAddress().getHostAddress()+":"+remoteIP.getPort(), true));        
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
        ByteBuffer buffer;
        synchronized(messagesToSend){
            while((buffer = messagesToSend.peek()) != null){
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
    }

    private Message readFromBuffer(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        byte[] remainingBytes = new byte[messageFromServer.remaining()];
        messageFromServer.get(remainingBytes);
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
            case 7:
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
        prepareMessage(message);
    }

    public void makeGuess(String guess) {
        if(message != null){
            message = new Message(MessageType.GUESS, guess, message.getCurrentWord(), message.getCorrectWord(), message.getRemainingAttempts(), message.getScore(), message.isGameRunning());
            prepareMessage(message);
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
