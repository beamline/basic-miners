package beamline.miners.live;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import beamline.core.miner.AbstractMiner;
import beamline.core.web.annotations.ExposedMiner;
import beamline.core.web.annotations.ExposedMinerParameter;
import beamline.core.web.miner.models.MinerParameterValue;
import beamline.core.web.miner.models.MinerView;
import beamline.core.web.miner.models.MinerViewGoogle;
import beamline.core.web.miner.models.notifications.RefreshViewNotification;
import beamline.miners.recorder.XLogHelper;

@ExposedMiner(
	name = "Live Visualizer",
	description = "Miner that shows a live feed of incoming stream",
	configurationParameters = {
		@ExposedMinerParameter(name = "Time window duration (min)", type = beamline.core.web.miner.models.MinerParameter.Type.INTEGER)
	},
	viewParameters = {})
public class LiveVisualizer extends AbstractMiner {

	private Integer minutesToStore;
	LinkedList<XEvent> eventList = new LinkedList<>();

	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		MinerParameterValue config = collection.iterator().next();
		minutesToStore = Integer.parseInt(config.getValue().toString());
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		XEvent event = new XEventImpl();
		XLogHelper.decorateElement(event, "concept:caseId", caseID);
		XLogHelper.decorateElement(event, "concept:name", activityName);
		XLogHelper.setTimestamp(event, new Date());
		eventList.add(event);
		trimList();
		notifyToClients(new RefreshViewNotification());
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		List<Object> headersTable = Arrays.asList("Cases", "Activity", "Timestamp");
		List<Object> headersBarChart = Arrays.asList("CaseId", "Freq");

		Map<String, Object> options = new HashMap<>() {
			{
				put("title", "Live Stream");
				put("subtitle", "Events received the last " + minutesToStore + " minutes");
			}
		};

		views.add(new MinerViewGoogle("Table view", headersTable, fillTable(), options, MinerViewGoogle.TYPE.Table));
		views.add(new MinerViewGoogle("Bar view", headersBarChart, fillBarChart(), options, MinerViewGoogle.TYPE.BarChart));

		return views;
	}

	public void trimList() {
		int threshold = minutesToStore * 60 * 1000;
		long latestTime = System.currentTimeMillis() - threshold;
		Date latestDateTime = new Date(latestTime);
		while (latestDateTime.after(XLogHelper.getTimestamp(eventList.getFirst()))) {
			eventList.removeFirst();
			if (eventList.isEmpty()) {
				break;
			}
		}

	}

	public List<List<Object>> fillTable() {
		List<List<Object>> values = new ArrayList<>();
		ListIterator<XEvent> listIterator = eventList.listIterator(eventList.size());
		while (listIterator.hasPrevious()) {
			XEvent event = (XEvent) listIterator.previous();
			values.add(Arrays.asList(
					event.getAttributes().get("concept:caseId").toString(),
					event.getAttributes().get("concept:name").toString(),
					event.getAttributes().get("time:timestamp").toString()));
		}
		return values;
	}

	public List<List<Object>> fillBarChart() {
		Map<String, Integer> freqMap = new HashMap<>();
		for (XEvent event : eventList) {
			String caseID = event.getAttributes().get("concept:caseId").toString();
			if (freqMap.containsKey(caseID)) {
				freqMap.put(caseID, freqMap.get(caseID) + 1);
			} else {
				freqMap.put(caseID, 1);
			}
		}
		List<List<Object>> values = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
			values.add(Arrays.asList(entry.getKey(), entry.getValue()));
		}
		return values;
	}

	public String convertToJson() {
		JSONObject responseDetailsJson = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (XEvent event : eventList) {
			jsonArray.add(event.getAttributes());
		}
		responseDetailsJson.put("events", jsonArray);
		return responseDetailsJson.toJSONString();

	}

}