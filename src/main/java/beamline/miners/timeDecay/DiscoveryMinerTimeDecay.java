package beamline.miners.timeDecay;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import beamline.miners.discoveryMiner.ProcessMap;
import beamline.miners.discoveryMiner.view.graph.ColorPalette;
import beamline.miners.discoveryMiner.view.graph.PMDotModel;
import beamline.core.miner.AbstractMiner;
import beamline.core.web.annotations.ExposedMiner;
import beamline.core.web.annotations.ExposedMinerParameter;
import beamline.core.web.miner.models.MinerParameter;
import beamline.core.web.miner.models.MinerParameterValue;
import beamline.core.web.miner.models.MinerView;
import beamline.core.web.miner.models.MinerView.Type;

@ExposedMiner(
	name = "Discovery Miner with Time Decay",
	description = "This miner discovers the activity flow",
	configurationParameters = {
		@ExposedMinerParameter(name = "Alpha (base of exponential decay)", type = MinerParameter.Type.DOUBLE, defaultValue = "0.9999999"),
		@ExposedMinerParameter(name = "Time granularity", type = MinerParameter.Type.CHOICE, defaultValue = "Seconds;Minutes;Hours;Days"),
		@ExposedMinerParameter(name = "Max parallel instances", type = MinerParameter.Type.INTEGER, defaultValue = "100"),
	},
	viewParameters = {
		@ExposedMinerParameter(name = "Relations threshold", type = MinerParameter.Type.RANGE_0_1, defaultValue = "0.5")
	})
public class DiscoveryMinerTimeDecay extends AbstractMiner {

	private double alpha;
	private double divisor = 1000;
	private int maxParallelInstances = 100;
	private Map<String, String> latestActivityInCase = null;
	private Map<Pair<String, String>, TimeDecayingCounter> relations = null;
	private Map<String, TimeDecayingCounter> activities = null;

	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		for(MinerParameterValue v : collection) {
			if (v.getName().equals("Alpha (base of exponential decay)") && v.getType() == MinerParameter.Type.DOUBLE) {
				alpha = Double.parseDouble(v.getValue().toString());
			}
			if (v.getName().equals("Max parallel instances") && v.getType() == MinerParameter.Type.INTEGER) {
				maxParallelInstances = Integer.parseInt(v.getValue().toString());
			}
			if (v.getName().equals("Time granularity")) {
				String value = v.getValue().toString();
				if (value.equals("Seconds")) {
					divisor = 1000;
				} else if (value.equals("Minutes")) {
					divisor = 1000 * 60;
				} else if (value.equals("Hours")) {
					divisor = 1000 * 60 * 60;
				} else if (value.equals("Days")) {
					divisor = 1000 * 60 * 60 * 24;
				}
			}
		}
		
		latestActivityInCase = new FifoHashMap<String, String>(maxParallelInstances);
		relations = new ConcurrentHashMap<Pair<String, String>, TimeDecayingCounter>();
		activities = new ConcurrentHashMap<String, TimeDecayingCounter>();
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		TimeDecayingCounter activityFreq = new TimeDecayingCounter(alpha, divisor);
		if (!activities.containsKey(activityName)) {
			activities.put(activityName, activityFreq);
		}
		activities.get(activityName).increment();
		

		if (latestActivityInCase.containsKey(caseID)) {
			Pair<String, String> relation = new ImmutablePair<String, String>(latestActivityInCase.get(caseID), activityName);
			TimeDecayingCounter relationFreq = new TimeDecayingCounter(alpha, divisor);
			if (!relations.containsKey(relation)) {
				relations.put(relation, relationFreq);
			}
			relations.get(relation).increment();
		}
		latestActivityInCase.put(caseID, activityName);
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		Double threshold = 0d;
		for (MinerParameterValue parameterValue : collection) {
			if (parameterValue.getName().equals("Relations threshold")) {
				threshold = Double.valueOf(String.valueOf(parameterValue.getValue()));
			}
		}
		ProcessMap processMap = mine(threshold);
		
		List<MinerView> views = new ArrayList<>();
		views.add(new MinerView("Graphical ", new PMDotModel(processMap, ColorPalette.Colors.BLUE).toString(), Type.GRAPHVIZ));
		views.add(new MinerView("Textual", texturalRepresentation(processMap), Type.RAW));
		return views;
	}

	public ProcessMap mine(double threshold) {
		ProcessMap process = new ProcessMap();
		
		// calculate max freq for relations
		Double maxRelationsFreq = Double.MIN_VALUE;
		for (Pair<String, String> relation : relations.keySet()) {
			TimeDecayingCounter relValues = relations.get(relation);
			maxRelationsFreq = Math.max(relValues.get(), maxRelationsFreq);
		}
		
		// calculate max freq for activities
		Double maxActivitiesFreq = Double.MIN_VALUE;
		for (Pair<String, String> relation : relations.keySet()) {
			String source = relation.getLeft();
			String sink = relation.getRight();
			TimeDecayingCounter relValues = relations.get(relation);
			double dependency = relValues.get() / maxRelationsFreq;
			if (dependency >= threshold && activities.containsKey(source) && activities.containsKey(sink)) {
				maxActivitiesFreq = Math.max(maxActivitiesFreq, activities.get(source).get());
				maxActivitiesFreq = Math.max(maxActivitiesFreq, activities.get(sink).get());
			}
		}
		
		// add all relations above freq
		for (Pair<String, String> relation : relations.keySet()) {
			String source = relation.getLeft();
			String sink = relation.getRight();
			TimeDecayingCounter relValues = relations.get(relation);
			double dependency = relValues.get() / maxRelationsFreq;
			if (dependency >= threshold && activities.containsKey(source) && activities.containsKey(sink)) {
				process.addRelation(source, sink, dependency);
				process.addActivity(source, activities.get(source).get() / maxActivitiesFreq);
				process.addActivity(sink, activities.get(sink).get() / maxActivitiesFreq);
			}
		}
		
		return process;
	}

	public String texturalRepresentation(ProcessMap processMap) {
		String outputString = "";

		for (String activity : processMap.getActivities()) {
			for (String outGoing : processMap.getOutgoingActivities(activity))
				outputString = outputString + activity + " -> " + outGoing + " (" + processMap.getRelationValue(Pair.of(activity, outGoing)) + ")<br>";
		}
		return outputString;

	}

}