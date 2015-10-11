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
        String regex = "(\\bAC\\b)";
        String split[] = text.split(regex);
        Airspace airspace = null;
        for (String l : split)
        {
            String lines[] = l.split("\r\n");
            if (!l.startsWith("*")) {
                Log.i(TAG, lines[1].trim());
                // Check is first char = * then discard this split[*]
                // Read the first line for the Airspace Category (AC)
                // Read the line with starts with AN, Following string is Name
                // -- AH, unit (non if FT), top level limit, folowed by reference (MSL)
                // -- AL, unit (non if FT), bottom level limit, folowed by reference (MSL)

                //

                if (l.startsWith("AC")) {
                }
                if (l.startsWith("AN")) {
                }
                if (l.startsWith("AH")) {
                }
                if (l.startsWith("AL")) {
                }
                if (l.startsWith("V")) {
                }
                if (l.startsWith("DB")) {
                }
                if (l.startsWith("DP")) {
                }
                if (l.startsWith("DC")) {
                }
            }

        }

    }

    public void TestDraw(GoogleMap map)
    {
        Coordinate[] coordinates = new Coordinate[15];
        LatLng l = Helpers.parseOpenAirLocation("DP 53:40:00 N 006:30:00 E");
        coordinates[0] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:38:00 N 006:35:00 E");
        coordinates[1] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:31:00 N 006:41:00 E");
        coordinates[2] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:30:15 N 006:44:30 E");
        coordinates[3] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:24:37 N 006:36:30 E");
        coordinates[4] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:12:25 N 006:09:33 E");
        coordinates[5] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 52:48:03 N 005:17:11 E");
        coordinates[6] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 52:45:54 N 004:56:22 E");
        coordinates[7] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 52:43:30 N 004:33:40 E");
        coordinates[8] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 52:45:25 N 004:28:03 E");
        coordinates[9] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 52:48:19 N 004:21:00 E");
        coordinates[10] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:05:00 N 004:21:00 E");
        coordinates[11] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:06:10 N 004:30:56 E");
        coordinates[12] = new Coordinate(l.longitude, l.latitude);
        l = Helpers.parseOpenAirLocation("DP 53:09:17 N 004:40:28 E");
        coordinates[13] = new Coordinate(l.longitude, l.latitude);

        // for the test to close the polygon
        l = Helpers.parseOpenAirLocation("DP 53:40:00 N 006:30:00 E");
        coordinates[14] = new Coordinate(l.longitude, l.latitude);

        LineString l1 = new GeometryFactory().createLineString(coordinates);

        Geometry[] geometries = new Geometry[3];
        GeometryFactory factory = new GeometryFactory();
        geometries[0] = factory.createPolygon(coordinates);
        factory.createLineString(coordinates);


        LatLng center = Helpers.parseOpenAirLocation("V X=53:15:00 N 004:57:00 E");
        LatLng begin = Helpers.parseOpenAirLocation("DB 53:15:00 N 004:43:38 E");
        LatLng end = Helpers.parseOpenAirLocation("53:19:30 N 004:45:59 E");
        geometries[1] = GeometricHelpers.drawArc(begin, end, center);

        center = Helpers.parseOpenAirLocation("V X=53:15:00 N 004:57:00 E");
        begin = Helpers.parseOpenAirLocation("DB 53:11:06 N 004:38:03 E");
        end = Helpers.parseOpenAirLocation("53:15:00 N 004:36:57 E");
        geometries[2] = GeometricHelpers.drawArc(begin, end, center);


        Geometry t1 = geometries[0].union(geometries[1]);
        Geometry t2 = t1.union(geometries[2]);

//        GeometryFactory factory1 = new GeometryFactory();
//        GeometryCollection col = factory1.createGeometryCollection(geometries);

        //BufferOp bufOp = new BufferOp(arc);
        //bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
//        Geometry buffer;
//        buffer = col.buffer(0.0001);//  bufOp.getResultGeometry(0.00001);

        Coordinate[] co = t2.getCoordinates();
        PolylineOptions o = new PolylineOptions();
        o.color(Color.RED);
        o.width(2);
        o.zIndex(1000);
        for (Coordinate coordinate : co)
        {
            LatLng p = new LatLng(coordinate.y, coordinate.x);
            o.add(p);
        }

        Polyline p = map.addPolyline(o);
        p.setVisible(true);
        p.setZIndex(1000);

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
