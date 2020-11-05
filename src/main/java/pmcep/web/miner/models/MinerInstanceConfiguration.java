package pmcep.web.miner.models;

import java.util.Collection;

import lombok.Getter;

public class MinerInstanceConfiguration {

	@Getter
	private Stream stream;
	@Getter
	private Collection<MinerParameterValue> parameterValues;
	
	public MinerInstanceConfiguration(Stream stream, Collection<MinerParameterValue> parameterValues) {
		this.stream = stream;
		this.parameterValues = parameterValues;
	}
}
