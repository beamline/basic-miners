package pmcep.miners.live_visualizer;


import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pmcep.miner.AbstractMiner;
import pmcep.miners.recorder.XLogHelper;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;
import pmcep.web.miner.models.MinerViewGoogle;

import java.util.*;
import java.util.concurrent.TimeUnit;


@ExposedMiner(
        name = "Live Visualizer",
        description = "Miner that shows a live feed of incoming stream",
        configurationParameters = {
                @ExposedMinerParameter(name = "Memory (minutes)", type = MinerParameter.Type.INTEGER)
        },
        viewParameters = {})
public class VisualizingMiner extends AbstractMiner {

    private Integer minutesToStore;
    LinkedList<XEvent> eventList = new LinkedList<>();
    @Override
    public void configure(Collection<MinerParameterValue> collection) {
        MinerParameterValue config = collection.iterator().next();
        minutesToStore = Integer.parseInt(config.getValue().toString());
    }

    @Override
    public void consumeEvent(String caseID, String activityName) {
        XEvent event = new XEventImpl();
        XLogHelper.decorateElement(event, "concept:caseId", caseID);
        XLogHelper.decorateElement(event, "concept:name", activityName);
        XLogHelper.setTimestamp(event, new Date());
        eventList.add(event);

        trimList();




    }

    @Override
    public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
        List<MinerView> views = new ArrayList<>();
        List<Object> headers = Arrays.asList("Case", "Activity", "Timestamp");



        List<List<Object>> values =new ArrayList<>();
        for(XEvent event : eventList) {

            values.add(Arrays.asList("Case", "Activity", "Timestamp"));
            values.add(Arrays.asList( event.getAttributes().values()));
            values.add(Arrays.asList(event.getAttributes().get("concept:caseId"),event.getAttributes().get("cconcept:name"),
                    event.getAttributes().get("time:timestamp")));
        }

        Map<String, Object> options = new HashMap<String, Object>() {
            put("title", "Live Stream");
            put("subtitle", "Events received the last " + minutesToStore + " minutes");
        }};

        for (MinerViewGoogle.TYPE t : MinerViewGoogle.TYPE.values()) {
            views.add(new MinerViewGoogle(t + " view", headers, values, options, t));
        }

        return views;
    }

    public void trimList(){
        int threshold = minutesToStore * 60 * 1000;

        long latestTime = System.currentTimeMillis() - threshold;

        Date latestDateTime = new Date(latestTime);



        while (latestDateTime.after(XLogHelper.getTimestamp(eventList.getFirst()))) {
            System.out.println("latestTime after log time");
            eventList.removeFirst();

            if(eventList.isEmpty()){break;}
        }

    }

    public String convertToJson() {

        JSONObject responseDetailsJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(XEvent event : eventList) {
            jsonArray.add(event.getAttributes());
        }
        responseDetailsJson.put("events", jsonArray);

        return responseDetailsJson.toJSONString();

    }

}



