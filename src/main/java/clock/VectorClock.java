package clock;

import org.json.JSONException;
import org.json.JSONObject;
import sun.rmi.server.InactiveGroupException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class VectorClock implements Clock {

    // suggested data structure ...
    private Map<String,Integer> clock = new Hashtable<String,Integer>();


    public void update(Clock other) {
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
        return false;
    }

    public String toString() {
        JSONObject json = new JSONObject( this.clock );
        return json.toString();
    }

    public void setClockFromString(String clock) {
        boolean is_valid = true;
        Map<String,Integer> temp_clock = new HashMap<String, Integer>( );

        try {

            JSONObject json = new JSONObject( clock );
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
                this.clock = temp_clock;
            }
        }
    }

    public int getTime(int p) {

        return this.clock.getOrDefault( Integer.toString( p ), 0  );
    }

    public void addProcess(int p, int c) {
        this.clock.put( Integer.toString( p ), c );
    }
}