package client;

import clock.VectorClock;
import message.Message;
import message.MessageTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Client {

//    int type, String sender, int pid, VectorClock ts, String message)
    public static void main(String[] args) {
        try {

            VectorClock clock = new VectorClock();
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            int server_port = 8000;
            int pid = -1;
            DatagramSocket socket = new DatagramSocket(3000);

            String username = "Ting";

            Message msg = new Message(
                MessageTypes.REGISTER,
                username,
                0,
                clock,
                "register"
            );

            Message.sendMessage( msg, socket, addr, server_port );

            Message response = Message.receiveMessage( socket );
            if(response.type == MessageTypes.ACK){
                pid = msg.pid;
                System.out.println( "Assigned PID: " + pid );
                MessageListener listener = new MessageListener( socket, server_port, pid );
                Thread thread = new Thread( listener );
                thread.start();
                BufferedReader input_reader = new BufferedReader(new InputStreamReader(System.in));

                while(true){
                    try {
                        String in = input_reader.readLine();
                        if(in.length() != 0) {
                            msg = new Message(
                                MessageTypes.CHAT_MSG,
                                username,
                                pid,
                                clock,
                                in
                            );

                            Message.sendMessage(msg, socket, addr, server_port );
                        }
                    } catch (IOException e) {
                        System.out.println( "System Error, try again." );
                    }
                }
            } else {
                System.out.println( "Error: " + response.message );
                System.exit( 1 );
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
