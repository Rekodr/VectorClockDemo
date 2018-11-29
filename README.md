# VectorClockDemo
Simple project to demo how vector clocks work

Lamport’s logical clock concept (see refs at end for original paper) have been extended in a variety of ways that have proven 
useful in constructing distributed systems.  By using vector clocks instead of scalar clocks, we can determine causal 
relationships between events.  Others have extended vector clocks to work with a dynamic set of processes instead a fixed set 
of known processes (see Landes paper).


# Start server
java -jar server.jar

# Start client
java -jar client.jar

Note: You can start multiple clients
