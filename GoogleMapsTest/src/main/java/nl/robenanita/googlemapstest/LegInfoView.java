package nl.robenanita.googlemapstest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import nl.robenanita.googlemapstest.Route.Leg;
import nl.robenanita.googlemapstest.Route.Waypoint;

/**
 * Created by Rob Verhoef on 4-8-2014.
 */
public class LegInfoView extends View {
    public LegInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LegInfoView,
                0, 0);

        try {
            courceDeviation = a.getFloat(R.styleable.LegInfoView_courseDeviation, 0);
        } finally {
            a.recycle();
        }

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.argb(50, 100, 100, 100));

        clearPaint = new Paint();
        clearPaint.setStyle(Paint.Style.FILL);
        clearPaint.setColor(Color.TRANSPARENT);

        textColor = Color.DKGRAY;

        toDraw = drawType.all;

        distance = Distance.larger2000Meters;
    }

    private Paint bgPaint;
    private Paint clearPaint;
    private int textColor;
    protected enum drawType
    {
        all,
        waypointtext,
        arrow
    };
    drawType toDraw;

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int w = getWidth();

        if (toDraw==drawType.all) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.DKGRAY);
            canvas.drawPaint(paint);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.DKGRAY);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(15);
            textPaint.setTextAlign(Paint.Align.CENTER);

            //canvas.drawLines(leginfobounds, paint);
            canvas.drawLine(1, 1, w - 1, 1, paint);
            canvas.drawLine(w - 1, 1, w - 1, 100, paint);
            canvas.drawLine(w - 1, 100, 1, 100, paint);
            canvas.drawLine(1, 1, 1, 100, paint);
            canvas.drawLine(1, 35, w - 1, 35, paint);

            canvas.drawLine(1, 70, w, 70, paint);
            for (int i = 0; i < 20; i++) {
                float x = (float) i * ((float) getWidth() / 20);

                canvas.drawLine(x, 55, x, 70, paint);
                if (i == 1) canvas.drawText("-10", x - 1, 53, textPaint);
                if (i == 5) canvas.drawText("-5", x - 1, 53, textPaint);
                if (i == 10) canvas.drawText("0", x - 1, 53, textPaint);
                if (i == 15) canvas.drawText("5", x - 1, 53, textPaint);
                if (i == 19) canvas.drawText("10", x - 1, 53, textPaint);
            }

            // Use Color.parseColor to define HTML colors
//        paint.setColor(Color.parseColor("#CD5C5C"));
//        canvas.drawCircle(x / 2, y / 2, radius, paint);
        }

        if ((toDraw == drawType.all) || (toDraw==drawType.waypointtext))  DrawText(canvas);
        if ((toDraw == drawType.all) || (toDraw==drawType.arrow)) DrawDeviationArrow(canvas);


    }

    private void DrawDeviationArrow(Canvas canvas)
    {
        float r = ((float) getWidth()/2)/10;
        float x = ((float) getWidth()/2) + (courceDeviation*r);
        DrawArrow(canvas, Math.round(x), 75);
    }

    private void DrawArrow(Canvas canvas, int x, int y)
    {
        // erase arrow
        if (toDraw == drawType.arrow) {
            canvas.drawRect(2, 75, getWidth() - 2, getHeight() - 2, clearPaint);
            canvas.drawRect(2, 75, getWidth() - 2, getHeight() - 2, bgPaint);
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(Color.DKGRAY);

        canvas.drawLine(x,y, x, y+20, paint);
        canvas.drawLine(x,y, x-5, y+5, paint);
        canvas.drawLine(x,y, x+5, y+5, paint);
    }

    private void DrawText(Canvas canvas)
    {
        // erase text
        if (toDraw == drawType.waypointtext) {
            canvas.drawRect(2, 2, getWidth() - 2, 33, clearPaint);
            canvas.drawRect(2, 2, getWidth() - 2, 33, bgPaint);
        }

        int w = getWidth();

        Paint textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        //textPaint.setStrokeWidth(1);
        textPaint.setTextSize(15);

        canvas.drawText("To: " + _to, 2, 15, textPaint );
        canvas.drawText("DST: " + _dst, 2, 32, textPaint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("TRK: " + _trk + "    HDG: " + _hdg, w / 2, 32, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("ETA: " + _eta, w-2, 32, textPaint);
        canvas.drawText("ETE: " + _ete, w-2, 15, textPaint);

    }

    private String _to = "...";
    private String _dst = "..NM";
    private String _hdg = "000";
    private String _trk = "000";
    private String _eta = "..:..";
    private String _ete = "..:..:..";

    private float courceDeviation;
    public void setCourceDeviation(float courceDeviation)
    {
        this.courceDeviation = courceDeviation;
        toDraw = drawType.arrow;
        invalidate();
        requestLayout();
    }


    private Distance distance;
    public void setActiveLeg(Leg activeLeg, Distance distance)
    {
        this.distance = distance;
        setActiveLeg(activeLeg);
    }

    public void setActiveLeg(Leg activeLeg)
    {
        Waypoint m_to = activeLeg.getToWaypoint();
        this.courceDeviation = activeLeg.getDeviationFromTrack();

        _to = m_to.name;
        _dst = String.format("%.1f NM", activeLeg.getDistanceNM());
        _trk = String.format("%03d", activeLeg.getTrueTrack()); //+ " : " +
        //        String.format("%.4f", this.courceDeviation) + " : " +
        _hdg = String.format("%03d", activeLeg.getCourseTo());
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        _eta = (m_to.eto == null) ? "NA" : ft.format(m_to.eto);
        Calendar c = new GregorianCalendar(2013,1,1);
        c.add(Calendar.SECOND, activeLeg.getSecondsToGo());
        ft = new SimpleDateFormat("HH:mm:ss");
        _ete = ft.format(c.getTime());

        toDraw = drawType.all;
        //invalidate();
        //requestLayout();
        if (this.distance == Distance.larger2000Meters) textColor = Color.DKGRAY;
        if (this.distance == Distance.smaller2000Meters) textColor = Color.GREEN;
        if (this.distance == Distance.smaller1000Meters) textColor = Color.CYAN;
        if (this.distance == Distance.smaller500Meters) textColor = Color.RED;


        toDraw = drawType.all;
        invalidate();
        requestLayout();
    }

    public enum Distance
    {
        smaller1000Meters,
        smaller500Meters,
        smaller2000Meters,
        larger2000Meters,
        reachedDestination
    }
}
