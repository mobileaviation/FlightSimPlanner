package nl.robenanita.googlemapstest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rob Verhoef on 18-1-14.
 */
public class Airport implements Serializable {
    public Airport()
    {
        runways = new RunwaysList();
        frequencies = new ArrayList<Frequency>();
    }

    public int id;
    public AirportType type;
    public String name;
    public String ident;
    public double latitude_deg;
    public double longitude_deg;
    public double elevation_ft;
    public String continent;
    public String iso_country;
    public String iso_region;
    public String municipality;
    public String scheduled_service;
    public String gps_code;
    public String iata_code;
    public String local_code;
    public String home_link;
    public String wikipedia_link;
    public String keywords;
    public int version;
    public Date modified;
    public Marker marker;
    public double heading;
    public Integer MapLocation_ID;

    public RunwaysList runways;

    public ArrayList<Frequency> frequencies;

    public LatLng getLatLng()
    {
        return new LatLng(latitude_deg, longitude_deg);
    }
    public String getPointString()
    {
        Coordinate c = new Coordinate(longitude_deg, latitude_deg);
        return WKTWriter.toPoint(c);
    }

    public Bitmap GetSmallIcon(float angle, String iata_code, Context context)
    {
        if (type == AirportType.heliport) return BitmapFactory.decodeResource(context.getResources(), R.drawable.heliport);
        else
        return _getSmallAirportIcon(angle, iata_code, context );
    }

    public BitmapDescriptor GetIcon(float angle, String iata_code, Context context)
    {
        if (type == AirportType.large_airport) {
            return getLargeAirportIcon(iata_code, context);
            //return BitmapDescriptorFactory.fromResource(R.drawable.large_airport);
        }
        if (type == AirportType.heliport) return BitmapDescriptorFactory.fromResource(R.drawable.heliport);
        //if (type == AirportType.small_airport) return BitmapDescriptorFactory.fromResource(R.drawable.small_airport);
        //if (type == AirportType.medium_airport) return BitmapDescriptorFactory.fromResource(R.drawable.medium_airport);

        if (type == AirportType.medium_airport) return getSmallAirportIcon(angle, iata_code, context );
        if (type == AirportType.small_airport) return getSmallAirportIcon(angle, iata_code, context );


        //if (type == AirportType.closed) return null;

        return null;
    }

    private BitmapDescriptor getLargeAirportIcon(String iata_code, Context context)
    {
        Double width = 100d, height = 100d;
        Double centerX_deg = runways.getCenterX_deg();
        Double centerY_deg = runways.getCenterY_deg();

        Bitmap airportBitmap = Bitmap.createBitmap(width.intValue(), height.intValue(), Bitmap.Config.ARGB_8888);
        Canvas airportCanvas = new Canvas(airportBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.argb(40,0,0,0));
        airportCanvas.drawCircle((width.floatValue()/2), (height.floatValue()/2), (width.floatValue()/2)-2, p);

        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLUE);
        airportCanvas.drawCircle((width.floatValue()/2), (height.floatValue()/2), (width.floatValue()/2)-2, p);

        p.setStrokeWidth(2);
        p.setColor(Color.BLACK);

        Point lefTop = new Point(1000,1000);
        Point rightBotton = new Point(0,0);
        for (Runway r : this.runways)
        {
            double left = 50 + ((r.le_longitude_deg - centerX_deg) * 500);
            double top = 50 + ((centerY_deg - r.le_latitude_deg) * 1000);
            double right = 50 + ((r.he_longitude_deg - centerX_deg) * 500);
            double bottom = 50 + ((centerY_deg - r.he_latitude_deg) * 1000);

            if (lefTop.x>(int)left) lefTop.x = (int)left;
            if (lefTop.y>(int)top) lefTop.y = (int)top;
            if (rightBotton.x<(int)right) rightBotton.x = (int)right;
            if (rightBotton.y<(int)bottom) rightBotton.y = (int)bottom;

            p.setStrokeWidth(6);
            p.setColor(Color.argb(255,110,245,78));
            airportCanvas.drawLine((float)left, (float)top, (float)right, (float)bottom, p);
            p.setStrokeWidth(2);
            p.setColor(Color.BLACK);
            airportCanvas.drawLine((float)left, (float)top, (float)right, (float)bottom, p);
        }

//        int w = rightBotton.x - lefTop.x;
//        w = (w<50) ? 50 : w;
//        int h = rightBotton.y - lefTop.y;
//        h = (h<50) ? 50 : h;
//        if (w>h) h=w; else w=h;
//        Bitmap completeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas completeCanvas = new Canvas(completeBitmap);
//        Rect srcRect = new Rect(lefTop.x, lefTop.y, rightBotton.x, rightBotton.y);
//        RectF dstRect = new RectF(0f,0f,w, h);
//        completeCanvas.drawBitmap(airportBitmap, srcRect,dstRect, p);



        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(20);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        airportCanvas.drawText(iata_code, (width.intValue()/2)+1 ,21,textPaint);
        textPaint.setColor(Color.argb(255,246,249,89));
        airportCanvas.drawText(iata_code, (width.intValue()/2),20,textPaint);

