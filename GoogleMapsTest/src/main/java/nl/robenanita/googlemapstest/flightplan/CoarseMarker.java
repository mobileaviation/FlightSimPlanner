package nl.robenanita.googlemapstest.flightplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 21-8-2017.
 */

public class CoarseMarker {
    public CoarseMarker(Location from, Location to)
    {
        this.from = from;
        this.to = to;
        this.true_heading = from.bearingTo(to);
        if (this.true_heading<0) this.true_heading = this.true_heading + 360;
        this.compass_heading = this.true_heading;
        this.distance = from.distanceTo(to) / 1852f;
    }

    public CoarseMarker(Location from, Location to, Float compass_heading, Float true_heading,
                        Float distance)
    {
        this.from = from;
        this.to = to;
        this.true_heading = true_heading;
        this.compass_heading = compass_heading;
        this.distance = distance;
    }

    public void UpdateMarker(Location from, Location to, Context context, LatLng halfwayPoint)
    {
        this.true_heading = from.bearingTo(to);
        if (this.true_heading<0) this.true_heading = this.true_heading + 360;
        this.compass_heading = this.true_heading;
        UpdateMarker(to, from, this.true_heading ,this.true_heading, from.distanceTo(to) / 1852f, context, halfwayPoint);
    }

    public void UpdateMarker(Location from, Location to, Float compass_heading, Float true_heading,
                             Float distance, Context context, LatLng halfwayPoint)
    {
        this.from = from;
        this.to = to;
        this.true_heading = true_heading;
        this.compass_heading = compass_heading;
        this.distance = distance;

        coarseMarker.setRotation(true_heading + 90);
        coarseMarker.setIcon(GetIcon(context));
        coarseMarker.setPosition(halfwayPoint);
    }

    private Location from;
    private Location to;
    private Float compass_heading;
    private Float true_heading;
    private Float distance;
    private Marker coarseMarker;

    public BitmapDescriptor GetIcon(Context context)
    {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inMutable = true;
        Bitmap courseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_marker_square, op);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Helpers.convertDpToPixel(16f, context));
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        Canvas aircourseCanvas = new Canvas(courseBitmap);
        String h = "000" + Integer.toString(Math.round(compass_heading));
        String d = Integer.toString(Math.round(distance));
        h = h.substring(h.length()-3) + "\u00b0";
        aircourseCanvas.drawText(h, Helpers.convertDpToPixel(40f, context), Helpers.convertDpToPixel(50f, context), textPaint);
        textPaint.setTextSize(Helpers.convertDpToPixel(14f, context));
        aircourseCanvas.drawText(d, Helpers.convertDpToPixel(40f, context), Helpers.convertDpToPixel(62f, context), textPaint);
        textPaint.setTextSize(Helpers.convertDpToPixel(10f, context));
        aircourseCanvas.drawText("NM", Helpers.convertDpToPixel(60f, context), Helpers.convertDpToPixel(62f, context), textPaint);

        return BitmapDescriptorFactory.fromBitmap(courseBitmap);
    }

    public Marker setCoarseMarker(GoogleMap map, Context context, LatLng halfwayPoint)
    {
        MarkerOptions coarseMarkerOptions;
        coarseMarkerOptions = new MarkerOptions();
        coarseMarkerOptions.position(halfwayPoint);
        coarseMarkerOptions.title(Float.toString(compass_heading));
        coarseMarkerOptions.icon(this.GetIcon(context));
        coarseMarkerOptions.anchor(0.5f, 0.5f);
        coarseMarkerOptions.draggable(false);
        coarseMarkerOptions.rotation(true_heading + 90);
        coarseMarker = map.addMarker(coarseMarkerOptions);
        return coarseMarker;
    }

    public void RemoveCoarseMarker()
    {
        coarseMarker.remove();
    }
}
