package pmcep.miners.tests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import pmcep.miner.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter.Type;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;

@ExposedMiner(
	name = "File uploader tester",
	description = "",
	configurationParameters = {
		@ExposedMinerParameter(name = "file upload (*.xml)", type = Type.FILE),
		@ExposedMinerParameter(name = "string parameter", type = Type.STRING),
	},
	viewParameters = {}
)
public class FileUploadTester extends AbstractMiner {

	private File fileContent;
	private String stringContent;
	
	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		for (MinerParameterValue v : collection) {
			if (v.getType().equals(Type.FILE)) {
				fileContent = (File) v.getValue();
			} else if (v.getType().equals(Type.STRING)) {
				stringContent = (String) v.getValue();
			}
		}
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		try {
			views.add(new MinerView("File Content", FileUtils.readFileToString(fileContent, StandardCharsets.UTF_8.name()), MinerView.Type.RAW));
			views.add(new MinerView("String Content", stringContent, MinerView.Type.RAW));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return views;
	}
}
