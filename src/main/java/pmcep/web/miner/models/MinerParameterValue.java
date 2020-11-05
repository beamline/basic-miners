package pmcep.web.miner.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

public class MinerParameterValue {

	@Getter @Setter
	private String name;
	@Getter @Setter
	private Object value;
	@Getter @JsonIgnore
	private MinerParameter.Type type;
	
	public MinerParameterValue() {}
	
	private MinerParameterValue(String name, Object value, MinerParameter.Type type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}
	
	public MinerParameterValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public MinerParameterValue(String name, String value) {
		this(name, (Object) value, MinerParameter.Type.STRING);
	}
	
	public MinerParameterValue(String name, Integer value) {
		this(name, (Object) value, MinerParameter.Type.INTEGER);
	}
	
	public MinerParameterValue(String name, Double value) {
		this(name, (Object) value, MinerParameter.Type.DOUBLE);
	}
}
