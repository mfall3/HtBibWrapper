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
        StringBuilder stringBuilder = new StringBuilder("HT Bib Wrapper | Hello ");
        stringBuilder.append(volumeId).append("!");

        return stringBuilder.toString();
    }

}
