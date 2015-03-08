package nl.robenanita.googlemapstest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Rob Verhoef on 15-8-2014.
 */
public class CompassNeedle extends View{
    public CompassNeedle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public CompassNeedle(Context context, float heading)
//    {
//        super(context);
//        this.heading = heading;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.compassarrow);
        Matrix matrix = new Matrix();
        matrix.setRotate(heading,source.getWidth()/2,source.getHeight()/2);
        canvas.drawBitmap(source, matrix, new Paint());

    }

    private float heading;
    public void setHeading(float heading)
    {
        this.heading = heading;
        invalidate();
        requestLayout();
    }
}
