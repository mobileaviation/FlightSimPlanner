package nl.robenanita.googlemapstest.Instruments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.Resources;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 26-8-2014.
 */
public class CompassView extends View {
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);

        big = false;
        this.context = context;
        heading = 0;
        setupView(big);
    }

    Context context;
    boolean big;
    float heading;

    int c, os, cp;

    Bitmap compassBitmap;
    Bitmap outsideBitmap;
    Bitmap pointerBitmap;

    public void setBig(boolean big)
    {
        this.big = big;
        setupView(big);
        invalidate();
        requestLayout();
    }

    public void setHeading(float heading)
    {
        this.heading = heading;
        invalidate();
        requestLayout();
    }

    private void setupView(boolean big)
    {
        Resources res = context.getResources();

        if (big)
        {
            c = R.drawable.compassbig;
            os = R.drawable.instrumentoutsidebig;
            cp = R.drawable.compasspointerbig;
        }else
        {
            c = R.drawable.compass;
            os = R.drawable.instrumentoutside;
            cp = R.drawable.compasspointer;
        }

        compassBitmap = BitmapFactory.decodeResource(res, c);
        outsideBitmap = BitmapFactory.decodeResource(res, os);
        pointerBitmap = BitmapFactory.decodeResource(res, cp);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Bitmap rotateBitmap = Bitmap.createBitmap(compassBitmap.getWidth(), compassBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas compassCanvas = new Canvas(rotateBitmap);
        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(-heading, compassBitmap.getWidth()/2, compassBitmap.getHeight()/2);
        compassCanvas.drawBitmap(compassBitmap, rotateMatrix, null);

        int midx = outsideBitmap.getWidth()/2;
        int midy = outsideBitmap.getHeight()/2;

        canvas.drawBitmap(outsideBitmap, 0, 0, null);
        canvas.drawBitmap(rotateBitmap, (float)midx-(rotateBitmap.getWidth()/2), (float)midy-(rotateBitmap.getHeight()/2), null);
        canvas.drawBitmap(pointerBitmap, (float)midx-(pointerBitmap.getWidth()/2), (float)midy-(pointerBitmap.getHeight()/2), null);
    }
}
