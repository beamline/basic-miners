package pmcep.miners.recording_miner;

import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;


import java.util.*;

@ExposedMiner(
        name = "Recording Miner",
        description = "This Miner is used for recording a stream",
        configurationParameters = {
                @ExposedMinerParameter(name = "Attribute", type = MinerParameter.Type.STRING)

        },
        viewParameters = {
                @ExposedMinerParameter(name = "File type", type = MinerParameter.Type.STRING)
        }
)
public class RecordingMiner extends AbstractMiner {

    private Set<String> attributes = new HashSet<>();
    private Map<String, Trace> caseMap = new HashMap<String, Trace>();

    @Override
    public void configure(Collection<MinerParameterValue> collection) {
        for (MinerParameterValue parameterValue : collection) {
            if (parameterValue.getName().equals("Attribute")) {
                attributes.add(String.valueOf(parameterValue.getValue()));
            }
        }

    }

    @Override
    public void consumeEvent(String caseID, String activityName) {
        Event event = new Event();
        event.insertAttribute("Activity", activityName);
        int count = 0;
        Trace trace;

        if (caseMap.containsKey(caseID)) {
            trace = caseMap.get(caseID);

        } else {
            trace = new Trace();
            caseMap.put(caseID, trace);
        }

        trace.insertEvent(event);

        caseMap.put(caseID, trace);

    }

    @Override
    public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
        List<MinerView> views = new ArrayList<>();
        for (MinerParameterValue minerParameterValue : collection) {
            if (minerParameterValue.getName().equals("File type")) {
                switch (String.valueOf(minerParameterValue.getValue())) {
                    case "XML":

                        System.out.println(new XMLParser().convertToXML((HashMap<String, Trace>) caseMap));
                        break;


                }
            }

        }


        return null;
    }


}
