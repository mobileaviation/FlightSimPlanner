package nl.robenanita.googlemapstest.openaip;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.robenanita.googlemapstest.AirportType;
import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.database.AirspacesDataSource;

/**
 * Created by Rob Verhoef on 29-9-2015.
 */
public class Airspaces extends ArrayList<Airspace> {
    private String TAG = "GooglemapsTest";

    public Airspaces(Context context)
    {
        progressDialog = null;
        this.context = context;
    }

    public Airspaces(Context context, ProgressDialog progressDialog)
    {
        this.progressDialog = progressDialog;
        this.context = context;
    }

    private ProgressDialog progressDialog;
    private Context context;

    public void Add(Airspace airspace)
    {
        this.add(airspace);
    }

    public void OpenAipFile(Context context, String filename)
    {
        //String _filename = Environment.getExternalStorageDirectory().toString()+"/Download/" + filename;
        String _filename = "/sdcard/Download/airnavdb/" + filename;
        String XML = readFromFile(context, _filename);
        readOpenAipXML(XML);
        Log.i(TAG, "XML Read");



        insertIntoDatabase();
        Log.i(TAG, "Database Insert Finished");
    }

    public void OpenOpenAirTextFile(Context context, String filename)
    {
        String _filename = "/sdcard/Download/airnavdb/" + filename;
        String txt = readFromFile(context, _filename);
        readOpenAirText(txt);
    }



    private void insertIntoDatabase()
    {
        AirspacesDataSource dataSource = new AirspacesDataSource(context);
        dataSource.open();

        for (Airspace airspace : this)
        {
            dataSource.insertAirspace(airspace);
        }

        dataSource.close();
    }

    private void readOpenAirText(String text)
    {
        String lines[] = text.split("\r\n");
        Airspace airspace = null;
        LatLng location = null;
        LatLng center = null;
        Boolean circle  = false;
        Boolean positive = true;
        for (String l : lines)
        {

            if (!l.startsWith("*")) {
                // Check is first char = * then discard this split[*]
                // Read the first line for the Airspace Category (AC)
                // Read the line with starts with AN, Following string is Name
                // -- AH, unit (non if FT), top level limit, folowed by reference (MSL)
                // -- AL, unit (non if FT), bottom level limit, folowed by reference (MSL)

                //

                if (l.startsWith("AC")) {
                    if ((airspace != null) && (airspace.coordinates.size()>0) && !circle) airspace.coordinates.add(airspace.coordinates.get(0));
                    airspace = new Airspace();
                    this.add(airspace);
                    airspace.Category = AirspaceCategory.valueOf(l.replace("AC ", "").trim());
                }
                if (l.startsWith("AN")) {
                    if (airspace != null) {
                        airspace.Name = l.replace("AN ", "");
                        Log.i(TAG, "Airspace: " + airspace.Name + " added Index: " + Integer.toString(this.size()-1) );
                    }

                }
                if (l.startsWith("AH")) {
                    if (airspace != null) {
                        airspace.AltLimit_Top = Integer.getInteger(Helpers.findRegex("\\d+", l), 0);
                        String m = Helpers.findRegex("([MSL]+)|([FL]+)|([FT]+)|([SFC]+)|([UNLIM]+)|([AGL]+)", l);
                        if (m.equals("UNLIM")) airspace.AltLimit_Top = 100000;
                        airspace.AltLimit_Top_Ref = Helpers.parseReference(m);
                        airspace.AltLimit_Top_Unit = Helpers.parseUnit(m);
                    }
                }
                if (l.startsWith("AL")) {
                    if (airspace != null) {
                        airspace.AltLimit_Bottom = Integer.getInteger(Helpers.findRegex("\\d+", l), 0);
                        String m = Helpers.findRegex("([MSL]+)|([FL]+)|([FT]+)|([SFC]+)|([UNLIM]+)|([AGL]+)", l);
                        if (m.equals("UNLIM")) airspace.AltLimit_Top = 100000;
                        airspace.AltLimit_Bottom_Ref = Helpers.parseReference(m);
                        airspace.AltLimit_Bottom_Unit = Helpers.parseUnit(m);
                    }
                }
                if (l.startsWith("V D=-")) positive = false;
                if (l.startsWith("V D=+")) positive = true;

                if (l.startsWith("V X")) {
                    center = Helpers.parseOpenAirLocation(l);
                }
                if (l.startsWith("DB")) {
                    String[] be = l.split(",");
                    LatLng begin = Helpers.parseOpenAirLocation(be[0]);
                    LatLng end = Helpers.parseOpenAirLocation(be[1]);
                    if (positive) airspace.coordinates.addAll(GeometricHelpers.drawArc(begin, end, center, positive));
                        else airspace.coordinates.addAll(GeometricHelpers.drawArc(end, begin, center, positive));
                    circle = false;
                }
                if (l.startsWith("DP")) {
                    location = Helpers.parseOpenAirLocation(l);
                    airspace.coordinates.add(new Coordinate(location.longitude, location.latitude));
                    circle = false;
                }
                if (l.startsWith("DC")) {
                    String m = Helpers.findRegex("([0-9.]+\\w)|([0-9])", l);
                    airspace.coordinates.addAll(GeometricHelpers.drawCircle(center, Double.valueOf(m)));
                    circle = true;
                }
            }
        }
        if ((airspace != null) && (airspace.coordinates.size()>0)) airspace.coordinates.add(airspace.coordinates.get(0));
    }

