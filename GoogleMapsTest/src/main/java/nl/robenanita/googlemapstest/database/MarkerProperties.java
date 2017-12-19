package nl.robenanita.googlemapstest.database;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Airport.AirportType;

/**
 * Created by Rob Verhoef on 26-10-2014.
 */
public class MarkerProperties
{
    public MarkerProperties()
    {
        properties = new ArrayList<MarkerProperty>();
    }

    public MarkerProperty getNewMarkerProperty()
    {
        return new MarkerProperty();
    }

    public MarkerProperty getMarkerPropertyByAirportType(AirportType airportType)
    {
        MarkerProperty m = new MarkerProperty();
        m.visible = false;
        for (MarkerProperty mp : properties)
        {
            if (mp.airportType == airportType) m = mp;
        }

        return m;
    }

    public ArrayList<MarkerProperty> properties;

    public String ToXml() throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);

        serializer.startDocument("UTF-8", true);

        serializer.startTag("", "Properties");

        for (MarkerProperty m : properties) {
            serializer.startTag("", "Marker");
            serializer.attribute("", "AirportType", m.airportType.toString());
            serializer.attribute("", "Visible", Boolean.toString(m.visible));
            serializer.attribute("", "VisibleAbove", Integer.toString(Math.round(m.VisibleAbove)));
            serializer.endTag("", "Marker");
        }

        serializer.endTag("", "Properties");
        serializer.endDocument();


        return writer.toString();
    }

    public void FromXml(String xml)
    {
        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("Marker"))
                    {
                        MarkerProperty markerProperty = new MarkerProperty();
                        markerProperty.visible = Boolean.parseBoolean(parser.getAttributeValue(1));
                        markerProperty.VisibleAbove = Float.parseFloat(parser.getAttributeValue(2));
                        markerProperty.airportType = AirportType.valueOf(parser.getAttributeValue(0));
                        this.properties.add(markerProperty);
                    }
                }

                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getWhereByZoomLevel(Float zoomLevel)
    {
        String where = "WHERE type in (";

        for (MarkerProperty mp: properties) {

            if ((zoomLevel >= mp.VisibleAbove) && mp.visible)

                where = where + "'" + mp.airportType.toString()+"',";
        }

        where = where.substring(0, where.length()-1) + ")";

        return where;


    }

    public class MarkerProperty
    {
        public AirportType airportType;
        public boolean visible;
        public Float VisibleAbove;
    }
}