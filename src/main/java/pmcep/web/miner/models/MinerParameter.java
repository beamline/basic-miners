package pmcep.web.miner.models;

import lombok.Getter;

public class MinerParameter {

	public enum TypeEnum {
		STRING, INTEGER, DOUBLE;
	}
	
	@Getter
	private String name;
	@Getter
	private TypeEnum type;
	
	public MinerParameter(String name, TypeEnum type) {
		this.name = name;
		this.type = type;
	}
}
