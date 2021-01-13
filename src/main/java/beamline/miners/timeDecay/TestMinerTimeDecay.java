package beamline.miners.timeDecay;

import java.util.Arrays;

import beamline.core.web.miner.models.MinerParameterValue;
import beamline.core.web.miner.models.MinerView;

public class TestMinerTimeDecay {

	public static void main(String[] args) {
		
		DiscoveryMinerTimeDecay blcm = new DiscoveryMinerTimeDecay();
		
		blcm.configure(Arrays.asList(
			new MinerParameterValue("Alpha (base of exponential decay)", 0.1),
			new MinerParameterValue("Time granularity", "Seconds")));
		
		
		for (int i = 0; i < 100; i++) {
			blcm.consumeEvent("case_v1_" + i, "A");
			blcm.consumeEvent("case_v1_" + i, "B");
			blcm.consumeEvent("case_v1_" + i, "C");
			blcm.consumeEvent("case_v1_" + i, "D");
		}
		
		MinerView view;
		view = blcm.getViews(Arrays.asList(
			new MinerParameterValue("Relations threshold", 0.0))).get(1);
		System.out.println(view.getValue().toString().replace("<br>", "\n"));
		
		int repts = 0;
		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < 10; i++) {
				blcm.consumeEvent("case_v2_" + repts, "A2");
				blcm.consumeEvent("case_v2_" + repts, "B2");
				blcm.consumeEvent("case_v2_" + repts, "C2");
				blcm.consumeEvent("case_v2_" + repts, "D2");
				repts++;
			}
			
			System.out.println("Model after " + repts);
			System.out.println("================");
			view = blcm.getViews(Arrays.asList(
				new MinerParameterValue("Relations threshold", 0.0))).get(1);
			System.out.println(view.getValue().toString().replace("<br>", "\n"));
			
		}
	}
}
