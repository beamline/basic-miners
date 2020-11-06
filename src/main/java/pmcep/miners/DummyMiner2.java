package pmcep.miners;

import java.util.Collection;

import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;

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
	public MinerView getView(Collection<MinerParameterValue> collection) {
		return new MinerView("view 2", "test");
	}
}
