package client;

import message.Message;

import java.net.DatagramSocket;

public class MessageListener implements Runnable {

    private DatagramSocket socket;
    private int server_port;
    private int pid;
    MessageListener(DatagramSocket _socket, int _server_port, int _pid) {
        this.socket = _socket;
        this.server_port = _server_port;
        this.pid = _pid;
    }

    public void run() {
        Message response = null;
        System.out.println( "Listening..." );

        
        while(!Thread.currentThread().isInterrupted()) {
            response = Message.receiveMessage( socket );
            System.out.println( response.message );
        }
    }
}
