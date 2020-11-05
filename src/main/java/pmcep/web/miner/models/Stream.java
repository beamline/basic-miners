package pmcep.web.miner.models;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Stream {

	@Getter
	private String id;
	@Getter
	private String processName;
	@Getter
	private String brokerHost;
	@Getter
	private String topicBase;
	
	public Stream(String processName, String brokerHost, String topicBase) {
		this.id = UUID.randomUUID().toString();
		this.processName = processName;
		this.brokerHost = brokerHost;
		this.topicBase = topicBase;
	}
	
	public static Stream copy(Stream stream) {
		return new Stream(stream.getProcessName(), stream.getBrokerHost(), stream.getTopicBase());
	}
}
