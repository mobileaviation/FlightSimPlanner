package nl.robenanita.googlemapstest.Instruments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Rob Verhoef on 25-8-2014.
 */
public class AirspeedView extends View {


    public AirspeedView(Context context, AttributeSet attrs) {
        super(context, attrs);

        big = false;
        this.context = context;
        speed = 0;
        setupView(big);
    }

    private void setupView(boolean big)
    {
        Resources R = context.getResources();

        if (big){
            as = nl.robenanita.googlemapstest.R.drawable.airspeedbig;
            io = nl.robenanita.googlemapstest.R.drawable.instrumentoutsidebig;
            ap = nl.robenanita.googlemapstest.R.drawable.airspeedpointerbig;
        }
        else
        {
            as = nl.robenanita.googlemapstest.R.drawable.airspeed;
            io = nl.robenanita.googlemapstest.R.drawable.instrumentoutside;
            ap = nl.robenanita.googlemapstest.R.drawable.airspeedpointer;
        }

        airspeedBitmap = BitmapFactory.decodeResource(R, as);
        outsideBitmap = BitmapFactory.decodeResource(R, io);
        pointerBitmap = BitmapFactory.decodeResource(R, ap);
    }

    Context context;
    boolean big;
    float speed;


    int as, io, ap;
    Bitmap airspeedBitmap;
    Bitmap outsideBitmap;
    Bitmap pointerBitmap;

    public void setSpeed(float speed)
    {
        // convert from m/s to knots
        this.speed = speed * 1.9438444924574f;
        invalidate();
        requestLayout();
    }
    public void setBig(boolean big)
    {
        this.big = big;
        setupView(big);
        invalidate();
        requestLayout();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        double angle = ((double)speed - 15d) / 0.555d;// ((((double)speed-20) / 200d) * 330);
        if (angle<0) angle = 0;

        // Create blank bitmap of equal size
        Bitmap rotateBitmap = Bitmap.createBitmap(pointerBitmap.getWidth(), pointerBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas pointerCanvas = new Canvas(rotateBitmap);
//        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate((float)angle, pointerBitmap.getWidth()/2, pointerBitmap.getHeight()/2);
        pointerCanvas.drawBitmap(pointerBitmap, rotateMatrix, null);


        int midx = outsideBitmap.getWidth()/2;
        int midy = outsideBitmap.getHeight()/2;

        canvas.drawBitmap(outsideBitmap, 0, 0, null);
        canvas.drawBitmap(airspeedBitmap, (float)midx-(airspeedBitmap.getWidth()/2), (float)midy-(airspeedBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateBitmap, (float)midx-(rotateBitmap.getWidth()/2), (float)midy-(rotateBitmap.getHeight()/2), null);
    }
}
