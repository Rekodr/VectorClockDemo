package client;

import clock.VectorClock;
import message.Message;
import message.MessageComparator;
import org.json.JSONException;
import org.json.JSONObject;
import queue.PriorityQueue;

import java.net.DatagramSocket;

public class MessageListener implements Runnable {

    private DatagramSocket socket;
    private int server_port;
    private VectorClock clock;
    PriorityQueue<Message> queue;

    MessageListener(DatagramSocket _socket, int _server_port, VectorClock _clock) {
        this.socket = _socket;
        this.server_port = _server_port;
        this.clock = _clock;

        queue = new PriorityQueue<Message>( new MessageComparator() );
    }

    public static boolean first_condition(Message message, VectorClock clk) {
        if(message.ts.getTime( message.pid ) == clk.getTime( message.pid ) + 1)
            return true;

        System.out.println( "first failed cm: " + message.ts.getTime( message.pid ) + " cj: " + clk.getTime( message.pid ) );
        return false;
    }

    public static boolean seconde_condition(Message message, VectorClock clk) {
        try {
            JSONObject c1 = new JSONObject( message.ts.toString());
            JSONObject c2 = new JSONObject( clk.toString() );
            c1.remove( Integer.toString( message.pid ) );
            c2.remove( Integer.toString( message.pid ) );

            VectorClock cm = new VectorClock();
            cm.setClockFromString( c1.toString() );

            VectorClock cj = new VectorClock();
            cj.setClockFromString( c2.toString() );

            if(cm.happenedBefore( cj ))
                return true;


        } catch (JSONException e) {
            e.printStackTrace();
        }
//        System.out.println( "failed" );
        return false;
    }

    public void run() {
        Message response = null;
        System.out.println( "Listening..." );
        
        while(!Thread.currentThread().isInterrupted()) {
            response = Message.receiveMessage( socket );

            if(response != null) {
                System.out.println( response.message + " " + response.ts.toString() );
                queue.add( response );
            }
            Message top = queue.peek();

//            System.out.println( response.ts.toString() );
            while(top != null) {

                if(first_condition( top, clock ) && seconde_condition( top, clock )) {
                    System.out.println( top.message );
                    queue.poll();
                    clock.update( top.ts );
                    top = queue.peek();
                } else {
                    top = null;
                }
            }
        }
    }
}
