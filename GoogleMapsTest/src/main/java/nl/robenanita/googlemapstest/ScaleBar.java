package nl.robenanita.googlemapstest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Rob Verhoef on 9-5-2014.
 */
public class ScaleBar{
    private static final String STR_M = "m";
    private static final String STR_KM = "km";

    //Constants


    //instantiation
    private Context context;

    private int width, height, pi;
    private String unit;

    public ScaleBar(Context context)
    {
        this.context = context;
        setup();
    }

    private void setup()
    {
        width = this.context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        height = this.context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    private Location right;
    private Location left;

    public void DrawScaleBar(ImageView imageView, boolean shadow, LatLngBounds bounds) {
        right = new Location("lt");
        right.setLatitude(bounds.southwest.latitude);
        right.setLongitude(bounds.southwest.longitude);
        left = new Location("br");
        left.setLatitude(bounds.southwest.latitude);
        left.setLongitude(bounds.northeast.longitude);

        Bitmap myCanvasBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
        Bitmap tempBitmap = myCanvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas myCanvas = new Canvas(tempBitmap);

        this.DrawBar(myCanvas);

        imageView.setImageBitmap(tempBitmap);

    }

    public void DrawBar(Canvas canvas)
    {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);
        p.setStrokeWidth(1);
        p.setTextSize(15);
        canvas.drawLine(0,20, 0,49, p);
        canvas.drawLine(0,49, 249, 49, p);
        canvas.drawLine(249, 49, 249, 20, p);
        canvas.drawLine(0, 29, 249, 29, p);

        canvas.drawRect(0,29, 49, 39, p);
        canvas.drawRect(50,40, 99, 49, p);
        canvas.drawRect(100,30, 149, 39, p);
        canvas.drawRect(150,40, 199, 49, p);
        canvas.drawRect(200,30, 249, 39, p);


        float widthFactor = left.distanceTo(right) / width;
        float dis = widthFactor * canvas.getWidth();
        if (dis>1000) {
            unit = "Km";
            dis = dis / 1000;
        }else
        {
            unit = "m";
        }
        Integer d1 = 0;
        Integer d2 = Math.round(dis/6);
        Integer d3 = d2 + Math.round(dis/6);
        Integer d4 = d3 + Math.round(dis/6);
        Integer d5 = d4 + Math.round(dis/6);

        canvas.drawText(d1.toString(), 5, 19,p);
        canvas.drawText(d2.toString(), 55, 19,p);
        canvas.drawText(d3.toString(), 105, 19,p);
        canvas.drawText(d4.toString(), 155, 19,p);
        canvas.drawText(d5.toString(), 205, 19,p);

        canvas.drawText(unit, 255, 19, p);
    }
}
