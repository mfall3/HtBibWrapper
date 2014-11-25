package edu.illinois.htrc.catalog.htbibwrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Path("vol")
public class Volume {

    private final String endpoint = "http://catalog.hathitrust.org/api/volumes/full/htid/";

    private final JSONParser parser = new JSONParser();

    private final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

    private final Gson gson = new Gson();

    @GET
    @Path("{volumeId}")
    public String getHTMarcByVolumeId(@PathParam("volumeId") String volumeId) {

        //get the volumeId from the URI path
        String id = volumeId;

        //set the default returnString
        String returnString = "error";
        
        //if anything in this block fails, the whole process should end
        try{

            //make an XML document for the record
            DocumentBuilder docBuilder = null;
            docBuilder = docFactory.newDocumentBuilder();
            DOMImplementation domImplementation = docBuilder.getDOMImplementation();
            Document returnDoc = docBuilder.newDocument();
            Document tempDoc = null;
            Node tempNode = null;
            Element rootElement = returnDoc.createElementNS("http://www.loc.gov/MARC21/slim", "record");
            returnDoc.appendChild(rootElement);

            //call the HathiTrust Bib API
            String charset = "UTF-8";
            URL url = new URL(endpoint + id + ".json");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();

            //read the response as a string from the HathiTrust Bib API
            BufferedReader reader = new BufferedReader(new InputStreamReader(response));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }

            //parse the string as JSON
            JsonElement jelement = new JsonParser().parse(builder.toString());
            JsonObject jobject = jelement.getAsJsonObject();
            JsonObject recordJsonObject = jobject.getAsJsonObject("records");
            System.out.println(recordJsonObject.getAsJsonObject());
            HashMap<String, JsonObject> map = new HashMap<String, JsonObject>();
            map = gson.fromJson(recordJsonObject.toString(), map.getClass());
            ArrayList<String> keyStringArray = new ArrayList<String>();
            for (String key : map.keySet()) {
                keyStringArray.add(key);
            }

            if (keyStringArray.size() > 0) {

                JsonObject recordJson = recordJsonObject.getAsJsonObject(keyStringArray.get(0));
                
                //extract the marc-xml part of the json response
                String xmlString = recordJson.get("marc-xml").getAsString();
                InputSource inputSource = new InputSource();
                //test - System.out.println("xmlString: " + xmlString);

                inputSource.setCharacterStream(new StringReader(xmlString));
                tempDoc = docBuilder.parse(inputSource);

                // get record node
                tempNode = returnDoc.importNode(tempDoc.getFirstChild().getFirstChild(), true);

                // get children of record, add to created record element
                // to use a namespace
                NodeList recordChildren = tempNode.getChildNodes();

                for (int i = 0; i < recordChildren.getLength(); i++) {
                    Node recordChild = recordChildren.item(i);
                    returnDoc.getDocumentElement().appendChild(recordChild);
                }
            } else {
                System.out.println(keyStringArray);
            }
            //transform the xml document to a string
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(returnDoc), new StreamResult(writer));
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
            returnString = output;
            
        } catch (TransformerException ex) {
            Logger.getLogger(Volume.class.getName()).log(Level.SEVERE, null, ex);
            returnString = "internal error processing request";
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Volume.class.getName()).log(Level.SEVERE, null, ex);
            returnString = "internal error processing request";
        } catch (MalformedURLException ex) {
            Logger.getLogger(Volume.class.getName()).log(Level.SEVERE, null, ex);
            returnString = "internal error processing request";
        } catch (IOException ex) {
            Logger.getLogger(Volume.class.getName()).log(Level.SEVERE, null, ex);
            returnString = "internal error processing request";
        } catch (SAXException ex) {
            Logger.getLogger(Volume.class.getName()).log(Level.SEVERE, null, ex);
            returnString = "internal error processing request";
        }

        return returnString;
    }

}
