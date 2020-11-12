package pmcep.miners.discovery_miner;
import java.util.*;


import lombok.Getter;
import pmcep.miners.discovery_miner.view.graph.ColorPalette;
import pmcep.miners.discovery_miner.view.graph.PMDotModel;
import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;
import pmcep.web.miner.models.MinerView.Type;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@ExposedMiner(
        name = "Discovery Miner",
        description = "This miner discovers the activity flow",
        configurationParameters = {


        },
        viewParameters = {
                @ExposedMinerParameter(name = "threshold", type = MinerParameter.Type.DOUBLE)

        }
)
public class DiscoveryMiner extends AbstractMiner {

    private Map<String, String> latestActivityInCase = new HashMap<String, String>();

    private Map<Pair<String, String>, Double> relations = new HashMap<Pair<String,String>, Double>();


    private Map<String, Double> activities = new HashMap<String, Double>();
    private Double maxActivityFreq = Double.MIN_VALUE;
    private Double maxRelationsFreq = Double.MIN_VALUE;




    @Override
    public void configure(Collection<MinerParameterValue> collection) {

    }

    @Override
    public void consumeEvent(String caseID, String activityName) {
        process(caseID,activityName);

    }

    @Override
    public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
        Double threshold = 0d;
        String viewRep = "";
        for (MinerParameterValue parameterValue : collection){
            if (parameterValue.getName().equals("threshold")){
                threshold = Double.valueOf(String.valueOf(parameterValue.getValue())) / 100d;
            }
        }
        ProcessMap processMap = mine(threshold);

        List<MinerView> views = new ArrayList<>();
        views.add(new MinerView("Graphical ", new PMDotModel(processMap, ColorPalette.Colors.BLUE).toString(), Type.GRAPHVIZ));

        views.add(new MinerView("Textual", texturalRepresentation(processMap), Type.RAW));
        return views;

    }



    public void process(String caseId, String activityName) {
        System.out.println(caseId);
        Double activityFreq = 1d;
        if (activities.containsKey(activityName)) {
            activityFreq += activities.get(activityName);
            maxActivityFreq = Math.max(maxActivityFreq, activityFreq);
        }
        activities.put(activityName, activityFreq);

        if (latestActivityInCase.containsKey(caseId)) {
            Pair<String, String> relation = new ImmutablePair<String, String>(latestActivityInCase.get(caseId), activityName);
            Double relationFreq = 1d;
            if (relations.containsKey(relation)) {
                relationFreq += relations.get(relation);
                maxRelationsFreq = Math.max(maxRelationsFreq, relationFreq);
            }
            relations.put(relation, relationFreq);
        }
        latestActivityInCase.put(caseId, activityName);
    }

    public ProcessMap mine(double threshold) {
        ProcessMap process = new ProcessMap();
        for (String activity : activities.keySet()) {
            process.addActivity(activity, activities.get(activity) / maxActivityFreq);
        }
        for (Pair<String, String> relation : relations.keySet()) {
            double dependency = relations.get(relation) / maxRelationsFreq;

            if (dependency >= threshold) {

                process.addRelation(relation.getLeft(), relation.getRight(), dependency);
            }
        }
        Set<String> toRemove = new HashSet<String>();
        Set<String> selfLoopsToRemove = new HashSet<String>();
        for (String activity : activities.keySet()) {
            if (process.isStartActivity(activity) && process.isEndActivity(activity)) {
                toRemove.add(activity);
            }
            if (process.isIsolatedNode(activity)) {
                selfLoopsToRemove.add(activity);
            }
        }
        for (String activity : toRemove) {
            process.removeActivity(activity);
        }

        return process;
    }

    public String texturalRepresentation(ProcessMap processMap){
            String outputString ="";

            for (String activity : processMap.getActivities()){
                for (String outGoing : processMap.getOutgoingActivities(activity))
                outputString = outputString + activity + " -> " + outGoing + "<br>";
            }
            return outputString;

    }



}