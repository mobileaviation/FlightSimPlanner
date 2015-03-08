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
 * Created by Rob Verhoef on 27-10-2014.
 */
public class ChartBundleProperties {

    private HashMap<TileProviderFormats.chartBundleLayer, Boolean> properties;
    public HashMap<TileProviderFormats.chartBundleLayer, Boolean>getProperties()
    {
        return properties;
    }

    private boolean chartbundleEnabled;

    private String TAG = "GooglemapsTest";

    public ChartBundleProperties() {
        properties = new HashMap<TileProviderFormats.chartBundleLayer, Boolean>();
        chartbundleEnabled = false;
        ClearProperties();
    }

    public void LoadFromDatabase(Context context)
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open();
        Property p = propertiesDataSource.getMapSetup("CHARTBUNDLE");
        if (p != null)
        {
            FromXml(p.value2);
        }
        else
        {
            SaveToDatabase(context);
        }
        propertiesDataSource.close();
    }

    public void SaveToDatabase(Context context)
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open();
        Property p = propertiesDataSource.getMapSetup("CHARTBUNDLE");
        if (p != null)
        {
            p.value2 = ToXml();
            propertiesDataSource.updateProperty(p);
        }
        else
        {
            Property pn = new Property();
            pn.value1 = "setup";
            pn.name = "CHARTBUNDLE";
            pn.value2 = ToXml();
            propertiesDataSource.InsertProperty(pn);
        }
        propertiesDataSource.close();
    }

    public void SetValue(TileProviderFormats.chartBundleLayer chart, Boolean visible)
    {
        if (properties.containsKey(chart))
        {
            properties.remove(chart);
        }
        properties.put(chart, visible);
    }
    public boolean GetValue(TileProviderFormats.chartBundleLayer chart)
    {
        if (properties.containsKey(chart)) {
            return properties.get(chart) && chartbundleEnabled;
        }
        else return false;
    }

    public void ClearProperties()
    {
        properties.clear();
        properties.put(TileProviderFormats.chartBundleLayer.enra_4326, false);
        properties.put(TileProviderFormats.chartBundleLayer.enrh_4326, false);
        properties.put(TileProviderFormats.chartBundleLayer.enrl_4326, false);
        properties.put(TileProviderFormats.chartBundleLayer.sec_4326, true);
        properties.put(TileProviderFormats.chartBundleLayer.tac_4326, false);
        properties.put(TileProviderFormats.chartBundleLayer.wac_4326, false);
    }

    public TileProviderFormats.chartBundleLayer GetSelected()
    {
        TileProviderFormats.chartBundleLayer layer = null;
        Iterator<TileProviderFormats.chartBundleLayer> it = properties.keySet().iterator();
        while(it.hasNext()) {
            TileProviderFormats.chartBundleLayer key = it.next();
            Boolean val = properties.get(key);
            if (val) layer = key;
        }
        return layer;
    }

    public void setEnabled(Boolean enabled)
    {
        chartbundleEnabled = enabled;
    }
    public boolean getEnabled()
    {
        return chartbundleEnabled;
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
                    if (name.equals("ChartBundle"))
                    {
                        chartbundleEnabled = Boolean.parseBoolean(parser.getAttributeValue("", "Enabled"));
                    }
                    if (name.equals("Charts"))
                    {
                        TileProviderFormats.chartBundleLayer chart =
                                TileProviderFormats.chartBundleLayer.valueOf(parser.getAttributeValue(0));
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

            serializer.startTag("", "ChartBundle");
            serializer.attribute("", "Enabled", Boolean.toString(chartbundleEnabled));

            Iterator<TileProviderFormats.chartBundleLayer> it = properties.keySet().iterator();

            while(it.hasNext()) {
                TileProviderFormats.chartBundleLayer key = it.next();
                Boolean val = properties.get(key);
                serializer.startTag("","Charts");

                serializer.attribute("", "Chart", key.toString());
                serializer.attribute("", "Visible", val.toString() );

                serializer.endTag("", "Charts");
            }

            serializer.endTag("", "ChartBundle");
            serializer.endDocument();
        }
        catch (IOException e) {
            return "";
        }

        return writer.toString();
    }

}
