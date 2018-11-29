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

            BufferedReader input_reader = new BufferedReader(new InputStreamReader(System.in));


            VectorClock clock = new VectorClock();
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            int pid = -1;
            int server_port = 8000;

            System.out.println( "Enter your username: " );
            String username = input_reader.readLine();

            if(username.length() == 0) {
                System.out.println( "Invalid Username" );
                System.exit( 1 );
            }

            System.out.println( "Provide a port" );
            int my_port = Integer.parseInt( input_reader.readLine()) ;

            DatagramSocket socket = new DatagramSocket(my_port);


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
                socket.setSoTimeout(350);
                pid = response.pid;
                System.out.println( "Assigned PID: " + pid );
                clock.update( response.ts );
                System.out.println( clock.toString() );
                clock.addProcess( pid, 0 );
                MessageListener listener = new MessageListener( socket, server_port, clock );
                Thread thread = new Thread( listener );
                thread.start();

                while(true){
                    try {
                        String in = input_reader.readLine();
                        clock.tick( pid );
                        if(in.length() != 0) {
                            String text = "[" + username + "] " + in  ;
                            msg = new Message(
                                MessageTypes.CHAT_MSG,
                                username,
                                pid,
                                clock,
                                text
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
