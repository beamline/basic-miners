package pmcep.miners.type;

import pmcep.miners.exceptions.MinerException;
import pmcep.web.miner.models.Stream;

public abstract class AbstractMiner {

	public boolean running = false;
	public boolean configured = false;
	
	public abstract void configure();
	
	public abstract void consumeEvent();
	
	public abstract void getView();
	
	public void start(Stream stream) throws MinerException {
		if (running) {
			throw new MinerException("Miner instance already running");
		}
		if (!configured) {
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
