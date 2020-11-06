package pmcep.web.miner.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class MinerView {

	@Getter
	private String name;
	@Getter
	private Object value;
	
	public MinerView(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}
