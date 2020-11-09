package pmcep.web.miner.models;

import java.util.Collection;

import lombok.Getter;

public class MinerInstanceConfiguration {

	@Getter
	private String name;
	@Getter
	private Stream stream;
	@Getter
	private Collection<MinerParameterValue> parameterValues;
	
	public MinerInstanceConfiguration(String name, Stream stream, Collection<MinerParameterValue> parameterValues) {
		this.name = name;
		this.stream = stream;
		this.parameterValues = parameterValues;
	}
}
