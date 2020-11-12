package pmcep.miners.recording_miner;


import java.util.HashMap;
import java.util.Map;

public class Event {
    private Map<String,String> attributes = new HashMap<>();

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void insertAttribute(String name, String value){
        attributes.put(name,value);
    }
}
