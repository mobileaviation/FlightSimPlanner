package nl.robenanita.googlemapstest.Weather;

import android.content.Context;

import java.util.Date;

import nl.robenanita.googlemapstest.Airport;

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

    public Notam(Context c)
    {
        this.c = c;
        startDate = new Date();
        station_id = "";
    }
    private Context c;

    public String raw_text;
    public String message;
    private String station_id;
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

    public  String NotamCode;

    private NotamType notamType;
    public NotamType GetNotamType() { return notamType; }
    public void SetNotamType(String notamType)
    {
        if (notamType.equals("N")) this.notamType = NotamType.New;
        if (notamType.equals("R")) this.notamType = NotamType.Replace;
        if (notamType.equals("C")) this.notamType = NotamType.Cancel;
    }

    public String source;

    private Date startDate;
    public void SetStartDate(Integer start)
    {
        Integer s = start;
        startDate = new Date((long)start*1000);
    }

    private Date endDate;
    public void SetEndDate(Integer end)
    {
        endDate = new Date((long)end*1000);
    }

    private String qualifier;
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

        i = raw_text.indexOf("Q)");
        Integer s = raw_text.indexOf("A)");
        qualifier = raw_text.substring(i+3, s);

        String[] ii = raw_text.split(" ");
        for (int j=0; j<ii.length; j++)
        {
            if (ii.length>j-2) {
                if (ii[j].equals("B)"))
                    SetStartDate(Integer.parseInt(ii[j + 1].replaceAll("[\\D]", "")));
                if (ii[j].equals("C)"))
                    SetEndDate(Integer.parseInt(ii[j + 1].replaceAll("[\\D]", "")));
            }
        }
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
