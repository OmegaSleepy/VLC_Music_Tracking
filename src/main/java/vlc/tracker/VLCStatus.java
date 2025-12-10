package vlc.tracker;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.net.*;
import java.io.*;

import java.util.Base64;

public class VLCStatus {

    // Build the URL to VLC's status XML endpoint

    static URL url;

    static {
        try {
            url = new URI("http://localhost:8080/requests/status.xml").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // VLC uses Basic Authentication for HTTP interface
    // Username is empty, password is what you set in VLC
    static String auth = ":password";

    public static Song getCurrentSong() throws Exception {

        Document doc = getDocument();

        NodeList infos = doc.getElementsByTagName("info");
        NodeList lengths = doc.getElementsByTagName("length");
        NodeList states = doc.getElementsByTagName("state");

        int songLength = 0;
        if (lengths.getLength() > 0) {
            songLength = Integer.parseInt(lengths.item(0).getTextContent());
        }

        String state = "";
        if(states.getLength() > 0){
            state = states.item(0).getTextContent();
        }

        String title = (getAttribute(infos,"title"));
        String artist = (getAttribute(infos,"artist"));
        String album = (getAttribute(infos,"album"));
        String comment = (getAttribute(infos,"comment"));

        return new Song(title, artist, album, comment, songLength, state);
    }

    private static Document getDocument () throws IOException, ParserConfigurationException, SAXException {
        // Open an HTTP connection to VLC
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");  // HTTP GET request


        // Encode "username:password" in Base64 for the Authorization header
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoded);

        // Get the response stream from VLC
        InputStream stream = conn.getInputStream();

        // Set up XML parsing
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        // Parse the XML from VLC
        Document doc = dBuilder.parse(stream);
        return doc;
    }

    private static String getAttribute(NodeList infos, String attribute){

        for (int i = 0; i < infos.getLength(); i++) {
            Element e = (Element) infos.item(i);

            if(attribute.equals(e.getAttribute("name"))){
                return e.getTextContent();
            }
        }

        return attribute;
    }

}
