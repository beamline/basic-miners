package beamline.miners.live;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;
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
		@ExposedMinerParameter(name = "Time window duration (min)", type = beamline.core.web.miner.models.MinerParameter.Type.INTEGER, defaultValue = "10")
	},
	viewParameters = {})
public class LiveVisualizer extends AbstractMiner {

	private static final long MILLISECONDS_BIN = 10 * 1000; // 10 seconds
	public static final DateFormat df = new SimpleDateFormat("HH:mm:ss");
	
	private Integer minutesToStore;
	private LinkedList<XEvent> eventList = new LinkedList<>();
	private CircularFifoQueue<Observation> observationsOverTime;

	@Override
	public void configure(Collection<MinerParameterValue> collection) {
		MinerParameterValue config = collection.iterator().next();
		minutesToStore = Integer.parseInt(config.getValue().toString());
		
		observationsOverTime = new CircularFifoQueue<Observation>(minutesToStore * 6 * 10);
	}

	@Override
	public void consumeEvent(String caseID, String activityName) {
		XEvent event = new XEventImpl();
		XLogHelper.decorateElement(event, "concept:caseId", caseID);
		XLogHelper.decorateElement(event, "concept:name", activityName);
		XLogHelper.setTimestamp(event, new Date());
		eventList.add(event);
		trimList();
		
		if (observationsOverTime.isEmpty() ||
				!observationsOverTime.get(observationsOverTime.size() - 1).incrementIfWithinTime(MILLISECONDS_BIN)) {
			observationsOverTime.offer(new Observation(1));
		}
		
		notifyToClients(new RefreshViewNotification());
	}

	@Override
	public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
		List<MinerView> views = new ArrayList<>();
		List<Object> headersTable = Arrays.asList("Cases", "Activity", "Timestamp");
		List<Object> headersLineChartEventsTime = Arrays.asList("Time", "Events");
		List<Object> headersBarChartEventsCases = Arrays.asList("Case ID", "Frequency");
		List<Object> headersBarChartActivities = Arrays.asList("Activity", "Frequency");

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("title", "Live Stream");
		
		Map<String, Object> optionsLineChart = new HashMap<String, Object>();
		optionsLineChart.put("title", "Events over time");

		views.add(new MinerViewGoogle("List of events", headersTable, fillTable(), options, MinerViewGoogle.TYPE.Table));
		views.add(new MinerViewGoogle("Events over time", headersLineChartEventsTime, fillBarChartEventsPerTime(), optionsLineChart, MinerViewGoogle.TYPE.LineChart));
		views.add(new MinerViewGoogle("Events per cases", headersBarChartEventsCases, fillBarChartEventsCases(), options, MinerViewGoogle.TYPE.BarChart));
		views.add(new MinerViewGoogle("Activities", headersBarChartActivities, fillBarChartActivities(), options, MinerViewGoogle.TYPE.BarChart));

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
					event.getAttributes().get("time:timestamp").toString()
					));
		}
		return values;
	}

	public List<List<Object>> fillBarChartEventsCases() {
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

	public List<List<Object>> fillBarChartActivities() {
		Map<String, Integer> freqMap = new HashMap<>();
		for (XEvent event : eventList) {
			String caseID = event.getAttributes().get("concept:name").toString();
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

	public List<List<Object>> fillBarChartEventsPerTime() {
		List<List<Object>> values = new ArrayList<>();
		for (Observation obs : observationsOverTime) {
			values.add(Arrays.asList(df.format(obs.getTime()), obs.getObservations()));
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	public String convertToJson() {
		JSONObject responseDetailsJson = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (XEvent event : eventList) {
			jsonArray.add(event.getAttributes());
		}
		responseDetailsJson.put("events", jsonArray);
		return responseDetailsJson.toJSONString();
	}
	
	
	private class Observation implements Serializable {

		private static final long serialVersionUID = 7701614088946383669L;
		
		private Date time;
		private Integer obs;
		
		public Observation(Integer obs) {
			this.time = new Date();
			this.obs = obs;
		}
		
		public Date getTime() {
			return time;
		}
		
		public Integer getObservations() {
			return obs;
		}
		
		public boolean incrementIfWithinTime(long windowSizeInMilliseconds) {
			if (time.toInstant().plusMillis(windowSizeInMilliseconds).isAfter(new Date().toInstant())) {
				obs += 1;
				return true;
			}
			return false;
		}
	}
}
