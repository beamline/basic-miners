package pmcep.miners.recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import pmcep.miner.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;

@ExposedMiner(
	name = "Recording Miner",
	description = "This Miner is used for recording a stream",
	configurationParameters = {},
	viewParameters = {})
public class RecordingMiner extends AbstractMiner {

	private Map<String, XTrace> caseMap = new HashMap<String, XTrace>();

	@Override
	public void configure(Collection<MinerParameterValue> collection) { }

	@Override
	public void consumeEvent(String caseID, String activityName) {
		XEvent event = new XEventImpl();
		XLogHelper.decorateElement(event, "concept:name", activityName);
		XLogHelper.setTimestamp(event, new Date());
		if (!caseMap.containsKey(caseID)) {
			caseMap.put(caseID, XLogHelper.createTrace(caseID));
		}
		caseMap.get(caseID).add(event);
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		try {
			XLog log = XLogHelper.generateNewXLog(getStream().getProcessName());
			for (String caseId : caseMap.keySet()) {
				log.add(caseMap.get(caseId));
			}
			String xmlLink = saveToCloud(log);
			views.add(new MinerView("Download recording", xmlLink, MinerView.Type.BINARY));
		} catch (Exception e) { }

		return views;
	}

	public String saveToCloud(XLog log) throws IOException {
		File tempFile = File.createTempFile(UUID.randomUUID().toString(), "");
		tempFile.deleteOnExit();
		
		XSerializer serializer = new XesXmlSerializer();
		serializer.serialize(log, new FileOutputStream(tempFile));
		
		return "/files/xml/" + tempFile.getName();
	}

}
