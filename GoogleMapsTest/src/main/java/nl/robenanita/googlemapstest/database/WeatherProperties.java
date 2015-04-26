package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.Wms.TileProviderFormats;

/**
 * Created by Rob Verhoef on 24-11-2014.
 */
public class WeatherProperties {
    private HashMap<TileProviderFormats.weathermapLayer, Boolean> properties;
    public HashMap<TileProviderFormats.weathermapLayer, Boolean>getProperties()
    {
        return properties;
    }

    private String TAG = "GooglemapsTest";

    public WeatherProperties() {
        properties = new HashMap<TileProviderFormats.weathermapLayer, Boolean>();

        ClearProperties();
    }

    public void ClearProperties()
    {
        properties.clear();
        properties.put(TileProviderFormats.weathermapLayer.clouds, false);
        properties.put(TileProviderFormats.weathermapLayer.precipitation, false);
        properties.put(TileProviderFormats.weathermapLayer.pressure_cntr, false);
        properties.put(TileProviderFormats.weathermapLayer.ETA_PN, false);
        properties.put(TileProviderFormats.weathermapLayer.ETA_UU, false);
        properties.put(TileProviderFormats.weathermapLayer.RADAR_RDBR, false);
    }

    public void LoadFromDatabase(Context context)
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        Property p = propertiesDataSource.getMapSetup("WEATHERCHARTS");
        if (p != null)
        {
            FromXml(p.value2);
        }
        else
        {
            SaveToDatabase(context);
        }
        propertiesDataSource.close(true);
    }

    public void SaveToDatabase(Context context)
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        Property p = propertiesDataSource.getMapSetup("WEATHERCHARTS");
        if (p != null)
        {
            p.value2 = ToXml();
            propertiesDataSource.updateProperty(p);
        }
        else
        {
            Property pn = new Property();
            pn.value1 = "setup";
            pn.name = "WEATHERCHARTS";
            pn.value2 = ToXml();
            propertiesDataSource.InsertProperty(pn);
        }

        propertiesDataSource.close(true);
    }

    public void SetValue(TileProviderFormats.weathermapLayer chart, Boolean visible)
    {
        if (properties.containsKey(chart))
        {
            properties.remove(chart);
        }
        properties.put(chart, visible);
    }
    public boolean GetValue(TileProviderFormats.weathermapLayer chart)
    {
        if (properties.containsKey(chart)) {
            return properties.get(chart);
        }
        else return false;
    }

    private void FromXml(String xml)
    {
        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("WeatherCharts"))
                    {

                    }
                    if (name.equals("Charts"))
                    {
                        TileProviderFormats.weathermapLayer chart =
                                TileProviderFormats.weathermapLayer.valueOf(parser.getAttributeValue(0));
                        Boolean visible = Boolean.parseBoolean(parser.getAttributeValue(1));

                        if (properties.containsKey(chart))
                        {
                            properties.remove(chart);
                        }
                        properties.put(chart, visible);
                    }
                }

                eventType = parser.next();
            }

        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String ToXml() {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);

            serializer.startDocument("UTF-8", true);

            serializer.startTag("", "WeatherCharts");

            Iterator<TileProviderFormats.weathermapLayer> it = properties.keySet().iterator();

            while(it.hasNext()) {
                TileProviderFormats.weathermapLayer key = it.next();
                Boolean val = properties.get(key);
                serializer.startTag("","Charts");

                serializer.attribute("", "Chart", key.layerName());
                serializer.attribute("", "Visible", val.toString() );

                serializer.endTag("", "Charts");
            }

            serializer.endTag("", "WeatherCharts");
            serializer.endDocument();
        }
        catch (IOException e) {
            return "";
        }

        return writer.toString();
    }
}
