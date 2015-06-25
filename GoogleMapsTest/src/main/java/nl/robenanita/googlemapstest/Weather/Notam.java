package nl.robenanita.googlemapstest.Weather;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTWriter;

import java.util.Calendar;
import java.util.Date;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.Helpers;

/**
 * Created by Rob Verhoef on 4-3-2015.
 */
public class Notam {
    public enum NotamType
    {
        New,
        Replace,
        Cancel
    }

    public Notam()
    {
        startDate = new Date();
        station_id = "";
    }

    private String TAG = "GooglemapsTest";
    public String raw_text = "";
    public String message = "";
    private String station_id = "";

    private String fir = "";

    public void setStation_id(String station_id)
    {
        this.station_id = station_id;
//        AirportDataSource airportDataSource = new AirportDataSource(c);
//        airportDataSource.open(-1);
//        airport = airportDataSource.GetAirportByIDENT(station_id);
//        airportDataSource.close();
    }
    public String getStation_id()
    {
        return station_id;
    }

    public Airport airport;

    public  String NotamCode = "";

    public String NotamNumber = "";

    private NotamType notamType;
    public NotamType GetNotamType() { return notamType; }
    public void SetNotamType(String notamType)
    {
        if (notamType.equals("N")) this.notamType = NotamType.New;
        if (notamType.equals("R")) this.notamType = NotamType.Replace;
        if (notamType.equals("C")) this.notamType = NotamType.Cancel;
    }

    public String source = "";

    private Date notamTimeToDate(String notamTime)
    {
        // can also be "PERM"
        if (notamTime.length()>10) {

            notamTime = notamTime.replaceAll("[\\D]", "");

            Integer yy = Integer.parseInt(notamTime.substring(0, 2)) + 2000;
            Integer m = Integer.parseInt(notamTime.substring(2, 4));
            Integer d = Integer.parseInt(notamTime.substring(4, 6));
            Integer h = Integer.parseInt(notamTime.substring(6, 8));
            Integer mm = Integer.parseInt(notamTime.substring(8, 10));

            Calendar calendar = Calendar.getInstance();
            calendar.set(yy, m, d, h, m);
            return calendar.getTime();
        }
        else
        {
            return null;
        }
    }

    private Date startDate;
    public void SetStartDate(String start)
    {
        // 03 FEB 10:25 2015 = 1502031025
        startDate = notamTimeToDate(start);
    }
    public Date GetStartDate() { return (startDate!= null ? startDate : new Date()); }

    private Date endDate;
    public void SetEndDate(String end)
    {
        endDate = notamTimeToDate(end);
    }
    public Date GetEndDate() { return (endDate!= null ? endDate : new Date()); }

    private String qualifier = "";
    public void SetQualifier(String qualifier)
    {
        this.qualifier = qualifier;
    }


    public void SetRawText(String raw)
    {
        raw_text = raw;
        // more processing
        Integer i = raw_text.indexOf("NOTAM");
        SetNotamType(raw_text.substring(i+5, i+6));

        NotamNumber = raw_text.substring(0, i).trim();

        i = raw_text.indexOf("Q)");
        if (i>-1) {
            Integer s = raw_text.indexOf("A)");
            if (s>-1) {
                qualifier = raw_text.substring(i + 3, s);
                String[] qq = qualifier.split("/");
                if (qq.length==7) {
                    fir = qq[0];
                    location = qq[7];
                }
            }
        }

        String[] ii = raw_text.split(" ");

        for (int j=0; j<ii.length; j++)
        {
            if (ii.length>j-2) {
                if (ii[j].equals("B)"))

                    SetStartDate(ii[j + 1]);
                if (ii[j].equals("C)"))
                    SetEndDate(ii[j + 1]);
            }
        }

        getPosition();
    }

