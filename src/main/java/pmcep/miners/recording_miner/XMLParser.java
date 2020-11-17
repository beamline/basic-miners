package pmcep.miners.recording_miner;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XMLParser {

    public static final String xmlFilePath = "src/main/java/pmcep/miners/recording_miner/tmp_data/";
    public String convertToXML(HashMap<String,Trace> caseMap) {
        try{
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element root = document.createElement("cases");
            document.appendChild(root);
            for (Map.Entry<String, Trace> entry : caseMap.entrySet()) {
                // streamCase element
                Element streamCase = document.createElement("Case");
                root.appendChild(streamCase);

                Attr attr = document.createAttribute("Id");
                attr.setValue(entry.getKey());

                streamCase.setAttributeNode(attr);

                for (Event event : entry.getValue().getEvents()){
                    for ( Map.Entry<String,String> attribute : event.getAttributes().entrySet())

                    {
                        Element name = document.createElement(attribute.getKey());
                        name.appendChild(document.createTextNode(attribute.getValue()));
                        streamCase.appendChild(name);

                    }

                }

            }
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            String filePath = xmlFilePath + "./xml-" + java.util.UUID.randomUUID().toString() + ".xml";
            StreamResult streamResult = new StreamResult(new File(filePath));


            transformer.transform(domSource, streamResult);
            return filePath;

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }


}
