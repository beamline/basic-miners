package pmcep.miners.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pmcep.miner.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;
import pmcep.web.miner.models.MinerViewBinary;
import pmcep.web.miner.models.MinerViewGoogle;
import pmcep.web.miner.models.MinerViewGraphviz;
import pmcep.web.miner.models.MinerViewRaw;

@ExposedMiner(
	name = "Visualization tester",
	description = "",
	configurationParameters = {},
	viewParameters = {}
)
public class VisualizationsTester extends AbstractMiner {

	@Override
	public void configure(Collection<MinerParameterValue> collection) { }

	@Override
	public void consumeEvent(String caseID, String activityName) { }

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		views.add(new MinerViewRaw("Raw view", "Text example"));
		views.add(new MinerViewGraphviz("Graphviz view", "digraph G {Hello->World}"));
		views.add(new MinerViewBinary("Binary view", "http://speed.hetzner.de/100MB.bin"));
		
		List<Object> headers = Arrays.asList("Year", "Sales", "Expenses", "Profit");
		List<List<Object>> values = Arrays.asList(
				Arrays.asList("2014", 1000, 400, 209),
				Arrays.asList("2015", 2000, 800, 203),
				Arrays.asList("2016", 7000, 500, 206),
				Arrays.asList("2017", 3000, 300, 204),
				Arrays.asList("2018", 5000, 900, 201)
			);
		Map<String, Object> options = new HashMap<String, Object>() {{
			put("title", "Company Performance");
			put("subtitle", "Sales, Expenses, and Profit: 2014-2017");
		}};
		
		for (MinerViewGoogle.TYPE t : MinerViewGoogle.TYPE.values()) {
			views.add(new MinerViewGoogle(t + " view", headers, values, options, t));
		}
		
		return views;
	}

}
