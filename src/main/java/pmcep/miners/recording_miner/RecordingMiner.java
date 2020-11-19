package pmcep.miners.recording_miner;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.models.PublicAccessType;
import com.azure.storage.blob.specialized.BlockBlobClient;
import pmcep.miner.AbstractMiner;
import pmcep.web.annotations.ExposedMiner;
import pmcep.web.annotations.ExposedMinerParameter;
import pmcep.web.miner.models.MinerParameter;
import pmcep.web.miner.models.MinerParameterValue;
import pmcep.web.miner.models.MinerView;


import java.io.*;
import java.time.OffsetDateTime;
import java.util.*;

@ExposedMiner(
        name = "Recording Miner",
        description = "This Miner is used for recording a stream",
        configurationParameters = {
        },
        viewParameters = {
                @ExposedMinerParameter(name = "File type", type = MinerParameter.Type.STRING)
        }
)
public class RecordingMiner extends AbstractMiner {

    private Set<String> attributes = new HashSet<>();
    private Map<String, Trace> caseMap = new HashMap<String, Trace>();

    @Override
    public void configure(Collection<MinerParameterValue> collection) {
        for (MinerParameterValue parameterValue : collection) {
            if (parameterValue.getName().equals("Attribute")) {
                attributes.add(String.valueOf(parameterValue.getValue()));
            }
        }

    }

    @Override
    public void consumeEvent(String caseID, String activityName) {
        Event event = new Event();
        event.insertAttribute("Activity", activityName);
        int count = 0;
        Trace trace;

        if (caseMap.containsKey(caseID)) {
            trace = caseMap.get(caseID);

        } else {
            trace = new Trace();
            caseMap.put(caseID, trace);
        }

        trace.insertEvent(event);

        caseMap.put(caseID, trace);

    }

    @Override
    public List<MinerView> getViews(Collection<MinerParameterValue> collection) {
        List<MinerView> views = new ArrayList<>();
        for (MinerParameterValue minerParameterValue : collection) {
            if (minerParameterValue.getName().equals("File type")) {
                switch (String.valueOf(minerParameterValue.getValue())) {
                    case "XML":

                        BlockBlobClient blockBlobClient = createBlobContainer("xml");


                        String htmlLink = "<a href="+ blockBlobClient.getBlobUrl() +">Download XML file here</a>";

                        new Thread(() -> {
                            //upload XML asynchronous
                            String xmlString= new XMLParser().convertToXML((HashMap<String, Trace>) caseMap);
                            uploadToCloud(xmlString, blockBlobClient);
                            
                        }).start();

                        views.add(new MinerView("Textual", htmlLink, MinerView.Type.RAW));
                        break;


                }
            }

        }


        return views;
    }

    public BlockBlobClient createBlobContainer(String fileType){

        //Azure Connection string
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=opmframework;AccountKey=GZuLV1fA3apRDprLpZ/3kMCgiR8l6j9EhH+M88ncFB9xw91xXWveeEZbcJeBjCCIHJOk7+T6tcCh/E324x2dxg==;EndpointSuffix=core.windows.net";

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();

        //Unique container name
        String containerName = "opm-recording" + java.util.UUID.randomUUID();
        BlobContainerClient containerClient = blobServiceClient.createBlobContainer(containerName);
        BlobSignedIdentifier identifier = new BlobSignedIdentifier()
                .setId("name")
                .setAccessPolicy(new BlobAccessPolicy()
                        .setStartsOn(OffsetDateTime.now())
                        .setExpiresOn(OffsetDateTime.now().plusDays(1))
                        .setPermissions("r"));
        try{
            containerClient.setAccessPolicy(PublicAccessType.CONTAINER,Collections.singletonList(identifier));
        } catch (UnsupportedOperationException err) {
            System.out.printf("Set Access Policy failed because: %s\n", err);
        }

        BlockBlobClient blockBlobClient = containerClient.getBlobClient("Recording." + fileType).getBlockBlobClient();




        return blockBlobClient;
    }

    public void uploadToCloud(String fileString,BlockBlobClient blockBlobClient ){

        try (ByteArrayInputStream dataStream = new ByteArrayInputStream(fileString.getBytes())) {
            blockBlobClient.upload(dataStream, fileString.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
