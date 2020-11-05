package pmcep.miners.type;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import pmcep.miners.exceptions.MinerException;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.Stream;

public abstract class AbstractMiner {

	private boolean running = false;
	private boolean configured = false;
	@Getter @Setter
	private Stream stream = null;
	
	public abstract void configure(Collection<MinerParameterValue> collection);
	
	public abstract void consumeEvent();
	
	public abstract void getView();
	
	public void start(Stream stream) throws MinerException {
		if (running) {
			throw new MinerException("Miner instance already running");
		}
		if (stream == null || !configured) {
			throw new MinerException("Miner instance not yet configured");
		}
		// do something
		// ...
		running = true;
	}
	
	public void stop() throws MinerException {
		if (!running) {
			throw new MinerException("Miner instance not running");
		}
		// do something
		// ...
		running = false;
	}
	
	public boolean isRunnning() {
		return running;
	}
}