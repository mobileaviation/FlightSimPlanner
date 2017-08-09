package nl.robenanita.googlemapstest.Charts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Wms.TileProviderFormats;
import nl.robenanita.googlemapstest.database.AirportChartsDataSource;
import nl.robenanita.googlemapstest.database.AirportDataSource;

/**
 * Created by Rob Verhoef on 4-8-2017.
 */

public class MapCruncherMetadataReader extends AsyncTask<String, Integer, Void> {

    public void Read(String url, AirportCharts airportCharts, Context context)
    {
        this.airportCharts = airportCharts;
        this.url = url;
        this.context = context;
        this.xml_url = this.url + "MapCruncherMetadata.xml";
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private String url;
    private String xml_url;
    private String xml;
    private Document document;
    private AirportCharts airportCharts;
    private Context context;

    private final String TAG = "MapMetadataReader";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(String... strings) {
        URL _manifestUrl;
        try {
            _manifestUrl = new URL(xml_url);
            InputStream inputStream = _manifestUrl.openConnection().getInputStream();
            xml = readFromInputStream(inputStream);
            document = loadXMLFromString(xml);
            loadChartsFromXML(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readFromInputStream(InputStream inputStream)
    {
        if(inputStream != null)
        {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        }
        else
        {
            return "";
        }
    }

    private Document loadXMLFromString(String xml)
    {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(xml);

        try {
            ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
            builder = factory.newDocumentBuilder();
            try {
                doc = builder.parse(input);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return doc;
    }


    private void loadChartsFromXML(Document document) {
        NodeList nodes = document.getElementsByTagName("Layer");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            AirportChart airportChart = new AirportChart();

            airportChart.display_name = node.getAttributes().getNamedItem("DisplayName").getTextContent();
            airportChart.reference_name = node.getAttributes().getNamedItem("ReferenceName").getTextContent();

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element e = (Element) node;
                NodeList rect = e.getElementsByTagName("MapRectangle");
                Node r1 = rect.item(0).getChildNodes().item(1);
                Node r2 = rect.item(0).getChildNodes().item(3);

                airportChart.latitude_1_deg = Double.parseDouble(r1.getAttributes().getNamedItem("lat").getTextContent());
                airportChart.longitude_1_deg = Double.parseDouble(r1.getAttributes().getNamedItem("lon").getTextContent());
                airportChart.latitude_2_deg = Double.parseDouble(r2.getAttributes().getNamedItem("lat").getTextContent());
                airportChart.longitude_2_deg = Double.parseDouble(r2.getAttributes().getNamedItem("lon").getTextContent());

                Node namingScheme = e.getElementsByTagName("TileNamingScheme").item(0);
                airportChart.file_prefix = namingScheme.getAttributes().getNamedItem("FilePrefix").getTextContent();
                airportChart.file_suffix = namingScheme.getAttributes().getNamedItem("FileSuffix").getTextContent();

                Node thumbnail = e.getElementsByTagName("Thumbnail").item(1);
                airportChart.thumbnail_url = url + thumbnail.getAttributes().getNamedItem("URL").getTextContent();
                airportChart.url = url + airportChart.file_prefix + "/" + "#QUADKEY#" + airportChart.file_suffix;

                airportChart.active = true;
                airportChart.created_date = new Date();
                airportChart.version = 1;

                airportChart.airport_ident = airportChart.reference_name.substring(airportChart.reference_name.length()-4,
                        airportChart.reference_name.length());
                AirportDataSource airportDataSource = new AirportDataSource(context);
                airportDataSource.open(0);
                Airport airport = airportDataSource.GetAirportByIDENT(airportChart.airport_ident);
                airportChart.airport_id = airport.id;
                airportDataSource.close();

                AirportChartsDataSource airportChartsDataSource = new AirportChartsDataSource(context);
                airportChartsDataSource.open();
                airportChartsDataSource.InsertChart(airportChart);
                airportChartsDataSource.close();
            }

            airportCharts.add(airportChart);
            Log.i(TAG, "Charts inserted in database: " + airportCharts.size());
        }
    }
}
