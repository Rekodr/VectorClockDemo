package clock;

import org.json.JSONException;
import org.json.JSONObject;
import sun.rmi.server.InactiveGroupException;

import javax.print.attribute.standard.RequestingUserName;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class VectorClock implements Clock {

    // suggested data structure ...
    private Map<String,Integer> clock = new Hashtable<String,Integer>();


    public void update(Clock other) {
        boolean is_valid = true;
        Map<String,Integer> temp_clock = strToClock( other.toString() );
        for(String key : temp_clock.keySet()) {
            Integer curr = this.clock.getOrDefault( key, -1 );
            if (curr < temp_clock.get( key )) {
                this.clock.put( key, temp_clock.get( key ) );
            }
        }

    }

    public void setClock(Clock other) {
        for(Map.Entry<String, Integer> entry : clock.entrySet()) {
            Integer other_time = other.getTime( Integer.parseInt( entry.getKey() ) );

            if (other_time != null) {
                Integer local_time = entry.getValue();
                if (local_time != null || other_time > local_time)
                    clock.put( entry.getKey(), other_time );
            }
        }
    }

    public void tick(Integer pid) {
        Integer previous = clock.get( Integer.toString( pid ) );
        clock.put( Integer.toString( pid ), ++previous );
    }

    public boolean happenedBefore(Clock other) {
        Map<String, Integer> other_clock = strToClock( other.toString() );
        if (clock.size() <= other_clock.size()) {
            for (String k : clock.keySet() ) {
               if(clock.get( k ) > other_clock.getOrDefault( k, 100000000 )){
                   return false;
               }
            }
        } else {
            for (String k : other_clock.keySet() ) {
                if(other_clock.get( k ) < clock.getOrDefault( k, 100000000 )){
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        JSONObject json = new JSONObject( this.clock );
        return json.toString();
    }

    public void setClockFromString(String clock) {
        boolean is_valid = true;

        Map<String, Integer> c = strToClock( clock );
        if( c != null) {
            this.clock = c;
        }
    }

    public int getTime(int p) {

        return this.clock.getOrDefault( Integer.toString( p ), 0  );
    }

    public void addProcess(int p, int c) {
        this.clock.put( Integer.toString( p ), c );
    }

    public  Map<String, Integer> strToClock(String in) {
        boolean is_valid = true;
        Map<String,Integer> temp_clock = new HashMap<String, Integer>( );

        try {

            JSONObject json = new JSONObject( in );
            Iterator<String> it = json.keys();

            while(it.hasNext()) {
                String key = it.next();
                Integer value = json.getInt( key );
                temp_clock.put( key, json.getInt( key ) );
            }
        } catch (JSONException e) {
            is_valid = false;
        } finally {
            if(is_valid) {
                return temp_clock;
            } else {
                return null;
            }
        }
    }
}