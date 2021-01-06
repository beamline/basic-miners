package beamline.miners.discoveryMiner;

import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
	name = "Discovery Miner",
	description = "This miner discovers the activity flow",
	configurationParameters = { },
	viewParameters = {
		@ExposedMinerParameter(name = "Dependency threshold", type = MinerParameter.Type.RANGE_0_1, defaultValue = "0.5")
	})
public class DiscoveryMiner extends AbstractMiner {

	private Map<String, String> latestActivityInCase = new HashMap<String, String>();

	private Map<Pair<String, String>, Double> relations = new HashMap<Pair<String, String>, Double>();

	private Map<String, Double> activities = new HashMap<String, Double>();
	private Double maxActivityFreq = Double.MIN_VALUE;
	private Double maxRelationsFreq = Double.MIN_VALUE;

	@Override
	public void configure(Collection<MinerParameterValue> collection) { }

	@Override
	public void consumeEvent(String caseID, String activityName) {
		Double activityFreq = 1d;
		if (activities.containsKey(activityName)) {
			activityFreq += activities.get(activityName);
			maxActivityFreq = Math.max(maxActivityFreq, activityFreq);
		}
		activities.put(activityName, activityFreq);

		if (latestActivityInCase.containsKey(caseID)) {
			Pair<String, String> relation = new ImmutablePair<String, String>(latestActivityInCase.get(caseID),
					activityName);
			Double relationFreq = 1d;
			if (relations.containsKey(relation)) {
				relationFreq += relations.get(relation);
				maxRelationsFreq = Math.max(maxRelationsFreq, relationFreq);
			}
			relations.put(relation, relationFreq);
		}
		latestActivityInCase.put(caseID, activityName);
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		Double threshold = 0d;
		for (MinerParameterValue parameterValue : collection) {
			if (parameterValue.getName().equals("Dependency threshold")) {
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

	public String texturalRepresentation(ProcessMap processMap) {
		String outputString = "";

		for (String activity : processMap.getActivities()) {
			for (String outGoing : processMap.getOutgoingActivities(activity))
				outputString = outputString + activity + " -> " + outGoing + "<br>";
		}
		return outputString;

	}

}