    public void TestDraw(GoogleMap map)
    {
        Coordinate[] c1 = new Coordinate[14];
        List<Coordinate> list = new ArrayList<Coordinate>();

        LatLng l = Helpers.parseOpenAirLocation("DP 53:40:00 N 006:30:00 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:38:00 N 006:35:00 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:31:00 N 006:41:00 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:30:15 N 006:44:30 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:24:37 N 006:36:30 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:12:25 N 006:09:33 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 52:48:03 N 005:17:11 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 52:45:54 N 004:56:22 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 52:43:30 N 004:33:40 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 52:45:25 N 004:28:03 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 52:48:19 N 004:21:00 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:05:00 N 004:21:00 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:06:10 N 004:30:56 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:09:17 N 004:40:28 E");
        list.add(new Coordinate(l.longitude, l.latitude));

        LatLng center = Helpers.parseOpenAirLocation("V X=53:15:00 N 004:57:00 E");
        LatLng begin = Helpers.parseOpenAirLocation("DB 53:11:06 N 004:38:03 E");
        LatLng end = Helpers.parseOpenAirLocation("53:15:00 N 004:36:57 E");
        list.addAll(GeometricHelpers.drawArc(begin, end, center, true));

        center = Helpers.parseOpenAirLocation("V X=53:15:00 N 004:57:00 E");
        begin = Helpers.parseOpenAirLocation("DB 53:15:00 N 004:43:38 E");
        end = Helpers.parseOpenAirLocation("53:19:30 N 004:45:59 E");
        list.addAll(GeometricHelpers.drawArc(begin, end, center, true));

        center = Helpers.parseOpenAirLocation("V X=53:15:10 N 004:57:00 E");
        begin = Helpers.parseOpenAirLocation("DB 53:19:30 N 004:45:59 E");
        end = Helpers.parseOpenAirLocation("53:22:27 N 004:52:07 E");
        list.addAll(GeometricHelpers.drawArc(begin, end, center, true));

        l = Helpers.parseOpenAirLocation("DP 53:26:20 N 005:09:40 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:26:30 N 005:10:30 E");
        list.add(new Coordinate(l.longitude, l.latitude));
        l = Helpers.parseOpenAirLocation("DP 53:30:00 N 005:34:00 E");
        list.add(new Coordinate(l.longitude, l.latitude));

        list.add(list.get(0));

        PolylineOptions o = new PolylineOptions();
        o.color(Color.RED);
        o.width(2);
        o.zIndex(1000);
        for (Coordinate coordinate : list)
        {
            LatLng p = new LatLng(coordinate.y, coordinate.x);
            o.add(p);
        }

        Polyline p = map.addPolyline(o);
        p.setVisible(true);
        p.setZIndex(1000);

    }

    public LatLng drawAirspace(Airspace airspace, GoogleMap map)
    {
        PolylineOptions o = new PolylineOptions();
        o.color(Color.RED);
        o.width(2);
        o.zIndex(1000);
        LatLng pos = null;
        for (Coordinate coordinate : airspace.coordinates)
        {
            pos = new LatLng(coordinate.y, coordinate.x);
            o.add(pos);
        }

        Polyline p = map.addPolyline(o);
        p.setVisible(true);
        p.setZIndex(1000);
        return pos;
    }

    private void readOpenAipXML(String xml)
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
                        if (airspace != null) {
                            airspace.Name = parser.getText();
                            if (progressDialog != null) progressDialog.setMessage("Loading Airspaces: " + airspace.Name);
                            Log.i(TAG, "Load Airspace: " + airspace.Name);
                        }
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
