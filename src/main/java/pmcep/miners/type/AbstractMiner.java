package pmcep.miners.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import mqttxes.lib.XesMqttConsumer;
import mqttxes.lib.XesMqttEvent;
import mqttxes.lib.XesMqttEventCallback;
import pmcep.miners.exceptions.MinerException;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;
import pmcep.web.miner.models.Stream;

public abstract class AbstractMiner {

	private boolean running = false;
	private boolean configured = true;
	@Getter @Setter
	private Stream stream = null;

	private XesMqttConsumer client;
	
	public abstract void configure(Collection<MinerParameterValue> collection);
	
	public abstract void consumeEvent(String caseID, String activityName);
	
	public abstract List<MinerView> getViews(Collection<MinerParameterValue> collection);

	public Collection<MinerParameter> getConfigurationParameters() {
		ExposedMiner annotation = this.getClass().getAnnotation(ExposedMiner.class);
		HashSet<MinerParameter> params = new HashSet<MinerParameter>();
		for (ExposedMinerParameter p : annotation.configurationParameters()) {
			params.add(new MinerParameter(p.name(), p.type()));
		}
		return params;
	}
	
	public Collection<MinerParameter> getViewParameters() {
		ExposedMiner annotation = this.getClass().getAnnotation(ExposedMiner.class);
		HashSet<MinerParameter> params = new HashSet<MinerParameter>();
		for (ExposedMinerParameter p : annotation.viewParameters()) {
			params.add(new MinerParameter(p.name(), p.type()));
		}
		return params;
	}
	
	public void start() throws MinerException {
		if (running) {
			throw new MinerException("Miner instance already running");
		}
		if (stream == null || !configured) {
			throw new MinerException("Miner instance not yet configured");
		}
		this.client = new XesMqttConsumer(stream.getBrokerHost(), stream.getTopicBase());
		client.subscribe(stream.getProcessName(), new XesMqttEventCallback() {
			@Override
			public void accept(XesMqttEvent e) {
				consumeEvent(e.getCaseId(), e.getActivityName());
			}
		});

		client.connect();
		running = true;
	}
	
	public void stop() throws MinerException {
		if (!running) {
			throw new MinerException("Miner instance not running");
		}
		client.disconnect();
		running = false;
	}
	
	public boolean isRunnning() {
		return running;
	}
}