        Bitmap dstBitmap = Bitmap.createBitmap((int)Helpers.convertDpToPixel(100, context),
                (int)Helpers.convertDpToPixel(100, context),
                Bitmap.Config.ARGB_8888);
        Canvas dstCanvas = new Canvas(dstBitmap);
        Rect scrR = new Rect(0, 0, (int)airportBitmap.getWidth(), (int)airportBitmap.getHeight());
        RectF dstR = new RectF(0f, 0f, (float)dstBitmap.getWidth(), (float)dstBitmap.getHeight());
        dstCanvas.drawBitmap(airportBitmap, scrR, dstR, null);

        return BitmapDescriptorFactory.fromBitmap(dstBitmap);
    }

    private BitmapDescriptor getSmallAirportIcon(float angle, String ICAOCode, Context context)
    {
        return BitmapDescriptorFactory.fromBitmap(_getSmallAirportIcon(angle, ICAOCode, context));
    }

    private Bitmap _getSmallAirportIcon(float angle, String ICAOCode, Context context)
    {
        Bitmap rotateBitmap = Bitmap.createBitmap(30, 30,
                Bitmap.Config.ARGB_8888);
        Canvas rotateCanvas = new Canvas(rotateBitmap);
        rotateCanvas.save();
        rotateCanvas.rotate(angle,15,15);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);

        rotateCanvas.drawCircle(15,15,11,p);

        RectF r = new RectF(12,0,18,30);

        p.setColor(Color.WHITE);
        p.setStrokeWidth(1);
        p.setStyle(Paint.Style.FILL);
        rotateCanvas.drawRect(r, p);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        //rotateCanvas.drawRect(11,0,19,30, p);

        rotateCanvas.drawRect(r, p);


        rotateCanvas.restore();

        Bitmap textBitmap = Bitmap.createBitmap(60, 45,
                Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        textCanvas.drawBitmap(rotateBitmap, 15,15, null);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.argb(50,0,0,0));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(14);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textCanvas.drawText(ICAOCode, 31,14,textPaint);
        textPaint.setColor(Color.BLUE);
        textCanvas.drawText(ICAOCode, 30,13,textPaint);


        Bitmap dstBitmap = Bitmap.createBitmap((int)Helpers.convertDpToPixel(60, context),
                (int)Helpers.convertDpToPixel(45, context),
                Bitmap.Config.ARGB_8888);
        Canvas dstCanvas = new Canvas(dstBitmap);
        Rect scrR = new Rect(0, 0, (int)textBitmap.getWidth(), (int)textBitmap.getHeight());
        RectF dstR = new RectF(0f, 0f, (float)dstBitmap.getWidth(), (float)dstBitmap.getHeight());
        dstCanvas.drawBitmap(textBitmap, scrR, dstR, null);

        return dstBitmap;
        
    }

    public static AirportType ParseAirportType(String type)
    {
        AirportType a = AirportType.closed;
        int i = 0;
        if (type.equals("closed")) a = AirportType.closed;
        if (type.equals("heliport")) a = AirportType.heliport;
        if (type.equals("small_airport")) a = AirportType.small_airport;
        if (type.equals("medium_airport")) a = AirportType.medium_airport;
        if (type.equals("large_airport")) a = AirportType.large_airport;

        return a;
    }

    public String getAirportInfoString()
    {
        String info;

        info = "Airport Information................\n" +
                "Name : " + this.name + "\n" +
                "Code : " + this.ident + "\n" +
                "Type : " + this.type.toString() + "\n" +
                "\n" +
                "Location..........................\n" +
                "Latitude  : " + Double.toString(this.latitude_deg) + "\n" +
                "Longitude : " + Double.toString(this.longitude_deg) + "\n" +
                "Elevation : " + Long.toString(Math.round(this.elevation_ft)) + " ft";

        return info;
    }

    public String getRunwaysInfo()
    {
        String info = "No runway information.";
        if (this.runways != null)
        {
            if (this.runways.size()>0)
            {
                info = "Runways...........................\n";
                for(Runway r : this.runways)
                {
                    info = info + "No      : " + r.le_ident + "," + r.he_ident + "\n" +
                            "Length  : " + Long.toString(Math.round(r.length_ft)) + " ft\n" +
                            "Heading : " + Long.toString(Math.round(r.le_heading_degT)) + " : " + Long.toString(Math.round(r.he_heading_degT)) + "\n" +
                            "Displaced threshold: " + Long.toString(r.le_displaced_threshold_ft) + " ft : "  + Long.toString(r.he_displaced_threshold_ft) + " ft\n" +
                            "Suface  : " + r.surface + "\n" +
                            "----------------------------------------------\n" ;

                }
            }
        }

        return info;
    }

    public String getFrequenciesInfo()
    {
        String info = "No frequencies information.";
        if (this.frequencies != null)
        {
            if (this.frequencies.size()>0)
            {
                info = "Frequencies......................\n";
                for (Frequency f : this.frequencies)
                {
                    info = info + Double.toString(f.frequency_mhz) + " MHz : " + f.description + "\n";
                }
            }
        }

        return info;
    }
}


