package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.util.Xml;

import com.google.android.gms.maps.GoogleMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.Settings.LayersSetup.MapStyle;

/**
 * Created by Rob Verhoef on 24-11-2014.
 */
public class MapTypeProperties {
    private HashMap<Integer, Boolean> properties;
    public HashMap<Integer, Boolean>getProperties()
    {
        return properties;

    }

    private String TAG = "GooglemapsTest";

    public MapTypeProperties() {
        properties = new HashMap<Integer, Boolean>();

        ClearProperties();
    }

    public void ClearProperties()
    {
        properties.clear();
        properties.put(MapStyle.MAP_TYPE_HYBRID, false);
        properties.put(MapStyle.MAP_TYPE_NONE, false);
        properties.put(MapStyle.MAP_TYPE_NORMAL, false);
        properties.put(MapStyle.MAP_TYPE_SATELLITE, false);
        properties.put(MapStyle.MAP_TYPE_TERRAIN, false);
        properties.put(MapStyle.MAP_TYPE_AVIATION_DAY, false);
        properties.put(MapStyle.MAP_TYPE_AVIATION_NIGHT, false);
    }

    public void LoadFromDatabase(Context context)
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        Property p = propertiesDataSource.getMapSetup("MAPTYPE");
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
        Property p = propertiesDataSource.getMapSetup("MAPTYPE");
        if (p != null)
        {
            p.value2 = ToXml();
            propertiesDataSource.updateProperty(p);
        }
        else
        {
            SetValue(MapStyle.MAP_TYPE_TERRAIN, true);
            Property pn = new Property();
            pn.value1 = "setup";
            pn.name = "MAPTYPE";
            pn.value2 = ToXml();
            propertiesDataSource.InsertProperty(pn);
        }

        propertiesDataSource.close(true);
    }

    public void SetValue(Integer chart, Boolean visible)
    {
        if (properties.containsKey(chart))
        {
            properties.remove(chart);
        }
        properties.put(chart, visible);
    }
    public boolean GetValue(Integer chart)
    {
        if (properties.containsKey(chart)) {
            return properties.get(chart);
        }
        else return false;
    }

    public Integer GetSelected()
    {
        Integer layer = null;
        Iterator<Integer> it = properties.keySet().iterator();
        while(it.hasNext()) {
            Integer key = it.next();
            Boolean val = properties.get(key);
            if (val) layer = key;
        }
        return layer;
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
                    if (name.equals("GoogleMap"))
                    {

                    }
                    if (name.equals("ChartType"))
                    {
                        Integer chart = Integer.parseInt(parser.getAttributeValue(0));
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

            serializer.startTag("", "GoogleMap");

            Iterator<Integer> it = properties.keySet().iterator();

            while(it.hasNext()) {
                Integer key = it.next();
                Boolean val = properties.get(key);
                serializer.startTag("","ChartType");

                serializer.attribute("", "Chart", key.toString());
                serializer.attribute("", "Visible", val.toString() );

                serializer.endTag("", "ChartType");
            }

            serializer.endTag("", "GoogleMap");
            serializer.endDocument();
        }
        catch (IOException e) {
            return "";
        }

        return writer.toString();
    }

}
