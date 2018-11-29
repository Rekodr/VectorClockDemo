package message;

import java.util.Comparator;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

    public int compare(Message lhs, Message rhs) {
        if(lhs.ts.happenedBefore( rhs.ts )) {
            return -1;
        } else if(rhs.ts.happenedBefore( lhs.ts )) {
            return 1;
        }
        return 0;
    }
}

