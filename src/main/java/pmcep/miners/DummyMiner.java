package pmcep.miners;

import java.util.Collection;

import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter.Type;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;

@ExposedMiner(
	name = "test miner",
	description = "this is an empty miner used for testing",
	configurationParameters = {
		@ExposedMinerParameter(name = "test config string", type = Type.STRING),
		@ExposedMinerParameter(name = "test config double", type = Type.DOUBLE)
	},
	viewParameters = {
		@ExposedMinerParameter(name = "test view double", type = Type.DOUBLE),
	}
)
public class DummyMiner extends AbstractMiner {

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
		return new MinerView("view 1", "test");
		// TODO Auto-generated method stub
		
	}

}
