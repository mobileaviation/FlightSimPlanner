package nl.robenanita.googlemapstest.openaip;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.AirportType;

/**
 * Created by Rob Verhoef on 29-9-2015.
 */
public class Airspaces extends ArrayList<Airspace> {
    private String TAG = "GooglemapsTest";

    public Airspaces()
    {

    }

    public void Add(Airspace airspace)
    {
        this.add(airspace);
    }

    public void OpenAipFile(Context context, String filename)
    {
        //String _filename = Environment.getExternalStorageDirectory().toString()+"/Download/" + filename;
        String _filename = "/sdcard/Download/" + filename;
        String XML = readFromFile(context, _filename);
        readXML(XML);
        Log.i(TAG, "XML Read");
    }

    private void readXML(String xml)
    {
        XmlPullParser parser = Xml.newPullParser();
        Airspace airspace = null;
        Boolean readAltTop = true;
        String name = "";

        try {
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_TAG) {
                    name = parser.getName();
                    if (name.equals("ASP")) {
                        airspace = new Airspace();
                        Add(airspace);
                        airspace.Category = AirspaceCategory.valueOf(parser.getAttributeValue(null, "CATEGORY"));
                    }

                    if (name.equals("ALTLIMIT_TOP")) {
                        readAltTop = true;
                        if (airspace != null) {
                            String a = parser.getAttributeValue(null, "REFERENCE");
                            airspace.AltLimit_Top_Ref = AltitudeReference.valueOf(
                                    parser.getAttributeValue(null, "REFERENCE"));
                        }
                    }

                    if (name.equals("ALTLIMIT_BOTTOM")) {
                        readAltTop = false;
                        if (airspace != null) {
                            String a = parser.getAttributeValue(null, "REFERENCE");
                            airspace.AltLimit_Bottom_Ref = AltitudeReference.valueOf(
                                    parser.getAttributeValue(null, "REFERENCE"));
                        }
                    }

                    if (name.equals("ALT")) {
                        if (readAltTop) {
                            if (airspace != null) {
                                airspace.AltLimit_Top_Unit = AltitudeUnit.valueOf(
                                        parser.getAttributeValue(null, "UNIT"));
                            }
                        } else {
                            if (airspace != null) {
                                airspace.AltLimit_Bottom_Unit = AltitudeUnit.valueOf(
                                        parser.getAttributeValue(null, "UNIT"));
                            }
                        }
                    }

                }
                if (eventType == XmlPullParser.TEXT) {
                    if (name.equals("VERSION")) {
                        if (airspace != null) airspace.Version = parser.getText();
                    }

                    if (name.equals("ID")) {
                        if (airspace != null) airspace.ID = Integer.parseInt(parser.getText());
                    }

                    if (name.equals("COUNTRY")) {
                        if (airspace != null) airspace.Country = parser.getText();
                    }

                    if (name.equals("NAME")) {
                        if (airspace != null) airspace.Name = parser.getText();
                    }



                    if (name.equals("ALT")) {
                        if (readAltTop) {
                            if (airspace != null) {
                                airspace.AltLimit_Top = Integer.parseInt(parser.getText());
                            }
                        } else {
                            if (airspace != null) {
                                airspace.AltLimit_Bottom = Integer.parseInt(parser.getText());
                            }
                        }
                    }
                    if (name.equals("POLYGON"))
                    {
                        if (airspace != null) {
                            String p = "POLYGON ((" + parser.getText() + "))";
                            WKTReader r = new WKTReader();
                            airspace.Geometry = r.read(p);
                        }
                    }


                    name = "";
                }

                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(Context context, String fileName) {
        if (context == null) {
            return null;
        }

        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream (new File(fileName));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int size = inputStream.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                inputStream.close();
                ret = new String(buffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

}