    public void SetRawFAAText(String FAARaw)
    {
        if (FAARaw.contains("NOTAM")) //&& FAARaw.contains("Q)")
        {
            // This is an european formatted Notam
            // Process using SetRawText
            SetRawText(FAARaw);
            // And extract the message
            if (FAARaw.contains("E)"))  // This is the description which should always be there
            {
                String message = FAARaw.substring(FAARaw.indexOf("E)")+2,
                        (FAARaw.indexOf("F)")>-1) ? FAARaw.indexOf("F)") : FAARaw.length() );


                if (FAARaw.contains("D)"))
                {
                    String d = FAARaw.substring(FAARaw.indexOf("D)") + 2, FAARaw.length());
                    message = message + "\n" + d.substring(1, (d.indexOf(")")>-1) ? d.indexOf(")")-1 : d.length());
                }

                SetMessage(message.trim());
            }
        }
        else
        {
            // American style Notam
            // Starts with a !
            if (FAARaw.startsWith("!"))
            {
                // Message starts a 3rd space
                raw_text = FAARaw;
                String[] mm = FAARaw.split(" ");
                String m = FAARaw.substring(mm[0].length()+1 + mm[1].length()+1 + mm[2].length()+1, FAARaw.length());
                NotamNumber = mm[0].substring(1, mm[0].length()) + "_" + mm[1];
                SetMessage(m);
            }
        }
    }

    public void PlaceNotamMarker(GoogleMap map)
    {
        LatLng pos = getPosition();
        if (pos != null) {
            CircleOptions co = new CircleOptions();
            co.center(getPosition());
            co.fillColor(Color.CYAN);
            co.radius(500);
            co.strokeColor(Color.BLACK);
            co.strokeWidth(1);
            marker = map.addCircle(co);
            map.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }
    private Circle marker;

    private LatLng getPosition()
    {
        // Dutch notams
        // PSN 521837N0045613E
        LatLng latLng = null;

        latLng = Helpers.getDutchFormatPosition(raw_text);

        return latLng;
    }

    private String location = "";
    public String getLocationString()
    {
        String[] ll = location.split("[NSWE]");

        // 5259N00454E999
        // lat 52.59 N 00 4.54E

        // 5218N00446E005
        // lat 52.18N00 4.46E00  5NM
        // 5218
        // 00446
        // 005

        if (ll.length==3) {

            String min = null;
            String deg = null;
            String sec = null;
            Double lat = null;

            try {
                min = ll[0].substring(ll[0].length() - 2, ll[0].length());
                deg = ll[0].substring(0, ll[0].length() - 2);
                sec = ll[1].substring(0, 2);

                lat = Double.valueOf(deg) + (Double.valueOf(min) / 60) + (Double.valueOf(sec) / 3600)
                        * ((location.contains("S")) ? -1 : 1);
            } catch (NumberFormatException e) {
                lat = Double.valueOf(0d);
            }

            Double lon = null;
            try {
                ll[1] = ll[1].substring(2, ll[1].length());

                min = ll[1].substring(ll[1].length() - 2, ll[1].length());
                deg = ll[1].substring(0, ll[1].length() - 2);
                sec = ll[2].substring(0, 2);

                lon = Double.valueOf(deg) + (Double.valueOf(min) / 60) + (Double.valueOf(sec) / 3600)
                        * ((location.contains("W")) ? -1 : 1);
            } catch (NumberFormatException e) {
                lon = Double.valueOf(0d);
            }

            // 5129N00023W005
            // lat 51.29N00 0.23W00 5NM

            //([LATITUDE_DEG])+([LATITUDE_MIN]/60)+([LATITUDE_SEC]/3600))*
            //IF [Latitude_Direction]="South" THEN -1 ELSE 1 END


            // lon, lat
            Coordinate geographicalLocation = new Coordinate(lon, lat);
            return WKTWriter.toPoint(geographicalLocation);
        }
        else return (airport != null) ?
                WKTWriter.toPoint(new Coordinate(airport.longitude_deg, airport.latitude_deg))
                : "Unknown";
    }


    public void SetMessage(String message)
    {
        this.message = message;
    }
    public String GetMessage()
    {
        return this.message;
    }

}
