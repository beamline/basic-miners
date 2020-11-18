package pmcep.miners.notifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pmcep.miner.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;
import pmcep.web.miner.models.MinerParameter.Type;

@ExposedMiner(
	name = "Activity notifier",
	description = "Miner that notifies when some activities are performed",
	configurationParameters = {
		@ExposedMinerParameter(name = "Activity name", type = Type.STRING)
	},
	viewParameters = {})
public class ActivityNotifierMiner extends AbstractMiner {

	private String activityToNotify;
	
	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		MinerParameterValue config = collection.iterator().next();
		activityToNotify = config.getValue().toString();
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		if (activityName.equals(activityToNotify)) {
			notifyToClients("Activity \"" + activityToNotify + "\" observed in case " + caseID);
		}
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		views.add(new MinerView("Notifications", "The notifications are coming as activities are observed", MinerView.Type.RAW));
		return views;
	}

}