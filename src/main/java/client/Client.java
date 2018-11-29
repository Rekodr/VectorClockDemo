package client;

import clock.VectorClock;
import message.Message;
import message.MessageTypes;

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
            int port = 8000;
            int pid = -1;
            DatagramSocket socket = new DatagramSocket(3000);

            Message msg = new Message(
                    MessageTypes.REGISTER,
                    "ting",
                    0, clock,
                    "register"
            );

            Message.sendMessage( msg, socket, addr, port );

            Message response = Message.receiveMessage( socket );
            if(response.type == MessageTypes.ACK){
                pid = msg.pid;
                System.out.println( "PID: " + pid );
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
