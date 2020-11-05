package pmcep.web.miner.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Stream {

	@Getter
	private String processName;
	@Getter
	private String brokerHost;
	@Getter
	private String topicBase;
	
	public Stream(String processName, String brokerHost, String topicBase) {
		this.processName = processName;
		this.brokerHost = brokerHost;
		this.topicBase = topicBase;
	}
}
