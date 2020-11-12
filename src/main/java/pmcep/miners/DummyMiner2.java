package pmcep.miners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;
import pmcep.web.miner.models.MinerView.Type;

@ExposedMiner(
	name = "test miner 2",
	description = "this is an empty miner used for testing",
	configurationParameters = { },
	viewParameters = { }
)
public class DummyMiner2 extends AbstractMiner {

	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		views.add(new MinerView("view test miner 2.1", "test value 2.1", Type.RAW));
		views.add(new MinerView("view test miner 2.2", "test value 2.2", Type.RAW));
		views.add(new MinerView("view test miner 2.3", "test value 2.3", Type.RAW));
		return views;
	}
}
