/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerNet;

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
    
    private static final int LINGER_TIME = 10000;
    private static final int PORT_NUMBER = 2222;
    private Selector selector;
    private ServerSocketChannel channel;
    
    public void serve() throws IOException{
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
                    startHandler(key);
                }
                else if (key.isReadable()){
                    receiveStream(key);
                }
                else if (key.isWritable()){
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
        clientSocket.register(selector, SelectionKey.OP_READ, clientHandler);
        clientSocket.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    private void receiveStream(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        try{
            client.handleMessage();   
        } catch (IOException ioe){
            disconnect(key);
        }
             
    }

    private void sendStream(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        client.sendMessages();
        key.interestOps(SelectionKey.OP_READ);
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
