package beamline.miners.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.models.PublicAccessType;
import com.azure.storage.blob.specialized.BlockBlobClient;

import beamline.core.web.annotations.ExposedMiner;
import beamline.core.web.miner.models.MinerParameterValue;
import beamline.core.web.miner.models.MinerView;
import beamline.core.miner.AbstractMiner;

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
			
			// saving temporary file
			File tempFile = File.createTempFile(UUID.randomUUID().toString(), "");
			tempFile.deleteOnExit();
			
			XSerializer serializer = new XesXmlSerializer();
			serializer.serialize(log, new FileOutputStream(tempFile));
			
			// saving to local file
			String xmlLocalLink = saveToLocal(tempFile);
			
			// saving to azure
			BlockBlobClient blockBlobClient = createBlobContainer("xml");
			saveToAzure(tempFile, blockBlobClient);
			
			// pack all views
			views.add(new MinerView("Download from miner's server", xmlLocalLink, MinerView.Type.BINARY));
			views.add(new MinerView("Download from Azure", blockBlobClient.getBlobUrl(), MinerView.Type.BINARY));
		} catch (Exception e) { }

		return views;
	}

	public String saveToLocal(File tempFile) throws IOException {
		return "/files/xml/" + tempFile.getName();
	}
	
	public void saveToAzure(File tempFile, BlockBlobClient blockBlobClient) throws IOException{
		FileInputStream fis = new FileInputStream(tempFile);
		blockBlobClient.upload(fis, tempFile.length());
	}
	
	public BlockBlobClient createBlobContainer(String fileType){

		// Azure Connection string
		String connectStr = "DefaultEndpointsProtocol=https;AccountName=opmframework;AccountKey=GZuLV1fA3apRDprLpZ/3kMCgiR8l6j9EhH+M88ncFB9xw91xXWveeEZbcJeBjCCIHJOk7+T6tcCh/E324x2dxg==;EndpointSuffix=core.windows.net";
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();

		// Unique container name
		String containerName = "opm-recording" + java.util.UUID.randomUUID();
		BlobContainerClient containerClient = blobServiceClient.createBlobContainer(containerName);
		BlobSignedIdentifier identifier = new BlobSignedIdentifier().setId("name")
				.setAccessPolicy(new BlobAccessPolicy()
				.setStartsOn(OffsetDateTime.now())
				.setExpiresOn(OffsetDateTime.now().plusDays(1))
				.setPermissions("r"));
		try {
			containerClient.setAccessPolicy(PublicAccessType.CONTAINER, Collections.singletonList(identifier));
		} catch (UnsupportedOperationException err) {
			System.out.printf("Set Access Policy failed because: %s\n", err);
		}
		BlockBlobClient blockBlobClient = containerClient.getBlobClient("Recording." + fileType).getBlockBlobClient();

		return blockBlobClient;
	}

}
