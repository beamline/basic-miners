package beamline.miners.notifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import beamline.core.web.annotations.ExposedMiner;
import beamline.core.web.annotations.ExposedMinerParameter;
import beamline.core.web.miner.models.MinerParameterValue;
import beamline.core.web.miner.models.MinerView;
import beamline.core.web.miner.models.MinerParameter.Type;
import beamline.core.web.miner.models.notifications.ToastrNotification;
import beamline.core.miner.AbstractMiner;

@ExposedMiner(
	name = "Activity notifier",
	description = "Miner that notifies when some activities are performed",
	configurationParameters = {
		@ExposedMinerParameter(name = "Activity name", type = Type.STRING, defaultValue = "")
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
			notifyToClients(new ToastrNotification("Activity \"" + activityToNotify + "\" observed in case " + caseID));
		}
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		views.add(new MinerView("Notifications", "The notifications are coming as activities are observed", MinerView.Type.RAW));
		return views;
	}

}
