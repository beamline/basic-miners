package pmcep.miners;

import pmcep.miners.type.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter.TypeEnum;

@ExposedMiner(
	name = "test miner",
	description = "this is an empty miner used for testing",
	configurationParameters = {
		@ExposedMinerParameter(name = "test config string", type = TypeEnum.STRING),
		@ExposedMinerParameter(name = "test config double", type = TypeEnum.DOUBLE)
	},
	viewParameters = {
		@ExposedMinerParameter(name = "test view double", type = TypeEnum.DOUBLE),
	}
)
public class DummyMiner extends AbstractMiner {

	@Override
	public void configure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void consumeEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getView() {
		// TODO Auto-generated method stub
		
	}

}
