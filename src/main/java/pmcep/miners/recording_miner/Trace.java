package pmcep.miners.recording_miner;

import java.util.HashSet;
import java.util.Set;

public class Trace {
    private Set<Event> events = new HashSet<>();

    public Set<Event> getEvents() {
        return events;
    }

    public void insertEvent(Event event){
        events.add(event);
    }
    public Integer getSize(){
        return events.size();
    }

}
