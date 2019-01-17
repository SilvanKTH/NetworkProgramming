/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerNet;

import Common.SynchronizedStdOut;
import java.io.IOException;
import java.net.InetSocketAddress;
import static java.net.SocketOptions.SO_TIMEOUT;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silvanzeller
 */
public class GameServer {
    
    private final SynchronizedStdOut consoleOutput = new SynchronizedStdOut();

    private static final int LINGER_TIME = 10000;
    private static final int PORT_NUMBER = 2222;
    private Selector selector;
    private ServerSocketChannel channel;
    
    public void serve() throws IOException{
        consoleOutput.println("+++in serve() method+++");
        try {
            selector = Selector.open();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(PORT_NUMBER));
            channel.register(selector, SelectionKey.OP_ACCEPT);
            consoleOutput.println("+++serve() connectable on "+channel.getLocalAddress());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        while(true){
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while(keys.hasNext()){
                SelectionKey key = keys.next();
                keys.remove();
                if(!key.isValid()){
                    continue;
                }
                if(key.isAcceptable()){
                    consoleOutput.println("+++serve() key is acceptable+++");
                    startHandler(key);
                }
                else if (key.isReadable()){
                    consoleOutput.println("+++serve() key is readable+++");
                    receiveStream(key);
                }
                else if (key.isWritable()){
                    consoleOutput.println("+++serve() key is writable+++");
                    sendStream(key);
                }
            }
        }        
    }

    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel clientSocket = serverSocket.accept();
        clientSocket.configureBlocking(false);
        
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        consoleOutput.println("in startHandler(), created new ClientHandler instance");
        clientSocket.register(selector, SelectionKey.OP_READ, clientHandler);
        clientSocket.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    private void receiveStream(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        //
        consoleOutput.println("+++in receiveStream()+++");
        consoleOutput.println(key.attachment().toString());
        //

        try{
            client.handleMessage();   
        } catch (IOException ioe){
            consoleOutput.println("+++Could not enter handleMessage()+++");
            disconnect(key);
        }
        
    }

    private void sendStream(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        try {            
            client.sendMessages();
            key.interestOps(SelectionKey.OP_READ);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        
    }

    private void disconnect(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        client.disconnect();
        key.cancel();
    }
    
    void readyToSend(SocketChannel clientSocket) {
        clientSocket.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }
    
}
